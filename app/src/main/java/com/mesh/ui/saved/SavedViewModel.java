package com.mesh.ui.saved;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mesh.Database.DBManager;
import com.mesh.message.UserCollection;

import java.util.ArrayList;

public class SavedViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<UserCollection>> userCollections;

    public SavedViewModel(Application application) {
        super(application);
    }

    public LiveData<ArrayList<UserCollection>> getUserCollectons() {
        userCollections = new MutableLiveData<>();
        loadUserCollections();
        return userCollections;
    }

    private void loadUserCollections() {
        new Thread(() -> {
            DBManager dbManager = new DBManager(this.getApplication());
            dbManager.open();
            userCollections.postValue(dbManager.getAllUserCollections());
            dbManager.close();
        }).start();
    }
}