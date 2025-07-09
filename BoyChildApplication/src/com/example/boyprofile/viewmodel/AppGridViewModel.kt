package com.example.boyprofile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.boyprofile.model.AppInfo
import com.example.boyprofile.repository.AppRepository

class AppGridViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    private val _apps = MutableLiveData<List<AppInfo>>()
    val apps: LiveData<List<AppInfo>> = _apps

    fun loadApps(category: String?, childUserId: Int) {
        val restrictions = repository.getAppRestrictions(childUserId)
        val allApps = repository.getInstalledApps()

        val filtered = allApps.filter { app ->
            val appSpecificAllowed = restrictions.appSpecific[app.packageName]
            if (appSpecificAllowed != null) return@filter appSpecificAllowed

            val appCategory = repository.getCategoryForApp(app.packageName, "")
            if (appCategory != null && category == appCategory) {
                return@filter restrictions.categories.getOrDefault(appCategory, true)
            }

            return@filter category == null || appCategory == category
        }

        _apps.postValue(filtered)
    }
}

