package com.jake.reviewquest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

public class linkShortenerDialog extends DialogFragment {

    private ProgressBar progressBar;
    private int numberOfLinks;
    private int currentProgress;

    public linkShortenerDialog()
    {
        currentProgress = 0;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.update_progress_dialog, null);
        progressBar = dialogView.findViewById(R.id.linkShorteningProgressBar);
        progressBar.setMax(numberOfLinks);

        AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        return builder.create();
    }

    public void updateProgress()
    {
        currentProgress++;
        progressBar.setProgress(currentProgress);
        Log.i("UPDATE PROGRESS", "CURRENT PROGRESS = " + String.valueOf(currentProgress));
        if(currentProgress == numberOfLinks)
        {
            dismiss();
        }
    }
    public void incrementNumberOfLinks()
    {
        numberOfLinks++;
    }

}
