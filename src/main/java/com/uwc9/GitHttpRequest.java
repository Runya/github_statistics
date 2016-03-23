package com.uwc9;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class GitHttpRequest {

    private final static Logger logger = Logger.getLogger(GitHttpRequest.class);

    public static final String USER_AGENT = "Mozilla/5.0";

    public Function<String, String> getStarsRequestUrl = (repoName) -> {
        if (repoName.contains("/"))
            return "https://api.github.com/repos/" + repoName + "/stargazers";
        else
            return "https://api.github.com/repositories/" + repoName + "/stargazers";
    };

    public Supplier<String> getRateLimitUrl = () -> "https://api.github.com/rate_limit";

    // HTTP GET request
    public Response sendGet(String url, Map<String, String> param, Map<String, String> headers) throws Exception {

        StringBuilder params = new StringBuilder();
        for (String key : param.keySet()) {
            params.append(key)
                    .append("=")
                    .append(param.get(key))
                    .append("&");
        }

        String newUrl = url;
        if (params.length() != 0) {
            if (newUrl.contains("?")) newUrl += "&" + params.toString();
            else newUrl += "?" + params.toString();
        }

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(newUrl);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);

        for (String k : headers.keySet()) {
            request.addHeader(k, headers.get(k));
        }

        HttpResponse response = client.execute(request);

        logger.info("\nSending 'GET' request to URL : " + newUrl);
        logger.info("Response Code : " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        return new Response(response.getAllHeaders(), result.toString(), newUrl);
    }

}


class Request {

    private HttpGet httpGet;
    private HttpClient client;
    private String params;
    private final static Logger LOGGER = Logger.getLogger(Request.class);


    public Request(Map<String, String> head, Map<String, String> param) {
        StringBuilder params = new StringBuilder();
        for (String key : param.keySet()) {
            params.append(key)
                    .append("=")
                    .append(param.get(key))
                    .append("&");
        }

        this.params = params.toString();
        this.client = HttpClientBuilder.create().build();
        this.httpGet = new HttpGet();

        httpGet.addHeader("User-Agent", GitHttpRequest.USER_AGENT);

        for (String k : head.keySet()) {
            httpGet.addHeader(k, head.get(k));
        }
    }

    public Response sendGet(String url) throws IOException {
        String newUrl = url + "?" + params;
        httpGet.setURI(URI.create(url));

        HttpResponse response = client.execute(httpGet);

        LOGGER.info("\nSending 'GET' request to URL : " + newUrl);
        LOGGER.info("Response Code : " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        return new Response(response.getAllHeaders(), result.toString(), newUrl);
    }


}

class Response {

    private final static Logger LOGGER = Logger.getLogger(Response.class);

    private Header[] headers;
    private String content;
    private String url;

    public Response(Header[] headers, String content, String url) {
        this.headers = headers;
        this.content = content;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public String getHeaderProperties(String key, String def) {
        for (Header h : headers) {
            if (h.getName().equals(key)) return h.getValue();
        }
        return def;
    }

    Pattern pattern = Pattern.compile("<(.*?)>; rel=\"(.*?)\"");

    public String getNextUrl() {
        return getLinkUrl("next");
    }

    public String getLastUrl() {
        return getLinkUrl("last");
    }

    public String getPrevUrl() {
        return getLinkUrl("prev");
    }

    private String getLinkUrl(String key) {
        try {
            String links = getHeaderProperties("Link", "");
            if (links.equals(""))
                throw new Exception(key + " linc not found.");
            Matcher matcher = pattern.matcher(links);
            while (matcher.find()) {
                String prop = matcher.group(2);
                if (prop.equals(key)) return matcher.group(1);
            }
            throw new Exception(key + " link not found.");
        } catch (Exception e) {
            LOGGER.error(e);
            return "";
        }

    }

    public int getNonParsePageCount() {
        int index = url.indexOf("?page=");
        if (index == -1) index = url.indexOf("&page=");
        if (index == -1) return 0;
        return Integer.parseInt(url.substring(index + 6, Math.max(url.substring(index + 6).indexOf("&"), url.length())));
    }
}