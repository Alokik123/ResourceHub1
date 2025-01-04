package com.dev.resourcehub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // Import EditText
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide; // Import Glide
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class SearchActivity extends AppCompatActivity {

    private Button btnBooks, btnDraftingTools, btnNotes;
    private LinearLayout searchResultsContainer;
    private EditText etSearch, searchInput;
    private FirebaseFirestore db;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchscreen); // Make sure this matches your layout file

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        searchInput = findViewById(R.id.et_search);
        etSearch = findViewById(R.id.et_search);
        btnBooks = findViewById(R.id.booksButton);
        btnDraftingTools = findViewById(R.id.draftingToolsButton);
        btnNotes = findViewById(R.id.notesButton);
        searchResultsContainer = findViewById(R.id.searchResultsContainer);

        // Set button click listeners
        btnBooks.setOnClickListener(v -> searchItems("Books"));
        btnDraftingTools.setOnClickListener(v -> searchItems("Drafting Tools"));
        btnNotes.setOnClickListener(v -> searchItems("Notes"));
    }

    private void searchItems(String category) {
        String searchTerm = etSearch.getText().toString().trim(); // Get text from the EditText
        if (searchTerm.isEmpty()) {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear previous results
        searchResultsContainer.removeAllViews();

        // Query Firestore for items matching the search term
        db.collection("items") // Replace with your collection name
                .whereEqualTo("category", category) // Assuming you have a category field
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String condition = document.getString("condition");
                            String price = document.getString("price");
                            String imageUrl = document.getString("imageUrl"); // Assuming you have an image URL field

                            // Create a new card view for each item
                            addCardView(title, condition, price, imageUrl, document.getId());
                        }
                    } else {
                        Toast.makeText(SearchActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addCardView(String title, String condition, String price, String imageUrl, String itemId) {
        // Inflate the card view layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.item_card_view, searchResultsContainer, false);

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
            Intent intent = new Intent(SearchActivity.this, NewListingActivity.class);
            intent.putExtra("itemId", itemId); // Pass the item ID to the detail activity
            startActivity(intent);
        });

        // Add the card view to the results container
        searchResultsContainer.addView(cardView);
    }
}