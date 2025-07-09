package com.example.parentlauncher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestrictionSettingsActivity extends Activity {

    private RestrictionSettingsViewModel viewModel;
    
    private CheckBox watchCheckBox;
    private CheckBox playCheckBox;
    private CheckBox drawCheckBox;
    private CheckBox readCheckBox;
    private EditText searchApps;
    private RecyclerView appsRecyclerView;
    private Button saveRestrictionsButton;
    private ImageButton backButton;
    
    private AppRecyclerAdapter appRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restriction_settings);

        int childUserId = getIntent().getIntExtra("child_user_id", -1);
        if (childUserId == -1) {
            Toast.makeText(this, "Error: Child User ID not provided.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(RestrictionSettingsViewModel.class);
        viewModel.setChildUserId(childUserId);

        initializeViews();
        setupListeners();
        setupObservers();
    }

    private void initializeViews() {
        watchCheckBox = findViewById(R.id.checkbox_watch);
        playCheckBox = findViewById(R.id.checkbox_play);
        drawCheckBox = findViewById(R.id.checkbox_draw);
        readCheckBox = findViewById(R.id.checkbox_read);
        searchApps = findViewById(R.id.search_apps);
        appsRecyclerView = findViewById(R.id.apps_recycler_view);
        saveRestrictionsButton = findViewById(R.id.save_restrictions_button);
        backButton = findViewById(R.id.back_button_restrictions);

        appsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        appRecyclerAdapter = new AppRecyclerAdapter(this, new ArrayList<>()); // Initialize with empty list
        appsRecyclerView.setAdapter(appRecyclerAdapter);
    }

    private void setupListeners() {
        searchApps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filterApps(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        saveRestrictionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRestrictions();
            }
        });
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void setupObservers() {
        viewModel.getCategoryRestrictions().observe(this, restrictions -> {
            if (restrictions != null) {
                watchCheckBox.setChecked(restrictions.getOrDefault("watch", true));
                playCheckBox.setChecked(restrictions.getOrDefault("play", true));
                drawCheckBox.setChecked(restrictions.getOrDefault("draw", true));
                readCheckBox.setChecked(restrictions.getOrDefault("read", true));
            }
        });

        viewModel.getAppList().observe(this, apps -> {
            if (apps != null) {
                appRecyclerAdapter.updateAppList(apps);
            }
        });
    }

    private void saveRestrictions() {
        Map<String, Boolean> currentCategoryRestrictions = viewModel.getCategoryRestrictions().getValue();
        if (currentCategoryRestrictions != null) {
            currentCategoryRestrictions.put("watch", watchCheckBox.isChecked());
            currentCategoryRestrictions.put("play", playCheckBox.isChecked());
            currentCategoryRestrictions.put("draw", drawCheckBox.isChecked());
            currentCategoryRestrictions.put("read", readCheckBox.isChecked());
        }

        List<AppInfo> currentAppList = appRecyclerAdapter.getAllApps(); // Get the current state from the adapter

        viewModel.saveRestrictions(currentCategoryRestrictions, currentAppList);
        Toast.makeText(this, "Restrictions saved.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


