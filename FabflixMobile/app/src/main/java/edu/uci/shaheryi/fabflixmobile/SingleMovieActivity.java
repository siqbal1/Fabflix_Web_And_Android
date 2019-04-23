package edu.uci.shaheryi.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class SingleMovieActivity extends AppCompatActivity {

    String movieId;
    String movieTitle;
    String movieDirector;
    String movieYear;
    String movieGenres;
    String movieCastList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        Intent i = getIntent();

        movieId = i.getStringExtra("movieId");

        Log.d("movieId", movieId);

        getMovieInfo();
    }

    public void getMovieInfo()
    {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("movieId", movieId);

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

<<<<<<< HEAD
        final StringRequest searchRequest = new StringRequest(Request.Method.POST, "https://18.220.134.46:8443/project1/api/android-singleMovie",
=======
        final StringRequest searchRequest = new StringRequest(Request.Method.POST, "https://52.15.77.91:8443/project1/api/android-singleMovie",
>>>>>>> d6cf9ef9fcd95f2379d0462e60dd191d2cd7651f
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            movieTitle = jsonObject.getString("movieTitle").toString();
                            movieDirector = jsonObject.getString("movieDirector").toString();
                            movieYear = jsonObject.getString("movieYear").toString();
                            movieGenres = jsonObject.getString("movieGenres").toString();
                            movieCastList = jsonObject.getString("starsList").toString();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        populateView();
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("security.error", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }  // HTTP POST Form Data
        };

        queue.add(searchRequest);
    }

    public void populateView()
    {
        Log.d("title", movieTitle);
        Log.d("director", movieDirector);
        Log.d("genres", movieGenres);
        Log.d("cast", movieCastList);

        TextView titleView = (TextView) findViewById(R.id.singleMovieTitleView);
        TextView directorView = (TextView) findViewById(R.id.singleMovieDirectorView);
        TextView yearView = (TextView) findViewById(R.id.singleMovieYearView);
        TextView genresView = (TextView) findViewById(R.id.singleMovieGenresView);
        TextView castView = (TextView) findViewById(R.id.singleMovieCastView);

        titleView.setText(movieTitle);
        directorView.append(movieDirector);
        yearView.append(movieYear);
        genresView.setText(movieGenres);
        castView.setText(movieCastList);
    }

    public void goToSearch(View view)
    {
        Intent i = new Intent(this, SearchActivity.class);

        startActivity(i);
    }




}
