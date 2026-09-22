package com.sc.aipdriver.activities.dialogs;

import android.content.Context;
import android.widget.Toast;

public class DisplayToast implements Runnable {
    private final Context mContext;
    String mText;

    public DisplayToast(Context mContext, String text) {
        this.mContext = mContext;
        this.mText = text;
    }

    @Override
    public void run() {
        Toast.makeText(mContext, mText,Toast.LENGTH_LONG).show();
    }
}
