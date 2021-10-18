package com.jake.reviewquest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class ClearHistoryDialog extends DialogFragment implements View.OnClickListener{

    private Button yesButton;
    private Button noButton;
    private final RecyclerView recyclerView;
    private final String LOG = "CLEAR_HISTORY_DIALOG";

    public ClearHistoryDialog(RecyclerView recyclerView)
    {
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.clear_history_dialog, null);
        yesButton = dialogView.findViewById(R.id.yesHistoryButton);
        noButton = dialogView.findViewById(R.id.noHistoryButton);

        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        return builder.create();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.yesHistoryButton:
                this.dismiss();
                HistoryDatabaseContract.emptyDatabase(requireContext());
                recyclerView.setAdapter(new HistoryDisplayRecyclerViewAdapter(getContext(),
                        HistoryContentProvider.LAST_REQUEST_SENT_DATE + " DESC"));
                break;
            case R.id.noHistoryButton:
                this.dismiss();
                break;
            default:
                Log.e(LOG, "onCLick Error");
        }
    }
}
