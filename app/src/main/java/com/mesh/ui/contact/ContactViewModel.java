package com.mesh.ui.contact;

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

public class ContactViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<Contact>> contacts;
    private LocalBroadcastManager localBroadcastManager;
    protected static final String RECEIVE_JSON = "refresh_contact_adapter";
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            loadContacts("");
        }
    };


    public ContactViewModel(@NonNull Application application) {
        super(application);
        initialiseLocalBroadcastManager();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    LiveData<ArrayList<Contact>> getContacts(String filter){
        contacts = new MutableLiveData<>();
        loadContacts(filter);
        return contacts;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void loadContacts(String filter) {
        new Thread(() -> {
            DBManager dbManager = new DBManager(this.getApplication());
            dbManager.open();
            if (filter != null && filter.length() != 0)
                contacts.postValue(dbManager.getAllContactsForHome());
            else
                contacts.postValue(dbManager.getAllContactsForHome());
            dbManager.close();
        }).start();
    }

    private void initialiseLocalBroadcastManager() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this.getApplication());
        localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(RECEIVE_JSON));
    }
}