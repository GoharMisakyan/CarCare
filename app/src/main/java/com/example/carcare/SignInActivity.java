package com.example.carcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Intent;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {
TextView notYetRegistered, forgotPassword;
ImageView show_hide_pwd;
EditText email, password;
Button signInBtn;
boolean valid = true;
FirebaseAuth fAuth;
FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        fStore = FirebaseFirestore.getInstance();

        fAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserAccessLevel(currentUser.getUid());
            finish();
        }


        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);

            }
        });



        notYetRegistered =findViewById(R.id.signupRedirectText);
        email = findViewById(R.id.signIn_email);
        password = findViewById(R.id.signIn_password);
        signInBtn = findViewById(R.id.signin_button);

        //Show/hide pwd icon
        show_hide_pwd = findViewById(R.id.login_show_hide_pwd);
        show_hide_pwd.setImageResource(R.drawable.ic_hide_pwd);
        show_hide_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                   password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                   show_hide_pwd.setImageResource(R.drawable.ic_hide_pwd);
               } else {
                   password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                   show_hide_pwd.setImageResource(R.drawable.ic_show_pwd);
               }
            }
        });

       signInBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.d("SignInActivity", "Sign in button clicked.");
               checkField(email);
               checkField(password);
               Log.d("SignInActivity", "Email valid: " + valid + ", Password valid: " + valid);

               if(valid){
                   Log.d("SignInActivity", "Attempting sign-in...");

                   fAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                       @Override
                       public void onSuccess(AuthResult authResult) {
                           Log.d("SignInActivity", "Sign-in successful.");


                           //Checking if the user has verified email
                           FirebaseUser user = fAuth.getCurrentUser();
                           if (user.isEmailVerified()){
                               checkUserAccessLevel(authResult.getUser().getUid());
                               Toast.makeText(SignInActivity.this, "Signed In Successfully!", Toast.LENGTH_SHORT).show();


                           }else {
                               user.sendEmailVerification();
                               fAuth.signOut();
                               showAlertDialog();
                           }

                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                          // Log.e("SignInActivity", "Sign-in failed: " + e.getMessage());
                           Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   });

               }
           }
       });
        notYetRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email by pressing the button below. You will not be able to Sign In without email verification.");
        builder.setCancelable(false); // Dialog cannot be canceled by tapping outside

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent= new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                dialog.dismiss(); // Dismiss the dialog after user clicks "Continue"
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    private void checkUserAccessLevel(String uid){
        FirebaseUser user = fAuth.getCurrentUser();
        DocumentReference df = fStore.collection("Users").document(uid);
        //extracting the data
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG","onSuccess: " + documentSnapshot.getData());
                // identifying access level

               if (user.getUid().equals("1ekcwcOSV8WttQgaFwCyLpH2Iuj2")){
                    Intent intent = new Intent(SignInActivity.this, HomePageAdmin.class);
                    startActivity(intent);
                    finish();
                } else if (documentSnapshot.getString("isOwner") != null) {
                   //is an owner
                    startActivity(new Intent(SignInActivity.this, NavigationBarActivity.class));

                    finish();
                }

                /*if(documentSnapshot.getString("isUser") != null){
                    //is a regular user


                    startActivity(new Intent(SignInActivity.this, MapActivity.class));

                    finish();
                }*/else{
                    //is an user

                    startActivity(new Intent(SignInActivity.this, MapActivity.class));
                    finish();
                }

            }
        });
    }

    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Field can not be empty");
            valid = false;
        } else {
            valid = true;
        }

        return valid;
    }



}