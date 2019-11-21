package com.example.mesh.ui.message;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.mesh.R;
import com.example.mesh.ui.home.ContactAdapter;
import com.example.mesh.ui.home.ContactInfo;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private final String CONTACT_PARCEL = "Contact Parcel";
    private ContactInfo contactInfo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        contactInfo = (ContactInfo) getIntent().getParcelableExtra(CONTACT_PARCEL);
        actionBar.setTitle(contactInfo.getName());
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(new BitmapDrawable(getResources(), contactInfo.getBitmap()));
        actionBar.setDisplayUseLogoEnabled(true);

        // Recycler View
        RecyclerView recList = findViewById(R.id.messageList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);


        speechBubbleAdaptor speechBubbleAdaptor = new speechBubbleAdaptor(this);
        recList.setAdapter(speechBubbleAdaptor);
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


}
