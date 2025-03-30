package com.dev.resourcehub;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText; // Import EditText
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide; // Import Glide
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private LinearLayout searchResultsContainer;
    private EditText etSearch;
    private FirebaseFirestore db;
    private List<QueryDocumentSnapshot> allItems; // List to hold all items

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchscreen); // Make sure this matches your layout file

        db = FirebaseFirestore.getInstance();

        etSearch = findViewById(R.id.et_search);
        searchResultsContainer = findViewById(R.id.searchResultsContainer);

        // Load all items initially
        loadAllItems();

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchTerm = etSearch.getText().toString().trim();
                Log.d("SearchActivity", "Search term entered: " + searchTerm); // Log the search term
                if (!searchTerm.isEmpty()) {
                    filterItems(searchTerm); // Call the filter method
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                }
                return true; // Return true to indicate the action was handled
            }
            return false; // Return false for other actions
        });
    }

    private void loadAllItems() {
        // Clear previous results
        searchResultsContainer.removeAllViews();

        // Fetch all items from Firestore
        db.collection("items_uploaded") // Replace with your collection name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allItems = new ArrayList<>(); // Initialize the list
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            allItems.add(document); // Add each document to the list
                            String name = document.getString("name");
                            String uploaderName = document.getString("uploaderName");
                            String imageUrl = document.getString("imageUri");
                            String documentId = document.getId();

                            // Create a new card view for each item
                            addSearchResultItem(name, uploaderName, imageUrl, documentId);
                        }
                    } else {
                        Toast.makeText(SearchActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterItems(String searchTerm) {
        // Clear previous results
        searchResultsContainer.removeAllViews();

        // Log the search term
        Log.d("SearchActivity", "Filtering for: " + searchTerm); // Ensure this log appears

        boolean itemsFound = false;

        // Filter through all items
        for (QueryDocumentSnapshot document : allItems) {
            String name = document.getString("name");
            if (name != null && name.toLowerCase().contains(searchTerm.toLowerCase())) {
                itemsFound = true; // Found a matching item
                String uploaderName = document.getString("uploaderName");
                String imageUrl = document.getString("imageUri");
                String documentId = document.getId();

                // Create a new card view for each matching item
                addSearchResultItem(name, uploaderName, imageUrl, documentId);
            }
        }

        // Show message if no items were found
        if (!itemsFound) {
            Toast.makeText(SearchActivity.this, "No items found for: " + searchTerm, Toast.LENGTH_SHORT).show();
        }
    }

    private void addSearchResultItem(String name, String uploaderName, String imageUrl, String documentId) {
        LinearLayout itemLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 16, 16, 16); // Set bottom margin to create space between items
        itemLayout.setLayoutParams(layoutParams);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setBackgroundResource(R.drawable.border_background); // Set background with border
        itemLayout.setPadding(16, 16, 16, 16); // Add padding for better spacing

        // Set OnClickListener to navigate to ItemDetail
        itemLayout.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, ItemDetail.class);
            intent.putExtra("documentId", documentId); // Pass the document ID
            intent.putExtra("imageUrl", imageUrl); // Pass the image URL
            startActivity(intent);
        });

        // Create and set up the ImageView
        ImageView itemImageView = new ImageView(this);
        itemImageView.setLayoutParams(new LinearLayout.LayoutParams(
                200,
                100));
        itemImageView.setContentDescription("Item Image");

        // Load the image using Glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.book1) // Use book1.jpg as the placeholder
                .error(R.drawable.notificationicon) // Optional: Error image if loading fails
                .listener(new com.bumptech.glide.request.RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Log the error
                        Log.e("SearchActivity", "Image load failed for URL: " + imageUrl, e);
                        // Show a Toast message
                        Toast.makeText(SearchActivity.this, "Failed to load image for: " + name, Toast.LENGTH_SHORT).show();
                        return false; // Return false to allow Glide to handle the error placeholder
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false; // Return false to allow Glide to handle the resource
                    }
                })
                .into(itemImageView);

        // Create and set up the title TextView
        TextView titleTextView = new TextView(this);
        titleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Set width to match parent for centering
                LinearLayout.LayoutParams.WRAP_CONTENT));
        titleTextView.setText(name); // Use name instead of title
        titleTextView.setTextColor(getResources().getColor(android.R.color.black)); // Set text color
        titleTextView.setTextSize(16);
        titleTextView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleTextView.setPadding(0, 8, 0, 0); // Add some padding above the title
        titleTextView.setGravity(Gravity.CENTER); // Center the text

        // Add the ImageView and TextViews to the item layout
        itemLayout.addView(itemImageView);
        itemLayout.addView(titleTextView); // Add the title TextView next

        // Add the item layout to the search results container
        searchResultsContainer.addView(itemLayout);
    }
}