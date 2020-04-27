package com.mesh;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.mesh.Database.DBManager;
import com.mesh.ui.home.HomeFragment;
import com.mesh.ui.home.ImagePickerDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String RECEIVE_JSON = "MainActivity.RECEIVE_JSON";
    public static final String ImagePickerFragmentTag = "image_picker_dialog";
    public static final int PICK_IMAGE = 2;
    public static final int CAPTURE_IMAGE = 3;

    private final String NOTIFICATION_LISTENER_KEY = "enabled_notification_listeners"; // Specific by Documentations
    private final String NOTIFICATION_LISTENER_SETTING = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private final int ALL_PERMISSIONS = 1;

    public static String galleryIconImage, cameraIconImage;

    private boolean mergeSwitchVisible;
    private String[] neededPermissions;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private Toast switchToast;
    private ImagePickerDialog imagePickerDialog;
    private ImageView profilePicture;
    private AlertDialog notificationAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imagePickerDialog = new ImagePickerDialog(this);
        switchToast = Toast.makeText(this, "", Toast.LENGTH_SHORT); // Merge-Swap toast
        initialiseToolbar();
        initialiseNavigationDrawer();
        if (getDeleteNotificationSetting()) // Tell MeshListener to delete related notifications
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MeshListener.RECEIVE_JSON));

       if (!notificationIsEnabled()) { // May need to remove in future. Need to research into signature permissions
            initialiseAlertDialog();
            notificationAlert.show();
        }
        if (!allPermissionsEnabled())
            ActivityCompat.requestPermissions(this, neededPermissions, ALL_PERMISSIONS);

        galleryIconImage = getGalleryPackage();
        cameraIconImage = getCameraPackage();
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
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController((NavigationView) findViewById(R.id.nav_view), navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            mergeSwitchVisible = destination.getId() == R.id.nav_home;
            invalidateOptionsMenu();
        });
        profilePicture = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.profile_picture);
        // TODO: 16/3/2020 get Profile Picture from database
        String temp = "test"; // Remove when can save profile picture
        Glide.with(this).load(temp).apply(RequestOptions.circleCropTransform()).placeholder(R.mipmap.default_icon).into(profilePicture);
        profilePicture.setOnClickListener(v -> {
            imagePickerDialog.show(getSupportFragmentManager(), ImagePickerFragmentTag);
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
        View alertRoot = getLayoutInflater().inflate(R.layout.layout_alert_dialog_all, null);
        ((TextView) alertRoot.findViewById(R.id.title)).setText(R.string.alert_dialog_notification_listener);
        ((TextView) alertRoot.findViewById(R.id.details)).setText(R.string.alert_dialog_enable_notification_warning);
        alertRoot.findViewById(R.id.ok_button).setOnClickListener(v -> {
            startActivity(new Intent(NOTIFICATION_LISTENER_SETTING));
            notificationAlert.dismiss();
        });
        alertRoot.findViewById(R.id.cancel_button).setOnClickListener(v -> notificationAlert.cancel());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(alertRoot);
        notificationAlert = builder.create();
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


    private String getCameraPackage() {
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.get(0).activityInfo.packageName;
    }

    private String getGalleryPackage() {
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.get(0).activityInfo.packageName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Create Setting Button
        getMenuInflater().inflate(R.menu.toolbar_overflow, menu);
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

    public void goToHome() { // To fix issue with dragging
        navController.navigate(R.id.nav_home);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PICK_IMAGE || requestCode == CAPTURE_IMAGE) && data != null && data.getData() != null) {
            String realPath = Image.getPath(this, data.getData());
            Image.with(this).insert(realPath).into(null);
            Glide.with(this).load(realPath).apply(RequestOptions.circleCropTransform()).placeholder(R.mipmap.default_icon).into(profilePicture);
        }
        try {
            imagePickerDialog.dismiss();
        } catch (IllegalStateException e) {
            Log.e("Mesh", "Failed to dismiss ImagePickerDialog");
        }
    }


    public void resetIcon() {
        Image.with(this).insert(null).into(null);
        Glide.with(this).load(R.mipmap.default_icon).apply(RequestOptions.circleCropTransform()).into(profilePicture);
        imagePickerDialog.dismiss();
    }
}
