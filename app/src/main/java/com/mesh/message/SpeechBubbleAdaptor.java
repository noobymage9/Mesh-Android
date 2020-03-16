package com.mesh.message;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SpeechBubbleAdaptor extends RecyclerView.Adapter<SpeechBubbleAdaptor.SpeechBubbleViewHolder> {

    private ArrayList<Message> messageList;
    private Context context;
    private HashMap<String, Integer> contactColor;
    private SaveDeleteSnackbar saveDeleteSnackbar;
    private Random random;
    private MessageActivity messageActivity;

    public class SpeechBubbleViewHolder extends RecyclerView.ViewHolder {
        protected TextView content, timestamp, title;
        protected ImageView sourceIcon;
        protected Message message;
        protected View background, bubble;

        public SpeechBubbleViewHolder(@NonNull View itemView) {  //
            super(itemView);
            content = itemView.findViewById(R.id.incoming_bubble_text);
            timestamp = itemView.findViewById(R.id.incoming_bubble_timestamp);
            title = itemView.findViewById(R.id.incoming_bubble_title);
            sourceIcon = itemView.findViewById(R.id.incoming_bubble_source);
            background = itemView.findViewById(R.id.speech_bubble_background);
            bubble = itemView.findViewById(R.id.bubble);

            background.setOnClickListener(v -> {
                if (saveDeleteSnackbarExist()) {
                    saveDeleteSnackbar.dismiss();
                    for (Message message : messageList)
                        message.setSelected(false);
                    messageActivity.resetRecyclerView();
                    notifyDataSetChanged();
                }
            });

            bubble.setOnClickListener(v -> {
                if (saveDeleteSnackbarExist()) {
                    message.setSelected(!message.isSelected());
                    if (!someAreSelected()) {
                        saveDeleteSnackbar.dismiss();
                        messageActivity.resetRecyclerView();
                        saveDeleteSnackbar = null;
                    }
                    notifyDataSetChanged();
                }
            });

            bubble.setOnLongClickListener(v -> {
                if (saveDeleteSnackbar == null)
                    saveDeleteSnackbar = SaveDeleteSnackbar.make(messageActivity.findViewById(R.id.snackBar_location), SaveDeleteSnackbar.LENGTH_INDEFINITE, messageList);
                saveDeleteSnackbar.show();
                messageActivity.setRecyclerViewAboveSnackBar();
                message.setSelected(true);
                notifyDataSetChanged();
                return true;
            });
        }
    }




    public SpeechBubbleAdaptor(ArrayList<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
        this.contactColor = new HashMap<>();
        this.random = new Random();
        messageActivity = ((MessageActivity) context);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(SpeechBubbleViewHolder speechBubbleViewHolder, int i) {
        Message message = messageList.get(i);
        speechBubbleViewHolder.message = message;
        speechBubbleViewHolder.timestamp.setText(message.getTime());
        if (message.isSelected())
            speechBubbleViewHolder.bubble.getBackground().setTint(context.getResources().getColor(R.color.accent));
        else
            speechBubbleViewHolder.bubble.getBackground().setTintList(null);
        if (messageActivity.isGroup()){
            RelativeLayout.LayoutParams timestampLayoutParams = (RelativeLayout.LayoutParams) speechBubbleViewHolder.timestamp.getLayoutParams();
            if (message.getContactName().length() > message.getMessageContent().length()) {
                timestampLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.incoming_bubble_title);
                timestampLayoutParams.addRule(RelativeLayout.END_OF, R.id.incoming_bubble_title);
            } else {
                timestampLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.incoming_bubble_text);
                timestampLayoutParams.addRule(RelativeLayout.END_OF, R.id.incoming_bubble_text);
            }
            speechBubbleViewHolder.title.setVisibility(View.VISIBLE);
            speechBubbleViewHolder.title.setText(message.getContactName());
            if (!contactColor.containsKey(message.getContactName())){
                contactColor.put(message.getContactName(), Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            }
            speechBubbleViewHolder.title.setTextColor(contactColor.get(message.getContactName()));

        }
        speechBubbleViewHolder.content.setText(message.getMessageContent());
        if (speechBubbleViewHolder.content.getLineCount() > 2) {
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
            case "SMS":
                speechBubbleViewHolder.sourceIcon.setImageResource(R.mipmap.sms_logo);
        }
    }

    @NonNull
    @Override
    public SpeechBubbleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.speech_bubble, viewGroup, false);

        return new SpeechBubbleViewHolder(itemView);
    }

    private boolean someAreSelected() {
        for (Message message : messageList)
            if (message.isSelected())
                return true;
        return false;
    }

    public boolean saveDeleteSnackbarExist() {
        return saveDeleteSnackbar != null && saveDeleteSnackbar.isShown();
    }

    public SaveDeleteSnackbar getSaveDeleteSnackbar() {
        return saveDeleteSnackbar;
    }

    public ArrayList<Message> getMessageList(){
        return this.messageList;
    }
}