package com.chatapp.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chatapp.help.Constant;
import com.chatapp.help.Preferences;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class BaseActivity extends AppCompatActivity {

    private Preferences preferences;
    private DocumentReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = new Preferences(this);
        HashMap<String, String> userData = preferences.getLogInDetails();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        reference = db.collection(Constant.USER_DIR)
                .document(Objects.requireNonNull(userData.get(Constant.USER_ID)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.update(Constant.KEY_AVAILABILITY,0);
    }


    @Override
    protected void onResume() {
        super.onResume();
        reference.update(Constant.KEY_AVAILABILITY,1);
    }
}
