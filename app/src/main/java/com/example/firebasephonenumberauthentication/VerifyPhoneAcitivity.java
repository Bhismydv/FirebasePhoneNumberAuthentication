package com.example.firebasephonenumberauthentication;

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
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneAcitivity extends AppCompatActivity {

    //three objects needed
    private String mVerificationId;
    private EditText editTextCode;
    private FirebaseAuth mAuth;

    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_acitivity);

        //initialize objects
        mAuth=FirebaseAuth.getInstance();
        editTextCode=findViewById(R.id.editTextCode);
        btnSignIn=findViewById(R.id.buttonSignIn);

        //getting mobile no. from the previous activity
        //sending the verification code to the number
        Intent intent=getIntent();
        String mobile=intent.getStringExtra("mobile");
        sendVerificationCode(mobile);


        //if the automatically sms detection did not work, user can also enter code manually
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code=editTextCode.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6){
                    editTextCode.setError("Enter Valid Code");
                    editTextCode.requestFocus();
                    return;
                }

                //verifying the code entered manually
                verifyVarificationCode(code);
            }
        });
    }

    private void verifyVarificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,code);
        signInWithPhoneAuthCredentials(credential);

    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(VerifyPhoneAcitivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //verification successful we will start the profile activity
                    Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else {
                    //verification unsuccessful
                    String message= "Something is wrong...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                message="Invalid Code Entered...";
                            }
                    Toast.makeText(VerifyPhoneAcitivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void sendVerificationCode(String mobile) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+" + mobile,60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD,mCallbacks
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //getting the code sent by sms
            String code=phoneAuthCredential.getSmsCode();

            //some times code is not detected
            if (code!=null){
                editTextCode.setText(code);
                //verifying the code
                verifyVarificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneAcitivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //when the code is generated then this method will receive the code

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            //super.onCodeSent(s, forceResendingToken);

            //storing the verificataion id that is sent to the user
            mVerificationId=s;
        }
    };
}
