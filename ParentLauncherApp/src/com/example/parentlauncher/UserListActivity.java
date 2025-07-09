package com.example.parentlauncher;

import android.app.Activity;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends Activity {

    private ListView userListView;
    private UserAdapter userAdapter;
    private UserListViewModel userListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        userListViewModel = new ViewModelProvider(this).get(UserListViewModel.class);

        userListView = findViewById(R.id.user_list_view);
        Button backButton = findViewById(R.id.back_button_user_list);
        backButton.setOnClickListener(v -> finish());

        userAdapter = new UserAdapter(this, new ArrayList<>());
        userListView.setAdapter(userAdapter);

        userListViewModel.getUserList().observe(this, users -> {
            userAdapter.clear();
            userAdapter.addAll(users);
            userAdapter.notifyDataSetChanged();
        });

        userListViewModel.loadUsers();
    }

    private class UserAdapter extends ArrayAdapter<UserInfo> {
        public UserAdapter(Context context, List<UserInfo> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserInfo user = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_list_item, parent, false);
            }

            TextView userName = convertView.findViewById(R.id.user_name);
            TextView userType = convertView.findViewById(R.id.user_type);
            Button deleteButton = convertView.findViewById(R.id.delete_user_button);

            userName.setText(user.name);
            userType.setText("Type: " + userListViewModel.getProfileType(user.id));

            deleteButton.setOnClickListener(v -> userListViewModel.deleteUser(user));

            return convertView;
        }
    }
}


