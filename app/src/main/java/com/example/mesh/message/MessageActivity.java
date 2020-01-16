package com.example.mesh.message;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mesh.Database.DBManager;
import com.example.mesh.R;
import com.example.mesh.Setting;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    public static final String RECEIVE_JSON = "com.example.mesh.ui.message.RECEIVE_JSON";
    private final String CONTACT_PARCEL = "Contact Parcel";         // from ContactAdapter
    private speechBubbleAdaptor speechBubbleAdaptor;
    private LocalBroadcastManager localBroadcastManager;
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private String contactName;
    private ArrayList<String> messages;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            speechBubbleAdaptor.update(getMessages());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        contactName = getIntent().getExtras().getString(CONTACT_PARCEL);
        messages = getMessages();
        initialiseActionBar();
        initialiseRecyclerView();
        initialiseLocalBroadcastManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  // Back button
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_settings:
                Intent i = new Intent(this, Setting.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<String> getMessages() {
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        ArrayList<String> temp = dbManager.getMessages(contactName);
        dbManager.close();
        return temp;
    }

    private void initialiseActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setTitle("\t\t" + contactName); // Cheat fix for name and logo distance
        //actionBar.setLogo(new BitmapDrawable(getResources(), contactInfo.getBitmap()));
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void initialiseRecyclerView() {
        recyclerView = findViewById(R.id.messageList);
        recyclerView.setHasFixedSize(true);
        speechBubbleAdaptor = new speechBubbleAdaptor(messages);
        recyclerView.setAdapter(speechBubbleAdaptor);
    }

    private void initialiseLocalBroadcastManager() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_JSON);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

}
