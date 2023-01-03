package com.example.recom;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.recom.databinding.FragmentProfileBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Profile extends Fragment {
    private FragmentProfileBinding binding;
    private FirebaseAuth firebaseProfile;
    private StorageReference storageReference;
    private Uri uriImage;
    private String UID;


    public Profile() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
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
                    binding.progressBar2.setVisibility(View.GONE);

                    //Profile Photo
                    if(snapshot.hasChild("profileImage")){
                        Uri dpurl = firebaseUser.getPhotoUrl();
                        Picasso.get().load(dpurl).into(binding.profileImage);
                    }

                    //Cover Photo
                    if(snapshot.hasChild("coverImage")){
                        String image = snapshot.child("coverImage").getValue().toString();
                        Picasso.get().load(image).into(binding.topBackground);
                    }

                }
                else{
                    Toast.makeText(getActivity(), "Something went wrong! User details are not available at the moment.", Toast.LENGTH_LONG).show();
                    binding.progressBar2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something went wrong! User details are not available at the moment.", Toast.LENGTH_LONG).show();
                binding.progressBar2.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set view binding
        super.onCreate(savedInstanceState);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.progressBar2.setVisibility(View.VISIBLE);
        firebaseProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(getActivity(), "Something went wrong! User details are not available at the moment.", Toast.LENGTH_LONG).show();
        }
        else {
            showUserProfile(firebaseUser);
        }

        Uri uri = firebaseUser.getPhotoUrl();

        //set users dp in imageview
        if(uri != null){
            Picasso.get().load(uri).into(binding.profileImage);
        }

        //change profile pic
        binding.changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(requireActivity(), v);
                popupMenu.getMenuInflater().inflate(R.menu.choice, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.cProfilePic:
                                ImagePicker.Builder with = ImagePicker.with(requireActivity());
                                with.crop(1, 1);
                                with.compress(1024); //Final image size will be less than 1 MB(Optional)
                                with.maxResultSize(1080, 1080); //Final image resolution will be less than 1080 x 1080(Optional)
                                with.createIntent(intent -> {
                                    changeProfileImageLauncher.launch(intent);
                                    return null;
                                });
                                break;
                            case R.id.cCoverPic:
                                ImagePicker.Builder with1 = ImagePicker.with(requireActivity());
                                with1.crop(17, 10);
                                with1.compress(1024); //Final image size will be less than 1 MB(Optional)
                                with1.maxResultSize(1080, 1080); //Final image resolution will be less than 1080 x 1080(Optional)
                                with1.createIntent(intent -> {
                                    changeCoverImageLauncher.launch(intent);
                                    return null;
                                });
                                break;
                            default:
                                return false;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        //change cover photo

        //signout
        binding.btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseProfile.signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
                requireActivity().finish();
            }
        });

        return binding.getRoot();
    }

    private ActivityResultLauncher<Intent> changeProfileImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if(data != null && data.getData() != null){
                            String type = "displayImage";
                            uriImage = data.getData();
                            Intent changeImage = new Intent(getActivity(), ChangePicture.class);
                            changeImage.putExtra("type", type);
                            changeImage.putExtra("image", uriImage.toString());
                            startActivity(changeImage);
                        }
                    }
                }
            });

    private ActivityResultLauncher<Intent> changeCoverImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if(data != null && data.getData() != null){
                            String type1 = "coverImage";
                            uriImage = data.getData();
                            Intent changeCoverImage = new Intent(getActivity(), ChangePicture.class);
                            changeCoverImage.putExtra("type", type1);
                            changeCoverImage.putExtra("image", uriImage.toString());
                            startActivity(changeCoverImage);
                        }
                    }
                }
            });

    private void showUserProfile(FirebaseUser firebaseUser) {
        UID = firebaseUser.getUid();

        //get the data from database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
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
                    binding.progressBar2.setVisibility(View.GONE);

                    //Profile Photo
                    if(snapshot.hasChild("profileImage")){
                        String image = snapshot.child("profileImage").getValue().toString();
                        Picasso.get().load(image).into(binding.profileImage);
                    }

                    //Cover Photo
                    if(snapshot.hasChild("coverImage")){
                        String image = snapshot.child("coverImage").getValue().toString();
                        Picasso.get().load(image).into(binding.topBackground);
                    }

                }
                else{
                    Toast.makeText(getActivity(), "Something went wrong! User details are not available at the moment.", Toast.LENGTH_LONG).show();
                    binding.progressBar2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something went wrong! User details are not available at the moment.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.choice, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}