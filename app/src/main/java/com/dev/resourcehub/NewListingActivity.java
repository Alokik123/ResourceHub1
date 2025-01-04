package com.dev.resourcehub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NewListingActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput, priceInput;
    private Spinner categorySpinner;
    private Button newButton, usedButton, listButton;
    private ImageView uploadImage;
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseFirestore db;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_listing); // Make sure this matches your layout file

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        titleInput = findViewById(R.id.title_input);
        descriptionInput = findViewById(R.id.description_input);
        priceInput = findViewById(R.id.price_input);
        categorySpinner = findViewById(R.id.category_spinner);
        newButton = findViewById(R.id.new_button);
        usedButton = findViewById(R.id.used_button);
        listButton = findViewById(R.id.list_button);
        uploadImage=findViewById(R.id.upload_image);
        uploadImage.setOnClickListener(v -> openFileChooser());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        listButton.setOnClickListener(v -> uploadListing());
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // Get the image URI
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                uploadImage.setImageBitmap(bitmap); // Display the selected image
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadListing() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String price = priceInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : ""; // Get selected category

        // Basic validation
        if (title.isEmpty() || description.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new listing object
        Map<String, Object> listing = new HashMap<>();
        listing.put("title", title);
        listing.put("description", description);
        listing.put("category", category);
        listing.put("price", price);
        listing.put("condition", newButton.isSelected() ? "New" : "Used"); // Determine condition based on button selection
        listing.put("imageUrl", ""); // Add logic to upload image and get URL

        db.collection("items_uploaded") // Replace with your collection name
                .add(listing)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(NewListingActivity.this, "Listing uploaded successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NewListingActivity.this, HomeActivity.class)); // Redirect to Home Activity
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NewListingActivity.this, "Error uploading listing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}