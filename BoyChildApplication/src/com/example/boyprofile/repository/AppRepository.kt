package com.example.boyprofile.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.example.boyprofile.model.AppInfo
import com.example.boyprofile.model.AppRestrictions

class AppRepository(private val context: Context) {

    private val TAG = "AppRepository"
    private val pm = context.packageManager
    private val APP_RESTRICTION_URI: Uri = Uri.parse("content://com.patrick.parentlauncher.provider/app_restrictions")

    fun getInstalledApps(): List<AppInfo> {
        val launchIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val allApps = pm.queryIntentActivities(launchIntent, 0)
        return allApps
            .filter { info ->
                val pkg = info.activityInfo.packageName
                pkg !in listOf("com.android.settings", "com.android.systemui", "com.patrick.parentlauncher")
            }
            .map {
                AppInfo(
                    name = it.loadLabel(pm).toString(),
                    packageName = it.activityInfo.packageName
                )
            }
    }

    fun getAppRestrictions(childUserId: Int): AppRestrictions {
        val categoryRestrictions = mutableMapOf<String, Boolean>()
        val appSpecificRestrictions = mutableMapOf<String, Boolean>()
        var cursor: Cursor? = null

        try {
            cursor = context.contentResolver.query(
                APP_RESTRICTION_URI,
                arrayOf("category", "package_name", "is_allowed", "is_app_specific"),
                "child_user_id = ?",
                arrayOf(childUserId.toString()),
                null
            )

            cursor?.let {
                val categoryColumnIndex = it.getColumnIndex("category")
                val packageNameColumnIndex = it.getColumnIndex("package_name")
                val isAllowedColumnIndex = it.getColumnIndex("is_allowed")
                val isAppSpecificColumnIndex = it.getColumnIndex("is_app_specific")

                while (it.moveToNext()) {
                    val isAllowed = it.getInt(isAllowedColumnIndex) == 1
                    val isAppSpecific = it.getInt(isAppSpecificColumnIndex) == 1

                    if (isAppSpecific) {
                        val packageName = it.getString(packageNameColumnIndex)
                        if (packageName != null) {
                            appSpecificRestrictions[packageName] = isAllowed
                        }
                    } else {
                        val category = it.getString(categoryColumnIndex)
                        if (category != null) {
                            categoryRestrictions[category] = isAllowed
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error querying app restrictions: ", e)
        } finally {
            cursor?.close()
        }

        return AppRestrictions(categoryRestrictions, appSpecificRestrictions)
    }

    fun getCategoryForApp(packageName: String, activityName: String): String? {
        val pkg = packageName.lowercase()
        val activity = activityName.lowercase()

        return when {
            pkg.contains("youtube") || pkg.contains("netflix") || pkg.contains("stream") || activity.contains("gallery") -> "watch"
            pkg.contains("game") || pkg.contains("arcade") -> "play"
            pkg.contains("paint") || pkg.contains("draw") || activity.contains("sketch") -> "draw"
            pkg.contains("reader") || pkg.contains("book") || activity.contains("pdf") -> "read"
            else -> null
        }
    }
}

