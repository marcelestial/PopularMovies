package com.example.mal.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import java.util.List;

import com.squareup.picasso.Picasso;


public class MovieAdapter extends ArrayAdapter{

    private Context context;

    public MovieAdapter(Activity context, List<Movie> movies){
        super(context, 0, movies);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final Movie movie = (Movie) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
        }

        ImageView posterView = (ImageView) convertView.findViewById(R.id.list_item_poster);
        Picasso.with(context).load(movie.posterpath).into(posterView);

        return convertView;
    }
}
