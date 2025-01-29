package com.dev.resourcehub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser ;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText fullName, emailAddress, studentID, password, confirmPassword;
    private Button googleSignUpButton, signUpButton;
    private CheckBox termsCheckBox;
    private TextView alreadyHaveAccountText;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Firestore instance
    private GoogleSignInClient googleSignInClient; // Google Sign-In client
    private static final int RC_SIGN_IN = 9001; // Request code for Google Sign-In

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup); // Ensure this matches your layout file
        FirebaseApp.initializeApp(this);
               mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Use your Web Client ID
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize UI elements
        fullName = findViewById(R.id.fullName);
        emailAddress = findViewById(R.id.emailAddress);
        studentID = findViewById(R.id.studentID);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        googleSignUpButton = findViewById(R.id.googleSignUpButton);
        signUpButton = findViewById(R.id.signUpButton);
        termsCheckBox = findViewById(R.id.termsCheckBox);
        alreadyHaveAccountText = findViewById(R.id.alreadyHaveAccountText);

        // Google Sign-Up Button Click Listener
        googleSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        // Sign Up Button Click Listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser ();
            }
        });

        // Already Have Account Text Click Listener
        alreadyHaveAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class)); // Redirect to Login Activity
            }
        });
    }

    private void registerUser () {
        String email = emailAddress.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String name = fullName.getText().toString().trim();
        String studentId = studentID.getText().toString().trim();

        // Basic validation
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (studentId.isEmpty()) {
            Toast.makeText(this, "Please enter your student ID", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.isEmpty() || confirmPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(confirmPassword.getText().toString().trim())) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user with email and password
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User registration successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        storeUserData(user, name, studentId, "email",null);
                    } else {
                        // Log the error message with additional context
                        String errorMessage = "Registration failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error");
                        Log.d("Registration", errorMessage); // Log the error message
                        Toast.makeText(SignupActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Google Sign-In was successful, authenticate with Firebase
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser  user = mAuth.getCurrentUser ();
                            String photoUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null; // Get the photo URL
                            storeUserData(user, account.getDisplayName(), account.getEmail(), "google", photoUrl); // Pass the photo URL
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, "Google Sign-In failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (ApiException e) {
            // Google Sign-In failed
            int statusCode = e.getStatusCode();
            String errorMessage = "Google Sign-In failed: " + statusCode;

            // Log the error message with additional context
            Log.e("GoogleSignIn", errorMessage, e); // Log the exception with stack trace

            // Show a Toast message to the user
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void storeUserData(FirebaseUser  user, String name, String identifier, String method, String photoUrl) {
        // Create a new user object
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", identifier);
        userData.put("userId", user.getUid());
        userData.put("photoUrl", photoUrl); // Add the photo URL to the user data

        // Store user data in Firestore
        db.collection("users").document(user.getUid()).set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SignupActivity.this, "User  registered successfully", Toast.LENGTH_SHORT).show();
                    // Redirect to HomeActivity
                    startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignupActivity.this, "Error storing user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}