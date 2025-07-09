package com.example.kidgirlslauncher.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.example.kidgirlslauncher.model.AppInfo
import com.example.kidgirlslauncher.model.AppRestrictions

class AppRepository(private val context: Context) {
    private val TAG = "AppRepository"
    private val restrictionUri = Uri.parse("content://com.patrick.parentlauncher.provider/app_restrictions")

    fun getAppRestrictions(childUserId: Int): AppRestrictions {
        val categoryRestrictions = mutableMapOf<String, Boolean>()
        val appSpecificRestrictions = mutableMapOf<String, Boolean>()
        val resolver = context.contentResolver

        resolver.query(
            restrictionUri,
            arrayOf("category", "package_name", "is_allowed", "is_app_specific"),
            "child_user_id = ?",
            arrayOf(childUserId.toString()),
            null
        )?.use { cursor ->
            val categoryColumn = cursor.getColumnIndex("category")
            val packageColumn = cursor.getColumnIndex("package_name")
            val allowedColumn = cursor.getColumnIndex("is_allowed")
            val specificColumn = cursor.getColumnIndex("is_app_specific")

            while (cursor.moveToNext()) {
                val isAllowed = cursor.getInt(allowedColumn) == 1
                val isSpecific = cursor.getInt(specificColumn) == 1
                if (isSpecific) {
                    appSpecificRestrictions[cursor.getString(packageColumn)] = isAllowed
                } else {
                    categoryRestrictions[cursor.getString(categoryColumn)] = isAllowed
                }
            }
        }

        return AppRestrictions(categoryRestrictions, appSpecificRestrictions)
    }

    fun getFilteredApps(category: String?, childUserId: Int): List<AppInfo> {
        val pm = context.packageManager
        val launchIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val allApps = pm.queryIntentActivities(launchIntent, 0)
        val restrictions = getAppRestrictions(childUserId)

        return allApps.filter {
            val pkg = it.activityInfo.packageName
            pkg !in listOf("com.android.settings", "com.android.systemui", "com.patrick.parentlauncher")
        }.filter {
            val packageName = it.activityInfo.packageName
            val appRestriction = restrictions.appSpecific[packageName]
            if (appRestriction != null) return@filter appRestriction

            val appCategory = getCategory(packageName, it.activityInfo.name)
            if (appCategory != null && category == appCategory) {
                return@filter restrictions.categories.getOrDefault(appCategory, true)
            }

            return@filter category == null || appCategory == category
        }.map {
            AppInfo(it.loadLabel(pm).toString(), it.activityInfo.packageName)
        }
    }

    private fun getCategory(packageName: String, activityName: String): String? {
        val pkg = packageName.lowercase()
        val act = activityName.lowercase()
        return when {
            pkg.contains("video") || pkg.contains("youtube") || pkg.contains("netflix") ||
            pkg.contains("media") || pkg.contains("player") || pkg.contains("stream") ||
            act.contains("gallery") || act.contains("video") -> "watch"
            pkg.contains("game") || pkg.contains("play") || pkg.contains("puzzle") ||
            pkg.contains("arcade") || pkg.contains("racing") || pkg.contains("sport") -> "play"
            pkg.contains("paint") || pkg.contains("draw") || pkg.contains("sketch") ||
            pkg.contains("art") || pkg.contains("creative") || pkg.contains("design") ||
            act.contains("sketch") || act.contains("paint") -> "draw"
            pkg.contains("reader") || pkg.contains("book") || pkg.contains("pdf") ||
            pkg.contains("kindle") || pkg.contains("library") || pkg.contains("education") ||
            pkg.contains("learn") || act.contains("pdf") || act.contains("reader") -> "read"
            else -> null
        }
    }
}

