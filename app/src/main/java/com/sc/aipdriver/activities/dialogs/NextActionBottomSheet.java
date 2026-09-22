package com.sc.aipdriver.activities.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sc.aipdriver.R;

public class NextActionBottomSheet extends BottomSheetDialogFragment {

    public interface ActionListener {
        void onStartNext();
        void onGoBack();
        void onViewAllFarms();
    }

    private ActionListener listener;

    public NextActionBottomSheet(ActionListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_next_action, container, false);

        Button btnNext = view.findViewById(R.id.btn_start_next);
        Button btnBack = view.findViewById(R.id.btn_go_back);
        Button btnViewAll = view.findViewById(R.id.btn_view_all);

        btnNext.setOnClickListener(v -> {
            dismiss();
            listener.onStartNext();
        });

        btnBack.setOnClickListener(v -> {
            dismiss();
            listener.onGoBack();
        });

        btnViewAll.setOnClickListener(v -> {
            dismiss();
            listener.onViewAllFarms();
        });

        return view;
    }
}