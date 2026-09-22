package com.sc.aipdriver.activities.fragments;

import android.os.Bundle;


import android.view.View;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.adapters.AllNotificationAdapter;



/**
 * Created by dev on 26/10/17.
 */

public class AllNotification extends AppCompatActivity {
    View view;

    RecyclerView recyclerView;
    AllNotificationAdapter allNotificationAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allNotificationAdapter = new AllNotificationAdapter(this);
        recyclerView.setAdapter(allNotificationAdapter);
    }
}
