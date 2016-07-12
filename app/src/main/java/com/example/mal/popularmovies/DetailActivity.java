package com.example.mal.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent();
            if(intent != null && intent.hasExtra("movieTitle")){
                String title = intent.getStringExtra("movieTitle");
                String posterPath = intent.getStringExtra("moviePosterPath");
                String overview = intent.getStringExtra("movieOverview");
                String voteAvg = intent.getStringExtra("movieVoteAvg");
                String date = intent.getStringExtra("movieDate");

                Picasso.with(getActivity()).load(posterPath).into(((ImageView) rootView.findViewById(R.id.moviePoster)));

                ((TextView) rootView.findViewById(R.id.movieTitle)).setText(title);
                ((TextView) rootView.findViewById(R.id.movieDate)).setText(date);
                ((TextView) rootView.findViewById(R.id.movieVoteAvg)).setText(voteAvg);
                ((TextView) rootView.findViewById(R.id.movieOverview)).setText(overview);
            }

            return rootView;
        }
    }
}