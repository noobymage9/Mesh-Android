package com.mesh.ui.contact;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
                contacts.postValue(dbManager.getAllMergeParentContacts());
            else
                contacts.postValue(dbManager.getAllMergeParentContacts());
            dbManager.close();
        }).start();
    }

}