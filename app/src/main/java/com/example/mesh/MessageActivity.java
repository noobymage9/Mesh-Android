package com.example.mesh;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.mesh.ui.home.ContactInfo;

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


    }

    public boolean onOptionsItemSelected(MenuItem item) {
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
