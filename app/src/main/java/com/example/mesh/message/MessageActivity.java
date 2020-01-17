package com.example.mesh.message;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
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
    private final String CONTACT_PARCEL = "Contact Parcel";         // from ContactAdapter
    private speechBubbleAdaptor speechBubbleAdaptor;
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private String contactName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        contactName = getIntent().getExtras().getString(CONTACT_PARCEL);
        MessageViewModel messageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        messageViewModel.getMessages(contactName).observe(this, this::initialiseRecyclerView) ;
        initialiseActionBar();
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

    private void initialiseActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setTitle("\t\t" + contactName); // Cheat fix for name and logo distance
        //actionBar.setLogo(new BitmapDrawable(getResources(), contactInfo.getBitmap()));
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void initialiseRecyclerView(ArrayList<String> messages) {
        recyclerView = findViewById(R.id.messageList);
        recyclerView.setHasFixedSize(true);
        speechBubbleAdaptor = new speechBubbleAdaptor(messages);
        recyclerView.setAdapter(speechBubbleAdaptor);
    }

}
