package com.example.parentlauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.UserManager;
import android.os.UserHandle;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.hardware.display.DisplayManager;
import android.view.Display;
import android.content.ComponentName;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.app.IActivityManager;

public class UserSwitchReceiver extends BroadcastReceiver {

    private static final String TAG = "UserSwitchReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_USER_FOREGROUND.equals(intent.getAction())) {
            int userId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -1);
            Log.d(TAG, "User switched to: " + userId);

            // Check if the switched user is a child user (you might need a more robust check)
            // For now, let's assume any non-admin user is a child user for this context
            UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
            if (userManager != null && userManager.getUserInfo(userId) != null && !userManager.getUserInfo(userId).isAdmin()) {
                Log.d(TAG, "Detected child user foreground: " + userId);
                launchChildAppOnSecondaryDisplay(context, userId);
            }
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Boot completed. Checking current user.");
            // On boot, the system might start with the last active user or default user.
            // We need to check the current foreground user and if it's a child, launch the app.
            UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
            if (userManager != null) {
                try {
                    IActivityManager am = IActivityManager.Stub.asInterface(ServiceManager.getService(Context.ACTIVITY_SERVICE));
                    int currentUserId = am.getCurrentUser().id;
                    if (userManager.getUserInfo(currentUserId) != null && !userManager.getUserInfo(currentUserId).isAdmin()) {
                        Log.d(TAG, "Child user active on boot: " + currentUserId);
                        launchChildAppOnSecondaryDisplay(context, currentUserId);
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException getting current user on boot", e);
                }
            }
        }
    }

    private void launchChildAppOnSecondaryDisplay(Context context, int userId) {
        try {
            IActivityManager am = IActivityManager.Stub.asInterface(ServiceManager.getService(Context.ACTIVITY_SERVICE));
            am.startUserInBackground(userId);

            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            Display[] displays = dm.getDisplays();
            int targetDisplayId = Display.DEFAULT_DISPLAY;
            
            for (Display display : displays) {
                if (display.getDisplayId() != Display.DEFAULT_DISPLAY) {
                    targetDisplayId = display.getDisplayId();
                    Log.d(TAG, "Found secondary display: " + targetDisplayId);
                    break;
                }
            }

            if (targetDisplayId == Display.DEFAULT_DISPLAY) {
                Log.w(TAG, "No secondary display found, launching on default display");
            }

            Intent intent = new Intent();
            intent.setComponent(new ComponentName(
                "com.example.boyprofile",
                "com.example.boyprofile.MainActivity"
            ));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("android.intent.extra.user_handle", userId);

            ActivityOptions options = ActivityOptions.makeBasic();
            options.setLaunchDisplayId(targetDisplayId);

            Log.d(TAG, "Launching BoyChildApplication on display: " + targetDisplayId + " for user: " + userId);

            am.startActivityAsUser(
                null,
                context.getPackageName(),
                intent,
                intent.resolveTypeIfNeeded(context.getContentResolver()),
                null,
                null,
                0,
                0,
                null,
                options.toBundle(),
                userId
            );

        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException launching app from receiver", e);
        } catch (Exception e) {
            Log.e(TAG, "Failed to launch child app from receiver", e);
        }
    }
}


