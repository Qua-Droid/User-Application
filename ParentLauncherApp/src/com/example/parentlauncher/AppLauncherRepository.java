package com.example.parentlauncher;

import android.app.ActivityOptions;
import android.app.IActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

public class AppLauncherRepository {

    private static final String TAG = "AppLauncherRepository";
    private Context context;

    public AppLauncherRepository(Context context) {
        this.context = context;
    }

    public void launchChildAppOnSecondaryDisplay(int childUserId, String profileType) {
        String packageName;
        String activityName;

        if ("boy".equals(profileType)) {
            packageName = "com.example.boyprofile";
            activityName = "com.example.boyprofile.MainActivity";
        } else if ("girl".equals(profileType)) {
            packageName = "com.example.kidgirlslauncher";
            activityName = "com.example.kidgirlslauncher.GirlHomeActivity";
        } else {
            Toast.makeText(context, "Unknown profile type for selected user.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            IActivityManager am = IActivityManager.Stub.asInterface(ServiceManager.getService(Context.ACTIVITY_SERVICE));
            am.startUserInBackground(childUserId);

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
                Toast.makeText(context, "Warning: No secondary display found, launching on main display", Toast.LENGTH_LONG).show();
            }

            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, activityName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("android.intent.extra.user_handle", childUserId);

            ActivityOptions options = ActivityOptions.makeBasic();
            options.setLaunchDisplayId(targetDisplayId);

            Log.d(TAG, "Launching " + profileType + " application on display: " + targetDisplayId + " for user: " + childUserId);

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
                childUserId
            );

            Toast.makeText(context, profileType + " Profile launched on " +
                    (targetDisplayId == Display.DEFAULT_DISPLAY ? "main" : "secondary") + " display",
                    Toast.LENGTH_SHORT).show();

        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException launching app", e);
            Toast.makeText(context, "Error launching app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Failed to launch child app", e);
            Toast.makeText(context, "Failed to launch child app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}


