package com.uwc9;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GitRepo {

    private Map<LocalDate, Integer> starsInfo;
    private String repoName;
    private int nonParseStars;

    public Map<LocalDate, Integer> getStarsInfo() {
        return starsInfo;
    }

    public String getRepoName() {
        return repoName;
    }

    public int getNonParseStars() {
        return nonParseStars;
    }

    GitRepo(String repoName) {
        starsInfo = new HashMap<>();
        this.repoName = repoName;

    }

    public void setNonParseStars(int count) {
        this.nonParseStars = count;
    }

    public void addInfo(LocalDate data) {
        if (starsInfo.containsKey(data)) {
            starsInfo.put(data, starsInfo.get(data) + 1);
        } else {
            starsInfo.put(data, 1);
        }
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        for (LocalDate d : starsInfo.keySet()) {
            builder.append("Date: ")
                    .append(d.toString())
                    .append(" Count: ")
                    .append(starsInfo.get(d))
                    .append("\n");
        }
        return builder.toString();
    }

    public boolean haveSmallerDate(LocalDate lastDate) {
        for (LocalDate l : getStarsInfo().keySet()) {
            if (l.compareTo(lastDate) < 0) return true;
        }
        return false;
    }


    public int correctInfo(Period period) {
        return clearFromSmallerDate(period.getLastDate());
    }

    public int clearFromSmallerDate(LocalDate date) {
        final int[] res = {0};
        setStarsInfo(getStarsInfo()
                .entrySet()
                .stream()
                .filter(p -> {
                    if (p.getKey().compareTo(date) < 0) {
                        System.out.println("nice");
                        res[0] += p.getValue();
                        return false;
                    } else return true;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        return res[0];
    }

    public void setStarsInfo(Map<LocalDate,Integer> starsInfo) {
        this.starsInfo = starsInfo;
    }
}

enum Period {
    WEEK, MONTH;

    public LocalDate getLastDate() {
        LocalDate res = null;
        switch (this) {
            case WEEK:
                res = LocalDate.now().minusWeeks(1L).plusDays(1);
                break;
            case MONTH:
                res = LocalDate.now().minusMonths(1L).plusDays(1);
                break;
        }
        return res;
    }
}