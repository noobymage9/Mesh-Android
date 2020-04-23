package com.mesh.ui.favourite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.Database.DBManager;
import com.mesh.R;
import com.mesh.ui.home.Contact;
import com.mesh.ui.home.HomeFragment;
import com.mesh.ui.home.ImagePickerDialog;

import java.util.ArrayList;

public class FavouriteFragment extends Fragment {

    private FavouriteViewModel favouriteViewModel;
    private View root;
    private RecyclerView recyclerView;
    private FavouriteAdapter favouriteAdapter;
    private ImagePickerDialog imagePickerDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        favouriteViewModel =
                new ViewModelProvider(this).get(FavouriteViewModel.class);
        View root = inflater.inflate(R.layout.fragment_favourite, container, false);
        favouriteViewModel.getContactNames().observe(getViewLifecycleOwner(), contactList -> initialiseRecyclerView(root, contactList));
        imagePickerDialog = new ImagePickerDialog(this);

        return root;
    }

    private void initialiseRecyclerView (View root, ArrayList<Contact> contactList) {
        recyclerView = root.findViewById(R.id.contactList);
        recyclerView.setHasFixedSize(true);
        favouriteAdapter = new FavouriteAdapter(contactList,this);
        recyclerView.setAdapter(favouriteAdapter);
    }


    public void resetIcon() {
        DBManager dbManager = new DBManager(getContext());
        dbManager.open();
        dbManager.insertIcon(null, favouriteAdapter.getCurrentContactClicked().getID() + "");
        dbManager.close();
        imagePickerDialog.dismiss();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public ImagePickerDialog getImagePickerDialog() {
        return imagePickerDialog;
    }

}