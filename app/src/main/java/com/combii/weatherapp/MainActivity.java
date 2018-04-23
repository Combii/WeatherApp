package com.combii.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView textWeatherInfo;
    EditText weatherInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textWeatherInfo = (TextView) findViewById(R.id.textWeatherInfo);
        weatherInput = (EditText) findViewById(R.id.inputText);
    }


    public void checkWeather(View view) {
        weatherInformation(weatherInput.getText().toString());
    }

    private void weatherInformation(String cityName) {
        String weatherString;

        DownloadTask task = new DownloadTask();
        try {
            weatherString = task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=1fe1650f84332252fd205e504efbfe79").get();
            textWeatherInfo.setText(weatherString);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            StringBuilder result = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection;


            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result.append(current);
                    data = reader.read();
                }
                return convertToString(result.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private String convertToString(String result) {
            try {

                String weatherString = "";

                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    weatherString += jsonPart.getString("main") + ": \n";
                    weatherString += jsonPart.getString("description") + "\n";

                    Log.i("main", jsonPart.getString("main"));
                    Log.i("description", jsonPart.getString("description"));

                }
                return weatherString;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


    }
}
