package com.jake.reviewquest;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.net.URL;

import static com.jake.reviewquest.MainActivity.SHARED_PREFS;
import static com.jake.reviewquest.MainActivity.SPFURL;
import static com.jake.reviewquest.MainActivity.SPGURL;
import static com.jake.reviewquest.MainActivity.SPMESG;
import static com.jake.reviewquest.MainActivity.SPREMINDMESG;
import static com.jake.reviewquest.MainActivity.SPSL;
import static com.jake.reviewquest.MainActivity.SPYURL;
import static com.jake.reviewquest.MainActivity.SP_SHORT_FURL;
import static com.jake.reviewquest.MainActivity.SP_SHORT_GURL;
import static com.jake.reviewquest.MainActivity.SP_SHORT_YURL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetupFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetupFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SharedPreferences sharedPreferences;
    private Button saveButton;
    private Button getInstructionsButton;
    private EditText googleURL;
    private EditText facebookURL;
    private EditText yelpURL;
    private EditText message;
    private EditText reminder;
    private RadioButton shortenLinks;
    private RadioButton doNotShortenLinks;

    private boolean googleChanged = false;
    private boolean facebookChanged = false;
    private boolean yelpChanged = false;

    private String LOG = "SetupFrag";


    public SetupFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetupFrag newInstance(String param1, String param2) {
        SetupFrag fragment = new SetupFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setup_frag, container, false);
        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, 0);
        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this::onClickListener);

        googleURL = view.findViewById(R.id.GoogleURL);
        facebookURL = view.findViewById(R.id.FacebookURL);
        yelpURL = view.findViewById(R.id.YelpURL);
        message = view.findViewById(R.id.defaultMessage);
        reminder = view.findViewById(R.id.remindMessage);
        shortenLinks = view.findViewById(R.id.shortenLinks);
        doNotShortenLinks = view.findViewById(R.id.doNotShortenLinks);
        if(sharedPreferences.getBoolean(SPSL, true)) {
            shortenLinks.setChecked(true);
            doNotShortenLinks.setChecked(false);
        }
        else
        {
            shortenLinks.setChecked(false);
            doNotShortenLinks.setChecked(true);
        }
        googleURL.setText(sharedPreferences.getString(SPGURL, ""));
        facebookURL.setText(sharedPreferences.getString(SPFURL, ""));
        yelpURL.setText(sharedPreferences.getString(SPYURL, ""));
        message.setText(sharedPreferences.getString(SPMESG, ""));
        reminder.setText(sharedPreferences.getString(SPREMINDMESG, ""));
        googleURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Toast.makeText(getContext(), "google has been changed", Toast.LENGTH_SHORT).show();
                googleChanged = true;
            }
        });
        facebookURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Toast.makeText(getContext(), "facebook has been changed", Toast.LENGTH_SHORT).show();
                facebookChanged = true;
            }
        });
        yelpURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Toast.makeText(getContext(), "yelp has been changed", Toast.LENGTH_SHORT).show();
                yelpChanged = true;
            }
        });

        getInstructionsButton = view.findViewById(R.id.clickToGetInstructions);
        getInstructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new InstructionsDialog().show(getActivity().getSupportFragmentManager(), null);
            }
        });

        return view;
    }

    public void onClickListener(View view)
    {
        if(!isValidURL(googleURL.getText().toString()))
        {
            Log.i("Setup", "google is not valid URL");
            //do nothing, set errors.
        }
        else if(!isValidURL(facebookURL.getText().toString()))
        {
            Log.i("Setup", "facebook is not valid URL");
        }
        else if(!isValidURL(yelpURL.getText().toString()))
        {
            Log.i("Setup", "yelp is not valid URL");
        }
        else
        {
            //safe preferences and go back to homescreen
            if(shortenLinks.isChecked() && isNetworkConnected())
            {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                linkShortenerDialog dialog = new linkShortenerDialog();
                int changedLinks = 0;
                if(isValidURL(googleURL.getText().toString()) && googleChanged) {
                    changedLinks++;
                    shortenLink(googleURL, SP_SHORT_GURL, dialog);
                }
                if(isValidURL(facebookURL.getText().toString()) && facebookChanged) {
                    changedLinks++;
                    shortenLink(facebookURL, SP_SHORT_FURL, dialog);
                }
                if(isValidURL(yelpURL.getText().toString()) && yelpChanged) {
                    changedLinks++;
                    shortenLink(yelpURL, SP_SHORT_YURL, dialog);
                }
                if(changedLinks > 0)
                {
                    dialog.setCancelable(false);  //set eventually
                    dialog.show(getActivity().getSupportFragmentManager(), null);
                }
                editor.putBoolean(SPSL, shortenLinks.isChecked());
                editor.putString(SPGURL, googleURL.getText().toString());
                editor.putString(SPFURL, facebookURL.getText().toString());
                editor.putString(SPYURL, yelpURL.getText().toString());
                editor.putString(SPMESG, message.getText().toString());
                editor.putString(SPREMINDMESG, reminder.getText().toString());
                editor.commit();
            }
            else if(shortenLinks.isChecked() && (googleChanged || facebookChanged || yelpChanged))
            {
                Toast.makeText(getContext(), "ERROR: No Internet Connectivity, Please Connect to a Network", Toast.LENGTH_SHORT).show();
            }
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    public static boolean isValidURL(String url)
    {
        if(url.equals(""))
            return true;
        try{
            new URL(url).toURI();
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @SuppressLint("ResourceType")
    private void shortenLink(EditText editText, String SP_SHORT_URL, linkShortenerDialog dialog)
    {
        try {
        urlShortenerRunnable runnable = new urlShortenerRunnable(editText.getText().toString(), getActivity(), SP_SHORT_URL, dialog);
        Thread thread = new Thread(runnable);
        thread.start();

        if(editText.getId() == getResources().getInteger(R.id.YelpURL)) {
            yelpChanged = false;
        }
        else if(editText.getId() == getResources().getInteger(R.id.GoogleURL)) {
            googleChanged = false;
        }
        else if(editText.getId() == getResources().getInteger(R.id.FacebookURL)){
            facebookChanged = false;
        }
    }
        catch (Exception e)
        {
            Log.i(LOG,"" + e);
        }
    }
}