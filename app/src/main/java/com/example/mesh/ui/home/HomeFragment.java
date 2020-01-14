package com.example.mesh.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mesh.Database.DBManager;
import com.example.mesh.Database.DatabaseHelper;
import com.example.mesh.R;
import com.example.mesh.message.MessageActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ArrayList<ContactInfo> contactList;
    private DBManager dbManager;
    private ContactAdapter contactAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Recycler View
        RecyclerView recList = root.findViewById(R.id.contactList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);


        //contactList = getContacts();
        ArrayList<String> contactNames = getContactNames();

        /*
        for (int i = 0; i < 15; i++) { // Generating sample contacts
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.default_icon);
            if (i == 0)
                contactList.add(new ContactInfo("SIM Han Wei", bm, "(Text Here)"));
            else
                contactList.add(new ContactInfo("Contact " + i, bm, "(Text Here)"));
        }
        */

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MessageActivity.RECEIVE_JSON);
        bManager.registerReceiver(bReceiver, intentFilter);

        contactAdapter = new ContactAdapter(contactNames);
        recList.setAdapter(contactAdapter);
        return root;
    }

    private ArrayList<ContactInfo> getContacts(){
        ArrayList<String> allNames = getContactNames();
        ArrayList<Integer> allIDs = getContactIDs();
        ArrayList<ContactInfo> allContacts = new ArrayList<ContactInfo>();
        for (int i = 0; i < allNames.size(); i++) {
            allContacts.add(new ContactInfo(allIDs.get(i), allNames.get(i)));
        }
        return allContacts;
    }

    private ArrayList<Integer> getContactIDs() {
        ArrayList<Integer> temp = null;
        dbManager = new DBManager(getContext());
        dbManager.open();
        temp = dbManager.fetchAllContactIDs();
        return temp;
    }


    private ArrayList<String> getContactNames() {
        ArrayList<String> temp = null;
        dbManager = new DBManager(getContext());
        dbManager.open();
        temp = dbManager.fetchAllContactNames();
        return temp;
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            contactAdapter.updateByName(getContactNames());
        }
    };

}