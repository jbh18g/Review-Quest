package com.jake.reviewquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Date;
import java.util.HashMap;
import static java.text.DateFormat.getDateTimeInstance;

public class MainActivity extends AppCompatActivity{
    public final static String SHARED_PREFS = "shared_prefs_file";
    public final static String SPGURL = "google_url";
    public final static String SPFURL = "facebook_url";
    public final static String SPYURL = "yelp_url";
    public static final String SPMESG = "sp_message";
    public static final String SPSL = "shorten_links";
    public static final String SP_SHORT_GURL = "google_short_url";
    public static final String SP_SHORT_FURL = "facebook_short_url";
    public static final String SP_SHORT_YURL = "yelp_short_url";
    public static final String SPREMINDMESG = "sp_remind_message";

    public static final String DELIVERED = "sms_delivered";
    public static final String SENT = "sms_sent";
    public static final String NOTE_CHANNEL_ID_SENT = "ReviewQuestNotificationSENT";
    public static final String NOTE_CHANNEL_ID_DELIVERED = "ReviewQuestNotificationDELIVERED";
    public static final String PHONE_NUM_EXTRA = "phoneNumber";
    public static final String CONATCT_NAME_EXTRA = "contactName";
    public static final String RANDOM_INT_EXTRA = "randomInt";
    public static final String CUTTLY_API_KEY = "e5612347356eb01958eb4c4535820e575235b";
    private static final String SPLASH_SCREEN_ERROR = "splash_screen_error";
    private boolean errorHandle = false;
    public static final String SAMPLE_NUMBER = "(555) 123-4567";

    //private NotificationReceiver notificationReceiverDELIVERED;
    private NotificationReceiver notificationReceiverSENT;

    public static HashMap<String, String> numbersToText;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(SPLASH_SCREEN_ERROR, true);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel(NOTE_CHANNEL_ID_SENT, "Review Quest Sent", "Review Quest has Sent your SMS");
        //createNotificationChannel(NOTE_CHANNEL_ID_DELIVERED, "Review Quest Delivered", "Review Quest has Delivered your SMS");


        getSupportFragmentManager().beginTransaction().add(R.id.mainFrame, new SplashScreenFrag(), "SplashScreenFrag")
                .addToBackStack(null)
                .commit();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    try{
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                    catch(IllegalStateException e)
                    {
                        errorHandle = true;
                    }
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, new HomeScreen(), "HomeScreen")
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        }, 1750);


        askForPermissions(new String[]{ Manifest.permission.INTERNET, Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE });

        numbersToText = new HashMap<>();
    }

    private void askForPermissions(String[] permissions)
    {
            requestPermissions(permissions, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void createNotificationChannel(String CHANNEL_ID, String name, String description) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(this, String.valueOf(getSupportFragmentManager().getBackStackEntryCount()), Toast.LENGTH_SHORT).show();
        SetupFrag myFragment = (SetupFrag) getSupportFragmentManager().findFragmentByTag("SetupFrag");
        if (myFragment != null && myFragment.isVisible()) {
            new backSaveDialog(myFragment).show(getSupportFragmentManager(), null);
        }
        else if(getSupportFragmentManager().getBackStackEntryCount() > 1)
        {
            getSupportFragmentManager().popBackStack();
        }
        else
        {
            finish();
        }
    }

    public static void hideKeyboard(Activity activity)
    {
        InputMethodManager inputMethodManager = (InputMethodManager)activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if(view == null)
        {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(notificationReceiverSENT);
//        unregisterReceiver(notificationReceiverDELIVERED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(errorHandle)
        {
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, new HomeScreen(), "HomeScreen")
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
                    errorHandle = false;
        }
//        notificationReceiverDELIVERED = new NotificationReceiver();
//        registerReceiver(notificationReceiverDELIVERED, new IntentFilter(DELIVERED));

        notificationReceiverSENT = new NotificationReceiver();
        registerReceiver(notificationReceiverSENT, new IntentFilter(SENT));
    }


}