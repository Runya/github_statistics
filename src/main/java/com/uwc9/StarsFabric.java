package com.uwc9;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class StarsFabric {

    private Map<String, GitRepo> repo;

    private JSONParser parser = new JSONParser();

    private GitHttpRequest request;

    private Tokens tokens;

    @Autowired
    public StarsFabric(GitHttpRequest request, Tokens tokens) {
        this.request = request;
        repo = new HashMap<>();
        this.tokens = tokens;
    }

    @Autowired
    private GitHubProperties properties;

    public Response getLeftAlign(Response response) {
        return null;
    }



    public GitRepo getPeriodStarsInfo(Period period, String repoName) throws Exception {

        Map<String, String> params = new HashMap<>();
//        params.put("perpage", "100");
        params.put("access_token", tokens.getCorrectToken());

        Response response = request.sendGet(request.getStarsRequestUrl.apply(repoName), params, properties.ACCEPT);
        LocalDate lastDate = period.getLastDate();
        try {
            String lastUrl = response.getLastUrl();
            if (lastUrl.equals("")) lastUrl = response.getUrl();
            while (!lastUrl.equals("")) {
                response = request.sendGet(lastUrl, Collections.emptyMap(), properties.ACCEPT);
                GitRepo startInfo = getResponseGitInfo(response, repoName);
                updateRepoInfo(startInfo);
                if (startInfo.haveSmallerDate(lastDate)) break;
                lastUrl = response.getPrevUrl();
            }
        } catch (Exception e) {
            GitRepo starsInfo = getResponseGitInfo(response, repoName);
            updateRepoInfo(starsInfo);
        }

        GitRepo gitRepo = getGitRepo(repoName);

        int nonParsePageCount = response.getNonParsePageCount();

        gitRepo.setNonParseStars(nonParsePageCount * 30 + gitRepo.correctInfo(period));
        return gitRepo;

    }

    public void updateRepoInfo(GitRepo newRepo) throws ParseException, java.text.ParseException {
        GitRepo repo = getGitRepo(newRepo.getRepoName());
        repo.getStarsInfo().putAll(newRepo.getStarsInfo());
    }

    final static DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    public GitRepo getResponseGitInfo(Response response, String repoName) throws ParseException, java.text.ParseException {
        JSONArray jsonArray = (JSONArray) parser.parse(response.getContent());
        GitRepo gitRepo = new GitRepo(repoName);

        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            String time = (String) jsonObject.get("starred_at");
            LocalDate date = LocalDate.parse(time, FORMATTER);
            gitRepo.addInfo(date);
        }
        return gitRepo;
    }


    public GitRepo getGitRepo(String id) {
        if (repo.containsKey(id)) {
            return repo.get(id);
        } else {
            GitRepo repo = new GitRepo(id);
            this.repo.put(id, repo);
            return repo;
        }
    }

    public Status getStatusInfo(Response response, LocalDate date, String repoName) throws ParseException, java.text.ParseException {
        Set<LocalDate> localDates = getResponseGitInfo(response, repoName).getStarsInfo().keySet();
        if (localDates.size() == 0) return Status.NONE;
        if (localDates.size() == 1) return Status.ALLPAGE;
        if (haveSmaller(localDates, date) && !haveBigger(localDates, date)) return Status.END;
        if (!haveSmaller(localDates, date) && haveBigger(localDates, date)) return Status.START;
        if (haveSmaller(localDates, date) && haveBigger(localDates, date)) return Status.MIDLE;
        return Status.NONE;
    }

    private boolean haveBigger(Set<LocalDate> localDates, LocalDate date) {
        for (LocalDate d : localDates) {
            if (d.compareTo(date) > 0) return true;
        }
        return false;
    }

    private boolean haveSmaller(Set<LocalDate> localDates, LocalDate date) {
        for (LocalDate d : localDates) {
            if (d.compareTo(date) < 0) return true;
        }
        return false;
    }

    enum Status {
        ALLPAGE, START, END, NONE, MIDLE;
    }

}
