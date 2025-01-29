package com.dev.resourcehub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout llHome, llProfile, llSettings;
    private Button btnBooks, btnTools, btnStationery;

    private ImageView imgbook, imgProfile, imgSettings;
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
        newArrivalsContainer = findViewById(R.id.newArrivalsContainer);
        featuredItemsContainer = findViewById(R.id.featuredItemsContainer);
        imgbook = findViewById(R.id.calculus);
        imgProfile = findViewById(R.id.img_profile);
        imgSettings = findViewById(R.id.settingsIcon1);

        imgSettings.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        llHome = findViewById(R.id.ll_home);
        llProfile = findViewById(R.id.ll_profile);
        llSettings = findViewById(R.id.ll_settings);

        // Set click listeners for bottom navigation
        llHome.setOnClickListener(v -> selectTab("Home", HomeActivity.class));
        llProfile.setOnClickListener(v -> selectTab("Search", SearchActivity.class)); // Replace with your ProfileActivity
        llSettings.setOnClickListener(v -> selectTab("Profile", NewListingActivity.class)); // Replace with your SettingsActivity
        btnBooks = findViewById(R.id.btn_books);
        btnTools = findViewById(R.id.btn_tools);
        btnStationery = findViewById(R.id.btn_stationery);
        btnBooks.setOnClickListener(v -> selectCategory(btnBooks));
        btnTools.setOnClickListener(v -> selectCategory(btnTools));
        btnStationery.setOnClickListener(v -> selectCategory(btnStationery));
        imgbook.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ItemDetail.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchNewArrivals(); // Fetch new arrivals every time the activity is resumed
    }

    private void fetchNewArrivals() {
        db.collection("items_uploaded")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Assuming you have a timestamp field to order by
                .limit(10) // Limit to the latest 10 items
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        newArrivalsContainer.removeAllViews(); // Clear previous items
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title"); // Ensure this field exists
                            String uploaderName = document.getString("uploaderName"); // Ensure this field exists
                            String imageUrl = document.getString("imageUri"); // Ensure this field exists
                            String category = document.getString("category"); // Ensure this field exists
                            String documentId = document.getId(); // Get the document ID

                            // Log the fetched values for debugging
                            Log.d("FetchNewArrivals", "Title: " + title);
                            Log.d("FetchNewArrivals", "Uploader Name: " + uploaderName);
                            Log.d("FetchNewArrivals", "Image URL: " + imageUrl);
                            Log.d("FetchNewArrivals", "Category: " + category);

                            // Call the method with the correct parameters
                            addNewArrivalItem(title, uploaderName, imageUrl, category, documentId);
                        }
                        Toast.makeText(HomeActivity.this, "New arrivals fetched successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("HomeActivity", "Error fetching new arrivals: " + task.getException());
                        Toast.makeText(HomeActivity.this, "Error fetching new arrivals: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addNewArrivalItem(String title, String uploaderName, String imageUrl, String category, String documentId) {
        // Log the values being added
        Log.e("NewArrivalItem", "Title: " + title);
        Log.d("NewArrivalItem", "Uploader Name: " + uploaderName);
        Log.d("NewArrivalItem", "Image URL: " + imageUrl);
        Log.d("NewArrivalItem", "Category: " + category);

        // Create a new LinearLayout for the item
        LinearLayout itemLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Set width to match parent
                LinearLayout.LayoutParams.WRAP_CONTENT); // Height wraps content
        layoutParams.setMargins(0, 0, 0, 16); // Set bottom margin to create space between items
        itemLayout.setLayoutParams(layoutParams);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setBackgroundResource(R.drawable.border_background); // Set background with border
        itemLayout.setPadding(16, 16, 16, 16); // Add padding for better spacing

        // Set OnClickListener to navigate to ItemDetail
        itemLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ItemDetail.class);
            intent.putExtra("documentId", documentId); // Pass the document ID
            startActivity(intent);
        });

        // Create and set up the ImageView
        ImageView itemImageView = new ImageView(this);
        itemImageView.setLayoutParams(new LinearLayout.LayoutParams(
                200,
                100));
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.book1) // Use book1.jpg as the placeholder
                .error(R.drawable.notificationicon) // Optional: Error image if loading fails
                .into(itemImageView);
        itemImageView.setContentDescription("Book Cover");

        // Create and set up the title TextView
        TextView titleTextView = new TextView(this);
        titleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Set width to match parent for centering
                LinearLayout.LayoutParams.WRAP_CONTENT));
        titleTextView.setText(title); // Set the title text
        titleTextView.setTextColor(getResources().getColor(android.R.color.black)); // Set text color
        titleTextView.setTextSize(16);
        titleTextView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleTextView.setPadding(0, 8, 0, 0); // Add some padding above the title
        titleTextView.setGravity(Gravity.CENTER); // Center the text

        // Add the ImageView and TextViews to the item layout
        itemLayout.addView(itemImageView);
        itemLayout.addView(titleTextView); // Add the title TextView next

        // Add the item layout to the new arrivals container
        newArrivalsContainer.addView(itemLayout);
    }
    private void selectCategory(Button selectedButton) {
        // Reset background colors for all buttons
        resetCategoryButtonBackgrounds();

        // Change the background color of the selected button
        selectedButton.setSelected(true); // Mark the button as selected
    }

    private void resetCategoryButtonBackgrounds() {
        // Reset to default background color for all buttons
        btnBooks.setSelected(false);
        btnTools.setSelected(false);
        btnStationery.setSelected(false);
    }

    private void selectTab(String tabName, Class<?> activityClass) {
        // Reset background colors
        resetTabBackgrounds();

        // Change the background of the selected tab
        switch (tabName) {
            case "Home":
                llHome.setBackgroundResource(R.drawable.rounded_blue_background); // Set the rounded background
                break;
            case "Profile":
                llProfile.setBackgroundResource(R.drawable.rounded_blue_background); // Set the rounded background
                break;
            case "Settings":
                llSettings.setBackgroundResource(R.drawable.rounded_blue_background); // Set the rounded background
                break;
        }

        // Start the corresponding activity
        llSettings.postDelayed(() -> {
            Intent intent = new Intent(HomeActivity.this, activityClass);
            startActivity(intent);
        }, 200); // Delay for 200 milliseconds
    }

    private void resetTabBackgrounds() {
        llHome.setBackgroundColor(getResources().getColor(android.R.color.transparent)); // Reset to default
        llProfile.setBackgroundColor(getResources().getColor(android.R.color.transparent)); // Reset to default
        llSettings.setBackgroundColor(getResources().getColor(android.R.color.transparent)); // Reset to default
    }
}