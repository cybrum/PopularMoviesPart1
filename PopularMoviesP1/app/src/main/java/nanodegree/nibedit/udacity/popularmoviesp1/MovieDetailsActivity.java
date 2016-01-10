package nanodegree.nibedit.udacity.popularmoviesp1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Description : This activity is used for setting a detail fragment and showing movie detail data.
 * Created on : 12/25/2015
 * Author     : Nibedit Dey
 */

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieDetailActivityFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class MovieDetailActivityFragment extends Fragment {

        public MovieDetailActivityFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View mainView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
            Intent intent = getActivity().getIntent();
            if(intent == null || !intent.hasExtra("selected_movie")){
                return mainView;
            }
            MovieData movieInfo = intent.getParcelableExtra("selected_movie");
            ImageView moviePoster = (ImageView)mainView.findViewById(R.id.imgDetailPoster);
            if(movieInfo.posterUri != null) {
                Picasso.with(mainView.getContext()).load(movieInfo.posterUri.replace("w185", "w780")).into(moviePoster);
            }
            else{
                Picasso.with(mainView.getContext()).load(R.drawable.placeholder).into(moviePoster);
            }
            TextView title = (TextView)mainView.findViewById(R.id.txtDetailTitle);
            title.setText(movieInfo.name);
            TextView synopsis = (TextView)mainView.findViewById(R.id.txtDetailSummary);
            synopsis.setText(movieInfo.overview);
            TextView rating = (TextView)mainView.findViewById(R.id.txtDetailRating);
            rating.setText(movieInfo.voteAvg + "/10");
            Float ratingInfo= Float.parseFloat(movieInfo.voteAvg);
            ((RatingBar)mainView.findViewById(R.id.ratingBar)).setRating(ratingInfo);
            TextView release = (TextView)mainView.findViewById(R.id.txtDetailRelease);
            release.setText(movieInfo.releaseDate);
            return mainView;
        }

    }

}






