package com.example.mal.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
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

/**
 * A fragment containing the grid view and spinner toolbar
 */
public class MainActivityFragment extends Fragment {

    public ArrayList<Movie> movies;
    boolean refill = true;
    String sortStyle;
    private MovieAdapter movieAdapter;
    private GridView gridView;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        movies = new ArrayList<>();

        if(savedInstanceState != null){
            Log.d("AAAAONCREATE", "savedInstanceState != null");
            sortStyle = savedInstanceState.getString("SORT_STYLE");
            movies = savedInstanceState.getParcelableArrayList("MOVIES_LIST");
            refill = false;
        }
        else{
            Log.d("AAAAONCREATE", "savedInstanceState = null");
            sortStyle = getString(R.string.popsort);
            refill = true;
        }
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        //create the spinner and arrayadapter for the spinner items
        Spinner sortSpinner = (Spinner) v.findViewById(R.id.sort_spinner);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sorts_array, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        sortSpinner.setAdapter(arrayAdapter);

        int selectedItem;
        if (sortStyle.equals(getString(R.string.ratesort))){
            selectedItem = 1;
        }
        else if (sortStyle.equals(getString(R.string.favsort))){
            selectedItem = 2;
        }
        else{
            selectedItem = 0;
        }

        sortSpinner.setSelection(selectedItem);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    sortStyle = getString(R.string.popsort);
                }
                else if(position == 1){
                    sortStyle = getString(R.string.ratesort);
                }
                else{
                    sortStyle = getString(R.string.favsort);
                }

                populateMovies(sortStyle);

                //EDREHASIVAR
                if(gridView != null){
                    gridView.setSelection(0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

    public void onStart(){
        super.onStart();

        //EDREHASIVAR
        /*Log.d("AAAA", "refill: " + refill);

        if(refill || sortStyle == getString(R.string.favsort)){
            populateMovies(sortStyle);
        }*/
    }

    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString("SORT_STYLE", sortStyle);
        savedInstanceState.putParcelableArrayList("MOVIES_LIST", movies);
        super.onSaveInstanceState(savedInstanceState);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem){
        return true;
    }

    private void populateMovies(String sortStyle){
        Log.d("AAAA", "populateMovies: " + sortStyle);

        ConnectivityManager manager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        boolean connected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(connected || sortStyle == getString(R.string.favsort)){
            FetchMovieTask fetchMovieTask = new FetchMovieTask();
            fetchMovieTask.execute(sortStyle);
        }
        else {
            if (gridView != null){
                gridView.setAdapter(null);
                Toast.makeText(getActivity(),
                        "Unable to retrieve movies data. Please check your network connection.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public interface Callback{
        public void onItemSelected(ArrayList<Movie> movies, int position);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>>{
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private ArrayList<Movie> getMovieDataJson(String movieJsonStr)
        throws JSONException{

            final String RESULTS = "results";
            final String TITLE = "original_title";
            final String POSTER = "poster_path";
            final String OVERVIEW = "overview";
            final String VOTES = "vote_average";
            final String RELEASE = "release_date";

            ArrayList<Movie> movieArrayList = new ArrayList<>();

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

                movieArrayList.add(outputAttr);

            }
            return movieArrayList;
        }

        private ArrayList<Movie> getFavorites(){
            //EDREHASIVAR
            //fill in later
            return null;
        }

        protected ArrayList<Movie> doInBackground(String... params){

            if (params.length == 0){
                return null;
            }

            if(params[0] == getString(R.string.favsort)){
                return getFavorites();
            }
            else{
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String movieJsonStr = null;

                try {
                    String urlString = params[0] + getString(R.string.api_key);

                    URL moviesUrl = new URL(urlString);

                    // Create the request to TMDB, and open the connection
                    urlConnection = (HttpURLConnection) moviesUrl.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    if (buffer.length() == 0) {
                        Log.d("AAAA", "buffer length is 0");
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    movieJsonStr = buffer.toString();
                } catch (IOException ex){
                    Log.e("MainActivityFragment", "Error ", ex);
                    // If the code didn't successfully get the movie data, there's no point in attempting
                    // to parse it.
                    return null;
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
                } catch (JSONException ex){
                    Log.e(LOG_TAG, ex.getMessage(), ex);
                    ex.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Movie> result) {
            if (result != null) {
                if (movies.size() > 0) {
                    movies.clear();
                }

                movies = (ArrayList<Movie>) result.clone();

                if (gridView == null) {
                    gridView = (GridView) getActivity().findViewById(R.id.gridview);

                    movieAdapter = new MovieAdapter(getActivity(), movies);
                    gridView.setAdapter(movieAdapter);

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            ((Callback) getActivity()).onItemSelected(movies, position);
                        }
                    });
                }
                movieAdapter.notifyDataSetChanged();
            }
        }
    }
}