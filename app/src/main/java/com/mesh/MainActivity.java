package com.mesh;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.mesh.Database.DBManager;
import com.mesh.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String RECEIVE_JSON = "MainActivity.RECEIVE_JSON";

    private static final int PICK_IMAGE = 21;
    private static final int ALL_PERMISSIONS = 1;

    private final String NOTIFICATION_LISTENER_KEY = "enabled_notification_listeners";
    private final String NOTIFICATION_LISTENER_SETTING = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private boolean deleteNotification, mergeSwitchVisible;
    private String[] neededPermissions;
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
        if (!allPermissionsEnabled())
            ActivityCompat.requestPermissions(this, neededPermissions, ALL_PERMISSIONS);
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
        // Latest Update Issue
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController((NavigationView) findViewById(R.id.nav_view), navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            mergeSwitchVisible = destination.getId() == R.id.nav_home;
            invalidateOptionsMenu();
        });

        ImageView profilePicture = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.profile_picture);
        // TODO: 16/3/2020 get Profile Picture from database
        String profilePicturePath = "test";
        Glide.with(this).load(profilePicturePath).apply(RequestOptions.circleCropTransform()).placeholder(R.mipmap.default_icon).into(profilePicture);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra("return-data", true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
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

    private boolean allPermissionsEnabled() {
        boolean temp = true;
        ArrayList<String> tempList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            temp = false;
            tempList.add(Manifest.permission.RECEIVE_SMS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp = false;
            tempList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp = false;
            tempList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        neededPermissions = tempList.toArray(new String[tempList.size()]);
        return temp;
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
    public void onBackPressed() { // This cause home to be unable to back out of Mesh

        List<Fragment> fragmentList = getSupportFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager().getFragments();

        for (Fragment fragment : fragmentList) {
            if (fragment instanceof HomeFragment) {
                if (!((HomeFragment) fragment).dismissSnack())
                    super.onBackPressed();
                return;
            }
        }
        super.onBackPressed();
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
                homeFragment.dismissSnack();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            String realPath = ImageFilePath.getPath(this, data.getData());
            // TODO: 16/3/2020 Insert realpath into database for Personal Profile Picture
        }
    }

}
