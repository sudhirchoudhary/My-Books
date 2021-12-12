package com.example.mybooks;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class Query {
    public static final String LOG_TAG = Query.class.getSimpleName();

    public static List<BooksModal> fetchEarthquakeData(String requestUrl,int t) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<BooksModal> earthquake = extractFeatureFromJson(jsonResponse,t);
        // Return the {@link Event}
        return earthquake;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
            Log.e("dekh le", url.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    private static List<BooksModal> extractFeatureFromJson(String earthquakeJSON,int t) {
        List<BooksModal> list = new ArrayList<>();
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
            JSONArray array = baseJsonResponse.getJSONArray("items");
            for (int i = t; i < 5+t; i++) {
                JSONObject Volume = array.getJSONObject(i);
                JSONObject volumeInfo = Volume.getJSONObject("volumeInfo");
                String name = volumeInfo.getString("title");
                JSONArray author_array = volumeInfo.getJSONArray("authors");
                String author = author_array.getString(0);
                String rating = "";

                if (volumeInfo.has("averageRating")) {
                    rating = "Average Ratings"+volumeInfo.getString("averageRating");
                }
                else{
                    rating="Not Rated";
                }
                JSONObject imageLink = volumeInfo.getJSONObject("imageLinks");
                String imageUrl = imageLink.getString("smallThumbnail");
                if (author == null || author.length() == 0)
                    author = "Auhtor not found";
                else
                    author = "By " + author;
                BooksModal earthQuake = new BooksModal(name, author, rating, imageUrl);
                list.add(earthQuake);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return list;
    }
}
