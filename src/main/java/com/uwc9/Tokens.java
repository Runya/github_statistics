package com.uwc9;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
class Tokens {

    private final static Logger logger = Logger.getLogger(Tokens.class);

    private final AccessToken[] accessTokens;

    @Autowired
    public Tokens(GitHttpRequest request, GitHubProperties properties) {
        String[] tokens = properties.access_token.split(",");
        accessTokens = new AccessToken[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            accessTokens[i] = new AccessToken(tokens[i], request);
        }
    }

    public String getCorrectToken() throws Exception {
        for (AccessToken token : accessTokens) {
            if (token.getRateLimit() > 0) {
                return token.getToken();
            }
        }
        throw new Exception("Rate limit exceptions!");
    }

    static private class AccessToken {

        private final static Logger logger = Logger.getLogger(AccessToken.class);

        private GitHttpRequest request;
        private int rateLimit;
        private String token;
        private long timeStamp;

        Runnable updateInfo =  () -> {
            while (true) {
                try {
                    Response r = request.sendGet(request.getRateLimitUrl.get(), Collections.singletonMap("access_token", token), Collections.emptyMap());
                    rateLimit = Integer.parseInt(r.getHeaderProperties("X-RateLimit-Remaining", "0"));
                    timeStamp = Long.parseLong(r.getHeaderProperties("X-RateLimit-Reset", "0"));
                    logger.info(String.format("Access token updated, new rateLimit = %d, RateLimit-Reset = %d", rateLimit, timeStamp));
                    java.lang.Thread.sleep(timeStamp - System.currentTimeMillis() / 10000);
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        };

        private AccessToken(String token, GitHttpRequest request) {
            this.request = request;
            this.rateLimit = 0;
            this.token = token;
            long MINUTE = 60 * 1000;
            this.timeStamp = System.currentTimeMillis() + MINUTE;
            new Thread(updateInfo).start();
        }

        public int getRateLimit() {
            return rateLimit;
        }

        public String getToken() {
            rateLimit--;
            return token;
        }

        public long getTimeStamp() {
            return timeStamp;
        }
    }
}
