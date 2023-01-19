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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
    private FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private Uri uriImage;
    private String UID;


    public Profile() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if(firebaseUser != null){
            binding.progressBar2.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        } else{
            Toast.makeText(getActivity(), "Something went wrong! User details are not available at the moment.", Toast.LENGTH_LONG).show();
            binding.progressBar2.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set view binding
        super.onCreate(savedInstanceState);
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        //change profile/cover pic
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

        //account settings
        binding.accountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseUser != null) {
                    Intent settings = new Intent(requireActivity(), Settings.class);
                    if(firebaseUser.getPhotoUrl() != null) {
                        String urlPass = firebaseUser.getPhotoUrl().toString();
                        settings.putExtra("photo", urlPass);
                    }
                    else{
                        settings.putExtra("photo", "none");
                    }
                    startActivity(settings);
                }
                else{
                    Toast.makeText(requireActivity(), "Something went wrong! User is not online at the moment.", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.getVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new getVerified());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
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
                        firebaseProfile = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();
                        Intent data = result.getData();
                        if(data != null && data.getData() != null && firebaseUser != null){
                            String type = "displayImage";
                            uriImage = data.getData();
                            Intent changeImage = new Intent(getActivity(), ChangePicture.class);
                            changeImage.putExtra("type", type);
                            changeImage.putExtra("image", uriImage.toString());
                            startActivity(changeImage);
                        }
                        else{
                            Toast.makeText(requireActivity(), "Something went wrong! User is not online at the moment.", Toast.LENGTH_LONG).show();
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
                        firebaseProfile = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();
                        if(data != null && data.getData() != null && firebaseUser != null){
                            String type1 = "coverImage";
                            uriImage = data.getData();
                            Intent changeCoverImage = new Intent(getActivity(), ChangePicture.class);
                            changeCoverImage.putExtra("type", type1);
                            changeCoverImage.putExtra("image", uriImage.toString());
                            startActivity(changeCoverImage);
                        }
                        else{
                            Toast.makeText(requireActivity(), "Something went wrong! User is not online at the moment.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

    private void showUserProfile(FirebaseUser firebaseUser) {
        UID = firebaseUser.getUid();

        //get the data from database
        databaseReference = database.getReference("Users");
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
                    if(user.userRole == 0){
                        binding.textVerification.setText("Not Verified");
                        binding.getVerified.setVisibility(View.VISIBLE);
                    }
                    else if(user.userRole == 1){
                        binding.textVerification.setText("Verified");
                        binding.getVerified.setVisibility(View.GONE);
                    }
                    else if(user.userRole == 2){
                        binding.textVerification.setText("Barangay Official");
                        binding.getVerified.setVisibility(View.GONE);
                    }
                    binding.progressBar2.setVisibility(View.GONE);

                    //Profile Photo
                    if(snapshot.hasChild("displayImage")){
                        String image = snapshot.child("displayImage").getValue().toString();
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