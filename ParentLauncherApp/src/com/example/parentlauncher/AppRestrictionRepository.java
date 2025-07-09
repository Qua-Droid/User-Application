package com.example.parentlauncher;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppRestrictionRepository {

    private static final String TAG = "AppRestrictionRepository";
    private ContentResolver contentResolver;

    public AppRestrictionRepository(Context context) {
        this.contentResolver = context.getContentResolver();
    }

    public Map<String, Boolean> loadCategoryRestrictions(int childUserId) {
        Map<String, Boolean> categoryRestrictions = new HashMap<>();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(
                    AppRestrictionProvider.CONTENT_URI,
                    new String[]{"category", "is_allowed"},
                    "child_user_id = ? AND is_app_specific = 0",
                    new String[]{String.valueOf(childUserId)},
                    null
            );

            if (cursor != null) {
                int categoryIndex = cursor.getColumnIndex("category");
                int isAllowedIndex = cursor.getColumnIndex("is_allowed");

                while (cursor.moveToNext()) {
                    String category = cursor.getString(categoryIndex);
                    boolean isAllowed = cursor.getInt(isAllowedIndex) == 1;
                    categoryRestrictions.put(category, isAllowed);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading category restrictions", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return categoryRestrictions;
    }

    public List<AppInfo> loadAppSpecificRestrictions(int childUserId, List<AppInfo> allApps) {
        List<AppInfo> updatedApps = new ArrayList<>(allApps);
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(
                    AppRestrictionProvider.CONTENT_URI,
                    new String[]{"package_name", "is_allowed"},
                    "child_user_id = ? AND is_app_specific = 1",
                    new String[]{String.valueOf(childUserId)},
                    null
            );

            if (cursor != null) {
                int packageNameIndex = cursor.getColumnIndex("package_name");
                int isAllowedIndex = cursor.getColumnIndex("is_allowed");

                while (cursor.moveToNext()) {
                    String packageName = cursor.getString(packageNameIndex);
                    boolean isAllowed = cursor.getInt(isAllowedIndex) == 1;

                    for (AppInfo app : updatedApps) {
                        if (app.getPackageName().equals(packageName)) {
                            app.setAllowed(isAllowed);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading app-specific restrictions", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return updatedApps;
    }

    public void saveRestrictions(int childUserId, Map<String, Boolean> categoryRestrictions, List<AppInfo> appSpecificRestrictions) {
        try {
            // Clear existing restrictions for this child user
            contentResolver.delete(
                    AppRestrictionProvider.CONTENT_URI,
                    AppRestrictionContract.AppRestrictionEntry.COLUMN_CHILD_USER_ID + "=?",
                    new String[]{String.valueOf(childUserId)}
            );

            // Save category restrictions
            for (Map.Entry<String, Boolean> entry : categoryRestrictions.entrySet()) {
                saveCategoryRestriction(childUserId, entry.getKey(), entry.getValue());
            }

            // Save app-specific restrictions
            for (AppInfo app : appSpecificRestrictions) {
                saveAppSpecificRestriction(childUserId, app.getPackageName(), app.isAllowed());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error saving restrictions", e);
        }
    }

    private void saveCategoryRestriction(int childUserId, String category, boolean isAllowed) {
        ContentValues values = new ContentValues();
        values.put(AppRestrictionContract.AppRestrictionEntry.COLUMN_CHILD_USER_ID, childUserId);
        values.put(AppRestrictionContract.AppRestrictionEntry.COLUMN_CATEGORY, category);
        values.put(AppRestrictionContract.AppRestrictionEntry.COLUMN_IS_ALLOWED, isAllowed ? 1 : 0);
        values.put(AppRestrictionContract.AppRestrictionEntry.COLUMN_IS_APP_SPECIFIC, 0);
        values.putNull(AppRestrictionContract.AppRestrictionEntry.COLUMN_PACKAGE_NAME);

        contentResolver.insert(AppRestrictionProvider.CONTENT_URI, values);
    }

    private void saveAppSpecificRestriction(int childUserId, String packageName, boolean isAllowed) {
        ContentValues values = new ContentValues();
        values.put(AppRestrictionContract.AppRestrictionEntry.COLUMN_CHILD_USER_ID, childUserId);
        values.put(AppRestrictionContract.AppRestrictionEntry.COLUMN_PACKAGE_NAME, packageName);
        values.put(AppRestrictionContract.AppRestrictionEntry.COLUMN_IS_ALLOWED, isAllowed ? 1 : 0);
        values.put(AppRestrictionContract.AppRestrictionEntry.COLUMN_IS_APP_SPECIFIC, 1);
        values.putNull(AppRestrictionContract.AppRestrictionEntry.COLUMN_CATEGORY);

        contentResolver.insert(AppRestrictionProvider.CONTENT_URI, values);
    }
}


