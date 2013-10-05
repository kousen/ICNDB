package com.kousenit.icrdb;

import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TAG = "ICNDB";

    private Button jokeButton;
    private TextView jokeView;

    // "true" ctor arg --> add default message converters
    private RestTemplate template = new RestTemplate(true);

    private static final String URL = 
       "http://api.icndb.com/jokes/random?limitTo=[nerdy]" +
               "&firstName=Carlos&lastName=Ray";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jokeView = (TextView) findViewById(R.id.text_view);
        jokeButton = (Button) findViewById(R.id.icndb_button);
        jokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    new JokeTask().execute();
                } else {
                    new LocalJokeTask().execute();
                }
            }
        });
     }
    
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
         
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean online = activeNetwork != null &&
               activeNetwork.isConnected();
        Log.v(TAG, (online ? "online" : "offline"));
        return online;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class JokeTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            IcndbJoke joke = template.getForObject(URL, IcndbJoke.class);
            return joke.getJoke();
        }

        
        @Override
        protected void onPostExecute(String result) {
            jokeView.setText(result);
        }
    }
    
    private class LocalJokeTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return template.getForObject(
                "http://10.0.2.2:5050?firstName=Carlos&lastName=Ray", String.class);
        }

        @Override
        protected void onPostExecute(String result) {
            jokeView.setText(result);
        }
    }

}
