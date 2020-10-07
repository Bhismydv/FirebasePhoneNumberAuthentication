package com.example.firebasephonenumberauthentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText editTextMobile;
    Button btnContinue;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize fields
        editTextMobile=findViewById(R.id.editTextPhone);
        btnContinue=findViewById(R.id.buttonContinue);
        currentUser= FirebaseAuth.getInstance().getCurrentUser();

        //check whether the user is logged in
        if (currentUser!=null){
            Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(intent);
            finish();
        }else {
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mobileNo=editTextMobile.getText().toString().trim();

                    if (mobileNo.isEmpty() || mobileNo.length() < 12){
                        editTextMobile.setError("Enter a Valid Number");
                        editTextMobile.requestFocus();
                        return;
                    }

                    Intent intent=new Intent(getApplicationContext(),VerifyPhoneAcitivity.class);
                    intent.putExtra("mobile", mobileNo);
                    startActivity(intent);
                }
            });
        }
    }
}
