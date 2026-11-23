package com.sc.aipdriver.activities.otherclasses;

import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StatusBarUtils {

    public static void applyStatusBarPadding(View headerView) {
        if (headerView == null) return;

        ViewCompat.setOnApplyWindowInsetsListener(headerView, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(
                    v.getPaddingLeft(),
                    statusBarHeight,
                    v.getPaddingRight(),
                    v.getPaddingBottom()
            );
            return insets;
        });
    }
}
