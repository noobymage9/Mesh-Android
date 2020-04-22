package com.mesh.ui.favourite;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mesh.Database.DBManager;
import com.mesh.ui.home.Contact;

import java.util.ArrayList;

public class FavouriteViewModel extends AndroidViewModel { // To format data for HomeFragment
    private MutableLiveData<ArrayList<Contact>> contactList;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadContactNames();
        }
    };

    public FavouriteViewModel(@NonNull Application application) {
        super(application);
        initialiseLocalBroadcastManager();
    }

    public LiveData<ArrayList<Contact>> getContactNames(){
        contactList = new MutableLiveData<>();
        loadContactNames();
        return contactList;
    }

    private void loadContactNames() {
        new Thread(() -> {
            DBManager dbManager = new DBManager(this.getApplication());
            dbManager.open();
            contactList.postValue(dbManager.getFavouriteContacts());
            dbManager.close();
        }).start();
    }

    private void initialiseLocalBroadcastManager() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this.getApplication());
        localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(com.mesh.MainActivity.RECEIVE_JSON));
    }
}