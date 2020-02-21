package com.mesh;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.mesh.Database.DBManager;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private final String NOTIFICATION_LISTENER_KEY = "enabled_notification_listeners";
    private final String NOTIFICATION_LISTENER_SETTING = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    public static final String RECEIVE_JSON = "MainActivity.RECEIVE_JSON";
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private boolean deleteNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        deleteNotification = dbManager.getDeleteNotificationSetting();
        dbManager.close();
        if (deleteNotification)
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(NotificationService.RECEIVE_JSON));
        initialiseToolbar();
        initialiseNavigationDrawer();
        if (!notificationIsEnabled()) {
            initialiseAlertDialog();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Create Setting Button
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() { // I don't what is this for
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  // Setting Button
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, Setting.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        deleteNotification = dbManager.getDeleteNotificationSetting();
        dbManager.close();
        if (deleteNotification)
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(NotificationService.RECEIVE_JSON));
    }

    private void initialiseToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar));
    }

    private void initialiseNavigationDrawer() {
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_search, R.id.nav_favourite,
                R.id.nav_saved, R.id.nav_contact)
                .setDrawerLayout(findViewById(R.id.drawer_layout))
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController((NavigationView) findViewById(R.id.nav_view), navController);
    }

    private boolean notificationIsEnabled() {
        ComponentName componentName = new ComponentName(this, NotificationService.class);
        String flat = Settings.Secure.getString(this.getContentResolver(), NOTIFICATION_LISTENER_KEY);
        return (flat != null) && (flat.contains(componentName.flattenToString()));
    }

    private void initialiseAlertDialog() {
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
