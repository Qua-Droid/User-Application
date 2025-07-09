package com.example.parentlauncher;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.app.IActivityManager;
import android.content.ComponentName;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";
    private MutableLiveData<Integer> currentChildUserId = new MutableLiveData<>();
    private AppLauncherRepository appLauncherRepository;
    private UserManagerRepository userManagerRepository;

    public MainViewModel(Application application) {
        super(application);
        appLauncherRepository = new AppLauncherRepository(application.getApplicationContext());
        userManagerRepository = new UserManagerRepository(application.getApplicationContext());
        loadCurrentChildUserId();
    }

    public LiveData<Integer> getCurrentChildUserId() {
        return currentChildUserId;
    }

    private void loadCurrentChildUserId() {
        int userId = SharedPreferencesManager.getCurrentChildUserId(getApplication().getApplicationContext());
        currentChildUserId.setValue(userId);
    }

    public void saveCurrentChildUserId(int userId) {
        SharedPreferencesManager.saveCurrentChildUserId(getApplication().getApplicationContext(), userId);
        currentChildUserId.setValue(userId);
    }

    public void launchChildApp(int userId) {
        if (userId == -1) {
            Toast.makeText(getApplication().getApplicationContext(), "Please create or select a child user first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String profileType = SharedPreferencesManager.getProfileType(getApplication().getApplicationContext(), userId);
        if (profileType == null) {
            Toast.makeText(getApplication().getApplicationContext(), "Unknown profile type for selected user.", Toast.LENGTH_SHORT).show();
            return;
        }

        appLauncherRepository.launchChildAppOnSecondaryDisplay(userId, profileType);
    }

    public void openRestrictionSettings(int userId) {
        if (userId == -1) {
            Toast.makeText(getApplication().getApplicationContext(), "Please create or select a child user first.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getApplication().getApplicationContext(), RestrictionSettingsActivity.class);
        intent.putExtra("child_user_id", userId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().getApplicationContext().startActivity(intent);
    }

    public void launchSystemSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().getApplicationContext().startActivity(intent);
    }

    public void launchHomeScreen() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().getApplicationContext().startActivity(startMain);
    }
}


