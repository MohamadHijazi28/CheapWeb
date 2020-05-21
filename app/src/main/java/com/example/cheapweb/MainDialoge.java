package com.example.cheapweb;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class MainDialoge extends AppCompatDialogFragment {

    private MainDialogeListener listener;

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure to Eixt").setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i){
                        listener.onYesClickMain();
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        return builder.create();
    }

    public interface MainDialogeListener{
        void onYesClickMain();
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener= (MainDialoge.MainDialogeListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString()+ "must implements MainDialogeListener");

        }
    }
}
