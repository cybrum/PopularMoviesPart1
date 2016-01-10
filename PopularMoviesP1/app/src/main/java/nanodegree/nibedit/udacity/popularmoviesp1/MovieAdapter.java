package nanodegree.nibedit.udacity.popularmoviesp1;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Description : This adapter is used for a grid view which shows movies list.
 * Created on : 12/28/2015
 * Author     : Nibedit Dey
 */
public class MovieAdapter extends ArrayAdapter<MovieData> {
    Context context;
    int layoutResourceId;
    ArrayList<MovieData> movies = null;

    public MovieAdapter(Context c, int layoutRId, ArrayList<MovieData> m){
        super(c, layoutRId, m);
        context = c;
        layoutResourceId = layoutRId;
        movies = m;
    }

    public void setMovies(ArrayList<MovieData> m){
        movies.addAll(m);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View cell = convertView;
        MoviePoster moviePoster;
        if(cell == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            cell = inflater.inflate(layoutResourceId, parent, false);
            moviePoster = new MoviePoster();
            moviePoster.imgPoster = (ImageView)cell.findViewById(R.id.imgPoster);
            cell.setTag(moviePoster);
        }
        else{
            moviePoster = (MoviePoster)cell.getTag();
        }
        MovieData movieData = movies.get(position);
        if(movieData.posterUri != null) {
            Picasso.with(context).load(movieData.posterUri).into(moviePoster.imgPoster);
        }
        else{
            Picasso.with(context).load(R.drawable.placeholder).into(moviePoster.imgPoster);
        }

        return cell;
    }

    private class MoviePoster {
        ImageView imgPoster;
    }
}

