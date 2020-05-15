package com.example.readup;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class Dialog extends AppCompatDialogFragment {

    private DialogListener dl;

    String Data;
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        LayoutInflater li = getActivity().getLayoutInflater();
        View v = li.inflate(R.layout.dialog,null);
        Data = "";
        b.setView(v).setTitle("Edit")
        .setNeutralButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
        .setNegativeButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Data = "Update";
                dl.getData(Data);
            }
        })
        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Data = "Delete";
                dl.getData(Data);
            }
        });
        return b.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dl = (DialogListener)context;
    }

    public interface DialogListener{
        String getData(String data);
    }
}
