package com.mesh.ui.home;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mesh.Database.DBManager;
import com.mesh.Image;
import com.mesh.MainActivity;
import com.mesh.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    public static final int PICK_IMAGE = 7;
    public static final int CAPTURE_IMAGE = 8;
    public boolean merge = true;
    private View root;
    private RecyclerView recyclerView;
    private ConversationAdapter conversationAdapter;
    private HomeViewModel homeViewModel;
    private DragSwipeController dragSwipeController;
    private ItemTouchHelper itemTouchHelper;
    private MergeSnackbar mergeSnackbar;
    private ImagePickerDialog imagePickerDialog;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getContactNames().observe(getViewLifecycleOwner(), contactList -> initialiseRecyclerView(root, contactList));
        imagePickerDialog = new ImagePickerDialog(this);
        return root;
    }

    private void initialiseRecyclerView(View root, ArrayList<Contact> contactList) {
        recyclerView = root.findViewById(R.id.contactList);
        recyclerView.setHasFixedSize(true);
        conversationAdapter = new ConversationAdapter(contactList,this);
        recyclerView.setAdapter(conversationAdapter);
        dragSwipeController = new DragSwipeController(this, recyclerView);
        itemTouchHelper = new ItemTouchHelper(dragSwipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    public boolean dismissSnack() {
        if (mergeSnackbar != null && mergeSnackbar.isShown()) {
            mergeSnackbar.dismiss();
            reset();
            return true;
        }
        return false;
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

    public DragSwipeController getDragSwipeController() {
        return dragSwipeController;
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
    } // to prevent bug where item dragged is not moving

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.e("HomeFragment", "Received data");
        if ((requestCode == PICK_IMAGE || requestCode == CAPTURE_IMAGE) && data != null && data.getData() != null) {
            Log.e("HomeFragement", "MATCHED");
            String realPath = Image.getPath(getContext(), data.getData());
            DBManager dbManager = new DBManager(getContext());
            dbManager.open();
            dbManager.insertIcon(realPath, conversationAdapter.getCurrentContactClicked().getID() + "");
            dbManager.close();
            conversationAdapter.getCurrentContactClicked().setProfilePic(realPath);
            conversationAdapter.notifyDataSetChanged();
        }
        imagePickerDialog.dismiss();
    }

    public ImagePickerDialog getImagePickerDialog() {
        return imagePickerDialog;
    }

    public void resetIcon() {
        DBManager dbManager = new DBManager(getContext());
        dbManager.open();
        dbManager.insertIcon(null, conversationAdapter.getCurrentContactClicked().getID() + "");
        dbManager.close();
        imagePickerDialog.dismiss();
        reset();
    }

    public ConversationAdapter getConversationAdapter() {
        return conversationAdapter;
    }

    public static Bitmap drawableToBitmap (Drawable drawable, int scale) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth() + scale, drawable.getIntrinsicHeight() + scale, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}