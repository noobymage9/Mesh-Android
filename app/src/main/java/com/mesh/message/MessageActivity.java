package com.mesh.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.Database.DBManager;
import com.mesh.MainActivity;
import com.mesh.R;
import com.mesh.Setting;
import com.mesh.ui.home.Contact;
import com.mesh.ui.home.ContactAdapter;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    public static final String CONTACT_NAME = "Contact Name";
    private SpeechBubbleAdaptor speechBubbleAdaptor;
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private Contact contact;
    private boolean isGroup;
    private ArrayList<Message> messages;
    private MessageViewModel messageViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        contact = getIntent().getExtras().getParcelable(ContactAdapter.CONTACT_PARCEL);
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        isGroup = dbManager.isGroup(contact.getID());
        dbManager.close();
        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        messageViewModel.getMessages(contact).observe(this, this::initialiseRecyclerView);
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
        setSupportActionBar(findViewById(R.id.toolbar));
        actionBar = getSupportActionBar();
        actionBar.setTitle("\t\t" + contact); // Cheat fix for name and logo distance
        //actionBar.setLogo(new BitmapDrawable(getResources(), contactInfo.getBitmap()));
        //actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    public void initialiseRecyclerView(ArrayList<Message> messages) {
        this.messages = messages;
        recyclerView = findViewById(R.id.messageList);
        recyclerView.setHasFixedSize(true);
        if (speechBubbleAdaptor != null)
            if (speechBubbleAdaptor.saveDeleteSnackbarExist())
                speechBubbleAdaptor.getSaveDeleteSnackbar().dismiss();
        speechBubbleAdaptor = new SpeechBubbleAdaptor(messages, this);
        recyclerView.setAdapter(speechBubbleAdaptor);
        resetRecyclerView();
    }

    @Override
    public void onBackPressed() {
        if (speechBubbleAdaptor.saveDeleteSnackbarExist()) {
            speechBubbleAdaptor.getSaveDeleteSnackbar().dismiss();
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MainActivity.RECEIVE_JSON));
        } else {
            super.onBackPressed();
        }
    }

    public boolean isGroup(){
        return isGroup;
    }

    public void setRecyclerViewAboveSnackBar(){
        float scale = this.getResources().getDisplayMetrics().density;
        int bottomPadding = (int) (55*scale + 0.5f);
        recyclerView.setPadding(0, 0, 0, bottomPadding);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    public void resetRecyclerView(){
        recyclerView.setPadding(0, 0, 0, 0);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    public MessageViewModel getMessageViewModel(){
        return this.messageViewModel;
    }

    public SpeechBubbleAdaptor getSpeechBubbleAdaptor(){
        return this.speechBubbleAdaptor;
    }
}
