package edu.uci.shaheryi.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieList extends AppCompatActivity {

    ArrayList<String> movieIds = new ArrayList<String>();
    ArrayList<String> movieTitles = new ArrayList<String>();
    ArrayList<String> movieDirectors = new ArrayList<String>();
    ArrayList<String> movieYears = new ArrayList<String>();
    ArrayList<String> movieGenres = new ArrayList<String>();
    ArrayList<String> movieCastList = new ArrayList<String>();
    String movieSearch;
    int offset;
    int reachedLastPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Intent i = getIntent();

        movieIds = i.getStringArrayListExtra("movieIds");
        movieTitles = i.getStringArrayListExtra("movieTitles");
        movieDirectors = i.getStringArrayListExtra("movieDirectors");
        movieYears = i.getStringArrayListExtra("movieYears");
        movieGenres = i.getStringArrayListExtra("movieGenres");
        movieCastList = i.getStringArrayListExtra("movieCastList");

        movieSearch = i.getStringExtra("movieSearch");

        //check if the offset is null
        //if not assign offset
        offset = i.getIntExtra("offset", 10);
        //boolea reached last page
        //if 1 = true; do not query any more
        //if 0 = false; can do more queries
        reachedLastPage = i.getIntExtra("reachedLastPage", 0);

        Log.d("offset", Integer.toString(offset));


        ListView listView = (ListView) findViewById(R.id.listView);

        CustomAdapter customAdapter = new CustomAdapter();

        listView.setAdapter(customAdapter);



    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            int count = 0;

            if(movieIds.size() < 10)
            {
                count = movieIds.size();

            }
            else if (movieIds.size() - offset < 10 && movieIds.size() - offset + 10 <= 10) {
                count = movieIds.size() - offset;
                count += 10;
            }
            else
                count = 10;

            Log.d("Count:", Integer.toString(count));
            //Log.d("offset", Integer.toString(offset));
            if(count < 10)
                reachedLastPage = 1;

            return count;
        }

        @Override
        //return the movieTitle
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.customlayout, null);

            TextView movieTitle = (TextView) view.findViewById(R.id.singleMovieTitleView);
            TextView movieDirector = (TextView) view.findViewById(R.id.movieDirector);
            TextView movieYear = (TextView) view.findViewById(R.id.singleMovieYearView);
            TextView movieGenre = (TextView) view.findViewById(R.id.movieGenres);
            TextView movieCast = (TextView) view.findViewById(R.id.movieCastList);



            int adjustment = offset - 10;

            movieTitle.setText(movieTitles.get(adjustment + i).toString());
            movieDirector.append(movieDirectors.get(adjustment + i).toString());
            movieYear.append(movieYears.get(adjustment + i).toString());
            movieGenre.append(movieGenres.get(adjustment + i).toString());
            movieCast.append(movieCastList.get(adjustment + i).toString());

            movieTitle.setTag(movieIds.get(adjustment + i).toString());

            return view;
        }


    }

    public void getNextResults(View view)
    {
        if(reachedLastPage == 0) {

            final Map<String, String> params = new HashMap<String, String>();
            params.put("movieSearch", movieSearch);
            params.put("offset", Integer.toString(offset));

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

                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        String id = jsonArray.getJSONObject(i).getString("movieId");
                                        String title = jsonArray.getJSONObject(i).getString("movieTitle");
                                        String director = jsonArray.getJSONObject(i).getString("movieDirector");
                                        String year = jsonArray.getJSONObject(i).getString("movieYear");
                                        String genres = jsonArray.getJSONObject(i).getString("movieGenres");
                                        String starList = jsonArray.getJSONObject(i).getString("starsList");

                                        movieIds.add(id);
                                        movieTitles.add(title);
                                        movieDirectors.add(director);
                                        movieYears.add(year);
                                        movieGenres.add(genres);
                                        movieCastList.add(starList);
                                    }

                                    nextMovieList();
                                } else {

                                    reachedLastPage = 1;
                                    Log.d("Final Page:", "Reached final Page");
                                    /*
                                TextView errorMessageView = (TextView) findViewById(R.id.errorMessageView);


                                errorMessageView.setTextColor(Color.RED);
                                errorMessageView.setText("No movies with that keyword. Please enter in another search phrase.");
                                */
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
        }
        else
        {
            nextMovieList();
            Log.d("Results:", "Final Page reached. No more queries");
        }

    }

    public void nextMovieList()
    {
        if(reachedLastPage == 0) {
            Intent i = new Intent(this, MovieList.class);

            i.putExtra("movieSearch", movieSearch);
            i.putExtra("offset", offset + 10);
            i.putExtra("reachedLastPage", reachedLastPage);

            i.putStringArrayListExtra("movieTitles", movieTitles);
            i.putStringArrayListExtra("movieIds", movieIds);
            i.putStringArrayListExtra("movieDirectors", movieDirectors);
            i.putStringArrayListExtra("movieYears", movieYears);
            i.putStringArrayListExtra("movieGenres", movieGenres);
            i.putStringArrayListExtra("movieCastList", movieCastList);

            startActivity(i);
        }
    }

    public void previousMovieList(View view)
    {
        if(offset - 10 != 0) {
            Intent i = new Intent(this, MovieList.class);

            i.putExtra("movieSearch", movieSearch);
            i.putExtra("offset", offset - 10);
            i.putExtra("reachedLastPage", reachedLastPage);

            i.putStringArrayListExtra("movieTitles", movieTitles);
            i.putStringArrayListExtra("movieIds", movieIds);
            i.putStringArrayListExtra("movieDirectors", movieDirectors);
            i.putStringArrayListExtra("movieYears", movieYears);
            i.putStringArrayListExtra("movieGenres", movieGenres);
            i.putStringArrayListExtra("movieCastList", movieCastList);

            startActivity(i);
        }

    }

    public void goToSingleMovie(View view)
    {
        Intent i = new Intent(this, SingleMovieActivity.class);

        i.putExtra("movieId", view.getTag().toString());

        startActivity(i);
    }

    public void goToSearch(View view)
    {
        Intent i = new Intent(this, SearchActivity.class);

        startActivity(i);
    }





}
