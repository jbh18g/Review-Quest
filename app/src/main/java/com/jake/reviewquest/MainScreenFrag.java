package com.jake.reviewquest;

import static com.jake.reviewquest.MainActivity.SAMPLE_NUMBER;
import static com.jake.reviewquest.MainActivity.SENT;
import static com.jake.reviewquest.MainActivity.SHARED_PREFS;
import static com.jake.reviewquest.MainActivity.SPFURL;
import static com.jake.reviewquest.MainActivity.SPGURL;
import static com.jake.reviewquest.MainActivity.SPYURL;
import static com.jake.reviewquest.MainActivity.numbersToText;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Random;


public class MainScreenFrag extends Fragment implements CompoundButton.OnCheckedChangeListener{

    private SharedPreferences sharedPreferences;
    private Button sendButton;
    private Button selectContactsButton;
    private Button previewSmsButton;
    private CheckBox leaveGoogleReview;
    private CheckBox leaveFacebookReview;
    private CheckBox leaveYelpReview;
    private EditText customMessage;
    private EditText phoneNumber;
    private EditText contactName;
    private RecyclerView recyclerView;
    private String LOG = "MAINSCREENFRAG";

    public MainScreenFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(numbersToText.isEmpty())
        {
            numbersToText.put(SAMPLE_NUMBER, "No Contacts Selected");
        }
        else if(numbersToText.size() > 1)
        {
            numbersToText.remove(SAMPLE_NUMBER);
        }
        View view = inflater.inflate(R.layout.fragment_main_screen, container, false);
        sendButton = view.findViewById(R.id.sendReviewButton);
        selectContactsButton = view.findViewById(R.id.selectContactButton);
        selectContactsButton.setOnClickListener(this::onClickListener);
        sendButton.setOnClickListener(this::onClickListener);
        previewSmsButton = view.findViewById(R.id.previewMessageButton);
        previewSmsButton.setOnClickListener(this::onClickListener);
        leaveFacebookReview = view.findViewById(R.id.facebookCheck);
        leaveGoogleReview = view.findViewById(R.id.googleCheck);
        leaveYelpReview = view.findViewById(R.id.yelpCheck);
        customMessage = view.findViewById(R.id.customMessageBox);
        phoneNumber = view.findViewById(R.id.phoneNumberBox);
        contactName = view.findViewById(R.id.enterContactNameBox);
        recyclerView = view.findViewById(R.id.displayContactsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ContactDisplayRecyclerViewAdapter(numbersToText));
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, 0);

        leaveGoogleReview.setOnCheckedChangeListener(this);
        leaveYelpReview.setOnCheckedChangeListener(this);
        leaveFacebookReview.setOnCheckedChangeListener(this);


        return view;
    }

    public void onClickListener(View view)
    {
        switch(view.getId())
        {
            case R.id.sendReviewButton: {
                if(sendReview()) {
                    Log.i(LOG, "got here");
                    getActivity().finish();
                    //getActivity().getSupportFragmentManager().popBackStack();
                }
                break;
            }
            case R.id.selectContactButton: {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, new SelectContactFrag(), "SelectContactFrag")
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.previewMessageButton:
            {
                if(leaveGoogleReview.isChecked() || leaveFacebookReview.isChecked() || leaveYelpReview.isChecked())
                    new textPreviewDialog(SMSSender.constructSMS(customMessage.getText().toString(),
                            getActivity(), leaveGoogleReview.isChecked(), leaveYelpReview.isChecked(),
                            leaveFacebookReview.isChecked())).show(getActivity().getSupportFragmentManager(), null);
                else
                    Toast.makeText(getContext(), "Please select which link(s) you would like to send to your client before previewing your message.", Toast.LENGTH_SHORT).show();
                break;
            }
            default:
                Log.e("BUTTON PRESS", "INVALID BUTTON PRESS FROM MAINSCREENFRAG");
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton.getId() == R.id.googleCheck)
        {
            if(sharedPreferences.getString(SPGURL, "").equals(""))
            {
                compoundButton.setChecked(false);
                Toast.makeText(getContext(), "No URL for Google Review Set, Please add a URL in the Setup Page", Toast.LENGTH_SHORT).show();
            }
        }
        if(compoundButton.getId() == R.id.yelpCheck)
        {
            if(sharedPreferences.getString(SPYURL, "").equals(""))
            {
                compoundButton.setChecked(false);
                Toast.makeText(getContext(), "No URL for Yelp Review Set, Please add a URL in the Setup Page", Toast.LENGTH_SHORT).show();
            }
        }
        if(compoundButton.getId() == R.id.facebookCheck)
        {
            if(sharedPreferences.getString(SPFURL, "").equals(""))
            {
                compoundButton.setChecked(false);
                Toast.makeText(getContext(), "No URL for Facebook Review Set, Please add a URL in the Setup Page", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean sendReview()
    {
        String pn = SMSSender.phoneNumberFormatter(phoneNumber.getText().toString());
        Log.i(LOG, pn + pn.matches("^(1)?(\\d){10}"));
        //deal with entered in number and also if no contacts in list is still in list
        if(pn.matches("^(1)?(\\d){10}")) {
            if (!contactName.getText().toString().equals("")){
                numbersToText.put(pn, contactName.getText().toString());
        }
            else
            {
                Toast.makeText(getContext(), "No Name Input For Phone Number, Please Input a Name", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else
        {
            Toast.makeText(getContext(), "Invalid Phone Number, Please Try Again", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(numbersToText.size() > 1)
            numbersToText.remove(SAMPLE_NUMBER);

        if(leaveFacebookReview.isChecked() || leaveYelpReview.isChecked() || leaveGoogleReview.isChecked())
        {
            if(numbersToText.size() > 0 && !numbersToText.containsValue("No Contacts Selected"))
            {
                Object[] smsToSendNums = numbersToText.keySet().toArray();
                Object[] smsToSendNames = numbersToText.values().toArray();
                Intent notificationIntent = new Intent();
                notificationIntent.setAction(SENT);
                for(int i = 0; i < smsToSendNums.length; i++) {
                    Log.i("ITEMS BEING SENT", (String)smsToSendNums[i]);
                    //sendSMS(constructSMS(), (String)smsToSendNums[i], (String)smsToSendNames[i], new Random().nextInt());
                    SMSSender.sendSMSViaIntent(customMessage.getText().toString(),(String)smsToSendNames[i],
                            (String)smsToSendNums[i], new Random().nextInt(), notificationIntent,
                            getActivity(), leaveGoogleReview.isChecked(), leaveYelpReview.isChecked(),
                            leaveFacebookReview.isChecked());
                }
                numbersToText.clear();
                //Toast.makeText(getContext(), "Opening default SMS provider", Toast.LENGTH_SHORT).show();
                return true;
            }
            else
                Toast.makeText(getContext(), "No Phone Number Entered or Selected From Contacts", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getContext(), "Please select which link(s) you would like to send to your client.", Toast.LENGTH_SHORT).show();
        return false;
    }

//    private void addReviewToDataBase(String contactName, String phoneNumber)
//    {
//        ContentValues values = new ContentValues();
//        values.put(HistoryContentProvider.CONTACT_NAME, contactName);
//        values.put(HistoryContentProvider.PHONE_NUMBER, phoneNumber);
//        values.put(HistoryContentProvider.LAST_REQUEST_SENT_DATE, System.currentTimeMillis());
//        values.put(HistoryContentProvider.GOOGLE_CHECKED,String.valueOf(leaveGoogleReview.isChecked()));
//        values.put(HistoryContentProvider.FACEBOOK_CHECKED,String.valueOf(leaveFacebookReview.isChecked()));
//        values.put(HistoryContentProvider.YELP_CHECKED,String.valueOf(leaveYelpReview.isChecked()));
//
//
//        HistoryDatabaseContract.addEntry(getContext(), values);
//    }

    //    private ArrayList<String> divideSMS(String sms)
//    {
//        ArrayList<String> parts = new ArrayList<>();
//        StringBuilder stringBuilder = new StringBuilder(sms);
//        int index = 0;
//        while(stringBuilder.length() > 0)
//        {
//            if(stringBuilder.length() >= 160) {
//                index = stringBuilder.substring(0, 161).lastIndexOf(' ');
//                parts.add(stringBuilder.substring(0, index));
//            }
//            else
//                parts.add(stringBuilder.toString());
//            stringBuilder.delete(0, ++index);
//        }
//        return parts;
//    }



//    private void sendSMS(String sms, String phoneNumber, String contactName, int pendingIntentNumber)
//    {
//        String scAddress = null;
//        Intent sendIntent = new Intent(SENT);
//        sendIntent.putExtra(PHONE_NUM_EXTRA, phoneNumber);
//        sendIntent.putExtra(CONATCT_NAME_EXTRA, contactName);
//        sendIntent.putExtra(RANDOM_INT_EXTRA, pendingIntentNumber);
//        Log.i("Sending SMS NOW", "To Phone Number " + phoneNumber + " Name: " + contactName);
//
////        Intent deliverIntent = new Intent(DELIVERED);
////        deliverIntent.putExtra(PHONE_NUM_EXTRA, phoneNumber);
//
//        PendingIntent sentIntent = PendingIntent.getBroadcast(getContext(), pendingIntentNumber, sendIntent, 0);
////        PendingIntent deliveryIntent = PendingIntent.getBroadcast(getContext(), 0, deliverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        SmsManager smsManager = SmsManager.getDefault();
//
//        if(sms.length() < 160) {
//            smsManager.sendTextMessage
//                    (phoneNumber, scAddress, sms,
//                            sentIntent,null);
//        }
//        else
//        {
//            ArrayList<String> list = divideSMS(sms);//smsManager.divideMessage(sms);
//                    //divideSMS(sms);
//            for(int i = 0; i < list.size(); i++)
//            {
//                smsManager.sendTextMessage
//                        (phoneNumber, scAddress, list.get(i),
//                                sentIntent,null);
//            }
//        }
//    }
    //    private void sendSMSViaIntent(String sms, String contactName, String phoneNumber, int pendingIntentNumber, Intent notificationIntent)
//    {
//        notificationIntent.putExtra(RANDOM_INT_EXTRA, pendingIntentNumber);
////        Intent smsIntent =  new Intent(Intent.ACTION_SENDTO);
////        smsIntent.setData(Uri.parse("smsto:"));
////        smsIntent.putExtra("address", phoneNumber);
////        smsIntent.putExtra("sms_body", sms);
////        smsIntent.putExtra("exit_on_sent", true);
//
//        //String phoneNumber="+91xxxxxxxxxx";
//        //String message ="Hi ABZ";
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
//        intent.putExtra("sms_body", sms);
//        try {
//            getActivity().sendBroadcast(notificationIntent);
//            startActivity(intent);
//            addReviewToDataBase(contactName, phoneNumber);
//            //startActivity(smsIntent);
//        }
//        catch (Exception e)
//        {
//            Log.i("SMS INTENT", "FAILED");
//        }
//    }

//    public String constructSMS(String s)
//    {
//        String sms = "";
//        if (customMessage.getText().toString().equals("")) {
//            sms = sms + sharedPreferences.getString(SPMESG, "NOT FOUND");
//        } else {
//            sms = sms + customMessage.getText().toString();
//        }
//        if (leaveGoogleReview.isChecked()) {
//            if(sharedPreferences.getBoolean(SPSL, false))
//            {
//                sms += "\nGoogle: " + sharedPreferences.getString(SP_SHORT_GURL, "NOT FOUND");
//            }
//            else
//                sms = sms + "\nGoogle: " + sharedPreferences.getString(SPGURL, "NOT FOUND");
//        }
//        if (leaveYelpReview.isChecked()) {
//            if(sharedPreferences.getBoolean(SPSL, false))
//            {
//                sms += "\nYelp: " + sharedPreferences.getString(SP_SHORT_YURL, "NOT FOUND");
//            }
//            else
//                sms = sms + "\nYelp: " + sharedPreferences.getString(SPYURL, "NOT FOUND");
//        }
//        if (leaveFacebookReview.isChecked()) {
//            if(sharedPreferences.getBoolean(SPSL, false))
//            {
//                sms += "\nFacebook: " + sharedPreferences.getString(SP_SHORT_FURL, "NOT FOUND");
//            }
//            else
//                sms = sms + "\nFacebook: " + sharedPreferences.getString(SPFURL, "NOT FOUND");
//        }
//        return sms;
//    }
}