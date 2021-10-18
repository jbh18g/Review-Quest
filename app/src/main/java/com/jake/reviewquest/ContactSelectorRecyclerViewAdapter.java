package com.jake.reviewquest;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;


public class ContactSelectorRecyclerViewAdapter extends RecyclerView.Adapter<ContactSelectorRecyclerViewAdapter.ViewHolder> implements View.OnClickListener{
    private final ArrayList<ArrayList<String>> localDataSet;
    private final HashMap<String, String> numbersToText;
    private final Context context;
    private int color;


    @Override
    public void onClick(View view) {
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView contactName;
        private final TextView phoneNumber;
        private final View v;

        public ViewHolder(View view) {
            super(view);
            v = view;
            view.setOnClickListener(this::onClick);
            Drawable originalColor = view.getBackground();
            if(originalColor instanceof ColorDrawable)
            {
                color = ((ColorDrawable) originalColor).getColor();
            }
            contactName = view.findViewById(R.id.contactName);
            phoneNumber = view.findViewById(R.id.contactNumber);
        }

        public TextView getContactName() {
            return contactName;
        }
        public TextView getPhoneNumber()
        {
            return phoneNumber;
        }

        @Override
        public void onClick(View view) {
            //Log.i("ADD TO HASHMAP", "BEGIN CLICK");
            String contactName =((TextView)view.findViewById(R.id.contactName)).getText().toString();
            String contactNumber =((TextView)view.findViewById(R.id.contactNumber)).getText().toString();
            if(!numbersToText.containsKey(contactNumber))
            {
                view.setBackgroundColor(new Color().parseColor("#FF8A80"));
                numbersToText.put(contactNumber, contactName);
                Toast.makeText(context, contactName + " was selected", Toast.LENGTH_LONG).show();
//                Object[] temp = numbersToText.keySet().toArray();
//                for(int i = 0; i < temp.length; i++)
//                {
//                    Log.i("ADD TO HASHMAP", numbersToText.get(temp[i]));
//                }
            }
            else
            {
                view.setBackgroundColor(color);
                numbersToText.remove(contactNumber);
                Toast.makeText(context, contactName + "was deselected", Toast.LENGTH_LONG).show();
//                Object[] temp = numbersToText.keySet().toArray();
//                for(int i = 0; i < temp.length; i++)
//                {
//                    Log.i("REMOVE FROM HASHMAP", numbersToText.get(temp[i]));
//                }
            }
        }

        public View getView()
        {
            return v;
        }
    }


    public ContactSelectorRecyclerViewAdapter(ArrayList<ArrayList<String>> dataSet, HashMap<String, String> numbersToText, Context context) {
        localDataSet = dataSet;
        this.numbersToText = numbersToText;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getContactName().setText(localDataSet.get(position).get(0));
        viewHolder.getPhoneNumber().setText(localDataSet.get(position).get(1));
        viewHolder.getView().setBackgroundColor(color);
        if(numbersToText.containsKey(localDataSet.get(position).get(1)))
            viewHolder.getView().setBackgroundColor(new Color().parseColor("#FF8A80"));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}
