package com.uwc9;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;


@Component
public class HttpRequest {

    private final String USER_AGENT = "Mozilla/5.0";

    private GitHubProperties properies;

    @Autowired
    public HttpRequest(GitHubProperties properties) {
        this.properies = properties;
    }


    public Function<String, String> getResponse = (url) -> {
        try {
            URL obj = new URL((getStarsRequestUrl.apply(url)));

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add request header
            con.setRequestProperty("Accept", "application/vnd.github.v3.star+json");

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {

                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";

    };

    public static Function<String, String> getStarsRequestUrl = (repoName) -> "https://api.github.com/repos/" + repoName + "/stargazers";

    public static Supplier<String> getRateLimitUrl = () -> "https://api.github.com/rate_limit";

    public Supplier<String> getAccessToken = () -> {


        for (String token : properies.access_token.split(",")) {
            try {
                Respons r = sendGet(getRateLimitUrl.get(), Collections.singletonMap("access_token", token), new HashMap<>());
                int rateLimit = Integer.parseInt(r.getHeaderProperties("X-Ratelimit-Reset", "0"));
                if (rateLimit > 0) {
                    return token;
                }
            } catch (Exception ignored) {
            }
        }

        //todo add exceptions
        return "";

    };

    // HTTP GET request
    public Respons sendGet(String url, Map<String, String> param, Map<String, String> headers) throws Exception {

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

        return new Respons(response.getAllHeaders(), result.toString());
    }

}


class Respons{

    private Header[] headers;
    private String content;

    public Respons(Header[] headers, String content) {
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