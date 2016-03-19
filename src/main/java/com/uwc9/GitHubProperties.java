package com.uwc9;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:github.properties")
public class GitHubProperties {

    @Value("${access_token}")
    public String access_token;

}