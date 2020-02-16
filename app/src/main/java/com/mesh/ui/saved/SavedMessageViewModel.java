package com.mesh.ui.saved;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mesh.Database.DBManager;
import com.mesh.message.Message;
import com.mesh.message.UserCollection;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SavedMessageViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<Message>> messages;
    private UserCollection userCollection;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadMessages();
        }
    };

    public SavedMessageViewModel(@NonNull Application application) {
        super(application);
        initialiseLocalBroadcastManager();
    }

    public LiveData<ArrayList<Message>> getMessages(UserCollection userCollection) {
        this.userCollection = userCollection;
        messages = new MutableLiveData<>();
        loadMessages();
        return  messages;
    }

    private void loadMessages() {
        new Thread(() -> {
            DBManager dbManager = new DBManager(this.getApplication());
            dbManager.open();
            messages.postValue(dbManager.getMessagesInUserCollection(userCollection.getID()));
            dbManager.close();
        }).start();
    }

    private void initialiseLocalBroadcastManager() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this.getApplication());
        localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(com.mesh.MainActivity.RECEIVE_JSON));
    }
}
