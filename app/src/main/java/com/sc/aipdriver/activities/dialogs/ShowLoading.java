package com.sc.aipdriver.activities.dialogs;

import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by dev on 15/3/18.
 */

public class ShowLoading {
    Context context;
    SweetAlertDialog pDialog;

    public ShowLoading(Context context) {
        this.context = context;
    }

    public void show() {
        try {
            if (pDialog!=null){
                pDialog.dismissWithAnimation();
            }
            pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setContentText("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }catch (Exception e){
            if (pDialog!=null) {
                pDialog.dismissWithAnimation();
            }
        }
    }

    public void dismiss() {
        if (pDialog!=null) {
            pDialog.dismissWithAnimation();
        }
    }
}
