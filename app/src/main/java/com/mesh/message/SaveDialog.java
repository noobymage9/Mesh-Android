package com.mesh.message;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.Database.DBManager;
import com.mesh.MainActivity;
import com.mesh.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SaveDialog extends Dialog {

    private EditText editText;
    private Button add, save;
    private RecyclerView recyclerView;
    private TagAdapter tagAdapter;
    private ArrayList<UserCollection> userCollections;
    private  MessageActivity messageActivity;

    public SaveDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.save_dialog);
        messageActivity = ((MessageActivity) ((ContextWrapper) getContext()).getBaseContext());

        initialiseEditText();
        initialiseAddButton();
        initialiseSaveButton();
        initialiseRecyclerView("");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initialiseSaveButton() {
        save = findViewById(R.id.save_bar);
        save.setOnClickListener(v -> {
            boolean selectedSome = false;
            for (UserCollection userCollection : userCollections) {
                if (userCollection.isSelected()) {
                    for (Message message : messageActivity.getSpeechBubbleAdaptor().getMessageList()) {
                        if (message.isSelected()) {
                            selectedSome = true;
                            DBManager dbManager = new DBManager(getContext());
                            dbManager.open();
                            dbManager.insertTag(message.getID(), userCollection.getID());
                        }
                    }
                    userCollection.setSelected(false);
                }
            }
            if (selectedSome)
                this.dismiss();
        });
    }

    private void initialiseEditText() {
        editText = findViewById(R.id.add_field);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    add.performClick();
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO: 17/2/2020 uncomment when DB is ready
                //initialiseRecyclerView(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initialiseAddButton() {
        add = findViewById(R.id.add_button);
        add.setOnClickListener(v -> {
            boolean added = false;
            DBManager dbManager = new DBManager(getContext());
            dbManager.open();
            if (editText.getText().length() != 0) {
                dbManager.insertUserCollection(editText.getText().toString());
                added = true;
            }
            dbManager.close();
            if (added) {
                editText.setText("");
                initialiseRecyclerView("");
                recyclerView.scrollToPosition(userCollections.size() - 1);
            }
        });
    }

    private void initialiseRecyclerView(String filter){
        DBManager dbManager = new DBManager(getContext());
        dbManager.open();
        if (filter.equals(""))
            userCollections = dbManager.getAllUserCollections();
        else
            // TODO: 17/2/2020 get tags based on filter
        dbManager.close();
        recyclerView = findViewById(R.id.tag_list);
        recyclerView.setHasFixedSize(true);
        tagAdapter = new TagAdapter(userCollections, getContext());
        recyclerView.setAdapter(tagAdapter);
    }

    @Override
    public void dismiss() {
        for (UserCollection userCollection : userCollections) {
            if (userCollection.isSelected()) {
                userCollection.setSelected(false);
            }
        }
        for (Message message : messageActivity.getSpeechBubbleAdaptor().getMessageList())
            if (message.isSelected()) {
                message.setSelected(false);
            }
        messageActivity.resetRecyclerView();
        messageActivity.initialiseRecyclerView(messageActivity.getSpeechBubbleAdaptor().getMessageList());
        if (messageActivity.getSpeechBubbleAdaptor().getSaveDeleteSnackbar() != null)
            messageActivity.getSpeechBubbleAdaptor().getSaveDeleteSnackbar().dismiss();
        super.dismiss();
    }

    @Override
    public void onBackPressed() {
        this.dismiss();
    }

}
