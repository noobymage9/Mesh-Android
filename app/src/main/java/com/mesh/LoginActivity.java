package com.mesh;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


public class LoginActivity extends AppCompatActivity {

    private EditText emailtf, passtf;
    private Button loginbtn, regformbtn;
    ProgressBar pb;
    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //INITIALIZING ALL DECLARED ELEMENTS
        initElements();

//        SENDING TO REGISTRATION FORM ON CLICK OF REGISTER BUTTON

        regformbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AccountRegisterActivity.class));
            }
        });


//        LOGIN BUTTON FUNCTION
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailtf.getText().toString().trim();
                String pass = passtf.getText().toString().trim();

//                VALIDATING USER INPUT
                if (validateInp(email, pass) == true) {


                    //PROGRESS BAR
                    pb.setVisibility(View.VISIBLE);

//                FIREBASE LOGIN GAME HERE:

                    fauth.signInWithEmailAndPassword(email, pass).addOnCompleteListener((new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                pb.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }));
                }
                else{
                    Toast.makeText(LoginActivity.this, "Error in Logging in!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validateInp(String email, String pass) {

//        IF TEXT FIELDS ARE EMPTY

        int error = 0;

        if (TextUtils.isEmpty(email)) {
            emailtf.setError("Email is required!");
            error = 1;
        }


        if (TextUtils.isEmpty(pass)) {
            passtf.setError("Password is required!");
            error = 1;
        }


//        CHECKING FORMAT OF EMAIL
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailtf.setError("Invalid Email!");
            error = 1;
        }


//        CHECKING LENGTH OF PASSWORD
        if (pass.length() < 6) {
            passtf.setError("Password must be atleast 6 characters!");
            error = 1;
        }

        return error == 0;

    }


    private void initElements() {
        emailtf = (EditText) findViewById(R.id.emailtf);
        passtf = (EditText) findViewById(R.id.passtf);

        loginbtn = (Button) findViewById(R.id.loginbtn);
        regformbtn = (Button) findViewById(R.id.regformbtn);

        pb = (ProgressBar) findViewById(R.id.pb);

        fauth = FirebaseAuth.getInstance();

    }


}
