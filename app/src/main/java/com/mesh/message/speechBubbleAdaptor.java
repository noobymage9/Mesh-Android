package com.mesh.message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mesh.R;

import java.util.ArrayList;
import java.util.List;

public class speechBubbleAdaptor extends RecyclerView.Adapter<speechBubbleAdaptor.speechBubbleViewHolder> {

    public static class speechBubbleViewHolder extends RecyclerView.ViewHolder {
        protected TextView message;
        protected ImageView sourceIcon;

        public speechBubbleViewHolder(final View v) {  //
            super(v);
            message = v.findViewById(R.id.incoming_bubble_text);
            sourceIcon = v.findViewById(R.id.incoming_bubble_source);
        }
    }

    private List<Message> messageList;

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
        switch (message.getSourceApp()) {
            case "WhatsApp": speechBubbleViewHolder.sourceIcon.setImageResource(R.mipmap.whatsapp_logo);
            case "Telegram": speechBubbleViewHolder.sourceIcon.setImageResource(R.mipmap.telegram_logo);
        }
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