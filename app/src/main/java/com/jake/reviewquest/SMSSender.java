package com.jake.reviewquest;

import static com.jake.reviewquest.MainActivity.RANDOM_INT_EXTRA;
import static com.jake.reviewquest.MainActivity.SHARED_PREFS;
import static com.jake.reviewquest.MainActivity.SPFURL;
import static com.jake.reviewquest.MainActivity.SPGURL;
import static com.jake.reviewquest.MainActivity.SPMESG;
import static com.jake.reviewquest.MainActivity.SPSL;
import static com.jake.reviewquest.MainActivity.SPYURL;
import static com.jake.reviewquest.MainActivity.SP_SHORT_FURL;
import static com.jake.reviewquest.MainActivity.SP_SHORT_GURL;
import static com.jake.reviewquest.MainActivity.SP_SHORT_YURL;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SMSSender {
    private static String SMS_LOG = "SMSSender";
    private static SharedPreferences sharedPreferences;
    public static void sendSMSViaIntent(String sms, String contactName, String phoneNumber,
                                        int pendingIntentNumber, Intent notificationIntent,
                                        Activity activity, boolean googleCheck, boolean yelpCheck,
                                        boolean faceCheck)
    {
        notificationIntent.putExtra(RANDOM_INT_EXTRA, pendingIntentNumber);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        intent.putExtra("sms_body", constructSMS(sms, activity, googleCheck, yelpCheck, faceCheck));
        try {
            activity.sendBroadcast(notificationIntent);
            activity.startActivity(intent);
            addReviewToDataBase(contactName, phoneNumber, activity.getApplicationContext(),
                    googleCheck, yelpCheck, faceCheck);
        }
        catch (Exception e)
        {
            Log.i(SMS_LOG, "FAILED" + e);
        }
    }

    private static void addReviewToDataBase(String contactName, String phoneNumber, Context context,
                                            boolean googleCheck, boolean yelpCheck, boolean faceCheck)
    {
        ContentValues values = new ContentValues();
        //always update time
        //if already in database, update time with most current updated time
        values.put(HistoryContentProvider.LAST_REQUEST_SENT_DATE, System.currentTimeMillis());
        Cursor c = HistoryDatabaseContract.queryByNumber(context,phoneNumber);
        c.moveToFirst();
        if(c.getCount() > 0 && c.getString(0).equals( phoneNumber))
        {
            //Log.i("SMS SENDER", "found it!");
            HistoryDatabaseContract.updateEntry(context, phoneNumber, values);
        }
        else
        {
            //for adding new entry
            //format string please at some point
            values.put(HistoryContentProvider.CONTACT_NAME, contactName);
            values.put(HistoryContentProvider.PHONE_NUMBER, phoneNumber);
            values.put(HistoryContentProvider.GOOGLE_CHECKED,String.valueOf(googleCheck));
            values.put(HistoryContentProvider.FACEBOOK_CHECKED,String.valueOf(yelpCheck));
            values.put(HistoryContentProvider.YELP_CHECKED,String.valueOf(faceCheck));

            HistoryDatabaseContract.addEntry(context, values);
        }
        c.close();
    }
    public static String constructSMS(String customMSG, Activity activity, boolean googleCheck,
                                      boolean yelpCheck, boolean faceCheck)
    {
        sharedPreferences = activity.getSharedPreferences(SHARED_PREFS, 0);
        String sms = "";
        if (customMSG.equals("")) {
            sms = sms + sharedPreferences.getString(SPMESG, "NOT FOUND");
        } else {
            sms = sms + customMSG;
        }
        if (googleCheck) {
            if(sharedPreferences.getBoolean(SPSL, false))
            {
                sms += "\nGoogle: " + sharedPreferences.getString(SP_SHORT_GURL, "NOT FOUND");
            }
            else
                sms = sms + "\nGoogle: " + sharedPreferences.getString(SPGURL, "NOT FOUND");
        }
        if (yelpCheck) {
            if(sharedPreferences.getBoolean(SPSL, false))
            {
                sms += "\nYelp: " + sharedPreferences.getString(SP_SHORT_YURL, "NOT FOUND");
            }
            else
                sms = sms + "\nYelp: " + sharedPreferences.getString(SPYURL, "NOT FOUND");
        }
        if (faceCheck) {
            if(sharedPreferences.getBoolean(SPSL, false))
            {
                sms += "\nFacebook: " + sharedPreferences.getString(SP_SHORT_FURL, "NOT FOUND");
            }
            else
                sms = sms + "\nFacebook: " + sharedPreferences.getString(SPFURL, "NOT FOUND");
        }
        return sms;
    }

    public static String phoneNumberFormatter(String phoneNumber)
    {
        return phoneNumber.replaceAll("\\D", "");
    }
}
