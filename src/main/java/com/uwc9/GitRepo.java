package com.uwc9;

import java.util.Date;
import java.util.HashMap;

public class GitRepo {

    HashMap<Date, Integer> starsInfo;
    String repoName;

    public HashMap<Date, Integer> getStarsInfo() {
        return starsInfo;
    }

    public String getRepoName() {
        return repoName;
    }

    GitRepo(String repoName){
        starsInfo = new HashMap<>();
        this.repoName = repoName;

    }

    public void addInfo(Date data) {
        if (starsInfo.containsKey(data)) {
            starsInfo.put(data, starsInfo.get(data) + 1);
        } else {
            starsInfo.put(data, 1);
        }
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        for (Date d : starsInfo.keySet()) {
            builder
                    .append("Date: ")
                    .append(d.toString())
                    .append(" Count: ")
                    .append(starsInfo.get(d))
                    .append("\n");
        }
        return builder.toString();
    }
}
