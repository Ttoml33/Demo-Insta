package com.tomlee.demoinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.tomlee.demoinsta.Fragments.HomeFragment;
import com.tomlee.demoinsta.Fragments.NotificationFragment;
import com.tomlee.demoinsta.Fragments.ProfileFragment;
import com.tomlee.demoinsta.Fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private boolean isBottomNavigationVisible = true;
    AppBarLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parent=findViewById(R.id.parent);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setElevation(0);
        setupScrollListener();


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_Home) {
                    fragment = new HomeFragment();
                } else if (itemId == R.id.nav_search) {
                    fragment = new SearchFragment();
                } else if (itemId == R.id.nav_add) {
                    fragment = null;
                    startActivity(new Intent(MainActivity.this, PostActivity.class));
                } else if (itemId == R.id.nav_Heart) {
                    fragment = new NotificationFragment();
                } else if (itemId == R.id.nav_profile) {
                    fragment = new ProfileFragment();
                }
                if (fragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Container,fragment).commit();
                }
                return true;
            }
        });
        Bundle intent =getIntent().getExtras();
        if (intent!=null){
            String profileID= intent.getString("publisherID");
            getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("profileID",profileID).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Container,new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        }else {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Container,new HomeFragment()).commit();

        }
    }
    public void setupScrollListener(){
        parent.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            private int oldScrollY = 0;

            @Override
            public void onScrollChanged() {
                int scrollY = parent.getScrollY();
                if (scrollY > oldScrollY) {
                    // Scrolling downward
                    if (isBottomNavigationVisible) {
                        bottomNavigationView.animate().translationY(bottomNavigationView.getHeight());
                        isBottomNavigationVisible = false;
                    }
                } else if (scrollY < oldScrollY) {
                    // Scrolling upward
                    if (!isBottomNavigationVisible) {
                        bottomNavigationView.animate().translationY(0);
                        isBottomNavigationVisible = true;
                    }
                }
                oldScrollY = scrollY;
            }
        });
    }
    }
