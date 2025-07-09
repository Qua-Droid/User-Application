package com.example.parentlauncher;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RestrictionSettingsViewModel extends AndroidViewModel {

    private static final String TAG = "RestrictionSettingsVM";

    private MutableLiveData<Map<String, Boolean>> categoryRestrictions = new MutableLiveData<>();
    private MutableLiveData<List<AppInfo>> appList = new MutableLiveData<>();
    private AppRestrictionRepository appRestrictionRepository;
    private AppRepository appRepository;

    private int childUserId;

    public RestrictionSettingsViewModel(Application application) {
        super(application);
        appRestrictionRepository = new AppRestrictionRepository(application.getApplicationContext());
        appRepository = new AppRepository(application.getApplicationContext());
    }

    public LiveData<Map<String, Boolean>> getCategoryRestrictions() {
        return categoryRestrictions;
    }

    public LiveData<List<AppInfo>> getAppList() {
        return appList;
    }

    public void setChildUserId(int userId) {
        this.childUserId = userId;
        loadRestrictions();
    }

    private void loadRestrictions() {
        // Load category restrictions
        Map<String, Boolean> loadedCategoryRestrictions = appRestrictionRepository.loadCategoryRestrictions(childUserId);
        // Ensure all categories are present, default to true if not found
        if (!loadedCategoryRestrictions.containsKey("watch")) loadedCategoryRestrictions.put("watch", true);
        if (!loadedCategoryRestrictions.containsKey("play")) loadedCategoryRestrictions.put("play", true);
        if (!loadedCategoryRestrictions.containsKey("draw")) loadedCategoryRestrictions.put("draw", true);
        if (!loadedCategoryRestrictions.containsKey("read")) loadedCategoryRestrictions.put("read", true);
        categoryRestrictions.setValue(loadedCategoryRestrictions);

        // Load all installed apps and then apply app-specific restrictions
        List<AppInfo> allInstalledApps = appRepository.getInstalledApps();
        List<AppInfo> updatedAppList = appRestrictionRepository.loadAppSpecificRestrictions(childUserId, allInstalledApps);
        appList.setValue(updatedAppList);
    }

    public void saveRestrictions(Map<String, Boolean> currentCategoryRestrictions, List<AppInfo> currentAppList) {
        appRestrictionRepository.saveRestrictions(childUserId, currentCategoryRestrictions, currentAppList);
    }

    public void filterApps(CharSequence constraint) {
        List<AppInfo> originalList = appList.getValue();
        if (originalList == null) return;

        List<AppInfo> filteredList = new ArrayList<>();
        if (constraint == null || constraint.length() == 0) {
            filteredList.addAll(originalList);
        } else {
            String filterPattern = constraint.toString().toLowerCase().trim();
            for (AppInfo app : originalList) {
                if (app.getName().toLowerCase().contains(filterPattern)) {
                    filteredList.add(app);
                }
            }
        }
        appList.setValue(filteredList);
    }
}


