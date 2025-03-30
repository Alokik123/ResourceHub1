package com.dev.resourcehub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout llHome, llProfile, llSettings;
    private Button btnBooks, btnTools, btnStationery;
    private ImageView imgProfile, imgSettings;
    private LinearLayout newArrivalsContainer;
    private FirebaseFirestore db;
    private String selectedCategory = "Books"; // Default category

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen); // Ensure this matches your layout file

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        newArrivalsContainer = findViewById(R.id.newArrivalsContainer);
        imgProfile = findViewById(R.id.img_profile);
        imgSettings = findViewById(R.id.settingsIcon1);
        llHome = findViewById(R.id.ll_home);
        llProfile = findViewById(R.id.ll_profile);
        llSettings = findViewById(R.id.ll_settings);
//        btnBooks = findViewById(R.id.btn_books);
//        btnTools = findViewById(R.id.btn_tools);
//        btnStationery = findViewById(R.id.btn_stationery);

        // Set click listeners for bottom navigation
        llHome.setOnClickListener(v -> selectTab("Home", HomeActivity.class));
        llProfile.setOnClickListener(v -> selectTab("Search", SearchActivity.class));
        llSettings.setOnClickListener(v -> selectTab("Profile", NewListingActivity.class));

        // Set click listeners for category buttons
//        btnBooks.setOnClickListener(v -> selectCategory(btnBooks));
//        btnTools.setOnClickListener(v -> selectCategory(btnTools));
//        btnStationery.setOnClickListener(v -> selectCategory(btnStationery));

        imgSettings.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchNewArrivals();
    }

//    private void selectCategory(Button selectedButton) {
////        resetCategoryButtonBackgrounds();
//        selectedButton.setSelected(true); // Mark the button as selected
//        selectedCategory = selectedButton.getText().toString(); // Set selected category
//        fetchNewArrivals(); // Fetch new arrivals for the selected category
//    }

    private void fetchNewArrivals() {
        db.collection("items_uploaded")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Assuming you have a timestamp field to order by
                .limit(10) // Limit to the latest 10 items
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        newArrivalsContainer.removeAllViews(); // Clear previous items
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String uploaderName = document.getString("name");
                            String imageUrl = document.getString("imageUri");
                            String documentId = document.getId();

                            // Log the fetched values for debugging
                            Log.d("FetchNewArrivals", "Title: " + title);
                            Log.d("FetchNewArrivals", "Uploader Name: " + uploaderName);
                            Log.d("FetchNewArrivals", "Image URL: " + imageUrl);

                            // Call the method to add the new arrival item
                            addNewArrivalItem(title, uploaderName, imageUrl, documentId);
                        }
                        Toast.makeText(HomeActivity.this, "New arrivals fetched successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("HomeActivity", "Error fetching new arrivals: " + task.getException());
                        Toast.makeText(HomeActivity.this, "Error fetching new arrivals: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addNewArrivalItem(String title, String uploaderName, String imageUrl, String documentId) {
        LinearLayout itemLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 16, 16, 16);
        itemLayout.setLayoutParams(layoutParams);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setBackgroundResource(R.drawable.border_background);
        itemLayout.setPadding(16, 16, 16, 16);

        // Set OnClickListener to navigate to ItemDetail
        itemLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ItemDetail.class);
            intent.putExtra("documentId", documentId);
            intent.putExtra("imageUrl", imageUrl);
            startActivity(intent);
        });

        // Create and set up the ImageView
        ImageView itemImageView = new ImageView(this);
        itemImageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                200));
        itemImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        itemImageView.setAdjustViewBounds(true);
        Log.d("ImageLoading", "Image URI: " + imageUrl);


        loadImageWithPicasso(itemImageView, imageUrl, documentId);
        itemImageView.setContentDescription("Book Cover");

        // Create and set up the title TextView
        TextView titleTextView = new TextView(this);
        titleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        titleTextView.setText(title);
        titleTextView.setTextColor(getResources().getColor(android.R.color.black));
        titleTextView.setTextSize(16);
        titleTextView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleTextView.setPadding(0, 8, 0, 0);
        titleTextView.setGravity(Gravity.CENTER);

        itemLayout.addView(itemImageView);
        itemLayout.addView(titleTextView);

        newArrivalsContainer.addView(itemLayout);
    }
    private void loadImageWithPicasso(ImageView imageView, String imageUrl, String documentId) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImageResource(R.drawable.notificationicon);
            Log.d("ImageDebug", "Attempting to load image for doc: " + documentId);
            Log.d("ImageDebug", "URI: " + imageUrl);
            return;
        }

        // First try loading directly with Picasso
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.book1)
                .error(R.drawable.notificationicon)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // If successful, cache the image
                        cacheImageLocally(imageUrl, documentId);
                    }

                    @Override
                    public void onError(Exception e) {
                        // If failed, try loading from cache
                        loadFromCache(imageView, documentId);
                    }
                });
    }

    private void cacheImageLocally(String imageUrl, String documentId) {
        new Thread(() -> {
            try {
                // Create cache file
                File cacheFile = new File(getCacheDir(), "images_" + documentId + ".jpg");
                Log.d("ImageDebug", "Caching image for doc: " + documentId);
                Log.d("ImageDebug", "Cache file path: " + cacheFile.getAbsolutePath());

                // Get the image
                Uri uri = Uri.parse(imageUrl);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                // Save to cache
                FileOutputStream out = new FileOutputStream(cacheFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.close();
            } catch (Exception e) {
                Log.e("ImageCache", "Failed to cache image", e);
            }
        }).start();
    }

    private void loadFromCache(ImageView imageView, String documentId) {
        File cacheFile = new File(getCacheDir(), "images_" + documentId + ".jpg");
        Log.d("ImageDebug", "Loading from cache for doc: " + documentId);
        Log.d("ImageDebug", "Cache exists: " + cacheFile.exists());
        if (cacheFile.exists()) {
            // Convert to content URI that Picasso can use
            Uri contentUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    cacheFile
            );

            Picasso.get()
                    .load(contentUri)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.notificationicon);
        }
    }
//    private void resetCategoryButtonBackgrounds() {
//        btnBooks.setSelected(false);
//        btnTools.setSelected(false);
//        btnStationery.setSelected(false);
//    }

    private void selectTab(String tabName, Class<?> activityClass) {
        resetTabBackgrounds();
        switch (tabName) {
            case "Home":
                llHome.setBackgroundResource(R.drawable.rounded_blue_background);
                break;
            case "Profile":
                llProfile.setBackgroundResource(R.drawable.rounded_blue_background);
                break;
            case "Settings":
                llSettings.setBackgroundResource(R.drawable.rounded_blue_background);
                break;
        }

        // Start the corresponding activity
        llSettings.postDelayed(() -> {
            Intent intent = new Intent(HomeActivity.this, activityClass);
            startActivity(intent);
        }, 200); // Delay for 200 milliseconds
    }

    private void resetTabBackgrounds() {
        llHome.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        llProfile.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        llSettings.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
}