package com.dev.resourcehub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout llHome, llProfile, llSettings;
    private Button btnBooks, btnTools, btnStationery;

    private ImageView imgHome, imgProfile, imgSettings;
    private LinearLayout newArrivalsContainer, featuredItemsContainer; // Containers for new arrivals and featured items
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen); // Make sure this matches your layout file

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        newArrivalsContainer = findViewById(R.id.newArrivalsContainer); // Assuming you have a container for new arrivals
        featuredItemsContainer = findViewById(R.id.featuredItemsContainer); // Assuming you have a container for featured items
        imgHome = findViewById(R.id.img_home);
        imgProfile = findViewById(R.id.img_profile);
        imgSettings = findViewById(R.id.img_settings);
        llHome = findViewById(R.id.ll_home);
        llProfile = findViewById(R.id.ll_profile);
        llSettings = findViewById(R.id.ll_settings);

        // Set click listeners for bottom navigation
        llHome.setOnClickListener(v -> selectTab("Home", HomeActivity.class));
        llProfile.setOnClickListener(v -> selectTab("Profile", SearchActivity.class)); // Replace with your ProfileActivity
        llSettings.setOnClickListener(v -> selectTab("Settings", NewListingActivity.class)); // Replace with your SettingsActivity
        btnBooks = findViewById(R.id.btn_books);
        btnTools = findViewById(R.id.btn_tools);
        btnStationery = findViewById(R.id.btn_stationery);
        btnBooks.setOnClickListener(v -> selectCategory(btnBooks));
        btnTools.setOnClickListener(v -> selectCategory(btnTools));
        btnStationery.setOnClickListener(v -> selectCategory(btnStationery));

        fetchLatestListings();
    }
    private void selectCategory(Button selectedButton) {
        // Reset background colors for all buttons
        resetCategoryButtonBackgrounds();

        // Change the background color of the selected button
        selectedButton.setBackgroundColor(getResources().getColor(R.color.blue)); // Change to your desired color
    }

    private void resetCategoryButtonBackgrounds() {
        // Reset to default background color for all buttons
        btnBooks.setBackgroundColor(getResources().getColor(android.R.color.transparent)); // Reset to default
        btnTools.setBackgroundColor(getResources().getColor(android.R.color.transparent)); // Reset to default
        btnStationery.setBackgroundColor(getResources().getColor(android.R.color.transparent)); // Reset to default
    }

    private void selectTab(String tabName, Class<?> activityClass) {
        // Reset background colors
        resetTabBackgrounds();

        // Change the background color of the selected tab
        switch (tabName) {
            case "Home":
                llHome.setBackgroundColor(getResources().getColor(R.color.blue)); // Change to your desired color
                break;
            case "Profile":
                llProfile.setBackgroundColor(getResources().getColor(R.color.blue)); // Change to your desired color
                break;
            case "Settings":
                llSettings.setBackgroundColor(getResources().getColor(R.color.blue)); // Change to your desired color
                break;
        }

        // Start the corresponding activity
        Intent intent = new Intent(HomeActivity.this, activityClass);
        startActivity(intent);
    }

    private void resetTabBackgrounds() {
        llHome.setBackgroundColor(getResources().getColor(android.R.color.transparent)); // Reset to default
        llProfile.setBackgroundColor(getResources().getColor(android.R.color.transparent)); // Reset to default
        llSettings.setBackgroundColor(getResources().getColor(android.R.color.transparent)); // Reset to default
    }
    private void fetchLatestListings() {
        db.collection("items_uploaded") // Your collection name
                .orderBy("timestamp", Query.Direction.DESCENDING) // Assuming you have a timestamp field
                .limit(10) // Limit to the latest 10 items
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            // Show the containers layout
                            findViewById(R.id.containersLayout).setVisibility(View.VISIBLE);
                            newArrivalsContainer.removeAllViews();
                            featuredItemsContainer.removeAllViews();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String title = document.getString("title");
                                String condition = document.getString("condition");
                                String price = document.getString("price");
                                String imageUrl = document.getString("imageuri"); // Use the correct field name

                                // Check if any of the retrieved fields are null
                                if (title != null && condition != null && price != null && imageUrl != null) {
                                    // Create a new card view for each item
                                    addNewArrivalCard(title, condition, price, imageUrl, document.getId());
                                    addFeaturedItemCard(title, condition, price, imageUrl, document.getId()); // Add to featured items as well
                                } else {
                                    // Log or handle the case where some fields are null
                                    Log.e("HomeActivity", "Document " + document.getId() + " has null fields.");
                                }
                            }
                        } else {
                            // Hide the containers layout if no results found
                            findViewById(R.id.containersLayout).setVisibility(View.GONE);
                        }
                    } else {
                        // Handle the error case
                        Toast.makeText(HomeActivity.this, "Error getting documents: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void addNewArrivalCard(String title, String condition, String price, String imageUrl, String itemId) {
        // Inflate the card view layout for new arrivals
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.item_card_view, newArrivalsContainer, false);

        // Set the data in the card view
        TextView titleView = cardView.findViewById(R.id.card_title);
        TextView conditionView = cardView.findViewById(R.id.card_condition);
        TextView priceView = cardView.findViewById(R.id.card_price);
        ImageView imageView = cardView.findViewById(R.id.card_image);

        titleView.setText(title);
        conditionView.setText(condition);
        priceView.setText(price);

        // Load the image using Glide
        Glide.with(this).load(imageUrl).into(imageView);

        // Set a click listener to handle clicks on the card
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, NewListingActivity.class);
            intent.putExtra("itemId", itemId); // Pass the item ID to the detail activity
            startActivity(intent);
        });

        // Add the card view to the new arrivals container
        newArrivalsContainer.addView(cardView);
    }

    private void addFeaturedItemCard(String title, String condition, String price, String imageUrl, String itemId) {
        // Inflate the card view layout for featured items
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.item_card_view, featuredItemsContainer, false);

        // Set the data in the card view
        TextView titleView = cardView.findViewById(R.id.card_title);
        TextView conditionView = cardView.findViewById(R.id.card_condition);
        TextView priceView = cardView.findViewById(R.id.card_price);
        ImageView imageView = cardView.findViewById(R.id.card_image);

        titleView.setText(title);
        conditionView.setText(condition);
        priceView.setText(price);

        // Load the image using Glide
        Glide.with(this).load(imageUrl).into(imageView);

        // Set a click listener to handle clicks on the card
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, NewListingActivity.class);
            intent.putExtra("itemId", itemId); // Pass the item ID to the detail activity
            startActivity(intent);
        });

        // Add the card view to the featured items container
        featuredItemsContainer.addView(cardView);
    }
}