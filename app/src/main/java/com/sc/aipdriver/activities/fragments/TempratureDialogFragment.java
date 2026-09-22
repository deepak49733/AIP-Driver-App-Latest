package com.sc.aipdriver.activities.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.models.RoutePlannerDetail;


/**
 * Created by dev on 5/12/17.
 */
public class TempratureDialogFragment extends BottomSheetDialogFragment {

    TextView dialogtitle;

    EditText semencolortemp;

    TextView noofbagsunloadedA;

    EditText semencolortempA;

    EditText noofbagsunloaded;

    EditText comments;

    EditText onloadingtemp;

    Button submit;
    SendData sendData;
    RoutePlannerDetail routePlannerDetail;
    String priority;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.temprature_dialog, null);
        dialog.setContentView(contentView);
        dialogtitle = contentView.findViewById(R.id.dialogtitle);
        semencolortemp = contentView.findViewById(R.id.semencoolertemp);
        noofbagsunloadedA = contentView.findViewById(R.id.noofbagsunloadedA);
        semencolortempA = contentView.findViewById(R.id.semencoolertempA);
        noofbagsunloaded = contentView.findViewById(R.id.noofbagsunloaded);
        comments = contentView.findViewById(R.id.comments);
        onloadingtemp = contentView.findViewById(R.id.onloadingtemp);
        submit = contentView.findViewById(R.id.submit);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);

            dialogtitle.setText(routePlannerDetail.getFarmName() +" ("+priority+")");
            noofbagsunloadedA.setText(""+ routePlannerDetail.getNumberOfBagsLoaded());
            semencolortempA.setText(""+ routePlannerDetail.getTemperatureOfSemenLoaded() );

            noofbagsunloadedA.setEnabled(false);
            semencolortempA.setEnabled(false);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submit.setEnabled(false);
                    if (valid()) {
                        sendData.datasave(semencolortemp.getText().toString(), noofbagsunloaded.getText().toString(), onloadingtemp.getText().toString(), comments.getText().toString());
                    }
                    submit.setEnabled(true);
                    }
            });
        }
    }

    private boolean valid() {
        if (semencolortemp.getText().toString().length() == 0 || noofbagsunloaded.getText().toString().length() == 0 ||
                onloadingtemp.getText().toString().length() == 0) {
            Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_LONG).show();
            return false;
        } else if (Integer.parseInt(routePlannerDetail.getNumberOfBagsLoaded()) != Integer.parseInt(noofbagsunloaded.getText().toString())) {
            noofbagsunloaded.setError("No. of unloaded quantity don't match with loaded bags");
            return false;
        } else
            return true;
    }

    public BottomSheetDialogFragment instance(RoutePlannerDetail routePlannerDetail, String snippet, SendData sendData) {
        this.sendData = sendData;
        this.routePlannerDetail = routePlannerDetail;
        this.priority = snippet;
        return this;
    }

    public interface SendData {
        void datasave(String semencoolertemp, String noofbagsloaded, String onloadingtemp, String comment);
    }
}

