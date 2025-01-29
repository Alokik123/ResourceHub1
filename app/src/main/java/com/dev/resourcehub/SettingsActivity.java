package com.dev.resourcehub;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private Button profilePictureButton;
    private Button editProfileButton;
    private Button policyButton;
    private Button dataPrefsButton;
    private Button themeChangeButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting); // Ensure this layout exists in your res/layout directory

        // Initialize buttons
        profilePictureButton = findViewById(R.id.profile_picture_button);
        editProfileButton = findViewById(R.id.edit_profile);
        policyButton = findViewById(R.id.policy);
        dataPrefsButton = findViewById(R.id.Data_Prefs);
        themeChangeButton = findViewById(R.id.Dark_Theme);
        logoutButton = findViewById(R.id.LogOut);

        // Set click listeners
        profilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ProfileActivity
                Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ProfileActivity and open the bio section
                Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
                intent.putExtra("openBioSection", true); // Pass a flag to open the bio section
                startActivity(intent);
            }
        });

        policyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to a dummy privacy policy website
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.example.com/privacy-policy")); // Replace with actual URL
                startActivity(intent);
            }
        });

        dataPrefsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to a dummy data preferences website
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.example.com/data-preferences")); // Replace with actual URL
                startActivity(intent);
            }
        });

        themeChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle between light and dark theme
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show confirmation dialog for logout
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("Logout")
                        .setMessage("Do you really want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Perform logout actions
                                logoutUser ();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void logoutUser () {
        // Clear user data from Firebase Firestore
        // Assuming you have a method to handle Firebase logout
        // FirebaseAuth.getInstance().signOut(); // Uncomment if using Firebase Auth

        // Redirect to SplashScreen
        Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}