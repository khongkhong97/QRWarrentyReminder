package com.khong.qrwarrentyreminder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressBar progressBar;
    EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    Spinner spinnerUserType;
    DatabaseReference databaseNewUser;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        spinnerUserType = (Spinner) findViewById(R.id.spinnerUserType);

        mAuth = FirebaseAuth.getInstance();

        ArrayAdapter adapterUserType = ArrayAdapter.createFromResource(this,R.array.userTypes,android.R.layout.simple_spinner_dropdown_item);
        adapterUserType.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerUserType.setAdapter(adapterUserType);

        findViewById(R.id.buttonSignUp).setOnClickListener(this);
        findViewById(R.id.textViewLogin).setOnClickListener(this);
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        final String userType = spinnerUserType.getSelectedItem().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        if(!password.equals(confirmPassword)){
            editTextPassword.setError("Both password are not same");
            editTextPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    String uid;
                    FirebaseUser user;
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    uid = user.getUid();
                    databaseNewUser = FirebaseDatabase.getInstance().getReference("user").child(uid);
                    UserInformation ui = new UserInformation(userType);
                    databaseNewUser.setValue(ui);
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"User Registered Successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSignUp:
                registerUser();
                startActivity(new Intent(this,MainActivity.class));
                break;

            case R.id.textViewLogin:
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}

