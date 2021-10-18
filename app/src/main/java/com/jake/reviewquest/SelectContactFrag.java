package com.jake.reviewquest;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.provider.ContactsContract;
import android.widget.ProgressBar;
import android.widget.SearchView;

import java.util.ArrayList;

import static com.jake.reviewquest.MainActivity.hideKeyboard;
import static com.jake.reviewquest.MainActivity.numbersToText;

public class SelectContactFrag extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;
    private Button continueButton;
    private SearchView searchBar;
    private ProgressBar progressBar;
    private String lastQueryName = "qwertyuiopasdfghjklzxcvbnm";

    private Cursor mCursor;
    private ContactSelectorRecyclerViewAdapter contactSelectorRecyclerViewAdapter;
    private ArrayList<ArrayList<String>> dataSet;
    //return only these rows
    private final String[] mProjection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER};
    //retuns list of names that contain search parameter
    private final String mSelectionClause = "display_name LIKE ?";
    //orders results in alphabetical order
    private final String mOrderBy = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";


    public SelectContactFrag() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_contact, container, false);
        //initialize data containers
        dataSet = new ArrayList<>();
        //find all views and set them up
        recyclerView = view.findViewById(R.id.contactRecyclerView);
        searchBar = view.findViewById(R.id.searchBarForContact);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //executes search query
                onClicked(view);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //makes whole search bar clickable
                searchBar.setIconified(false);
            }
        });

        progressBar = view.findViewById(R.id.queryProgressBar);
        //initalize recycler view adapter
        contactSelectorRecyclerViewAdapter = new ContactSelectorRecyclerViewAdapter(dataSet, numbersToText, getContext());
        //set up recyclerview
        recyclerView.setAdapter(contactSelectorRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        continueButton = view.findViewById(R.id.contactSerachContinueButton);
        continueButton.setOnClickListener(this::onClick);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onClicked(View view) {
        //searchBar.
        //only executes search if search string differs from last search string
        if (!lastQueryName.equals(searchBar.getQuery().toString())) {
            hideKeyboard(getActivity());        //HIDES KEYBOARD ON SEARCH
            lastQueryName = searchBar.getQuery().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //runs query on separate thread (so no locking up the UI
                    //allows for updating of progress bar
                    dataSet = queryContactsByName(searchBar.getQuery().toString(), dataSet);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //when query completes, updates recyclerview on UI thread;
                            contactSelectorRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }).start();
        }
    }

    private ArrayList<ArrayList<String>> queryContactsByName(String name, ArrayList<ArrayList<String>> arrayList) {
        final int[] currentProgress = {0};
        arrayList.clear();
        mCursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                mProjection, mSelectionClause, new String[]{"%" + name + "%"}, mOrderBy);
        if (mCursor != null && mCursor.getCount() > 0) {
            int nameColumnId = mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int hasNumberColumnId = mCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                String id = mCursor.getString(
                        mCursor.getColumnIndex(ContactsContract.Contacts._ID));
                //add name to array
                String contactName = mCursor.getString(nameColumnId);
                if (mCursor.getInt(hasNumberColumnId) > 0) {
                    Cursor phoneCursor = getActivity().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (phoneCursor != null && phoneCursor.getCount() > 0) {
                        phoneCursor.moveToFirst();
                        //put phone number into array
                        String phoneNumber = phoneCursor.getString(phoneCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        ArrayList<String> temp = new ArrayList<>();
                        temp.add(contactName);
                        temp.add(phoneNumber);
                        arrayList.add(temp);
                    }
                }
                progressBar.setProgress((int) (((double) ++currentProgress[0] / mCursor.getCount()) * 100));
                //Log.i("Progress Bar", String.valueOf((int) (((double) currentProgress[0] / mCursor.getCount()) * 100)));
                mCursor.moveToNext();
            }

            mCursor.close();
        }

        return arrayList;
    }


    @Override
    public void onClick(View view) {
        getActivity().getSupportFragmentManager().popBackStack();

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }
}

