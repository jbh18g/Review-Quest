package com.jake.reviewquest;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

import static com.jake.reviewquest.MainActivity.SAMPLE_NUMBER;

public class ContactDisplayRecyclerViewAdapter extends RecyclerView.Adapter<ContactDisplayRecyclerViewAdapter.ViewHolder> {

    protected HashMap<String, String> numbersToText;
    private Object[] keys;
    private Object[] values;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnLongClickListener{
        private final TextView contactName;
        private final TextView contactNumber;

        public ViewHolder(View view) {
            super(view);
            view.setOnLongClickListener(this);
            // Define click listener for the ViewHolder's View

            contactName = view.findViewById(R.id.contactName);
            contactNumber = view.findViewById(R.id.contactNumber);
        }

        public TextView getContactName() {
            return contactName;
        }

        public TextView getContactNumber() {
            return contactNumber;
        }

        @Override
        public boolean onLongClick(View view) {
            Log.i("YEET", "Long CLcik");
            int pos = getAdapterPosition();
            Log.i("YEET", String.valueOf(pos));
            if(pos != RecyclerView.NO_POSITION)
            {
                String temp =  ((TextView)(view.findViewById(R.id.contactNumber))).getText().toString();
                if(numbersToText.containsKey(temp))
                {
                    Log.i("Its There,", " ");
                }
                Log.i("Removing", ((TextView)(view.findViewById(R.id.contactNumber))).getText().toString());
                numbersToText.remove(temp);
                if(numbersToText.containsKey(temp))
                {
                    Log.i("STILL THERE", " ");
                }
                notifyItemRemoved(pos);
                if(numbersToText.isEmpty())
                {
                    numbersToText.put(SAMPLE_NUMBER, "No Contacts Selected");
                }
                keys = numbersToText.keySet().toArray();
                values = numbersToText.values().toArray();
            }
            return true;
        }
    }

    public ContactDisplayRecyclerViewAdapter(HashMap<String, String> numbersToText) {
        this.numbersToText = numbersToText;
        keys = this.numbersToText.keySet().toArray();
        values = this.numbersToText.values().toArray();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_layout, viewGroup, false);
        return new ViewHolder(view);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        //viewHolder.getTextView().setText(localDataSet[position]);
        viewHolder.getContactName().setText((String)values[position]);
        viewHolder.getContactNumber().setText((String)keys[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return keys.length;
    }

}
