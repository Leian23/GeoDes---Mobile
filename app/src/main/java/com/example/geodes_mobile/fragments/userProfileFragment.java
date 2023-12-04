package com.example.geodes_mobile.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.geodes_mobile.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class userProfileFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userId = mAuth.getCurrentUser().getUid();
    private View rootView; // Add this field

    // Constants for image selection
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    private final RequestOptions glideOptions = new RequestOptions().transform(new CircleCrop());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragments_userprofile, container, false);

        fetchUserData(rootView);

        ImageButton menuButton = rootView.findViewById(R.id.menu_button);
        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        Button applyChangesButton = rootView.findViewById(R.id.applyChangesButton);
        applyChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserData(rootView);
                uploadProfilePicture(); // Call the function to upload the profile picture
            }
        });

        Button changePasswordButton = rootView.findViewById(R.id.ChangePasswordButt);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangePasswordDialog();
            }
        });

        // Add this button setup
        Button changeProfileButton = rootView.findViewById(R.id.changeProfileButton);
        changeProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageFromGallery(view);
            }
        });

        return rootView;
    }

    private void fetchUserData(View rootView) {
        EditText firstNameEditText = rootView.findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = rootView.findViewById(R.id.lastNameEditText);
        EditText emailEditText = rootView.findViewById(R.id.emailEditText);
        ImageView profileImageView = rootView.findViewById(R.id.profileImageView);

        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String firstName = documentSnapshot.getString("firstName");
                    String lastName = documentSnapshot.getString("lastName");
                    String email = documentSnapshot.getString("email");
                    String profilePictureUrl = documentSnapshot.getString("profilePictureUrl");

                    firstNameEditText.setText(firstName);
                    lastNameEditText.setText(lastName);
                    emailEditText.setText(email);

                    // Check if the user has a custom profile picture
                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                        // Load the custom profile picture using Glide
                        Glide.with(requireContext())
                                .load(profilePictureUrl)
                                .apply(glideOptions)
                                .into(profileImageView);
                    } else {
                        // Load the default profile picture (you may need to replace the "default_picture_url" with the actual URL of your default picture)
                        Glide.with(requireContext())
                                .load("default_picture_url")
                                .apply(glideOptions)
                                .into(profileImageView);
                    }
                }
            }
        });
    }


    private void updateUserData(View rootView) {
        EditText firstNameEditText = rootView.findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = rootView.findViewById(R.id.lastNameEditText);
        EditText emailEditText = rootView.findViewById(R.id.emailEditText);

        String updatedFirstName = firstNameEditText.getText().toString();
        String updatedLastName = lastNameEditText.getText().toString();
        String updatedEmail = emailEditText.getText().toString();

        DocumentReference userRef = db.collection("users").document(userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", updatedFirstName);
        userData.put("lastName", updatedLastName);
        userData.put("email", updatedEmail);

        userRef.set(userData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "User data updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to update user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.password_dialog, null);

        EditText newPasswordEditText = dialogView.findViewById(R.id.newPassword);
        EditText confirmPasswordEditText = dialogView.findViewById(R.id.confirmPassword);
        EditText oldPasswordEditText = dialogView.findViewById(R.id.oldPassword);

        builder.setView(dialogView)
                .setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String newPassword = newPasswordEditText.getText().toString();
                        String confirmPassword = confirmPasswordEditText.getText().toString();
                        String oldPassword = oldPasswordEditText.getText().toString();

                        if (!newPassword.isEmpty() && !confirmPassword.isEmpty() && !oldPassword.isEmpty()) {
                            if (newPassword.trim().equals(confirmPassword.trim())) {
                                changeUserPassword(newPassword, oldPassword);

                            } else {
                                Toast.makeText(getContext(), "New password and confirm password do not match", Toast.LENGTH_SHORT).show();
                                Log.d("PasswordComparison", "newPassword: '" + newPassword + "', length: " + newPassword.length());
                                Log.d("PasswordComparison", "confirmPassword: '" + confirmPassword + "', length: " + confirmPassword.length());
                            }
                        } else {
                            Toast.makeText(getContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changeUserPassword(String newPassword, String oldPassword) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

            user.reauthenticate(credential)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Reauthentication successful, proceed to change password
                            user.updatePassword(newPassword)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            String errorMessage = e.getMessage();
                                            Log.e("PasswordChange", "Failed to change password: " + errorMessage);
                                            Toast.makeText(getContext(), "Failed to change password: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Reauthentication failed
                            String errorMessage = e.getMessage();
                            Log.e("PasswordChange", "Failed to reauthenticate: " + errorMessage);
                            // Handle the error, e.g., display a message to the user
                            Toast.makeText(getContext(), "Reauthentication failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Log.e("PasswordChange", "User not authenticated");
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectImageFromGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Set the selected image to the ImageView
            ImageView profileImageView = rootView.findViewById(R.id.profileImageView);
            profileImageView.setImageURI(selectedImageUri);

            // Log the selected image URI
            Log.d("ImageUpload", "Selected Image URI: " + selectedImageUri);

            // Call the uploadProfilePicture method after the image is set to ImageView
            uploadProfilePicture();
        }
    }




    private void uploadProfilePicture() {
        if (selectedImageUri != null) {
            Log.d("ImageUpload", "Uploading image...");
            // Create a storage reference
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("profilePictures")
                    .child(userId + ".jpg");

            // Upload the file
            storageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("ImageUpload", "Image uploaded successfully");
                        // Image uploaded successfully, update the user's profile picture URL in Firestore
                        updateUserProfilePicture(storageRef);
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error
                        Log.e("ImageUpload", "Failed to upload image: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        Log.d("ImageUpload", "Upload progress: " + progress + "%");
                    });
        }
    }




    private void updateUserProfilePicture(StorageReference storageRef) {
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Update the user's profile picture URL in Firestore
            DocumentReference userRef = db.collection("users").document(userId);

            Map<String, Object> userData = new HashMap<>();
            userData.put("profilePictureUrl", uri.toString());

            userRef.set(userData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ImageUpload", "User profile picture URL updated successfully");

                        // Load the updated profile picture into the ImageView using Glide
                        ImageView profileImageView = rootView.findViewById(R.id.profileImageView);

                        // Apply Glide transformation to make the image circular
                        Glide.with(requireContext())
                                .load(uri.toString())
                                .transform(new CircleCrop())  // This applies the circular transformation
                                .into(profileImageView);

                        Toast.makeText(getContext(), "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ImageUpload", "Failed to update profile picture: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                    });
        });
    }





}
