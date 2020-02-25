package com.mesh.ui.home;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.MainActivity;
import com.mesh.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private View root;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private HomeViewModel homeViewModel;
    private ItemDragAndDropCallback itemDragAndDropCallback;
    private ItemTouchHelper itemTouchHelper;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        ((MainActivity) getActivity()).getSupportActionBar();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getContactNames().observe(getViewLifecycleOwner(), contactList -> initialiseRecyclerView(root, contactList));
        return root;
    }

    private void initialiseRecyclerView(View root, ArrayList<Contact> contactList) {
        recyclerView = root.findViewById(R.id.contactList);
        recyclerView.setHasFixedSize(true);
        contactAdapter = new ContactAdapter(contactList, this.getContext());
        recyclerView.setAdapter(contactAdapter);
        itemDragAndDropCallback = new ItemDragAndDropCallback(this, recyclerView);
        itemTouchHelper = new ItemTouchHelper(itemDragAndDropCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    public void onBackPressed() {
        MergeSnackbar temp = itemDragAndDropCallback.getMergebar();
        if (temp.isShown()) {
               temp.dismiss();
        }
    }
}