package com.example.mesh;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private Toolbar toolbar;
    private boolean notificationServiceEnabled;
    private final String NOTIFICATION_LISTENER_KEY = "enabled_notification_listeners";
    private final String NOTIFICATION_LISTENER_SETTING = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Replace Default ActionBar to Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up NavigationView
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_search, R.id.nav_favourite,
                R.id.nav_saved, R.id.nav_contact, R.id.nav_setting)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Check for NotificationService Permission
        ComponentName componentName = new ComponentName(this, NotificationService.class);
        String flat = Settings.Secure.getString(this.getContentResolver(), NOTIFICATION_LISTENER_KEY);
        notificationServiceEnabled = (flat != null) && (flat.contains(componentName.flattenToString()));

        // Alert Dialog Pop Up
        if (!notificationServiceEnabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.enable_notification_warning);
            builder.setCancelable(true);

            builder.setPositiveButton(
                    R.string.positive_warning_button,
                    (dialog, id) -> startActivity(new Intent(NOTIFICATION_LISTENER_SETTING)));

            builder.setNegativeButton(
                    R.string.negative_warning_button,
                    (dialog, id) -> dialog.cancel());

            AlertDialog alert = builder.create();
            alert.show();

        }
    }


    public boolean onCreateOptionsMenu(Menu menu) { // Create Setting Button
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() { // I don't what is this for
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public boolean onOptionsItemSelected(MenuItem item) {  // Setting Button
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, Setting.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
