package com.mesh.message;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.Database.DBManager;
import com.mesh.R;

public class SaveDialog extends Dialog {

    private EditText editText;
    private Button add;
    private RecyclerView recyclerView;
    private TagAdapter tagAdapter;

    public SaveDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.save_dialog);

        initialiseEditText();
        initialiseButton();
        initialiseRecyclerView();
    }

    private void initialiseEditText() {
        editText = findViewById(R.id.add_field);

    }

    private void initialiseButton() {
        add = findViewById(R.id.add_button);
        add.setOnClickListener(v -> {
            DBManager dbManager = new DBManager(getContext());
            dbManager.open();
            if (editText.getText().length() != 0) {
                // insert tag into db
            }
            dbManager.close();
            initialiseRecyclerView();
        });
    }

    private void initialiseRecyclerView(){
        DBManager dbManager = new DBManager(getContext());
        dbManager.open();
        // getTags
        dbManager.close();
        recyclerView = findViewById(R.id.tag_list);
        recyclerView.setHasFixedSize(true);
        tagAdapter = new TagAdapter();
        recyclerView.setAdapter(tagAdapter);
    }

}
