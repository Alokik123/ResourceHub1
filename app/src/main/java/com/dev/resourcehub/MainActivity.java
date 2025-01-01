package com.dev.resourcehub;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Initialize the ViewPager and Adapter
        ViewPager2 viewPager = findViewById(R.id.onboardingViewPager);
        OnboardingAdapter adapter = new OnboardingAdapter();
        viewPager.setAdapter(adapter);
    }
}
