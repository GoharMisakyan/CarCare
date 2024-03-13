package com.example.carcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {
TextView notyetregistered;
EditText email, password;
Button signinbtn;
boolean valid = true;
FirebaseAuth fAuth;
FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        notyetregistered=findViewById(R.id.signupRedirectText);
        email = findViewById(R.id.signin_email);
        password = findViewById(R.id.signin_password);
        signinbtn = findViewById(R.id.signin_button);

       signinbtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               checkField(email);
               checkField(password);

               if(valid){
                   fAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                       @Override
                       public void onSuccess(AuthResult authResult) {
                           Toast.makeText(SignInActivity.this, "Signed In Successfuly!", Toast.LENGTH_SHORT).show();
                           checkUserAccessLevel(authResult.getUser().getUid());
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   });

               }
           }
       });
        notyetregistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    private void checkUserAccessLevel(String uid){
        DocumentReference df = fStore.collection("Users").document(uid);
        //extracting the data
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG","onSuccess: " + documentSnapshot.getData());
                // identifying access level

                if(documentSnapshot.getString("isUser") != null){
                    //is a regular user
                    startActivity(new Intent(getApplicationContext(), MapActivity.class));

                    finish();
                }else{
                    //is an owner
                    startActivity(new Intent(getApplicationContext(), NavigationBarActivity.class));
                    finish();
                }

            }
        });
    }

    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Field cannot be empty");
            valid = false;
        } else {
            valid = true;
        }

        return valid;
    }



}