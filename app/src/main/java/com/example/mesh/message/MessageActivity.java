package com.example.mesh.message;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mesh.Database.DBManager;
import com.example.mesh.R;
import com.example.mesh.Setting;
import com.example.mesh.ui.home.ContactInfo;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    public static final String RECEIVE_JSON = "com.example.mesh.ui.message.RECEIVE_JSON";
    private final String CONTACT_PARCEL = "Contact Parcel"; // from ContactAdapter
    private ContactInfo contactInfo;
    private speechBubbleAdaptor speechBubbleAdaptor;
    private LocalBroadcastManager bManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        contactInfo = (ContactInfo) getIntent().getParcelableExtra(CONTACT_PARCEL);

        ArrayList<String> messages = getMessages(contactInfo.getName());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("\t\t" + contactInfo.getName()); // Cheat fix for name and logo distance
        actionBar.setLogo(new BitmapDrawable(getResources(), contactInfo.getBitmap()));

        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Recycler View
        RecyclerView recList = findViewById(R.id.messageList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        speechBubbleAdaptor = new speechBubbleAdaptor(this, messages);
        recList.setAdapter(speechBubbleAdaptor);

        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_JSON);
        bManager.registerReceiver(bReceiver, intentFilter);


    }




    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            speechBubbleAdaptor.update(getMessages(contactInfo.getName()));
        }
    };


    private ArrayList<String> getMessages(String contactName)
    {
        ArrayList<String> messages = new ArrayList<String>();

        DBManager dbManager = new DBManager(this);
        dbManager.open();
        Cursor c = dbManager.fetchMessages(contactName);
        if (c.moveToFirst()) //c.getCount doesnt work, movetofirst resets cursor when view is created
        {
            do
            {
                messages.add(c.getString(0));
            } while (c.moveToNext());
        }

        dbManager.close();
        return messages;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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


}
