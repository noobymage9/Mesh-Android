package com.mesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class accountRegister extends AppCompatActivity {

    private EditText nametf, emailtf, passtf;
    private Button regbtn, loginfrmbtn;
    private ProgressBar pb;
    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_register);

        //INITIALIZING ALL DECLARED ELEMENTS
        initElements();

//        SENDING TO LOGIN FORM ON CLICK OF LOGIN BUTTON

        loginfrmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

//        REGITER BUTTON FUNCTION
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nametf.getText().toString().trim();
                String email = emailtf.getText().toString().trim();
                String pass = passtf.getText().toString().trim();

//                VALIDATING USER INPUT
                if(validateInp(name, email, pass) == true) {

                    pb.setVisibility(View.VISIBLE);

//                FIREBASE REGISTRATION GAME HERE:

                    fauth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (task.isSuccessful()) {
                                startActivity((new Intent(getApplicationContext(),Login.class)));
                                finish();
                            } else {
                                pb.setVisibility(View.INVISIBLE);
                                Toast.makeText(accountRegister.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(accountRegister.this, "Error in Registering!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validateInp(String name, String email, String pass) {

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

        return (error == 0) ? true : false;

    }

    private void initElements() {
        nametf = (EditText) findViewById(R.id.nametf);
        emailtf = (EditText) findViewById(R.id.emailtf);
        passtf = (EditText) findViewById(R.id.passtf);

        regbtn = (Button) findViewById(R.id.regbtn);
        loginfrmbtn = (Button) findViewById(R.id.loginfrmbtn);

        pb = (ProgressBar) findViewById(R.id.pb);

        fauth = FirebaseAuth.getInstance();

    }

}
