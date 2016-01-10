package nanodegree.nibedit.udacity.popularmoviesp1;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

/**
 * Description : This class is responsible for loading movie and showing data at the grid component.
 * Created on : 12/25/2015
 * Author     : Nibedit Dey
 */

public class MovieDetailsFragment extends Fragment {

    public MovieDetailsFragment() {
    }

    MovieAdapter movieArrayAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("CurrentMovies", movieArrayAdapter.movies);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_movie, container, false);

        GridView gridview = (GridView)rootView.findViewById(R.id.gridViewMovieList);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(getActivity(), MovieDetailsActivity.class);
                detailIntent.putExtra("selected_movie", movieArrayAdapter.getItem(position));
                startActivity(detailIntent);
            }
        });
        movieArrayAdapter = new MovieAdapter(getActivity(),R.layout.movie_layout, new ArrayList<MovieData>());

        if(savedInstanceState == null){
            updateMovieList(getString(R.string.menu_popular));
        }
        else {
            movieArrayAdapter.setMovies((ArrayList<MovieData>) savedInstanceState.get("CurrentMovies"));
        }
        gridview.setAdapter(movieArrayAdapter);

        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_popular) {
            updateMovieList(getString(R.string.menu_popular));
            return true;
        }
        else if(id == R.id.action_highest_rated){
            updateMovieList(getString(R.string.menu_highest_rated));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateMovieList(String sortingPreference){
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            MovieFetcher movieFetcher = new MovieFetcher();
            movieFetcher.execute(sortingPreference);
        }
        else{
            Toast toast = Toast.makeText(getActivity(), "No Network Connectivity",Toast.LENGTH_LONG);
            toast.show();
        }
    }


    class MovieFetcher extends AsyncTask<String,Void,MovieData[]> {

        private MovieData[] movieDataResponse;
        @Override
        protected MovieData[] doInBackground(String... params) {
            Uri.Builder movieDbUri = new Uri.Builder();

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonResponse;
            String orderBy = params[0].equals(getString(R.string.menu_popular))?"popularity.desc":"vote_average.desc";


            movieDbUri.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("discover")
                    .appendPath("movie")
                    .appendQueryParameter("sort_by", orderBy)
                    .appendQueryParameter("api_key", getString(R.string.api_key));
            try {
                URL url = new URL(movieDbUri.build().toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                //if there is no response from the server
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                //if response from server is empty
                if(buffer.length() == 0){
                    return null;
                }

                JsonResponse = buffer.toString();
                try{
                    movieDataResponse = parseMovieResponse(JsonResponse);
                }
                catch(JSONException j){
                    Log.e("Response Parsing Error ", j.getMessage());
                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            finally{
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try{
                        reader.close();
                    }
                    catch(final IOException e){
                        Log.e("Error Closing Stream ", e.getMessage());
                    }
                }
            }

            return movieDataResponse;
        }

        protected void onPostExecute(MovieData[] movieInfo){
            movieArrayAdapter.clear();
            for(MovieData tempMovieData: movieInfo){
                movieArrayAdapter.add(tempMovieData);
                movieArrayAdapter.notifyDataSetChanged();
            }
        }

        private MovieData[] parseMovieResponse(String json) throws JSONException{
            JSONObject movieListJson = new JSONObject(json);
            JSONArray movieListArray = movieListJson.getJSONArray("results");
            ArrayList<MovieData> returnMovieData = new ArrayList<>();
            for(int i = 0; i < movieListArray.length();i++){
                JSONObject tempJson= movieListArray.getJSONObject(i);
                MovieData tempMovieData = new MovieData(tempJson);
                returnMovieData.add(tempMovieData);
            }
            MovieData[] returnArray = new MovieData[returnMovieData.size()];
            return returnMovieData.toArray(returnArray);
        }
    }

}
