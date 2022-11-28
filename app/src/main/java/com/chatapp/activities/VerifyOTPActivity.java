package com.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.chaos.view.PinView;
import com.chatapp.R;
import com.chatapp.help.Constant;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private PinView pinView;
    private String sentOtp,code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otpactivity);

        auth = FirebaseAuth.getInstance();
        String phoneNumber = getIntent().getStringExtra(Constant.phoneNumber);
        pinView = findViewById(R.id.firstPinView);

        verifyPhoneNumber(phoneNumber);

        findViewById(R.id.button_otp_verify)
                .setOnClickListener(v -> {
                    code = Objects.requireNonNull(pinView.getText()).toString().trim();
                    if (!code.isEmpty() | code.length() == 6){
                        pinView.setText(code);
                        verifyCode(code);
                    }else {
                        Constant.setMessage(VerifyOTPActivity.this,Constant.empty);
                    }
                });
    }

    private void verifyPhoneNumber(String phoneNumber) {
        PhoneAuthOptions option = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(VerifyOTPActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        sentOtp = s;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        String sms = phoneAuthCredential.getSmsCode();
                        if (sms != null) {
                            pinView.setText(sms);
                            verifyCode(sms);
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Constant.setMessage(VerifyOTPActivity.this,e.getMessage());
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(option);

    }


    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(sentOtp,code);
        authenticateUser(credential);
    }

    public void authenticateUser(PhoneAuthCredential credential){
        auth.signInWithCredential(credential).addOnSuccessListener(authResult -> {
            startActivity(new Intent(getApplicationContext(),CreateProfileActivity.class));
            finish();
        }).addOnFailureListener(e -> Constant.setMessage(VerifyOTPActivity.this,e.getMessage()));
    }

}