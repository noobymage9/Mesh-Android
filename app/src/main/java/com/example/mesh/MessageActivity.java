package com.example.mesh;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.mesh.ui.home.ContactInfo;

public class MessageActivity extends AppCompatActivity {
    private final String CONTACT_PARCEL = "Contact Parcel";
    private ContactInfo contactInfo;
    private boolean firstTime = true; // switch to remove initial text in message activity
    private int counter = 0; // index the notification
    private TextView tv;


    //Your activity will respond to this action String
    public static final String RECEIVE_JSON = "com.your.package.RECEIVE_JSON";

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RECEIVE_JSON)) {

                String info = intent.getStringExtra("json");
                if (firstTime) {
                    firstTime = !firstTime;
                    tv.setText("");
                }
                tv.append("\n" + counter + ".\n");             // quick fix counter
                tv.append(info);
                counter++;
            }
        }
    };
    LocalBroadcastManager bManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        tv = findViewById(R.id.notificationText);
        tv.setMovementMethod(new ScrollingMovementMethod());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        contactInfo = (ContactInfo) getIntent().getParcelableExtra(CONTACT_PARCEL);
        actionBar.setTitle(contactInfo.getName());
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(new BitmapDrawable(getResources(), contactInfo.getBitmap()));
        actionBar.setDisplayUseLogoEnabled(true);

        // Create receiver (Temporary way to show log)
        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_JSON);
        bManager.registerReceiver(bReceiver, intentFilter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {  // Back button
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bManager.unregisterReceiver(bReceiver);
    }
}
