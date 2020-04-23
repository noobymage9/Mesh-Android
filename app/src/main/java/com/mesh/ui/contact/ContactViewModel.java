package com.mesh.ui.contact;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mesh.Database.DBManager;
import com.mesh.ui.home.Contact;

import java.util.ArrayList;

public class ContactViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<Contact>> contacts;


    public ContactViewModel(@NonNull Application application) {
        super(application);
    }

    LiveData<ArrayList<Contact>> getContacts(String filter){
        contacts = new MutableLiveData<>();
        loadContacts(filter);
        return contacts;
    }

    void loadContacts(String filter) {
        new Thread(() -> {
            DBManager dbManager = new DBManager(this.getApplication());
            dbManager.open();
            if (filter != null && filter.length() != 0)
                //contacts.postValue(dbManager.searchMessages(filter));
                ;
            else
                contacts.postValue(dbManager.getAllContacts());
            dbManager.close();
        }).start();
    }

}