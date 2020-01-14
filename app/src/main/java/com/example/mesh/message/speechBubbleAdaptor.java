package com.example.mesh.message;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mesh.R;

import java.util.ArrayList;
import java.util.List;

public class speechBubbleAdaptor extends RecyclerView.Adapter<speechBubbleAdaptor.speechBubbleViewHolder> {


    public void update(ArrayList<String> messages) {
        this.messageList = messages;
        notifyDataSetChanged();
    }

    /* This class is to serve as a mechanism to produce the "cards" for the recyclerView. Cards that are scrolled out of screen
            are reused for the next card that is entering the screen. This sorts of save resources.
         */
    public static class speechBubbleViewHolder extends RecyclerView.ViewHolder {
        protected TextView message;

        public speechBubbleViewHolder(final View v) {  //
            super(v);
            message = (TextView) v.findViewById(R.id.incoming_bubble);
        }
    }

    private int counter = 0; // index the notification
    private List<String> messageList = new ArrayList<>();

    //Your activity will respond to this action String



    public speechBubbleAdaptor(Context c, ArrayList<String> messageList) {

        // Create receiver (Temporary way to show log)
        /*
        bManager = LocalBroadcastManager.getInstance(c);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_JSON);
        bManager.registerReceiver(bReceiver, intentFilter);
         */

        this.messageList = messageList;
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public void onBindViewHolder(speechBubbleViewHolder speechBubbleViewHolder, int i) {
        String message = messageList.get(i);
        speechBubbleViewHolder.message.setText(message);
    }

    @Override
    public speechBubbleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.speech_bubble, viewGroup, false);

        return new speechBubbleViewHolder(itemView);
    }

}