package com.example.mesh.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mesh.Database.DBManager;
import com.example.mesh.R;
import com.example.mesh.message.MessageActivity;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private ArrayList<ContactInfo> contactList;

    private View root;
    private DBManager dbManager;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private LocalBroadcastManager localBroadcastManager;
    private HomeViewModel homeViewModel;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            contactAdapter.updateByName(getContactNames());
        }
    };


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.getContactNames().observe(this, contactNames -> initialiseRecyclerView(root, contactNames));
        return root;
    }

    private ArrayList<String> getContactNames() {
        ArrayList<String> temp;
        dbManager = new DBManager(getContext());
        dbManager.open();
        temp = dbManager.getAllContactNames();
        dbManager.close();
        return temp;
    }

    private void initialiseRecyclerView(View root, ArrayList<String> contactNames) {
        recyclerView = root.findViewById(R.id.contactList);
        recyclerView.setHasFixedSize(true);
        contactAdapter = new ContactAdapter(contactNames);
        recyclerView.setAdapter(contactAdapter);
    }

    private void initialiseLocalBroadcastManager() {
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(MessageActivity.RECEIVE_JSON));
    }

}