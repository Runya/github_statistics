package com.uwc9;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.function.Function;
import java.util.function.Supplier;

public class RequestURLs {

    public static Supplier getAccessToken = () -> "bacf91989414509ccea43cd0a1e1a383ee4eb666";

    public static Function getStarsUrl = (repoName) -> "https://api.github.com/repos/ " + repoName + "/stargazers?"
            + getAccessToken.get();

    public static Function getRespons = (url) -> {


        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException ignored) {}

        //add request header
        con.setRequestProperty("Accept", "application/vnd.github.v3.star+json");

        int responseCode = 0;
        try {
            responseCode = con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

}