package com.mesh;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class SettingActivity extends AppCompatActivity {

    private final int NUMBER_OF_SETTING = 3;
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
        setSupportActionBar(findViewById(R.id.toolbar));
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.menu_setting);
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

    private void initialiseRecyclerView() {
        recyclerView = findViewById(R.id.setting_list);
        recyclerView.setHasFixedSize(true);
        settingAdapter = new SettingAdapter(NUMBER_OF_SETTING, this);
        recyclerView.setAdapter(settingAdapter);
    }

}


