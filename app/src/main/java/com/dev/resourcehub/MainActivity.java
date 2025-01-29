package com.dev.resourcehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button btnNext, btnContinue;
    private int currentPage = 0;
    private FirebaseAuth mAuth; // Firebase Auth instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btn_next);
        btnContinue = findViewById(R.id.btn_continue);

        // Set up the ViewPager with an adapter
        ImagePagerAdapter adapter = new ImagePagerAdapter(this);
        viewPager.setAdapter(adapter);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage < adapter.getCount() - 1) {
                    currentPage++;
                    viewPager.setCurrentItem(currentPage);
                    Toast.makeText(MainActivity.this, "Navigated to: " + adapter.titles[currentPage], Toast.LENGTH_SHORT).show(); // Toast for navigation
                } else {
                    // Check Firebase Auth status
                    checkUserStatus();
                }
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check Firebase Auth status
                checkUserStatus();
            }
        });

        // Set a page change listener to update the current page
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void checkUserStatus() {
        if (mAuth.getCurrentUser () != null) {
            // User is signed in, redirect to HomeActivity
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            Toast.makeText(MainActivity.this, "Redirecting to Home Activity", Toast.LENGTH_SHORT).show();
        } else {
            // User is not signed in, redirect to SignupActivity
            startActivity(new Intent(MainActivity.this, SignupActivity.class));
            Toast.makeText(MainActivity.this, "Redirecting to Signup Activity", Toast.LENGTH_SHORT).show();
        }
    }
}