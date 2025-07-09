package com.example.kidgirlslauncher.viewmodel

import androidx.lifecycle.*
import com.example.kidgirlslauncher.model.AppInfo
import com.example.kidgirlslauncher.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: AppRepository) : ViewModel() {

    private val _apps = MutableLiveData<List<AppInfo>>()
    val apps: LiveData<List<AppInfo>> get() = _apps

    fun loadApps(category: String?, childUserId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getFilteredApps(category, childUserId)
            _apps.postValue(result)
        }
    }
}

// ViewModel Factory
class CategoryViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

