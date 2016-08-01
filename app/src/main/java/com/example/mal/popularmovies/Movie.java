package com.example.mal.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{
    String posterpath, title, overview, date, voteAvg;

    public Movie(String posterpath, String title, String overview, String date, String voteAvg){
        this.posterpath = posterpath;
        this.title = title;
        this.overview = overview;
        this.date = date;
        this.voteAvg = voteAvg;
    }

    private Movie(Parcel in){
        posterpath = in.readString();
        title = in.readString();
        overview = in.readString();
        date = in.readString();
        voteAvg = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(posterpath);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(date);
        parcel.writeString(voteAvg);
    }

    public final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel parcel){
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i){
            return new Movie[i];
        }
    };
}
