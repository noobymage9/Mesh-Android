package com.mesh.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.R;

import java.util.ArrayList;
import java.util.List;

public class SpeechBubbleAdaptor extends RecyclerView.Adapter<SpeechBubbleAdaptor.speechBubbleViewHolder> {

    private List<Message> messageList;
    private Context context;
    public SaveDeleteSnackbar saveDeleteSnackbar;

    public class speechBubbleViewHolder extends RecyclerView.ViewHolder {
        protected TextView content;
        protected ImageView sourceIcon;
        protected TextView timestamp;
        protected Message message;
        protected View background, bubble;

        public speechBubbleViewHolder(@NonNull View itemView) {  //
            super(itemView);
            content = itemView.findViewById(R.id.incoming_bubble_text);
            sourceIcon = itemView.findViewById(R.id.incoming_bubble_source);
            timestamp = itemView.findViewById(R.id.incoming_bubble_timestamp);
            background = itemView.findViewById(R.id.speech_bubble_background);
            bubble = itemView.findViewById(R.id.bubble);

            background.setOnClickListener(v -> {
                if (saveDeleteSnackbarExist()) {
                    saveDeleteSnackbar.dismiss();
                    for (Message message : messageList)
                        message.setSelected(false);
                    notifyDataSetChanged();
                }
            });

            bubble.setOnClickListener(v -> {
                if (saveDeleteSnackbarExist()) {
                    message.setSelected(!message.isSelected());
                    if (!someAreSelected()) {
                        saveDeleteSnackbar.dismiss();
                        saveDeleteSnackbar = null;
                    }
                    notifyDataSetChanged();
                }
            });

            bubble.setOnLongClickListener(v -> {
                if (saveDeleteSnackbar == null)
                    saveDeleteSnackbar = SaveDeleteSnackbar.make((ViewGroup) ((MessageActivity) context).findViewById(R.id.snackBar_location), SaveDeleteSnackbar.LENGTH_INDEFINITE, messageList);
                saveDeleteSnackbar.show();
                message.setSelected(true);
                notifyDataSetChanged();
                return true;
            });
        }
    }

    private boolean someAreSelected() {
        for (Message message : messageList)
            if (message.isSelected())
                return true;
            return false;
    }


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
        if (message.isSelected()) {
            speechBubbleViewHolder.bubble.setBackground(context.getResources().getDrawable(R.drawable.incoming_speech_bubble_highlighted));
        } else {
            speechBubbleViewHolder.bubble.setBackground(context.getResources().getDrawable(R.drawable.incoming_speech_bubble));
        }
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

    public boolean saveDeleteSnackbarExist() {
        return (saveDeleteSnackbar.isShown() || saveDeleteSnackbar != null);
    }

}