package com.mesh.message;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.Database.DBManager;
import com.mesh.MainActivity;
import com.mesh.R;

import java.util.ArrayList;
import java.util.List;

public class SpeechBubbleAdaptor extends RecyclerView.Adapter<SpeechBubbleAdaptor.speechBubbleViewHolder> {

    public class speechBubbleViewHolder extends RecyclerView.ViewHolder {
        protected TextView content;
        protected ImageView sourceIcon;
        protected TextView timestamp;
        protected Message message;

        public speechBubbleViewHolder(@NonNull View itemView) {  //
            super(itemView);
            content = itemView.findViewById(R.id.incoming_bubble_text);
            sourceIcon = itemView.findViewById(R.id.incoming_bubble_source);
            timestamp = itemView.findViewById(R.id.incoming_bubble_timestamp);
            itemView.setOnLongClickListener(v -> {
                itemView.setBackground(context.getResources().getDrawable(R.drawable.incoming_speech_bubble_highlighted));
                PopupMenu popup = new PopupMenu(context, itemView);
                popup.getMenuInflater()
                        .inflate(R.menu.message_popup, popup.getMenu());
                DBManager dbManager = new DBManager(context);
                dbManager.open();
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getTitle().toString()) {
                        case "Save":

                            break;
                        case "Delete":
                            dbManager.deleteFromMessageTable(message.getID());
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(MainActivity.RECEIVE_JSON));
                            break;
                    }
                    return true;
                });
                popup.setOnDismissListener(menu -> itemView.setBackground(context.getResources().getDrawable(R.drawable.incoming_speech_bubble)));
                popup.show(); //showing popup menu
                return true;
            });
        }
    }

    private List<Message> messageList;
    private Context context;

    public SpeechBubbleAdaptor(ArrayList<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public void onBindViewHolder(speechBubbleViewHolder speechBubbleViewHolder, int i) {
        Message message = messageList.get(i);
        speechBubbleViewHolder.message = message;
        speechBubbleViewHolder.content.setText(message.getMessageContent());
        speechBubbleViewHolder.timestamp.setText(message.getTime());
        if (message.getMessageContent().length() > 100) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) speechBubbleViewHolder.sourceIcon.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.incoming_bubble_text);
            layoutParams.addRule(RelativeLayout.BELOW, 0);
            speechBubbleViewHolder.sourceIcon.setLayoutParams(layoutParams);
        }
        switch (message.getSourceApp()) {
            case "WhatsApp":
                speechBubbleViewHolder.sourceIcon.setImageResource(R.mipmap.whatsapp_logo_foreground);
                break;
            case "Telegram":
                speechBubbleViewHolder.sourceIcon.setImageResource(R.mipmap.telegram_logo_foreground);
                break;
        }
    }

    @Override
    public speechBubbleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.speech_bubble, viewGroup, false);

        return new speechBubbleViewHolder(itemView);
    }

}