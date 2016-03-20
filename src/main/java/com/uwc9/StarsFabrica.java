package com.uwc9;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
public class StarsFabrica {

    private Map<String, GitRepo> repos;

    JSONParser parser = new JSONParser();

    @Autowired
    private GitHttpRequest request;

    public Response getLeftAlign(Response response) {
        return null;
    }

    public void updateRepoInfo(Response response, GitRepo repo) throws ParseException, java.text.ParseException {
        JSONArray jsonArray = (JSONArray) parser.parse(response.getContent());

        for (Object object : jsonArray) {
           JSONObject jsonObject = (JSONObject) object;
            String time = (String) jsonObject.get("starred_at");
            SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
            Date date = dt.parse(time.substring(0, time.length() - 1).replace("T", " "));

            repo.addInfo(date);
        }
    }



    public GitRepo getGitRepo(String id) {
        if (repos.containsKey(id)) {
            return repos.get(id);
        } else  {
            GitRepo repo = new GitRepo(id);
            repos.put(id, repo);
            return repo;
        }
    }


}
