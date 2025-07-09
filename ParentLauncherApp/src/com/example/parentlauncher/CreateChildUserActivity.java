package com.example.parentlauncher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

public class CreateChildUserActivity extends Activity {

    public static final String EXTRA_CHILD_USER_ID = "child_user_id";
    public static final String EXTRA_PROFILE_TYPE = "profile_type";

    private CreateChildUserViewModel createChildUserViewModel;

    private EditText childNameEditText;
    private Button createOkButton;
    private ImageButton backButton;
    private RadioGroup profileTypeRadioGroup;
    private RadioButton radioBoy;
    private RadioButton radioGirl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_child_user);

        createChildUserViewModel = new ViewModelProvider(this).get(CreateChildUserViewModel.class);

        childNameEditText = findViewById(R.id.child_name_edit_text);
        createOkButton = findViewById(R.id.create_user_ok_button);
        backButton = findViewById(R.id.back_button_create_user);
        profileTypeRadioGroup = findViewById(R.id.profile_type_radio_group);
        radioBoy = findViewById(R.id.radio_boy);
        radioGirl = findViewById(R.id.radio_girl);

        createOkButton.setOnClickListener(v -> createChildUser());
        backButton.setOnClickListener(v -> onBackPressed());

        createChildUserViewModel.getNewChildUserId().observe(this, userId -> {
            if (userId != null && userId != -1) {
                String profileType = createChildUserViewModel.getNewProfileType().getValue();
                Toast.makeText(this, "Child user created and selected: ID " + userId + ", Type: " + profileType, Toast.LENGTH_LONG).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_CHILD_USER_ID, userId);
                resultIntent.putExtra(EXTRA_PROFILE_TYPE, profileType);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        createChildUserViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    private void createChildUser() {
        String childName = childNameEditText.getText().toString().trim();
        String profileType = null;
        if (radioBoy.isChecked()) {
            profileType = "boy";
        } else if (radioGirl.isChecked()) {
            profileType = "girl";
        }
        createChildUserViewModel.createChildUser(childName, profileType);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}


