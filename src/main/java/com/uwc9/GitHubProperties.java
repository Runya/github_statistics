package com.uwc9;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@PropertySource("classpath:github.properties")
public class GitHubProperties {

    @Value("${access_token}")
    public String access_token;

    public Map<String, String> ACCEPT = Collections.singletonMap("Accept", "application/vnd.github.v3.star+json");

}