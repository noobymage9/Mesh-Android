package com.mesh;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
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
import com.mesh.Database.DBManager;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private final String NOTIFICATION_LISTENER_KEY = "enabled_notification_listeners";
    private final String NOTIFICATION_LISTENER_SETTING = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    public static final String RECEIVE_JSON = "MainActivity.RECEIVE_JSON";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        if (dbManager.getDeleteNotificationSetting())
            deleteNotification();
        dbManager.close();
        initialiseToolbar();
        initialiseSideBar();
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

    private void initialiseToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar));
    }

    private void initialiseSideBar() {
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_search, R.id.nav_favourite,
                R.id.nav_saved, R.id.nav_contact, R.id.nav_setting)
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void deleteNotification() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        StatusBarNotification statusBarNotification[] = notificationManager.getActiveNotifications();
        for (int i = 0; i < statusBarNotification.length; i++) {
            String temp = statusBarNotification[i].getPackageName();
            Log.e("TEST", "TEST");
            if (temp.equals(NotificationService.WHATSAPP_PACKAGE) ||
                temp.equals(NotificationService.TELEGRAM_PACKAGE)) {
                notificationManager.cancel(statusBarNotification[i].getId());
            }
        }
    }

}
