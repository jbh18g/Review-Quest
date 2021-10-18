package com.jake.reviewquest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.jake.reviewquest.MainActivity.CUTTLY_API_KEY;
import static com.jake.reviewquest.MainActivity.SHARED_PREFS;

public class urlShortenerRunnable implements Runnable{
    private final String str;
    private volatile String shortenedURL;
    private final Activity activity;
    private volatile SharedPreferences sharedPreferences;
    private final String sharedPrefsKey;
    private volatile HttpURLConnection conn;
    private volatile BufferedReader br;
    private volatile linkShortenerDialog dialog;

    urlShortenerRunnable(String url, Activity a, String key, linkShortenerDialog dialog) {
        sharedPrefsKey = key;
        activity = a;
        str = url;
        this.dialog = dialog;
    }

    @Override
    public void run() {
            if (!str.equals("")) {
                try {
                    dialog.incrementNumberOfLinks();
                    URL url = new URL("https://cutt.ly/api/api.php?key=" + CUTTLY_API_KEY + "&short=" + str + "");
                    Log.i("URL", url.toString());
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    if (conn.getResponseCode() == 200) {
                        br = new BufferedReader(new InputStreamReader(
                                (conn.getInputStream())));

                        shortenedURL = parseJSONString(br.readLine());

                        sharedPreferences = activity.getSharedPreferences(SHARED_PREFS, 0);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(sharedPrefsKey, shortenedURL);
                        editor.commit();

                        conn.disconnect();
                        br.close();
                    }
                    if (conn.getResponseCode() == 429) {
                        Log.e("RUNTIME ERROR", "Failed : HTTP error code : 429.  Too Many Requests!");
                        if (conn != null)
                            conn.disconnect();
                        if (br != null)
                            br.close();
                        dialog.dismiss();
                    }
                    if (conn.getResponseCode() != 200) {
                        Log.e("RUNTIME ERROR", "Failed : HTTP error code : "
                                + conn.getResponseCode());
                        if (conn != null)
                            conn.disconnect();
                        if (br != null)
                            br.close();
                        dialog.dismiss();
                        activity.runOnUiThread(() -> Toast.makeText(activity, "ERROR: Please Try Again", Toast.LENGTH_SHORT).show());
                    }
                    dialog.updateProgress();

                } catch (JSONException | IOException e) {

                    e.printStackTrace();
                    activity.runOnUiThread(()-> Toast.makeText(activity, "Exception Occurred, Please check your links in the setup page", Toast.LENGTH_SHORT).show());
                    if (conn != null)
                        conn.disconnect();
                    dialog.dismiss();

                }
            }
        }

    private String parseJSONString(String string) throws JSONException {
        JSONObject reader = new JSONObject(string);
        JSONObject url = reader.getJSONObject("url");
        return url.getString("shortLink");
    }

}
