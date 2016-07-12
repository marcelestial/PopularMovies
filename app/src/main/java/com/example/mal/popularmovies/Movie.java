package com.example.mal.popularmovies;


public class Movie{
    String posterpath, title, overview, date, voteAvg;

    public Movie(String posterpath, String title, String overview, String date, String voteAvg){
        this.posterpath = posterpath;
        this.title = title;
        this.overview = overview;
        this.date = date;
        this.voteAvg = voteAvg;
    }

}
