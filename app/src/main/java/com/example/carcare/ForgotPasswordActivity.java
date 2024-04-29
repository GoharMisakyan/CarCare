 package com.example.carcare;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

 public class ForgotPasswordActivity extends AppCompatActivity {
    private Button buttonPwdReset;
    private EditText editTextPwdResetEmail;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Forgot Password");
        }



        editTextPwdResetEmail = findViewById(R.id.editText_reset_pwd_email);
        buttonPwdReset = findViewById(R.id.button_password_reset);

        buttonPwdReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextPwdResetEmail.getText().toString();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                    editTextPwdResetEmail.setError("Email is required");
                    editTextPwdResetEmail.requestFocus();
                }else if (TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your valid email", Toast.LENGTH_SHORT).show();
                    editTextPwdResetEmail.setError("Valid email is required");
                    editTextPwdResetEmail.requestFocus();
                }else {
                    resetPassword(email);
                }
            }
        });
    }

     private void resetPassword(String email) {
        fAuth = FirebaseAuth.getInstance();
        fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your inbox for password reset link", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        editTextPwdResetEmail.setError("User does not exist or is no longer valid. Please register again");
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });
     }
 }