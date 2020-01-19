package com.mesh.ui.home;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private View root;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private HomeViewModel homeViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.getContactNames().observe(this, contactNames -> initialiseRecyclerView(root, contactNames));
        return root;
    }

    private void initialiseRecyclerView(View root, ArrayList<String> contactNames) {
        recyclerView = root.findViewById(R.id.contactList);
        recyclerView.setHasFixedSize(true);
        ((LinearLayoutManager) recyclerView.getLayoutManager()).setReverseLayout(true);
        contactAdapter = new ContactAdapter(contactNames, this.getContext());
        recyclerView.setAdapter(contactAdapter);
        recyclerView.scrollToPosition(contactNames.size() - 1);
    }

}