package com.mesh;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.mesh.message.Message;

import java.util.ArrayList;

public class Setting extends AppCompatActivity {

    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private SettingAdapter settingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initialiseActionBar();
        initialiseRecyclerView();


    }

    private void initialiseActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.menu_setting);
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

    private void initialiseRecyclerView() {
        recyclerView = findViewById(R.id.setting_list);
        recyclerView.setHasFixedSize(true);
        settingAdapter = new SettingAdapter();
        recyclerView.setAdapter(settingAdapter);
    }

}


