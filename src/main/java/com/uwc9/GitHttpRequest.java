package com.uwc9;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;


@Component
public class GitHttpRequest {

    private final static Logger logger = Logger.getLogger(GitHttpRequest.class);

    private String USER_AGENT = "Mozilla/5.0";

    public Function<String, String> getStarsRequestUrl = (repoName) -> "https://api.github.com/repos/" + repoName + "/stargazers";

    public Supplier<String> getRateLimitUrl = () -> "https://api.github.com/rate_limit";

    // HTTP GET request
    public Response sendGet(String url, Map<String, String> param, Map<String, String> headers) throws Exception {

        StringBuilder params = new StringBuilder();
        for (String key : param.keySet()) {
            System.out.println(key + " " + param.get(key));
            params.append(key)
                    .append("=")
                    .append(param.get(key))
                    .append("&");
        }

        String newUrl = url + "?" + params;

        HttpClient client = HttpClientBuilder.create().build();;
        HttpGet request = new HttpGet(newUrl);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);

        for (String k : headers.keySet()) {
            request.addHeader(k, headers.get(k));
        }

        HttpResponse response = client.execute(request);

        System.out.println("\nSending 'GET' request to URL : " + newUrl);
        System.out.println("Response Code : " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        return new Response(response.getAllHeaders(), result.toString());
    }

}


class Response {

    private Header[] headers;
    private String content;

    public Response(Header[] headers, String content) {
        this.headers = headers;
        this.content = content;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHeaderProperties(String key, String def) {
        for (Header h : headers) {
            if (h.getName().equals(key)) return h.getValue();
        }
        return def;
    }
}