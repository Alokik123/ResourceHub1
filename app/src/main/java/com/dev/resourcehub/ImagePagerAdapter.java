package com.dev.resourcehub;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ImagePagerAdapter extends PagerAdapter {

    private Context context;
    private int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3}; // Add your images here
    String[] titles = {
            "Welcome to Resource Hub",
            "Easily sell your used textbooks, notes, and other educational materials to fellow students.",
            "Our platform ensures a smooth transaction process, helping you earn money while contributing to the community."
    };

    public ImagePagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pager_item, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView);

        try {
            imageView.setImageResource(images[position]);
            textView.setText(titles[position]);
            Log.d("ImagePagerAdapter", "Position: " + position + ", Image: " + images[position] + ", Title: " + titles[position]);

            // Toast message for successful image loading
            Toast.makeText(context, "Image loaded successfully: " + titles[position], Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Toast message for error in loading image
            Toast.makeText(context, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}