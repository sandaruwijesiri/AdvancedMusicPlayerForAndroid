package com.example.testapplication2.Miscellaneous;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DeletePlaylistDialog extends DialogFragment {

    DeletePlaylistDialog.DeletePlaylistDialogListener deletePlaylistDialogListener;
    int adapterPosition = -1;

    public DeletePlaylistDialog(DeletePlaylistDialogListener deletePlaylistDialogListener, int madapterPosition) {
        this.deletePlaylistDialogListener = deletePlaylistDialogListener;
        this.adapterPosition = madapterPosition;
    }

    public interface DeletePlaylistDialogListener {
        public void onDeletePlaylistDialogPositiveClick(DialogFragment dialog, int AdapterPosition);
        public void onDeletePlaylistDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Delete Playlist?")
        //builder.setMessage("App requires permission to read external storage in order to access audio files. Grant permission?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deletePlaylistDialogListener.onDeletePlaylistDialogPositiveClick(com.example.testapplication2.Miscellaneous.DeletePlaylistDialog.this, adapterPosition);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deletePlaylistDialogListener.onDeletePlaylistDialogNegativeClick(com.example.testapplication2.Miscellaneous.DeletePlaylistDialog.this);
                    }
                });
        return builder.create();
    }
}
