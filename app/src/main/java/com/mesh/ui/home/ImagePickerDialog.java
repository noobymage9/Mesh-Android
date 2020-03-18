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
import com.mesh.Database.DBManager;
import com.mesh.MainActivity;
import com.mesh.R;

public class ImagePickerDialog extends BottomSheetDialogFragment {

    Fragment fragment;

    public ImagePickerDialog(Fragment fragment) {
        this.fragment = fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.image_source_bottom_sheet, container, false);
        ImageView gallery = root.findViewById(R.id.gallery_icon);
        ImageView camera = root.findViewById(R.id.camera_icon);
        try {
            Glide.with(root).load(fragment.getActivity().getPackageManager().getApplicationIcon(MainActivity.galleryPackage)).fitCenter().into(gallery);
            Glide.with(root).load(fragment.getActivity().getPackageManager().getApplicationIcon(MainActivity.cameraPackage)).fitCenter().into(camera);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("ERROR", e + "");
        }

        RelativeLayout galleryGroup = root.findViewById(R.id.gallery_group);
        galleryGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra("return-data", true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                if (intent.resolveActivity(fragment.getActivity().getPackageManager()) != null)
                    fragment.startActivityForResult(Intent.createChooser(intent, "Select Picture"), HomeFragment.PICK_IMAGE);
            }
        });

        RelativeLayout cameraGroup = root.findViewById(R.id.camera_group);
        cameraGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(fragment.getActivity().getPackageManager()) != null && fragment.getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                    fragment.getActivity().startActivityForResult(intent, HomeFragment.CAPTURE_IMAGE);
            }
        });

        Button reset = root.findViewById(R.id.reset_button);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeFragment) fragment).resetIcon();
            }
        });
        return root;
    }
}
