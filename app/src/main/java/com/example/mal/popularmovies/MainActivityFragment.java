package com.example.mal.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A fragment containing the grid view
 */
public class MainActivityFragment extends Fragment {

    private MovieAdapter movieAdapter;

    ArrayList<Movie> movies = new ArrayList<>();

    public MainActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();

        movieAdapter = new MovieAdapter(getActivity(), movies);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_flavor);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie detailMovie = (Movie)movieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movieTitle", detailMovie.title)
                        .putExtra("moviePosterPath", detailMovie.posterpath)
                        .putExtra("movieOverview", detailMovie.overview)
                        .putExtra("movieVoteAvg", detailMovie.voteAvg)
                        .putExtra("movieDate", detailMovie.date);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>>{

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private ArrayList<Movie> getMovieDataJson(String movieJsonStr)
                throws JSONException {

            final String RESULTS = "results";
            final String TITLE = "original_title";
            final String POSTER = "poster_path";
            final String OVERVIEW = "overview";
            final String VOTES = "vote_average";
            final String RELEASE = "release_date";

            ArrayList<Movie> newThumbids = new ArrayList<>();

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject results = movieArray.getJSONObject(i);

                String posterPath = "http://image.tmdb.org/t/p/w342" + results.getString(POSTER);
                String title = results.getString(TITLE);
                String overview = results.getString(OVERVIEW);
                String date = results.getString(RELEASE).substring(0, 4);
                String voteAvg = results.getString(VOTES) + " / 10";
                Movie outputAttr = new Movie(posterPath, title, overview, date, voteAvg);

                newThumbids.add(outputAttr);

            }
            return newThumbids;

        }

        protected ArrayList<Movie> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                // Construct the URL for the TMDB query
                URL url = new URL("http://api.themoviedb.org/3/movie/popular?api_key=473109b3964f3fc35d5cf0caa9941f46");

                // Create the request to TMDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    movieJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    movieJsonStr = null;
                }
                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("MainActivityFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                movieJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivityFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            movieAdapter.clear();
            for(Movie movie : movies){
                movieAdapter.add(movie);
            }
        }
    }
}