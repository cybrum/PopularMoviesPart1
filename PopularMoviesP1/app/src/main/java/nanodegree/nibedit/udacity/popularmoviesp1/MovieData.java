package nanodegree.nibedit.udacity.popularmoviesp1;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description : Represents movie data retrieved from the web server
 * Created on : 12/25/2015
 * Author     : Nibedit Dey
 */
public class MovieData implements Parcelable {
    String name;
    String id;
    String posterUri;
    String overview;
    String voteAvg;
    String releaseDate;

    public MovieData(Parcel in){
        this.name = in.readString();
        this.id = in.readString();
        this.posterUri = in.readString();
        this.overview = in.readString();
        this.voteAvg = in.readString();
        this.releaseDate = in.readString();
    }

    public MovieData(JSONObject movieData){
        try {
            this.name = movieData.getString("original_title");
            this.id = movieData.getString("id");
            if (!movieData.getString("poster_path").equals("null")) {
                this.posterUri = "http://image.tmdb.org/t/p/w342" + movieData.getString("poster_path");
            }
            this.overview = movieData.getString("overview");
            this.voteAvg = movieData.getString("vote_average");
            this.releaseDate = movieData.getString("release_date");
        }
        catch (JSONException e){
            Log.e("Fetching Error: ", e.getMessage());
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(posterUri);
        dest.writeString(overview);
        dest.writeString(voteAvg);
        dest.writeString(releaseDate);
    }

    public static final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
}
