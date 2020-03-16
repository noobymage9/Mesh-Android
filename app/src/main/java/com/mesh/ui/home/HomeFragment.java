package com.mesh.ui.home;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mesh.Database.DBManager;
import com.mesh.ImageFilePath;
import com.mesh.MainActivity;
import com.mesh.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    protected static final int PICK_IMAGE = 7;
    public boolean merge = true;
    private View root;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private HomeViewModel homeViewModel;
    private ItemDragAndDropCallback itemDragAndDropCallback;
    private ItemTouchHelper itemTouchHelper;
    private MergeSnackbar mergeSnackbar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getContactNames().observe(getViewLifecycleOwner(), contactList -> initialiseRecyclerView(root, contactList));
        return root;
    }

    private void initialiseRecyclerView(View root, ArrayList<Contact> contactList) {
        recyclerView = root.findViewById(R.id.contactList);
        recyclerView.setHasFixedSize(true);
        contactAdapter = new ContactAdapter(contactList,this);
        recyclerView.setAdapter(contactAdapter);
        itemDragAndDropCallback = new ItemDragAndDropCallback(this, recyclerView);
        itemTouchHelper = new ItemTouchHelper(itemDragAndDropCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    public void dismissSnack() {
        if (mergeSnackbar != null && mergeSnackbar.isShown()) {
            mergeSnackbar.dismiss();
            reset();
        }
    }

    public void setMerge(boolean merge){
        this.merge = merge;
    }

    public boolean isMerge(){
        return merge;
    }

    public ItemTouchHelper getItemTouchHelper() {
        return itemTouchHelper;
    }

    public ItemDragAndDropCallback getItemDragAndDropCallback() {
        return itemDragAndDropCallback;
    }

    public void displaySnackBar(int draggedFolderPosition, int folderPosition) {
        mergeSnackbar = MergeSnackbar.make(getActivity().findViewById(R.id.snackBar_location), Snackbar.LENGTH_INDEFINITE, draggedFolderPosition, folderPosition, recyclerView);
        mergeSnackbar.show();
    }

    public MergeSnackbar getMergeSnackbar(){
        return mergeSnackbar;
    }

    public void reset(){
        ((MainActivity) getActivity()).goToHome();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            String realPath = ImageFilePath.getPath(getContext(), data.getData());
            DBManager dbManager = new DBManager(getContext());
            dbManager.open();
            dbManager.insertIcon(realPath, contactAdapter.getCurrentContactClicked().getID() + "");
            dbManager.close();
            reset();
        }
    }

}