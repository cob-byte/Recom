package com.example.recom;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.recom.databinding.FragmentGetVerifiedBinding;
import com.example.recom.databinding.FragmentProfileBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class getVerified extends Fragment {
    private FragmentGetVerifiedBinding binding;
    private FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private StorageReference storageReference, storageReference1;
    private Uri votersCert, brgyCert;

    public getVerified() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set view binding
        super.onCreate(savedInstanceState);
        binding = FragmentGetVerifiedBinding.inflate(inflater, container, false);

        binding.btnVID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Builder VC = ImagePicker.with(requireActivity());
                VC.compress(1024); //Final image size will be less than 1 MB(Optional)
                VC.maxResultSize(1080, 1080); //Final image resolution will be less than 1080 x 1080(Optional)
                VC.createIntent(intent -> {
                    votersCertLauncher.launch(intent);
                    return null;
                });
            }
        });

        binding.btnBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Builder BC = ImagePicker.with(requireActivity());
                BC.compress(1024); //Final image size will be less than 1 MB(Optional)
                BC.maxResultSize(1080, 1080); //Final image resolution will be less than 1080 x 1080(Optional)
                BC.createIntent(intent -> {
                    brgyCertLaunch.launch(intent);
                    return null;
                });
            }
        });

        binding.submitAPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(votersCert == null || votersCert.toString().isEmpty()){
                    Toast.makeText(requireActivity(), "Please choose a file for voter's certificate.!", Toast.LENGTH_SHORT).show();
                    binding.btnVID.setError("Voter's Certificate is required!");
                    binding.btnVID.requestFocus();
                }
                else if(brgyCert == null || brgyCert.toString().isEmpty()){
                    Toast.makeText(requireActivity(), "Please choose a file for barangay certificate.!", Toast.LENGTH_SHORT).show();
                    binding.btnVID.setError("Barangay Certificate is required!");
                    binding.btnVID.requestFocus();
                }
                else{
                    new AlertDialog.Builder(requireActivity())
                            .setMessage("Are you sure you want to proceed with the following documents?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    uploadDocuments();
                                }
                            })
                            .setNegativeButton("No",null)
                            .show();
                }
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        return binding.getRoot();
    }

    private void uploadDocuments() {
        storageReference = FirebaseStorage.getInstance().getReference("pendingVerify").child(firebaseUser.getUid()).child("votersCert");
        //upload image
        storageReference.putFile(votersCert).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        databaseReference = database.getReference("pendingVerify");
                        String downloadUri = uri.toString();
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("votersCert", downloadUri);

                        databaseReference.child(firebaseUser.getUid()).updateChildren(userMap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireActivity(), "Something went wrong! User is not online at the moment.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        storageReference1 = FirebaseStorage.getInstance().getReference("pendingVerify").child(firebaseUser.getUid()).child("brgyCert");
        //upload image
        storageReference1.putFile(brgyCert).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        databaseReference = database.getReference("pendingVerify");
                        String downloadUri = uri.toString();
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("brgyCert", downloadUri);
                        userMap.put("name", firebaseUser.getDisplayName());
                        userMap.put("uid", firebaseUser.getUid());
                        userMap.put("photourl", firebaseUser.getPhotoUrl());

                        databaseReference.child(firebaseUser.getUid()).updateChildren(userMap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireActivity(), "Something went wrong! User is not online at the moment.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        getParentFragmentManager().popBackStack();;
        Toast.makeText(requireActivity(), "Please wait for confirmation.", Toast.LENGTH_SHORT).show();
    }

    private final ActivityResultLauncher<Intent> votersCertLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        firebaseProfile = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();
                        Intent data = result.getData();
                        if(data != null && data.getData() != null && firebaseUser != null){
                            votersCert = data.getData();
                            Picasso.get().load(votersCert).into(binding.imageVCert);
                            binding.btnVID.setText("CHANGE FILE");
                            binding.textPreview1.setText("PREVIEW");
                        }
                        else{
                            Toast.makeText(requireActivity(), "Something went wrong! User is not online at the moment.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

    private final ActivityResultLauncher<Intent> brgyCertLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        firebaseProfile = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();
                        Intent data = result.getData();
                        if(data != null && data.getData() != null && firebaseUser != null){
                            brgyCert = data.getData();
                            Picasso.get().load(brgyCert).into(binding.imageBCert);
                            binding.btnBC.setText("CHANGE FILE");
                            binding.textPreview2.setText("PREVIEW");
                        }
                        else{
                            Toast.makeText(requireActivity(), "Something went wrong! User is not online at the moment.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
}