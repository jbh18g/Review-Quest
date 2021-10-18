package com.jake.reviewquest;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static com.jake.reviewquest.HistoryContentProvider.LAST_REQUEST_SENT_DATE;
import static com.jake.reviewquest.MainActivity.SENT;
import static com.jake.reviewquest.MainActivity.SHARED_PREFS;
import static com.jake.reviewquest.MainActivity.SPREMINDMESG;
import static com.jake.reviewquest.MainActivity.SP_SHORT_GURL;

public class HistoryDisplayRecyclerViewAdapter extends RecyclerView.Adapter<HistoryDisplayRecyclerViewAdapter.ViewHolder>{

    private Cursor cursor;
    private static String LOG = "HistoryAdapterLOG";
    //private Context context;

    public HistoryDisplayRecyclerViewAdapter(Context context, String orderBy)
    {
        cursor = HistoryDatabaseContract.queryEntireDatabase(context,  orderBy);
        //this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HistoryDisplayRecyclerViewAdapter.ViewHolder holder, int position) {
        if(cursor.moveToNext())
        {
            Long temp = cursor.getLong(2);
            SimpleDateFormat formatter= new SimpleDateFormat("M-d-Y");
            Date date = new Date(temp);

            holder.getName().setText(cursor.getString(1));
            holder.getNumber().setText(cursor.getString(0).replaceFirst("(1)?(\\d{3})(\\d{3})(\\d{4})", "$1($2) $3-$4"));
            holder.getDate().setText(formatter.format(date));
            holder.googleString = cursor.getString(4);
            if(position%2 == 0)
            {
                holder.getRow().setBackgroundColor(Color.parseColor("#FF84FFFF"));
            }
            else
            {
                holder.getRow().setBackgroundColor(Color.parseColor("#A7FFEB"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        private TextView name;
        private TextView number;
        private TextView date;
        private TableRow row;
        public String googleString;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contactNameView);
            number = itemView.findViewById(R.id.phoneNumberView);
            date = itemView.findViewById(R.id.dateSentView);
            row = itemView.findViewById(R.id.historyTableRow);
            row.setOnLongClickListener(this::onLongClick);
        }

        public TextView getName() {
            return name;
        }

        public TextView getNumber() {
            return number;
        }

        public TextView getDate() {
            return date;
        }

        public TableRow getRow() { return row; }

        @Override
        public boolean onLongClick(View view) {
            SharedPreferences sp  = view.getContext().getSharedPreferences(SHARED_PREFS, 0);

            String s = sp.getString(SPREMINDMESG, "none found");
            String pn = SMSSender.phoneNumberFormatter(number.getText().toString());
            Intent notificationIntent = new Intent();
            notificationIntent.setAction(SENT);

            Cursor c = HistoryDatabaseContract.queryByNumber(view.getContext(), pn);
            c.moveToFirst();
            String p = c.getString(0);
            String n = c.getString(1);
            boolean b3 = Boolean.parseBoolean(c.getString(3));
            boolean b4 = Boolean.parseBoolean(c.getString(4));
            boolean b5 = Boolean.parseBoolean(c.getString(5));
            c.close();
            SMSSender.sendSMSViaIntent(s, n, p, new Random().nextInt(), notificationIntent,
                    (Activity) view.getContext(), b3, b4, b5);
            ((Activity)view.getContext()).finish();
            return false;
        }
    }
}
