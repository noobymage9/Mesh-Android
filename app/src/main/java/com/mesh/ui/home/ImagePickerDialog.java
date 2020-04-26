package com.mesh.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mesh.MainActivity;
import com.mesh.R;
import com.mesh.ui.contact.ContactDetailActivity;
import com.mesh.ui.favourite.FavouriteFragment;

public class ImagePickerDialog extends BottomSheetDialogFragment {

    Fragment fragment;
    Activity activity;

    public ImagePickerDialog(Fragment fragment) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
    }

    public ImagePickerDialog(Activity activity) {
        this.activity = activity;
    }

    public boolean isActivity() {
        return fragment == null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_bottomsheet, container, false);
        ImageView gallery = root.findViewById(R.id.gallery_icon);
        ImageView camera = root.findViewById(R.id.camera_icon);
        try {
            Glide.with(root).load(activity.getPackageManager().getApplicationIcon(MainActivity.galleryIconImage)).fitCenter().into(gallery);
            Glide.with(root).load(activity.getPackageManager().getApplicationIcon(MainActivity.cameraIconImage)).fitCenter().into(camera);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("ERROR", e + "");
        }

        RelativeLayout galleryGroup = root.findViewById(R.id.gallery_group);
        galleryGroup.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra("return-data", true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (intent.resolveActivity(activity.getPackageManager()) != null)
                if (!isActivity())
                    fragment.startActivityForResult(Intent.createChooser(intent, "Select Picture"), MainActivity.PICK_IMAGE);
                else
                    activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), MainActivity.PICK_IMAGE);

        });

        RelativeLayout cameraGroup = root.findViewById(R.id.camera_group);
        cameraGroup.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(activity.getPackageManager()) != null && activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                if (!isActivity())
                    fragment.startActivityForResult(intent, MainActivity.CAPTURE_IMAGE);
                else
                    activity.startActivityForResult(intent, MainActivity.CAPTURE_IMAGE);
        });

        Button reset = root.findViewById(R.id.reset_button);
        reset.setOnClickListener(v -> {
            if (!isActivity()) {
                if (fragment instanceof HomeFragment) ((HomeFragment) fragment).resetIcon();
                if (fragment instanceof FavouriteFragment)
                    ((FavouriteFragment) fragment).resetIcon();
            } else
                if (activity instanceof ContactDetailActivity) ((ContactDetailActivity) activity).resetIcon();
                if (activity instanceof MainActivity) ((MainActivity) activity).resetIcon();
        });
        return root;
    }
}
