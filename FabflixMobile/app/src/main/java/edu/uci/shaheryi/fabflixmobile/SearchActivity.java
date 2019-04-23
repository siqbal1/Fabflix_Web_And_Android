package edu.uci.shaheryi.fabflixmobile;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    ArrayList<String> movieIds = new ArrayList<String>();
    ArrayList<String> movieTitles = new ArrayList<String>();
    ArrayList<String> movieDirectors = new ArrayList<String>();
    ArrayList<String> movieYears = new ArrayList<String>();
    ArrayList<String> movieGenres = new ArrayList<String>();
    ArrayList<String> movieCastList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }



    public void getSearchResults(View view) {
        //get the value of the search text
        EditText searchText = (EditText) findViewById(R.id.movieSearch);

        //make map to send params
        final Map<String, String> params = new HashMap<String, String>();
        params.put("movieSearch", searchText.getText().toString());


        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

<<<<<<< HEAD
        final StringRequest searchRequest = new StringRequest(Request.Method.POST, "https://18.220.134.46:8443/project1/api/android-search",
=======
        final StringRequest searchRequest = new StringRequest(Request.Method.POST, "https://52.15.77.91:8443/project1/api/android-search",
>>>>>>> d6cf9ef9fcd95f2379d0462e60dd191d2cd7651f
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response", response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            if(jsonArray.length() > 0)
                            {
                                for(int i = 0; i < jsonArray.length(); i++)
                                {
                                    String id = jsonArray.getJSONObject(i).getString("movieId");
                                    String title = jsonArray.getJSONObject(i).getString("movieTitle");
                                    String director = jsonArray.getJSONObject(i).getString("movieDirector");
                                    String year = jsonArray.getJSONObject(i).getString("movieYear");
                                    String genres = jsonArray.getJSONObject(i).getString("movieGenres");
                                    String starList = jsonArray.getJSONObject(i).getString("starsList");

                                    /*
                                    Log.d("id", id);
                                    Log.d("title", title);
                                    Log.d("director", director);
                                    Log.d("stars", starList);
                                    */


                                    movieIds.add(id);
                                    movieTitles.add(title);
                                    movieDirectors.add(director);
                                    movieYears.add(year);
                                    movieGenres.add(genres);
                                    movieCastList.add(starList);
                                }

                                goToMovieList();
                            }
                            else
                            {
                                TextView errorMessageView = (TextView) findViewById(R.id.errorMessageView);

                                errorMessageView.setTextColor(Color.RED);
                                errorMessageView.setText("No movies with that keyword. Please enter in another search phrase.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



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

        //after getting the first array params
        //go to the movieList page
    }

    public void goToMovieList()
    {
        Intent i = new Intent(this, MovieList.class);

        EditText movieSearch = (EditText) findViewById(R.id.movieSearch);

        Log.d("Search", movieSearch.getText().toString());

        i.putStringArrayListExtra("movieTitles", movieTitles);
        i.putStringArrayListExtra("movieIds", movieIds);
        i.putStringArrayListExtra("movieDirectors", movieDirectors);
        i.putStringArrayListExtra("movieYears", movieYears);
        i.putStringArrayListExtra("movieGenres", movieGenres);
        i.putStringArrayListExtra("movieCastList", movieCastList);

        i.putExtra("movieSearch", movieSearch.getText().toString());



        startActivity(i);
    }



}


