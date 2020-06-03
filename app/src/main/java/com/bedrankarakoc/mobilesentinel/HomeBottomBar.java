package com.bedrankarakoc.mobilesentinel;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeBottomBar extends AppCompatActivity {

    Fragment homeFragment = new HomeFragment();
    Fragment detectionFragment = new DetectionFragment();
    Fragment loggingFragment = new LoggingFragment();
    Fragment settingsFragment = new SettingsFragment();
    Fragment active = homeFragment;
    FragmentManager fm = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_bottom_bar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        fm.beginTransaction().add(R.id.fragment_container, settingsFragment, "4").hide(settingsFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, loggingFragment, "3").hide(loggingFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, detectionFragment, "2").hide(detectionFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, homeFragment, "1").commit();
        /*//I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    homeFragment).commit();

        }
*/
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

                            fm.beginTransaction().hide(active).show(homeFragment).commit();
                            active = homeFragment;
                            return true;
                        case R.id.nav_detection:

                            fm.beginTransaction().hide(active).show(detectionFragment).commit();
                            active = detectionFragment;
                            return true;
                        case R.id.nav_logging:

                            fm.beginTransaction().hide(active).show(loggingFragment).commit();
                            active = loggingFragment;
                            return true;
                        case R.id.nav_settings:
                            fm.beginTransaction().hide(active).show(settingsFragment).commit();
                            active = settingsFragment;
                            return true;

                    }

                    return false;
                }
            };

}
