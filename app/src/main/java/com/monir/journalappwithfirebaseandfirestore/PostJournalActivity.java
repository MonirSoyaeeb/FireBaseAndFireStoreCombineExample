package com.monir.journalappwithfirebaseandfirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.monir.journalappwithfirebaseandfirestore.model.Journal;
import com.monir.journalappwithfirebaseandfirestore.util.JournalApi;

import java.sql.Time;
import java.util.Date;

public class PostJournalActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY_CODE = 1;
    private Button saveButton;
    private ProgressBar progressBar;
    private ImageView addPhotoButton;
    private ImageView postImage;
    private EditText titleEditText;
    private EditText thoughtEditText;
    private TextView currentUserTextView;
    private TextView textViewPostDate;

    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;


    // Connection to firestore

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Journal");
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);
        getSupportActionBar().setElevation(0);
        setTitle("Journal");

        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBarPost);
        titleEditText = findViewById(R.id.editTextPostTitle);
        thoughtEditText = findViewById(R.id.editTextYourThought);
        addPhotoButton = findViewById(R.id.button_add_photo);
        postImage = findViewById(R.id.imageViewPost);
        currentUserTextView = findViewById(R.id.post_text);
        textViewPostDate = findViewById(R.id.post_date);
        saveButton = findViewById(R.id.button_post_save);
        progressBar.setVisibility(View.INVISIBLE);

        saveButton.setOnClickListener(this);
        addPhotoButton.setOnClickListener(this);
        storageReference = FirebaseStorage.getInstance().getReference();

        if(JournalApi.getInstance() != null){
            currentUserId = JournalApi.getInstance().getUserId();
            currentUserName = JournalApi.getInstance().getUsername();

            currentUserTextView.setText(currentUserName);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null){

                }else{

                }
            }
        };



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_post_save:
                // saveJournal
                saveJournal();
                break;
            case R.id.button_add_photo:


                // get image from gallery/phone
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;
        }
    }

    private void saveJournal() {
        String title = titleEditText.getText().toString().trim();
        String though = thoughtEditText.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(though) && imageUri != null){
            StorageReference filepath = storageReference   // journal_images / our_image.jpeg
                    .child("Journal_images")
                    .child("my_images_"+ Timestamp.now().getSeconds());  // my_images_356516351

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.INVISIBLE);

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();

                            //Todo:Create e journal object - model
                            Journal journal = new Journal();
                            journal.setTitle(title);
                            journal.setThough(though);
                            journal.setImageUrl(imageUrl);
                            journal.setTimeAdded(new Timestamp(new Date()));
                            journal.setUserName(currentUserName);
                            journal.setUserId(currentUserId);

                            // Todo: Invoke our collectionReference

                            collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(PostJournalActivity.this,JournalListActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PostJournalActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Todo: and save a journal instance
                        }
                    });



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData(); // we have the actual path to the image
                postImage.setImageURI(imageUri);//show image

            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseUser == null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}