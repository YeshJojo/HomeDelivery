package com.jojo.homedelivery;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shuhart.stepview.StepView;

import java.util.concurrent.TimeUnit;


public class Authentication extends AppCompatActivity {
    private int currentStep = 0;
    LinearLayout layout1,layout2,layout3;
    StepView stepView;
    DatabaseReference mDatabaseRef;

    private static final String TAG = "FirebasePhoneNumAuth";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth firebaseAuth;

    private String phoneNumber;
    private Button sendCodeButton;
    private Button verifyCodeButton;
    private Button signOutButton;
    private Button button3;
    private EditText phoneNum;
    private PinView verifyCodeET;
    private TextView phonenumberText;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private ProgressDialog loading, loading2;

    public Authentication() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(Authentication.this,MainActivity.class));
            finish();
        }

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        sendCodeButton = findViewById(R.id.submit1);
        verifyCodeButton = findViewById(R.id.submit2);
        button3 = findViewById(R.id.submit3);
        firebaseAuth = FirebaseAuth.getInstance();
        phoneNum = findViewById(R.id.phonenumber);
        verifyCodeET = findViewById(R.id.pinView);
        phonenumberText = findViewById(R.id.phonenumberText);

        stepView = findViewById(R.id.step_view);
        stepView.setStepsNumber(3);
        stepView.go(0, true);
        layout1.setVisibility(View.VISIBLE);

        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneNum.getText().toString();
                phonenumberText.setText(phoneNumber);

                if (TextUtils.isEmpty(phoneNumber)) {
                    phoneNum.setError("Enter a Phone Number");
                    phoneNum.requestFocus();
                } else if (phoneNumber.length() < 10) {
                    phoneNum.setError("Please enter a valid phone");
                    phoneNum.requestFocus();
                } else {
                    if (currentStep < stepView.getStepCount() - 1) {
                        currentStep++;
                        stepView.go(currentStep, true);
                    } else {
                        stepView.done(true);
                    }
                    Log.d("phoneNumber", phoneNumber);
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.VISIBLE);
                    String code = "+91";
                    phoneNumber = code.concat(phoneNumber);
                    Log.d("phoneNumber", phoneNumber);
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS,Authentication.this, mCallbacks);
                }
            }
        });

        mCallbacks =new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                loading = ProgressDialog.show(Authentication.this, null, "Validating OTP...", true, true);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }
            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
            }
        };

        verifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verificationCode = verifyCodeET.getText().toString();
                if(verificationCode.isEmpty()){
                    Toast.makeText(Authentication.this,"Enter verification code", Toast.LENGTH_SHORT).show();
                }else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentStep < stepView.getStepCount() - 1) {
                    currentStep++;
                    stepView.go(currentStep, true);
                } else {
                    stepView.done(true);
                }
                Handler handler = new Handler();
                loading2 = ProgressDialog.show(Authentication.this, null, "Creating Profile...", true, true);
                UpdateSettings();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loading2.dismiss();
                        startActivity(new Intent(Authentication.this,MainActivity.class));
                        finish();
                    }
                },3000);
            }
        });

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            loading.dismiss();
                            if (currentStep < stepView.getStepCount() - 1) {
                                currentStep++;
                                stepView.go(currentStep, true);
                            } else {
                                stepView.done(true);
                            }
                            layout1.setVisibility(View.GONE);
                            layout2.setVisibility(View.GONE);
                            layout3.setVisibility(View.VISIBLE);

                        } else {
                            loading.dismiss();
                            Toast.makeText(Authentication.this,"Something wrong", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }
    private void UpdateSettings() {
        String currentUserID = firebaseAuth.getCurrentUser().getUid();
        String number = phoneNum.getText().toString().trim();

        mDatabaseRef.child("Users").child(currentUserID).child("phone").setValue(number).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Authentication.this, "Phone number updated successfully...", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String message = task.getException().toString();
                            Toast.makeText(Authentication.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
