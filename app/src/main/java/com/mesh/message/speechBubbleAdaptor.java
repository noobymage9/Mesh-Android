package com.mesh.message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mesh.R;

import java.util.ArrayList;
import java.util.List;

public class speechBubbleAdaptor extends RecyclerView.Adapter<speechBubbleAdaptor.speechBubbleViewHolder> {

    public static class speechBubbleViewHolder extends RecyclerView.ViewHolder {
        protected TextView message;

        public speechBubbleViewHolder(final View v) {  //
            super(v);
            message = v.findViewById(R.id.incoming_bubble_text);
        }
    }

    private List<Message> messageList;
    private List<String> sourceAppList;
    //Your activity will respond to this action String

    public speechBubbleAdaptor(ArrayList<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public void onBindViewHolder(speechBubbleViewHolder speechBubbleViewHolder, int i) {
        Message message = messageList.get(i);
        speechBubbleViewHolder.message.setText(message.getMessageContent());
    }

    @Override
    public speechBubbleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.speech_bubble, viewGroup, false);

        return new speechBubbleViewHolder(itemView);
    }

    public void update(ArrayList<Message> messages) {
        this.messageList = messages;
        notifyDataSetChanged();
    }

}