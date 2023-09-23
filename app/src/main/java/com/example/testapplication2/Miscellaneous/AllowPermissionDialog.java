package com.example.testapplication2.Miscellaneous;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.testapplication2.OtherClasses.Methods;

public class AllowPermissionDialog extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface AllowPermissionDialogListener {
        public void onAllowPermissionDialogPositiveClick(DialogFragment dialog);
        public void onAllowPermissionDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    AllowPermissionDialogListener allowPermissionDialogListener;

    // Override the Fragment.onAttach() method to instantiate the AllowPermissionDialogListener
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Verify that the host activity/fragment implements the callback interface
        try {
            // Instantiate the AllowPermissionDialogListener so we can send events to the host
            allowPermissionDialogListener = (AllowPermissionDialogListener) requireActivity();
        } catch (ClassCastException e) {
            Methods.addToErrorLog(e.getMessage(),getContext());
            // The activity/fragment doesn't implement the interface, throw exception
            throw new ClassCastException(requireActivity().toString()
                    + " must implement AllowPermissionDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Allow Permission?");
        builder.setMessage("App requires permission to read external storage in order to access audio files. Grant permission?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        allowPermissionDialogListener.onAllowPermissionDialogPositiveClick(com.example.testapplication2.Miscellaneous.AllowPermissionDialog.this);
                    }
                })
                .setNegativeButton("Maybe later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        allowPermissionDialogListener.onAllowPermissionDialogNegativeClick(com.example.testapplication2.Miscellaneous.AllowPermissionDialog.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
