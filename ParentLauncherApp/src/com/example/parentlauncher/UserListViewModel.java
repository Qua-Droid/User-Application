package com.example.parentlauncher;

import android.app.Application;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserManager;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class UserListViewModel extends AndroidViewModel {

    private static final String TAG = "UserListViewModel";
    private MutableLiveData<List<UserInfo>> userList = new MutableLiveData<>();
    private UserManagerRepository userManagerRepository;

    public UserListViewModel(Application application) {
        super(application);
        userManagerRepository = new UserManagerRepository(application.getApplicationContext());
        loadUsers();
    }

    public LiveData<List<UserInfo>> getUserList() {
        return userList;
    }

    public void loadUsers() {
        List<UserInfo> childUsers = userManagerRepository.getChildUsers();
        userList.setValue(childUsers);
    }

    public void deleteUser(UserInfo user) {
        if (userManagerRepository.deleteUser(user)) {
            Toast.makeText(getApplication().getApplicationContext(), "User " + user.name + " deleted.", Toast.LENGTH_SHORT).show();
            loadUsers(); // Refresh the list
        } else {
            Toast.makeText(getApplication().getApplicationContext(), "Failed to delete user " + user.name + ".", Toast.LENGTH_SHORT).show();
        }
    }

    public String getProfileType(int userId) {
        return userManagerRepository.getProfileType(userId);
    }
}


