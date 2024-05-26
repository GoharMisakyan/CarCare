package com.example.carcare;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    TextView alreadyUser;
    EditText email, password, confirmPassword, phone, name;
    Button sigUpBtn;
    boolean valid = true;
    boolean approved = true;
    Switch ownerSwitch;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        alreadyUser = findViewById(R.id.signinRedirectText);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.confirm_password);
        name = findViewById(R.id.signup_name);
        phone = findViewById(R.id.signup_phoneNumber);
        ownerSwitch = findViewById(R.id.ownerSwitch);
        sigUpBtn = findViewById(R.id.signup_button);



        sigUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkField(name);
                checkField(email);
                checkField(password);
                checkField(confirmPassword);
                checkField(phone);

                if (valid) {
                   registerUser();
                }
            }
        });

        alreadyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }


    private void registerUser() {
        fAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = fAuth.getCurrentUser();
                        Toast.makeText(SignUpActivity.this, "Your Account has been Created!", Toast.LENGTH_SHORT).show();
                        DocumentReference df = fStore.collection("Users").document(user.getUid());
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("FullName", name.getText().toString());
                        userInfo.put("UserEmail", email.getText().toString());
                        userInfo.put("PhoneNumber", phone.getText().toString());
                        userInfo.put("isOwner", ownerSwitch.isChecked() ? 1 : null);  // 1 for owner, 0 for regular user

                        df.set(userInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        user.sendEmailVerification();
                                        Toast.makeText(SignUpActivity.this, "All the data has been saved", Toast.LENGTH_SHORT).show();

                                        if (ownerSwitch.isChecked()) {
                                            startActivity(new Intent(SignUpActivity.this, CarServiceRegistrationActivity.class));
                                            Toast.makeText(SignUpActivity.this, "Please Fill In details about yourCar Service, Submit them and wait for the approval result.", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(SignUpActivity.this, "Please verify your email and come back to sign in", Toast.LENGTH_SHORT).show();
                                            fAuth.signOut();
                                            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                        }

                                        finish();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this, "Failed to Create an Account", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Failed to Create an Account: "  + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public boolean checkField(EditText textField) {
        if (textField.getText().toString().isEmpty()) {
            textField.setError("This field can not be EMPTY");


            valid = false;
        } else if (!confirmPassword.getText().toString().equals(password.getText().toString())){
            valid = false;
            confirmPassword.setError("Passwords do not match");
            confirmPassword.requestFocus();
            confirmPassword.clearComposingText();
            password.clearComposingText();

        }else{
            valid = true;
        }

        return valid;
    }

}
