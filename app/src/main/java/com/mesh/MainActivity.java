package com.mesh;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.mesh.Database.DBManager;
import com.mesh.ui.home.HomeFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 1;
    public static final String RECEIVE_JSON = "MainActivity.RECEIVE_JSON";
    private final String NOTIFICATION_LISTENER_KEY = "enabled_notification_listeners";
    private final String NOTIFICATION_LISTENER_SETTING = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private boolean deleteNotification, mergeSwitchVisible;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private Toast switchToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseToolbar();
        initialiseNavigationDrawer();

        switchToast = Toast.makeText(this, "", Toast.LENGTH_SHORT); // Merge-Swap toast
        deleteNotification = getDeleteNotificationSetting();
        if (deleteNotification) // Tell MeshListener to delete related notifications
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MeshListener.RECEIVE_JSON));

        if (!notificationIsEnabled()) // May need to remove in future. Need to research into signature permissions
            initialiseAlertDialog();
        if (!receiveSMSEnabled())
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
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
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            mergeSwitchVisible = destination.getId() == R.id.nav_home;
            invalidateOptionsMenu();
        });
    }

    private boolean getDeleteNotificationSetting() {
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        boolean temp = dbManager.getDeleteNotificationSetting();
        dbManager.close();
        return temp;
    }

    private boolean notificationIsEnabled() {
        ComponentName componentName = new ComponentName(this, MeshListener.class);
        String flat = Settings.Secure.getString(this.getContentResolver(), NOTIFICATION_LISTENER_KEY);
        return (flat != null) && (flat.contains(componentName.flattenToString()));
    }

    private void initialiseAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_dialog_enable_notification_warning);
        builder.setCancelable(true);

        builder.setPositiveButton(
                R.string.alert_dialog_positive_button,
                (dialog, id) -> startActivity(new Intent(NOTIFICATION_LISTENER_SETTING)));

        builder.setNegativeButton(
                R.string.alert_dialog_negative_button,
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean receiveSMSEnabled() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
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
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager().getFragments();

        for (Fragment fragment : fragmentList) {
            if (fragment instanceof HomeFragment) {
                ((HomeFragment) fragment).dismissSnackbar();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem mergeSwitch = menu.findItem(R.id.merge_switch);
        if (mergeSwitchVisible) {
            mergeSwitch.setVisible(true);
            ((Switch) mergeSwitch.getActionView()).setOnCheckedChangeListener((buttonView, isChecked) -> {
                HomeFragment homeFragment = null;
                List<Fragment> fragmentList = getSupportFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager().getFragments();
                for (Fragment fragment : fragmentList)
                    if (fragment instanceof HomeFragment)
                        homeFragment = (HomeFragment) fragment;
                homeFragment.dismissSnackbar();
                homeFragment.setMerge(!isChecked);
                if (isChecked) {
                    switchToast.setText(R.string.action_swap_mode);
                    switchToast.show();
                } else {
                    goToHome();
                    switchToast.setText(R.string.action_merge_mode);
                    switchToast.show();
                }

            });
        } else
            mergeSwitch.setVisible(false);


        return super.onPrepareOptionsMenu(menu);
    }

    public void goToHome() {
        navController.navigate(R.id.nav_home);
    }

}
