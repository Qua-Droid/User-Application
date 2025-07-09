package com.example.parentlauncher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_CREATE_CHILD_USER = 1;
    private MainViewModel mainViewModel;

    private Button createChildButton;
    private Button launchChildAppButton;
    private Button manageRestrictionsButton;
    private Button listUsersButton;
    private ImageButton backButton;
    private ImageButton settingsButton;
    private ImageButton homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initializeViews();
        setupListeners();

        mainViewModel.getCurrentChildUserId().observe(this, userId -> {
            if (userId != -1) {
                Toast.makeText(this, "Last active child user ID loaded: " + userId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeViews() {
        createChildButton = findViewById(R.id.create_child_button);
        launchChildAppButton = findViewById(R.id.launch_child_app);
        manageRestrictionsButton = findViewById(R.id.manage_restrictions_button);
        listUsersButton = findViewById(R.id.list_users_button);
        backButton = findViewById(R.id.back_button);
        settingsButton = findViewById(R.id.settings_button);
        homeButton = findViewById(R.id.home_button);
    }

    private void setupListeners() {
        createChildButton.setOnClickListener(v -> openCreateChildUserScreen());
        launchChildAppButton.setOnClickListener(v -> mainViewModel.launchChildApp(mainViewModel.getCurrentChildUserId().getValue()));
        manageRestrictionsButton.setOnClickListener(v -> mainViewModel.openRestrictionSettings(mainViewModel.getCurrentChildUserId().getValue()));
        listUsersButton.setOnClickListener(v -> openListUsersScreen());
        backButton.setOnClickListener(v -> onBackPressed());
        settingsButton.setOnClickListener(v -> mainViewModel.launchSystemSettings());
        homeButton.setOnClickListener(v -> mainViewModel.launchHomeScreen());
    }

    private void openCreateChildUserScreen() {
        Intent intent = new Intent(this, CreateChildUserActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CREATE_CHILD_USER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_CHILD_USER) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    int newChildUserId = data.getIntExtra(CreateChildUserActivity.EXTRA_CHILD_USER_ID, -1);
                    String profileType = data.getStringExtra(CreateChildUserActivity.EXTRA_PROFILE_TYPE);
                    if (newChildUserId != -1 && profileType != null) {
                        mainViewModel.saveCurrentChildUserId(newChildUserId);
                        Toast.makeText(this, "Child user created and selected: ID " + newChildUserId + ", Type: " + profileType, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to get new child user ID.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Child user creation cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openListUsersScreen() {
        Intent intent = new Intent(this, UserListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


