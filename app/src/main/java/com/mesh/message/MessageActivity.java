package com.mesh.message;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mesh.Database.DBManager;
import com.mesh.MainActivity;
import com.mesh.R;
import com.mesh.SettingActivity;
import com.mesh.ui.home.Contact;
import com.mesh.ui.home.ConversationAdapter;
import com.mesh.ui.search.SearchAdapter;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    public static final String CONTACT_NAME = "Contact Name";
    private SpeechBubbleAdaptor speechBubbleAdaptor;
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private Contact contact;
    private String searchMessage;
    private boolean isGroup;
    private ArrayList<Message> messages;
    private MessageViewModel messageViewModel;
    private int searchedMessageIndex;
    private ValueAnimator highlight;
    private ValueAnimator unHighlight;
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    animateHighlight(recyclerView.findViewHolderForAdapterPosition(searchedMessageIndex).itemView.findViewById(R.id.bubble));
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING: // Not working for some reason
                    highlight.cancel();
                    unHighlight.cancel();
            }
        }
    };

    public void animateHighlight(View view) {
        Drawable unwrappedDrawable = view.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        highlight.setDuration(500); // milliseconds
        highlight.addUpdateListener(animator -> DrawableCompat.setTint(wrappedDrawable, (int) animator.getAnimatedValue()));
        highlight.start();
        highlight.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                unHighlight.setDuration(2000); // milliseconds
                unHighlight.addUpdateListener(animator -> DrawableCompat.setTint(wrappedDrawable, (int) animator.getAnimatedValue()));
                unHighlight.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        recyclerView.removeOnScrollListener(onScrollListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        int colorFrom = getResources().getColor(R.color.FloralWhite);
        int colorTo = getResources().getColor(R.color.Tiger);
        highlight = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        unHighlight = ValueAnimator.ofObject(new ArgbEvaluator(), colorTo, colorFrom);
        contact = getIntent().getExtras().getParcelable(ConversationAdapter.CONVERSATION_PARCEL);

        DBManager dbManager = new DBManager(this);
        dbManager.open();
        isGroup = dbManager.isGroup(contact.getID());
        dbManager.close();
        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        messageViewModel.getMessages(contact).observe(this, this::initialiseRecyclerView);
        initialiseActionBar();
        searchMessage = getIntent().getExtras().getString(SearchAdapter.SEARCH_MESSAGE_PARCEL);

    }

    private int getSearchMessageIndex(String searchMessage) {
        for (int i = 0; i < this.messages.size(); i++) {
            Message temp = messages.get(i);
            if (!temp.isDate())
                if (temp.getMessageContent().equals(searchMessage)) return i;
        }
        return 0;
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
        View view = LayoutInflater.from(this).inflate(R.layout.layout_toolbar_messageactivity, null);
        ImageView contactIcon = view.findViewById(R.id.contact_icon);
        TextView contactName = view.findViewById(R.id.contact_name);
        if (contact.getProfilePic() != null)
            Glide.with(this).load(contact.getProfilePic()).apply(RequestOptions.circleCropTransform()).into(contactIcon);
        else
            contactIcon.setVisibility(View.GONE);
        contactName.setText(contact.toString());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.addView(view);

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initialiseRecyclerView(ArrayList<Message> messages) {
        this.messages = messages;
        recyclerView = findViewById(R.id.messageList);
        recyclerView.setHasFixedSize(true);
        speechBubbleAdaptor = new SpeechBubbleAdaptor(messages, this);
        recyclerView.setAdapter(speechBubbleAdaptor);
        if (searchMessage != null) {
            searchedMessageIndex = getSearchMessageIndex(searchMessage);
            recyclerView.removeOnScrollListener(onScrollListener);
            recyclerView.addOnScrollListener(onScrollListener);
            if (messages.size() > 20) {
                if (searchedMessageIndex < messages.size() / 2)
                    recyclerView.scrollToPosition(searchedMessageIndex + 20);
                else
                    recyclerView.scrollToPosition(searchedMessageIndex - 20);
                recyclerView.smoothScrollToPosition(searchedMessageIndex);
            } else { // Fix in future. Crash on no scroll
                // animate(recyclerView.findViewHolderForAdapterPosition(searchedMessageIndex).itemView);
            }
        } else
            resetRecyclerView();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        if (messages.size() > 20) {
            recyclerView.scrollToPosition(messages.size() - 20);
            recyclerView.smoothScrollToPosition(messages.size() - 1);
        }
    }

    public MessageViewModel getMessageViewModel(){
        return this.messageViewModel;
    }

    public SpeechBubbleAdaptor getSpeechBubbleAdaptor(){
        return this.speechBubbleAdaptor;
    }
}
