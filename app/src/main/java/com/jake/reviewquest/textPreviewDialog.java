package com.jake.reviewquest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class textPreviewDialog extends DialogFragment implements View.OnClickListener {
    private Button closeButton;
    private TextView smsPreview;
    private final String sms;

    public textPreviewDialog(String sms)
    {
        this.sms = sms;
    }

    @Override
    public void onClick(View view) {
        this.dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.text_preview_dialog, null);
        closeButton = dialogView.findViewById(R.id.textPreviewDialogCloseButton);
        closeButton.setOnClickListener(this);
        smsPreview = dialogView.findViewById(R.id.smsPreviewTextView);
        smsPreview.setText(sms);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        return builder.create();
    }
}
