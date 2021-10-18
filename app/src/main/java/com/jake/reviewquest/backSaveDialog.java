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

public class backSaveDialog extends DialogFragment implements View.OnClickListener {

    private Button saveButton;
    private Button exitWithoutSavingButton;
    private Button continueMakingChangesButton;
    private final SetupFrag fragment;

    public  backSaveDialog(SetupFrag fragment)
    {
        this.fragment = fragment;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.saveSetupButton:
                this.dismiss();
                fragment.onClickListener(null);
                break;
            case R.id.exitWithoutSavingButton:
                this.dismiss();
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.continueWorkingButton:
                this.dismiss();
                break;
            default:
                Log.e("backSaveDialog", "Registering Click Not in onClick Method");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.back_save_dialog, null);
        saveButton = dialogView.findViewById(R.id.saveSetupButton);
        exitWithoutSavingButton = dialogView.findViewById(R.id.exitWithoutSavingButton);
        continueMakingChangesButton = dialogView.findViewById(R.id.continueWorkingButton);

        saveButton.setOnClickListener(this);
        exitWithoutSavingButton.setOnClickListener(this);
        continueMakingChangesButton.setOnClickListener(this);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);

        return builder.create();
    }
}
