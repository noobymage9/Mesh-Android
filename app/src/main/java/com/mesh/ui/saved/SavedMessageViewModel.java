package com.mesh.ui.saved;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mesh.Database.DBManager;
import com.mesh.message.Message;
import com.mesh.message.UserCollection;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SavedMessageViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<Message>> messages;
    private UserCollection userCollection;

    public SavedMessageViewModel(@NonNull Application application) {
        super(application);
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

}
