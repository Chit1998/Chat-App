package com.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;

import com.chatapp.R;
import com.chatapp.help.Constant;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

public class SignUpActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 100;
    private CountryCodePicker countryCodePicker;
    private EditText ePhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getPermissions();
        countryCodePicker = findViewById(R.id.ccp);
        ePhoneNumber = findViewById(R.id.ePhoneNumber);

        findViewById(R.id.button_sent_otp)
                .setOnClickListener(v -> sentOTP(ePhoneNumber.getText().toString(), countryCodePicker.getDefaultCountryCode()));

    }

//    TODO Permissions
    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(
                SignUpActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(
                    SignUpActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constant.setMessage(SignUpActivity.this,Constant.permission_verified);
            }else {
                Constant.setMessage(SignUpActivity.this,Constant.permission_unverified);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void sentOTP(String phoneNumber, String countryCode) {
        startActivity(new Intent(getApplicationContext(), VerifyOTPActivity.class)
                .putExtra(Constant.phoneNumber,"+"+countryCode+phoneNumber));
    }
}