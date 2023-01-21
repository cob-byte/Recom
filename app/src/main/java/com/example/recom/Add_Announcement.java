package com.example.recom;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recom.databinding.ActivityAddAnnouncementBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Add_Announcement extends AppCompatActivity {
    private ActivityAddAnnouncementBinding binding;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();
    private DatabaseReference databaseReference;
    private StorageReference imageRef;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAnnouncementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.BtnPostAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input values
                String TypeTitle = binding.TypeTitle.getText().toString();
                String TypeDescription = binding.TypeDescription.getText().toString();
                String TypeImageDesc = binding.TypeImageDesc.getText().toString();

                // Validate input values
                if (TypeTitle.isEmpty()) {
                    Toast.makeText(Add_Announcement.this, "Please enter a title", Toast.LENGTH_SHORT).show();

                } else if (TypeDescription.isEmpty()) {
                    Toast.makeText(Add_Announcement.this, "Please enter a body", Toast.LENGTH_SHORT).show();

                } else if (TypeImageDesc.isEmpty()) {
                    Toast.makeText(Add_Announcement.this, "Please enter a a short description for your image", Toast.LENGTH_SHORT).show();

                } else if (imageUri == null) {
                    Toast.makeText(Add_Announcement.this, "Please select an image", Toast.LENGTH_SHORT).show();

                } else {
                    databaseReference = database.getReference("Announcement");

                    // Upload image to Firebase Storage
                    imageRef = FirebaseStorage.getInstance().getReference("Announcement").child(databaseReference.push().getKey());
                    imageRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get download URL of image
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm MM/dd/yyyy");
                                        Date date = new Date();
                                        String currentDateTime = dateFormat.format(date);
                                        String image = "https://firebasestorage.googleapis.com/v0/b/recom-e0d64.appspot.com/o/Barangay.svg.png?alt=media&token=4919230c-83ba-45b9-a2f5-3557c29395d1";
                                        String downloadUri = uri.toString();
                                        Announcement announcement = new Announcement(TypeTitle, TypeDescription, TypeImageDesc, downloadUri, currentDateTime, firebaseUser.getDisplayName(), image);
                                        // Save announcement to database with image URL
                                        databaseReference.push().setValue(announcement).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(Add_Announcement.this, "Announcement created successfully!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Add_Announcement.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });

        binding.BtnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Builder with1 = ImagePicker.with(Add_Announcement.this);
                with1.crop(20, 15);
                with1.compress(1024); //Final image size will be less than 1 MB(Optional)
                with1.maxResultSize(1080, 1080); //Final image resolution will be less than 1080 x 1080(Optional)
                with1.createIntent(intent -> {
                    addImage.launch(intent);
                    return null;
                });
            }
        });
        binding.TypeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.AnnTitle.setText(binding.TypeTitle.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.TypeDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.AnnDescription.setText(binding.TypeDescription.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.TypeImageDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.ShortImageDesc.setText(binding.TypeImageDesc.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private ActivityResultLauncher<Intent> addImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();
                        Intent data = result.getData();
                        if(data != null && data.getData() != null && firebaseUser != null){
                            String type = "displayImage";
                            imageUri = data.getData();
                            Picasso.get().load(imageUri).into(binding.BtnAddImage);
                            Picasso.get().load(imageUri).into(binding.AnnImage);
                        }
                        else{
                            Toast.makeText(Add_Announcement.this, "Something went wrong! User is not online at the moment.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
}