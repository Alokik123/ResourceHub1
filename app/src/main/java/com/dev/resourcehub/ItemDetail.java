package com.dev.resourcehub;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class ItemDetail extends AppCompatActivity {
    private FirebaseFirestore db;
    private ImageView itemImageView;
    private TextView titleTextView, conditionTextView, priceTextView, usernameTextView, phoneTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail); // Ensure this layout exists in your res/layout directory

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        itemImageView = findViewById(R.id.item_image); // Make sure to have an ImageView in your item_detail layout
        titleTextView = findViewById(R.id.title);
        conditionTextView = findViewById(R.id.description); // Add this TextView in your layout
        priceTextView = findViewById(R.id.price_value); // Add this TextView in your layout
        usernameTextView = findViewById(R.id.username);
        phoneTextView = findViewById(R.id.phone_number);

        // Get the document ID from the intent
        String documentId = getIntent().getStringExtra("documentId");

        // Fetch the item details from Firestore
        fetchItemDetails(documentId);
    }

    private void fetchItemDetails(String documentId) {
        // Get the image URL passed from HomeActivity
        String passedImageUrl = getIntent().getStringExtra("imageUrl");
        Log.d("ItemDetail", "Image URL passed from HomeActivity: " + passedImageUrl);

        // Use the passed image URL if it's not null or empty
        if (passedImageUrl != null && !passedImageUrl.isEmpty()) {
            try {
                Uri uri = Uri.parse(passedImageUrl);
                Log.d("ItemDetail", "Parsed URI: " + uri.toString());

                Glide.with(this)
                        .load(uri) // Use the passed image URL
                        .placeholder(R.drawable.placeholder) // Placeholder image
                        .error(R.drawable.book1) // Error image
                        .into(itemImageView);
            } catch (Exception e) {
                Log.e("ItemDetail", "Error parsing image URI: " + e.getMessage());
                itemImageView.setImageResource(R.drawable.placeholder); // Set a default image if URI is invalid
            }
        } else {
            Log.e("ItemDetail", "Image URL is null or empty");
            itemImageView.setImageResource(R.drawable.placeholder); // Set a default image if URL is invalid
        }

        // Fetch other details from Firestore
        db.collection("items_uploaded").document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String title = document.getString("title");
                            String condition = document.getString("condition");
                            String price = document.getString("price");
                            String uploaderName = document.getString("name");
                            String phoneNumber = document.getString("phone");

                            // Log the fetched values for debugging
                            Log.d("ItemDetail", "Title: " + title);
                            Log.d("ItemDetail", "Condition: " + condition);
                            Log.d("ItemDetail", "Price: " + price);
                            Log.d("ItemDetail", "Uploader Name: " + uploaderName);
                            Log.d("ItemDetail", "Phone Number: " + phoneNumber);

                            // Set the text fields
                            titleTextView.setText(title);
                            conditionTextView.setText(condition);
                            priceTextView.setText(price);
                            usernameTextView.setText(uploaderName);
                            phoneTextView.setText(phoneNumber);
                        } else {
                            Toast.makeText(ItemDetail.this, "Item not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("ItemDetail", "Error fetching item details: " + task.getException());
                        Toast.makeText(ItemDetail.this, "Error fetching item details", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}