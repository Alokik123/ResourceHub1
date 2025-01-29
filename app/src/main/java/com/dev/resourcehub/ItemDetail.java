package com.dev.resourcehub;

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
    private TextView titleTextView, conditionTextView, priceTextView, usernameTextView, locationTextView;

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
        usernameTextView = findViewById(R.id.username); // Add this TextView in your layout
        locationTextView = findViewById(R.id.location); // Add this TextView in your layout

        // Get the document ID from the intent
        String documentId = getIntent().getStringExtra("documentId");

        // Fetch the item details from Firestore
        fetchItemDetails(documentId);
    }

    private void fetchItemDetails(String documentId) {
        db.collection("items_uploaded").document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String title = document.getString("title");
                            String imageUrl = document.getString("imageUri");
                            String condition = document.getString("condition");
                            String price = document.getString("price");
                            String uploaderName = document.getString("uploaderName");
                            String location = document.getString("location");

                            // Set the data to the views
                            titleTextView.setText(title);
                            conditionTextView.setText(condition);
                            priceTextView.setText(price);
                            usernameTextView.setText(uploaderName);
                            locationTextView.setText(location);
                            Glide.with(this).load(imageUrl).into(itemImageView);
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