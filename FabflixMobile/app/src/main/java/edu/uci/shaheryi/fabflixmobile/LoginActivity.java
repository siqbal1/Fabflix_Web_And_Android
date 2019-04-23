package edu.uci.shaheryi.fabflixmobile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       /* Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("last_activity") != null) {
                Toast.makeText(this, "Last activity was " + bundle.get("last_activity") + ".", Toast.LENGTH_LONG).show();
            }
            String msg = bundle.getString("message");
            if (msg != null && !"".equals(msg)) {
                ((TextView) findViewById(R.id.last_page_msg_container)).setText(msg);

            }

        }
        */
    }

    public void connectToTomcat(View view)
    {
        EditText usernameField = (EditText) findViewById(R.id.username);
        EditText passwordField = (EditText) findViewById(R.id.password);
        //post request form data
        final Map<String, String> params = new HashMap<String, String>();
        params.put("username", usernameField.getText().toString());
        Log.d("username", usernameField.getText().toString());

        params.put("password", passwordField.getText().toString());
        Log.d("password", passwordField.getText().toString());

        //make boolean value to test whether the the login was a success


        //connect to the server

        //use same network queue across application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        String url = "https://10.0.2.2:8443/project1-star-example/api/android-login";
        Log.d("validUrl", valueOf(URLUtil.isValidUrl(url)));
        // 10.0.2.2 is the host machine when running the android emulator
<<<<<<< HEAD
        final StringRequest loginRequest = new StringRequest(Request.Method.POST, "https://18.220.134.46:8443/project1/api/android-login",
=======
        final StringRequest loginRequest = new StringRequest(Request.Method.POST, "https://52.15.77.91:8443/project1/api/android-login",
>>>>>>> d6cf9ef9fcd95f2379d0462e60dd191d2cd7651f
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                            Log.d("response", response);

                            try {
                                JSONObject responseObject = new JSONObject(response);

                                //make string to see if the status is success
                                String responseStatus = responseObject.getString("status");
                                Log.d("status", responseStatus);

                                //if(response.equals("{\"status\":\"success\",\"message\":\"Login Success\"}"))
                                if (responseStatus.equals("success")) {
                                    //send to another page
                                    ((TextView) findViewById(R.id.http_response)).setTextColor(Color.GREEN);
                                    ((TextView) findViewById(R.id.http_response)).setText("Welcome.");
                                    goToSearch();
                                } else {
                                    ((TextView) findViewById(R.id.http_response)).setTextColor(Color.RED);
                                    ((TextView) findViewById(R.id.http_response)).setText("Incorrect username or password.");
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
                        ((TextView)findViewById(R.id.http_response)).setText(error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }  // HTTP POST Form Data

        };

        queue.add(loginRequest);

        HttpCookie cookie = new HttpCookie("loggedIn", "true");
        
    }

    public void goToSearch()
    {
        Intent goToIntent = new Intent(this, SearchActivity.class);

        startActivity(goToIntent);

    }

}
