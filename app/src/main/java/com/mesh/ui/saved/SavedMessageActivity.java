package com.mesh.ui.saved;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.R;
import com.mesh.SettingActivity;
import com.mesh.message.Message;
import com.mesh.message.UserCollection;

import java.util.ArrayList;

public class SavedMessageActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private UserCollection userCollection;
    private ArrayList<Message> messages;
    private SavedMessageAdapter savedMessageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        userCollection = getIntent().getParcelableExtra(UserGroupAdapter.USER_COLLECTION_PARCEL);
        SavedMessageViewModel messageViewModel = new ViewModelProvider(this).get(SavedMessageViewModel.class);
        messageViewModel.getMessages(userCollection).observe(this, this::initialiseRecyclerView);
        initialiseActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_overflow, menu);
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
                Intent i = new Intent(this, SettingActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initialiseActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        actionBar = getSupportActionBar();
        actionBar.setTitle("\t\t" + userCollection); // Cheat fix for name and logo distance
        //actionBar.setLogo(new BitmapDrawable(getResources(), contactInfo.getBitmap()));
        //actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    public void initialiseRecyclerView(ArrayList<Message> messages) {
        this.messages = messages;
        recyclerView = findViewById(R.id.messageList);
        recyclerView.setHasFixedSize(true);
        if (savedMessageAdapter != null)
            if (savedMessageAdapter.deleteSnackbarExist())
                savedMessageAdapter.getDeleteSnackbar().dismiss();
        savedMessageAdapter = new SavedMessageAdapter(messages, this);
        recyclerView.setAdapter(savedMessageAdapter);
        resetRecyclerView();
    }

    public void setRecyclerViewAboveSnackBar(){
        float scale = this.getResources().getDisplayMetrics().density;
        int bottomPadding = (int) (55*scale + 0.5f);
        recyclerView.setPadding(0, 0, 0, bottomPadding);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    public void resetRecyclerView(){
        recyclerView.setPadding(0, 0, 0, 0);
        if (messages.size() > 20) {
            recyclerView.scrollToPosition(messages.size() - 20);
            recyclerView.smoothScrollToPosition(messages.size() - 1);
        }
    }

    public UserCollection getUserCollection() {
        return userCollection;
    }
}
