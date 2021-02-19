package com.monir.journalappwithfirebaseandfirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.monir.journalappwithfirebaseandfirestore.util.JournalApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {

    private Button buttonCreateAccount;
    private EditText editTextUserName;
    private EditText editTextMail;
    private EditText editTextPassword;
    private ProgressBar progressBarCreateAccount;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    // Firestore connection

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getSupportActionBar().setElevation(0);
        setTitle("Journal");

        buttonCreateAccount = findViewById(R.id.button_create_account);
        editTextMail = findViewById(R.id.email_create_act);
        editTextPassword = findViewById(R.id.password_create_act);
        editTextUserName = findViewById(R.id.userName_create_act);
        progressBarCreateAccount = findViewById(R.id.progress_bar_create_act);


        firebaseAuth = FirebaseAuth.getInstance();


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null){
                    // User is already logged in.
                }else {
                    // No user is logged in
                }
            }
        };

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(editTextMail.getText().toString())
                        && !TextUtils.isEmpty(editTextPassword.getText().toString())
                        && !TextUtils.isEmpty(editTextUserName.getText().toString())){

                    String email = editTextMail.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();
                    String username = editTextUserName.getText().toString().trim();

                    createUserEmailAccount(email,password,username);
                }else{
                    Toast.makeText(CreateAccountActivity.this, "Empty field is not allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // creating user account
   private void createUserEmailAccount(String email,String password, String username){
        if(!TextUtils.isEmpty(email) && ! TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)){

            progressBarCreateAccount.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        // We take user to add journalActivity
                        currentUser = firebaseAuth.getCurrentUser();
                        if(currentUser != null);
                        String currentUserId = currentUser.getUid();

                        // Create a user map so that we can create and add a user in the database user collection

                        Map<String,String> userObj = new HashMap<>();
                        userObj.put("userId",currentUserId);
                        userObj.put("username",username);

                        // Save to our firestore database

                        collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                       if(Objects.requireNonNull(task.getResult()).exists()){

                                           progressBarCreateAccount.setVisibility(View.INVISIBLE);

                                           String name = task.getResult().getString("username");

                                           JournalApi journalApi = JournalApi.getInstance(); // Global Api in our app
                                           journalApi.setUserId(currentUserId);
                                           journalApi.setUsername(name);

                                           Intent intent = new Intent(CreateAccountActivity.this,
                                                   PostJournalActivity.class);

                                           intent.putExtra("username",username);
                                           intent.putExtra("userId",currentUserId);
                                           startActivity(intent);
                                       } else {
                                               progressBarCreateAccount.setVisibility(View.INVISIBLE);
                                       }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }else {
                        // something went wrong
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

       }else{

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}