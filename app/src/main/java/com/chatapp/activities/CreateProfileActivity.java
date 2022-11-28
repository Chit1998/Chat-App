package com.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chatapp.R;
import com.chatapp.help.Constant;
import com.chatapp.help.Preferences;
import com.chatapp.models.UserDataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Objects;

public class CreateProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE = 1;
    private static final String TAG = "CreateProfileActivity";
    private static final int REQUEST_PERMISSION_CODE = 10;

    private EditText ePersonName, eAbout;
    private ImageView imageUser;
    private Uri imageUri = null;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private StorageReference imageRef;
    private Preferences preferences;
    private HashMap<String,String> userData;
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        preferences = new Preferences(this);
        userData = preferences.getLogInDetails();
        storage = FirebaseStorage.getInstance();
        imageRef = storage.getReference().child("profileImages").child(auth.getUid());
        init();
        if (userData.get(Constant.KEY_PROFILE_NAME) == null){
            super.onStart();
        }else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

    }
    //    TODO Token firebase messaging generate
    @Override
    protected void onStart() {
        if (userData.get(Constant.KEY_PROFILE_NAME) == null){
        }else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        super.onStart();
    }

    private void init(){
        ePersonName = findViewById(R.id.ePersonName);
        eAbout = findViewById(R.id.eAbout);
        imageUser = findViewById(R.id.imageUser);

        findViewById(R.id.cardUserImage)
                .setOnClickListener(v -> {
                    if (!getPermissions()){
                        CropImage.activity()
                                .setAspectRatio(1,1).start(CreateProfileActivity.this);
                    }
                });

        findViewById(R.id.image_button_save)
                .setOnClickListener(v -> {
                    if (ePersonName.getText().toString().isEmpty()){
                        ePersonName.setError("Empty");
                    }else {
                        saveData(ePersonName.getText().toString(),eAbout.getText().toString(),url);
                    }
                });
    }

    //    TODO Permissions
    private boolean getPermissions() {
        if (ContextCompat.checkSelfPermission(
                CreateProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(
                    CreateProfileActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE
            );

            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

//    TODO image uri
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            if (data != null){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageUri = result.getUri();
                imageRef.child("image.jpg")
                        .putFile(imageUri)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                task
                                        .getResult()
                                        .getStorage()
                                        .getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            Glide
                                                    .with(this)
                                                    .load(uri)
                                                    .centerCrop()
                                                    .into(imageUser);
                                            url = uri.toString();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(CreateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onFailure: "+e.getMessage());
                                        });
                            }else {
                                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void saveData(String name, String about, String url) {
        UserDataModel model = new UserDataModel(name, about, url, auth.getUid());
        firestore.collection(Constant.USER_DIR)
                .document(Objects.requireNonNull(auth.getUid()))
                .set(model)
                .addOnCompleteListener(task -> {
                    preferences.setData(name, about, url, auth.getUid());
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(CreateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}