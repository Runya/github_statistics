package com.uwc9;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@Configuration
public class HelloController {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello(){
        GitHubClient client = new GitHubClient();

        return "hello/hello";
    }
}
