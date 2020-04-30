package com.mesh.ui.search;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mesh.Database.DBManager;
import com.mesh.message.Message;
import com.mesh.ui.home.Contact;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<Message>> messages;


    public SearchViewModel(@NonNull Application application) {
        super(application);
    }

    LiveData<ArrayList<Message>> getMessages(String filter){
        messages = new MutableLiveData<>();
        loadMessages(filter);
        return messages;
    }

    void loadMessages(String filter) {
        new Thread(() -> {
            DBManager dbManager = new DBManager(this.getApplication());
            dbManager.open();
            ArrayList<Message> temp = dbManager.searchMessages(filter);
            messages.postValue(temp);
            dbManager.close();
        }).start();
    }
}