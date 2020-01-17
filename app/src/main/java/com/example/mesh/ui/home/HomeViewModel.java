package com.example.mesh.ui.home;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mesh.Database.DBManager;
import com.example.mesh.message.MessageActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HomeViewModel extends AndroidViewModel { // To format data for HomeFragment
    private MutableLiveData<ArrayList<String>> contactNames;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadContactNames();
        }
    };

    public HomeViewModel(@NonNull Application application) {
        super(application);
        initialiseLocalBroadcastManager();
    }

    public LiveData<ArrayList<String>> getContactNames(){
        contactNames = new MutableLiveData<>();
        loadContactNames();
        return contactNames;
    }

    private void loadContactNames() {
        new Thread(() -> {
            DBManager dbManager = new DBManager(this.getApplication());
            dbManager.open();
            contactNames.setValue(dbManager.getAllContactNames());
            dbManager.close();
        });
    }

    private void initialiseLocalBroadcastManager() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this.getApplication());
        localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(MessageActivity.RECEIVE_JSON));
    }
}