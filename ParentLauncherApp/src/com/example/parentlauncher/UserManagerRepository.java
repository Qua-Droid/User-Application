package com.example.parentlauncher;

import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UserManagerRepository {

    private static final String TAG = "UserManagerRepository";
    private UserManager userManager;
    private Context context;

    public UserManagerRepository(Context context) {
        this.context = context;
        this.userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
    }

    public List<UserInfo> getChildUsers() {
        List<UserInfo> childUsers = new ArrayList<>();
        if (userManager != null) {
            List<UserInfo> allUsers = userManager.getUsers();
            for (UserInfo user : allUsers) {
                if (user.isManagedProfile() || user.isRestricted() || user.isGuest() || user.id == android.os.Process.myUserHandle().getIdentifier()) {
                    continue;
                }
                childUsers.add(user);
            }
        } else {
            Log.e(TAG, "User Manager not available");
        }
        return childUsers;
    }

    public boolean deleteUser(UserInfo user) {
        if (userManager != null) {
            if (userManager.canAddMoreUsers()) {
                boolean success = userManager.removeUser(user.id);
                if (success) {
                    SharedPreferencesManager.clearProfileType(context, user.id);
                    return true;
                } else {
                    Log.e(TAG, "Failed to delete user " + user.name);
                    return false;
                }
            } else {
                Log.e(TAG, "Cannot delete user: insufficient permissions or user type.");
                return false;
            }
        } else {
            Log.e(TAG, "User Manager not available");
            return false;
        }
    }

    public String getProfileType(int userId) {
        return SharedPreferencesManager.getProfileType(context, userId);
    }
}


