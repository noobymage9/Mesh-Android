package com.mesh.message;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mesh.Database.DBManager;
import com.mesh.ui.home.Contact;

import java.util.ArrayList;

public class MessageViewModel extends AndroidViewModel { // To format data for MessageActivity
    private MutableLiveData<ArrayList<Message>> messages;
    private LocalBroadcastManager localBroadcastManager;
    private Contact contact;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null && intent.getExtras().getString(MessageActivity.CONTACT_NAME).equals(contact.getName()))
                loadMessages();
        }
    };

    public MessageViewModel(@NonNull Application application) {
        super(application);
        initialiseLocalBroadcastManager();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    LiveData<ArrayList<Message>> getMessages(Contact contact){
        this.contact = contact;
        messages = new MutableLiveData<>();
        loadMessages();
        return messages;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void loadMessages() {
        new Thread(() -> {
            DBManager dbManager = new DBManager(this.getApplication());
            dbManager.open();
            messages.postValue(dbManager.getMessages(contact.getID()));
            dbManager.close();
        }).start();
    }

    void initialiseLocalBroadcastManager() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this.getApplication());
        localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(com.mesh.MainActivity.RECEIVE_JSON));
    }

    void deregisterReceiver(){
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}