package com.bedrankarakoc.mobilesentinel;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;
//Debug


public class HomeBottomBar extends AppCompatActivity {


    // Permissions
    private String[] permissions = {"android.permissions.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permissions.READ_PHONE_STATE", "android.permission.ACCESS_COARSE_LOCATION"
    , "android.permission.ACCESS_COARSE_UPDATES", "android.permission.ACCESS_FINE_LOCATION"};
    private int requestCode = 1337;

    TextView actionBarText;

    Fragment homeFragment = new HomeFragment();
    Fragment detectionFragment = new DetectionFragment();
    Fragment loggingFragment = new LoggingFragment();
    Fragment settingsFragment = new SettingsFragment();
    Fragment active = homeFragment;
    FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_bottom_bar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        requestPermissions(permissions, requestCode);

        //getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#ff8700\">" + getString(R.string.app_name) + "</font>"));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        actionBarText = findViewById(R.id.actionBarTitle);




        fragmentManager.beginTransaction().add(R.id.fragment_container, settingsFragment, "4").hide(settingsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, loggingFragment, "3").hide(loggingFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, detectionFragment, "2").hide(detectionFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, homeFragment, "1").commit();

    }

    @Override
    public void onBackPressed() {
        System.out.println("Pressed back");

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            actionBarText.setText("Home");
                            fragmentManager.beginTransaction().hide(active).show(homeFragment).commit();
                            active = homeFragment;
                            return true;
                        case R.id.nav_detection:
                            actionBarText.setText("Detection");
                            fragmentManager.beginTransaction().hide(active).show(detectionFragment).commit();
                            active = detectionFragment;
                            return true;
                        case R.id.nav_logging:
                            actionBarText.setText("Logging");
                            fragmentManager.beginTransaction().hide(active).show(loggingFragment).commit();
                            active = loggingFragment;
                            return true;
                        case R.id.nav_settings:
                            actionBarText.setText("Settings");
                            fragmentManager.beginTransaction().hide(active).show(settingsFragment).commit();
                            active = settingsFragment;
                            return true;

                    }

                    return false;
                }
            };




}
