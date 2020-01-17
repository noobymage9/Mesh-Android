package com.mesh.message;

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

import java.util.ArrayList;

public class MessageViewModel extends AndroidViewModel { // To format data for MessageActivity
    private MutableLiveData<ArrayList<Message>> messages;
    private LocalBroadcastManager localBroadcastManager;
    private String contactName;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadMessages();
        }
    };

    public MessageViewModel(@NonNull Application application) {
        super(application);
        initialiseLocalBroadcastManager();
    }

    public LiveData<ArrayList<Message>> getMessages(String contactName){
        messages = new MutableLiveData<>();
        this.contactName = contactName;
        loadMessages();
        return messages;
    }

    private void loadMessages() {
        new Thread(() -> {
            DBManager dbManager = new DBManager(this.getApplication());
            dbManager.open();
            messages.postValue(dbManager.getMessages(contactName));
            dbManager.close();
        }).start();
    }

    private void initialiseLocalBroadcastManager() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this.getApplication());
        localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(com.mesh.MainActivity.RECEIVE_JSON));
    }
}