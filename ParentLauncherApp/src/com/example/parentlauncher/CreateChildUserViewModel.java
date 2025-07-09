package com.example.parentlauncher;

import android.app.Application;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.os.IUserManager;

public class CreateChildUserViewModel extends AndroidViewModel {

    private static final String TAG = "CreateChildUserViewModel";
    private MutableLiveData<Integer> newChildUserId = new MutableLiveData<>();
    private MutableLiveData<String> newProfileType = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Integer> getNewChildUserId() {
        return newChildUserId;
    }

    public LiveData<String> getNewProfileType() {
        return newProfileType;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public CreateChildUserViewModel(Application application) {
        super(application);
    }

    public void createChildUser(String childName, String profileType) {
        if (childName.isEmpty()) {
            errorMessage.setValue("Please enter a child name");
            return;
        }

        if (profileType == null || profileType.isEmpty()) {
            errorMessage.setValue("Please select a profile type (Boy or Girl)");
            return;
        }

        try {
            IUserManager iUserManager = IUserManager.Stub.asInterface(ServiceManager.getService("user"));
            UserInfo userInfo = iUserManager.createUserWithThrow(childName, "android.os.usertype.full.SECONDARY", /* flags = */ 0);

            if (userInfo != null) {
                SharedPreferencesManager.saveProfileType(getApplication().getApplicationContext(), userInfo.id, profileType);
                newChildUserId.setValue(userInfo.id);
                newProfileType.setValue(profileType);
                errorMessage.setValue(null); // Clear any previous error
            } else {
                errorMessage.setValue("Failed to create child user. Check permissions.");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException during user creation", e);
            errorMessage.setValue("RemoteException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception creating user", e);
            errorMessage.setValue("Error creating user: " + e.getMessage());
        }
    }
}


