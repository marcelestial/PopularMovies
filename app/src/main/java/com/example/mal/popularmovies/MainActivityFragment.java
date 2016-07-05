package com.example.mal.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import java.util.Arrays;

/**
 * A fragment containing the grid view
 */
public class MainActivityFragment extends Fragment {

    private MovieAdapter movieAdapter;

    Movie[] movies = {};

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new MovieAdapter(getActivity(), Arrays.asList(movies));

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_flavor);
        gridView.setAdapter(movieAdapter);

        return rootView;
    }
}
