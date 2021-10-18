package com.jake.reviewquest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class InstructionsDialog extends DialogFragment implements View.OnClickListener{
    private Button closeButton;
    private TextView googleHyperlink;
    private TextView facebookHyperlink;
    private TextView yelpHyperlink;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.instructions_dialog, null);
        closeButton = dialogView.findViewById(R.id.instructionsDialogCloseButton);
        closeButton.setOnClickListener(this);
        setUpHyperlinks(dialogView, googleHyperlink, R.id.googleMyBusinessHyperlink);
        setUpHyperlinks(dialogView, facebookHyperlink, R.id.facebookBusinessHyperlink);
        setUpHyperlinks(dialogView, yelpHyperlink, R.id.yelpBusinessHyperlink);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);

        return builder.create();
    }

    @Override
    public void onClick(View view) {
        this.dismiss();
    }

    private void setUpHyperlinks(View view, TextView tv, int tvResourceID)
    {
        tv = view.findViewById(tvResourceID);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
