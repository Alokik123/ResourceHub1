package com.dev.resourcehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide; // Make sure to add Glide dependency for image loading
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profilePicture;
    private TextView profileName;
    private TextView profileUsername; // This will hold the email
    private TextView profileBio;
    private Button editProfileButton;
    private Button viewHistoryButton;

    private FirebaseFirestore db;
    private String userId; // Assume you have the user ID from Firebase Auth
    private FirebaseAuth mAuth; // Firebase Auth instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile); // Ensure this layout exists

        // Initialize UI elements
        profilePicture = findViewById(R.id.profile_picture);
        profileName = findViewById(R.id.profile_name);
        profileUsername = findViewById(R.id.profile_username); // This will display the email
//        profileBio = findViewById(R.id.profile_bio);
//        editProfileButton = findViewById(R.id.edit_profile_button);
        viewHistoryButton = findViewById(R.id.view_history_button);

        // Initialize Firestore and Firebase Auth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser ().getUid(); // Get the current user's ID

        // Fetch user data from Firestore
        fetchUserProfile();

        // Set up the edit profile button
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditProfile();
            }
        });

        // Set up the view history button
        viewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchUserProfile() {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email"); // Fetch the email
                String bio = documentSnapshot.getString("bio");
                String profileImageUrl = documentSnapshot.getString("photoUrl"); // Fetch the profile image URL

                // Set the retrieved values to the UI elements
                profileName.setText(name);
                profileUsername.setText(email); // Set the email to the username TextView
                profileBio.setText(bio);

                // Load the profile image using Glide
                Glide.with(this)
                        .load(profileImageUrl)
                        .into(profilePicture);
            }
        }).addOnFailureListener(e -> {
            // Handle the error
            Toast.makeText(ProfileActivity.this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void toggleEditProfile() {
        if (profileBio.isEnabled()) {
            // Save the edited bio
            String editedBio = profileBio.getText().toString();
            saveUserProfile(editedBio);
            profileBio.setEnabled(false);
            editProfileButton.setText("Edit Profile");
        } else {
            // Enable editing
            profileBio.setEnabled(true);
            editProfileButton.setText("Save Profile");
        }
    }

    private void saveUserProfile(String bio) {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.update("bio", bio).addOnSuccessListener(aVoid -> {
            // Successfully updated
            Toast.makeText(ProfileActivity.this, "Bio updated successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // Handle the error
            Toast.makeText(ProfileActivity .this, "Error updating bio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}