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
                    if (ownerSwitch.isChecked()) {
                        /*AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                        builder.setTitle("Enter Special Code");
                        final EditText input = new EditText(SignUpActivity.this);
                        builder.setView(input);

                        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String code = input.getText().toString().trim();
                                checkSpecialCode(code);
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();*/
                        startActivity(new Intent(SignUpActivity.this, CarServiceRegistrationActivity.class));
                        /*if (approved) {
                            registerUser(true);
                        }*/

                        isApproved(fAuth.getCurrentUser().getUid(), new ApprovalCallback() {
                            @Override
                            public void onApproved(boolean approved) {
                                if (approved) {
                                    registerUser(true);
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Your registration request has been rejected. Please try again later.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(SignUpActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        registerUser(false);
                    }
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

    /*private void checkSpecialCode(String code) {
        fStore.collection("SpecialCodes")
                .document("G9B8b5c%b0j")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String storedCode = document.getString("code");
                                if (storedCode != null && storedCode.equals(code)) {
                                    registerUser(true);
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Invalid special code", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SignUpActivity.this, "Special code document not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }*/

    private void registerUser(boolean isOwner) {
        fAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = fAuth.getCurrentUser();
                        Toast.makeText(SignUpActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                        DocumentReference df = fStore.collection("Users").document(user.getUid());
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("FullName", name.getText().toString());
                        userInfo.put("UserEmail", email.getText().toString());
                        userInfo.put("PhoneNumber", phone.getText().toString());
                       // userInfo.put("ApprovalStatus", "pending");


                        if (isOwner) {
                            //owner
                        } else {
                            // regular user
                            userInfo.put("isUser", 1);
                        }

                        df.set(userInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        user.sendEmailVerification();
                                        Toast.makeText(SignUpActivity.this, "Account Created! Please verify your email.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
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
            //textField.requestFocus();

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

    /*public boolean isApproved(String userId) {
        fStore.collection("approvedCarServices")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                       approved = true;
                    } else {
                       approved = false;
                        Toast.makeText(SignUpActivity.this, "Your registration request has been rejected, if you find it wrong, please try again", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("SignUpActivity", "Error checking user approval: ", e);
                    Toast.makeText(SignUpActivity.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                });
        return approved;
    }*/
    public void isApproved(String userId, ApprovalCallback callback) {
        fStore.collection("approvedCarServices")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onApproved(true);
                    } else {
                        callback.onApproved(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("SignUpActivity", "Error checking user approval: ", e);
                    callback.onError(e.getMessage());
                });
    }

    interface ApprovalCallback {
        void onApproved(boolean approved);
        void onError(String errorMessage);
    }
}
