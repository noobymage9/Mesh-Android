package com.mesh.message;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mesh.MainActivity;
import com.mesh.R;
import com.mesh.Setting;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    private final String CONTACT_PARCEL = "Contact Parcel";         // from ContactAdapter
    private SpeechBubbleAdaptor speechBubbleAdaptor;
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private String contactName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        contactName = getIntent().getExtras().getString(CONTACT_PARCEL);
        MessageViewModel messageViewModel = ViewModelProviders.of
                (this).get(MessageViewModel.class);
        messageViewModel.getMessages(contactName).observe(this, this::initialiseRecyclerView) ;
        initialiseActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    private void initialiseRecyclerView(ArrayList<Message> messages) {
        recyclerView = findViewById(R.id.messageList);
        recyclerView.setHasFixedSize(true);
        speechBubbleAdaptor = new SpeechBubbleAdaptor(messages, this);
        recyclerView.setAdapter(speechBubbleAdaptor);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    @Override
    public void onBackPressed() {
        if (speechBubbleAdaptor.snackBarUp) {
            speechBubbleAdaptor.saveDeleteSnackbar.dismiss();
            speechBubbleAdaptor.snackBarUp = false;
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MainActivity.RECEIVE_JSON));
        } else {
            super.onBackPressed();
        }
    }
}
