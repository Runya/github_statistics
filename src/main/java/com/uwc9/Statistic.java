package com.uwc9;


import java.time.LocalDate;

public class Statistic {

    private int id;
    private LocalDate date;
    private int count;

    public Statistic(int id, LocalDate date, int count) {
        this.id = id;
        this.date = date;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
