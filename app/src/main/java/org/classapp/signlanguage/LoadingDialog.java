package org.classapp.signlanguage;

import android.app.Activity;
import android.view.LayoutInflater;
import androidx.appcompat.app.AlertDialog;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog alertDialog;

    LoadingDialog(Activity myactivity)
    {
        activity= myactivity;
    }

    void startLoadingDialog()
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog_loading,null));
        builder.setCancelable(false);


        alertDialog = builder.create();
        alertDialog.show();
    }


    void dismisDialog()
    {
        alertDialog.dismiss();
    }
}
