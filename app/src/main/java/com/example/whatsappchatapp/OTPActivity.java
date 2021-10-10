package com.example.whatsappchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.whatsappchatapp.databinding.ActivityOtpactivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    ActivityOtpactivityBinding binding;
    FirebaseAuth auth;
    String verificationID;
    String phoneNumber;
    ProgressDialog dailog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        dailog = new ProgressDialog(this);
        dailog.setMessage("Verifying OTP");
        dailog.setCancelable(false);

        getSupportActionBar().hide();

        binding.phoneLbl2.setText("Verify "+phoneNumber);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder()
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L , TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                    auth.signInWithCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull  FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull  String verifyId, @NonNull  PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyId, forceResendingToken);
                        verificationID = verifyId;
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = binding.otpbox.getText().toString();
                if(otp.isEmpty()){
                    Toast.makeText(OTPActivity.this, "Please Enter OTP", Toast.LENGTH_SHORT).show();
                }
                else{
                    dailog.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID , otp);
                    auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                dailog.dismiss();
                                Intent intent = new Intent(OTPActivity.this, SetupProfileActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(OTPActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }
}