package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.recom.databinding.ActivityChangePictureBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ChangePicture extends AppCompatActivity {
    private ActivityChangePictureBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private final FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePictureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        Intent intent = getIntent();
        String image_type= intent.getStringExtra("type").toString();
        String image_path= intent.getStringExtra("image").toString();
        Uri fileUri = Uri.parse(image_path);

        //get user info
        if(firebaseUser == null){
            Toast.makeText(ChangePicture.this, "Something went wrong! User details are not available at the moment.", Toast.LENGTH_LONG).show();
        }
        else {
            showUserProfile(firebaseUser, image_type, fileUri);
        }

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBar2.setVisibility(View.VISIBLE);
                if(firebaseUser == null){
                    Toast.makeText(ChangePicture.this, "Something went wrong! User is not online at the moment.", Toast.LENGTH_LONG).show();
                }
                else {
                    uploadPic(fileUri, image_type); //upload the image to firebase
                }
            }
        });
    }

    //save image
    private void uploadPic(Uri fileUri, String image_type) {
        if(fileUri != null){
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(image_type).child(firebaseUser.getUid());
            //upload image
            storageReference.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if(image_type.equals("displayImage")){
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(uri).build();
                                firebaseUser.updateProfile(profileUpdates);
                            }
                            databaseReference = database.getReference("Users");
                            String downloadUri = uri.toString();
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put(image_type, downloadUri);

                            databaseReference.child(firebaseUser.getUid()).updateChildren(userMap);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChangePicture.this, "Something went wrong! User is not online at the moment.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    }

    private void showUserProfile(FirebaseUser firebaseUse, String image_type, Uri fileUri) {
        String UID = firebaseUser.getUid();

        //get the data from database
        DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user != null){
                    binding.fullnameTop.setText(firebaseUser.getDisplayName());
                    binding.emailTop.setText(firebaseUser.getEmail());
                    binding.textFullName.setText(user.fname + ' ' + user.mname + ' ' + user.lname);
                    binding.textEmail.setText(firebaseUser.getEmail());
                    binding.textPhone.setText(user.phonenum);
                    binding.textBirthdate.setText(user.dobirth);
                    binding.textAddress.setText(user.address);
                    if(user.verification.equals("No")){
                        binding.textVerification.setText("Not Verified");
                    }
                    else if(user.verification.equals("Yes")){
                        binding.textVerification.setText("Verified");
                    }

                    if(image_type.equals("displayImage")){
                        if(snapshot.hasChild("coverImage")){
                            String image = snapshot.child("coverImage").getValue().toString();
                            Picasso.get().load(image).into(binding.topBackground);
                        }
                    }

                    //set display/cover image
                    if(image_type.equals("displayImage")){
                        binding.profileImage.setImageURI(fileUri);
                    }
                    else if(image_type.equals("coverImage")){
                        Uri defaulturl = firebaseUser.getPhotoUrl();
                        Picasso.get().load(defaulturl).into(binding.profileImage);

                        binding.topBackground.setImageURI(fileUri);
                    }

                    binding.progressBar2.setVisibility(View.GONE);
                }
                else{
                    Toast.makeText(ChangePicture.this, "Something went wrong! User details are not available at the moment.", Toast.LENGTH_LONG).show();
                    binding.progressBar2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChangePicture.this, "Something went wrong! User details are not available at the moment.", Toast.LENGTH_LONG).show();
            }
        });
    }
}