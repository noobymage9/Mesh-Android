package com.mesh.message;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.Image;
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
        protected TextView date;
        protected boolean isDate;

        public SpeechBubbleViewHolder(@NonNull View itemView, boolean isDate) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            date.setPaintFlags(date.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            this.isDate = isDate;
        }

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
        if (speechBubbleViewHolder.isDate) {
            speechBubbleViewHolder.date.setText(message.getDate());
        } else {
            speechBubbleViewHolder.message = message;
            speechBubbleViewHolder.timestamp.setText(message.getTime());
            if (message.isSelected())
                speechBubbleViewHolder.bubble.getBackground().setTint(context.getResources().getColor(R.color.accent));
            else
                speechBubbleViewHolder.bubble.getBackground().setTintList(null);
            if (messageActivity.isGroup()) {
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
                if (!contactColor.containsKey(message.getContactName())) {
                    contactColor.put(message.getContactName(), Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                }
                speechBubbleViewHolder.title.setTextColor(contactColor.get(message.getContactName()));

            }
            speechBubbleViewHolder.content.setText(message.getMessageContent());
            speechBubbleViewHolder.content.post(() -> {
                int lineCount = speechBubbleViewHolder.content.getLineCount();
                if (lineCount > 1) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) speechBubbleViewHolder.sourceIcon.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.incoming_bubble_text);
                    layoutParams.addRule(RelativeLayout.BELOW, 0);
                    speechBubbleViewHolder.sourceIcon.setLayoutParams(layoutParams);
                } else {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) speechBubbleViewHolder.sourceIcon.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, 0);
                    layoutParams.addRule(RelativeLayout.BELOW, R.id.incoming_bubble_timestamp);
                    speechBubbleViewHolder.sourceIcon.setLayoutParams(layoutParams);
                }
            });

            Image.setSource(message.getSourceApp(), messageActivity, speechBubbleViewHolder.sourceIcon);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).isDate()) return 0;
        else return 1;
    }

    @NonNull
    @Override
    public SpeechBubbleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0)
            return new SpeechBubbleViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_date_separator, viewGroup, false), true);
        else
             return new SpeechBubbleViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_speechbubble_message, viewGroup, false));
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