package com.example.grin.Classes;

public class Settings {
    private String sortBy;
    private String wantedList;
    private String showAll;
    private Double distance;

    public Settings( String sortBy, String wantedList, Double distance,String showAll) {
        this.sortBy = sortBy;
        this.wantedList = wantedList;
        this.distance = distance;
        this.showAll=showAll;
    }


    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getWantedList() {
        return wantedList;
    }

    public void setWantedList(String wantedList) {
        this.wantedList = wantedList;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
