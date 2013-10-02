package com.kousenit.icndb;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.web.client.RestTemplate;

import android.R.color;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

public class MainActivity extends Activity {
    private static final String TAG = "ICNDB";

    private Button jokeButton;
    private TextView jokeView;
    private boolean go;
    private Timer t;

    // "true" ctor arg --> add default message converters
    private RestTemplate template = new RestTemplate(true);

    private static final String URL = 
       "http://api.icndb.com/jokes/random?limitTo=[nerdy]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jokeView = (TextView) findViewById(R.id.text_view);
        jokeButton = (Button) findViewById(R.id.icndb_button);
        jokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go = !go;
                if (go) {
                    t = new Timer();
                    t.scheduleAtFixedRate(new TimerTask() {
                        public void run() {
                            new JokeTask().execute();                        
                        }
                    }, 0, 10*1000);  // new joke every 10 sec
                    jokeButton.setTextColor(getResources().getColor(
                            color.holo_green_dark));
                } else {
                    if (t != null) t.cancel();
                    jokeButton.setTextColor(getResources().getColor(
                            color.holo_red_dark));
                }
            }
        });
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
            String jsonTxt = template.getForObject(URL, String.class);
            Log.d(TAG, jsonTxt);
            IcndbJoke joke = new Gson().fromJson(jsonTxt, IcndbJoke.class);
            return joke.getJoke();
        }

        
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            jokeView.setText(result);
        }
    }
}
