package com.mesh.ui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mesh.Database.DBManager;
import com.mesh.Image;
import com.mesh.MainActivity;
import com.mesh.R;
import com.mesh.ui.home.Contact;
import com.mesh.ui.home.HomeFragment;
import com.mesh.ui.home.ImagePickerDialog;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private Contact contact;
    private ImageView contactIcon;
    private TextView contactName;
    private ImagePickerDialog imagePickerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        contact = getIntent().getExtras().getParcelable(ContactFragment.CONTACT_PARCEL);
        initialiseActionBar();
        contactName = findViewById(R.id.contact_name);
        contactName.setText(contact.getName());
        contactIcon = findViewById(R.id.contact_icon);
        //imagePickerDialog = new ImagePickerDialog(this);
        contactIcon.setOnClickListener(v -> {
            imagePickerDialog.show(getSupportFragmentManager(), MainActivity.ImagePickerFragmentTag);
        });

        DBManager dbManager = new DBManager(this);
        dbManager.open();
        if (dbManager.isGroup(contact.getID())) {
            Glide.with(this).load(contact.getProfilePic()).apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.all_group).into(contactIcon);
        } else {
            Glide.with(this).load(contact.getProfilePic()).apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.all_individual).into(contactIcon);
        }
        ArrayList<String> sourceApps = dbManager.getContactMostUsedSourceApps(contact.getID());
        int j = 0;
        while (j < sourceApps.size()) {
            ImageView sourceApp = null;
            switch (j) {
                case 0:
                    sourceApp = findViewById(R.id.first_source_app);
                    break;
                case 1:
                    sourceApp = findViewById(R.id.second_source_app);
                    break;
                case 2:
                    sourceApp = findViewById(R.id.third_source_app);
                    break;
                default:
                    break;
            }
            Image.setSource(sourceApps.get(j), this, sourceApp);
            j++;
        }
        dbManager.close();

    }


    private void initialiseActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == HomeFragment.PICK_IMAGE || requestCode == HomeFragment.CAPTURE_IMAGE) && data != null && data.getData() != null) {
            String realPath = Image.getPath(this, data.getData());
            DBManager dbManager = new DBManager(this);
            dbManager.open();
            dbManager.insertIcon(realPath, contact.getID() + "");
            dbManager.close();
            findViewById(R.id.activity_contact).invalidate();
        }
        imagePickerDialog.dismiss();
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void resetIcon() {
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        dbManager.insertIcon(null, contact.getID() + "");
        dbManager.close();
        imagePickerDialog.dismiss();
        findViewById(R.id.activity_contact).invalidate();
    }

}
