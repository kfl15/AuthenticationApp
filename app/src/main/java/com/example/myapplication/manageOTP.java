package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class manageOTP extends AppCompatActivity {

    EditText t2;
    Button b2;
    FirebaseAuth mAuth;
    String phonenumber;
    String otpid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_otp);

        phonenumber = getIntent().getStringExtra("mobile").toString();
        t2 = (EditText)findViewById(R.id.t2);
        b2 = (Button) findViewById(R.id.b2);
        mAuth = FirebaseAuth.getInstance();


        initiateOTP();

       b2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(t2.getText().toString().isEmpty()){
                   Toast.makeText(getApplicationContext(), "Blank field cannot be processed", Toast.LENGTH_LONG).show();
               }
               else if (t2.getText().toString().length() != 6){
                   Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_LONG).show();
               }
               else{
                   PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpid, t2.getText().toString());
                   signInWithPhoneAuthCredential(credential);
               }
           }
       });


    }

    private void initiateOTP() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phonenumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                otpid = s;
                            }

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(manageOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        /*
                        IN THIS BELOW IF(), THE APP GOES FROM manageOTP CLASS TO MainActivity CLASS.
                        * */
                        if (task.isSuccessful()) {
                            startActivity(new Intent( manageOTP.this, MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(manageOTP.this, "Sign In Error", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}