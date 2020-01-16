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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mesh.Database.DBManager;
import com.example.mesh.R;
import com.example.mesh.message.MessageActivity;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private ArrayList<ContactInfo> contactList;
    private ArrayList<String> contactNames;

    private View root;
    private DBManager dbManager;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            contactAdapter.updateByName(getContactNames());
        }
    };


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);
        contactNames = getContactNames();
        initialiseRecyclerView(root);
        initialiseLocalBroadcastManager();

        //contactList = getContacts();


        /*
        for (int i = 0; i < 15; i++) { // Generating sample contacts
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.default_icon);
            if (i == 0)
                contactList.add(new ContactInfo("SIM Han Wei", bm, "(Text Here)"));
            else
                contactList.add(new ContactInfo("Contact " + i, bm, "(Text Here)"));
        }
        */

        return root;
    }

    private ArrayList<ContactInfo> getContacts(){
        ArrayList<String> allNames = getContactNames();
        ArrayList<Integer> allIDs = getContactIDs();
        ArrayList<ContactInfo> allContacts = new ArrayList<>();
        for (int i = 0; i < allNames.size(); i++) {
            allContacts.add(new ContactInfo(allIDs.get(i), allNames.get(i)));
        }
        return allContacts;
    }

    private ArrayList<Integer> getContactIDs() {
        ArrayList<Integer> temp;
        dbManager = new DBManager(getContext());
        dbManager.open();
        temp = dbManager.fetchAllContactIDs();
        dbManager.close();
        return temp;
    }


    private ArrayList<String> getContactNames() {
        ArrayList<String> temp;
        dbManager = new DBManager(getContext());
        dbManager.open();
        temp = dbManager.fetchAllContactNames();
        dbManager.close();
        return temp;
    }

    private void initialiseRecyclerView(View root) {
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