package com.mesh.ui.saved;

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
import com.mesh.message.Message;
import com.mesh.message.SpeechBubbleAdaptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SavedMessageAdapter extends RecyclerView.Adapter<SavedMessageAdapter.SavedMessageViewHolder> {

    private ArrayList<Message> messageList;
    private Context context;
    private HashMap<String, Integer> contactColor;
    private DeleteSnackbar deleteSnackbar;
    private Random random;
    private SavedMessageActivity savedMessageActivity;

    public class SavedMessageViewHolder extends RecyclerView.ViewHolder{
        protected TextView content, timestamp, title;
        protected ImageView sourceIcon;
        protected Message message;
        protected View background, bubble;

        public SavedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.incoming_bubble_text);
            timestamp = itemView.findViewById(R.id.incoming_bubble_timestamp);
            title = itemView.findViewById(R.id.incoming_bubble_title);
            sourceIcon = itemView.findViewById(R.id.incoming_bubble_source);
            background = itemView.findViewById(R.id.speech_bubble_background);
            bubble = itemView.findViewById(R.id.bubble);

            background.setOnClickListener(v -> {
                if (deleteSnackbarExist()) {
                    deleteSnackbar.dismiss();
                    for (Message message : messageList)
                        message.setSelected(false);
                    savedMessageActivity.resetRecyclerView();
                    notifyDataSetChanged();
                }
            });

            bubble.setOnClickListener(v -> {
                if (deleteSnackbarExist()) {
                    message.setSelected(!message.isSelected());
                    if (!someAreSelected()) {
                        deleteSnackbar.dismiss();
                        savedMessageActivity.resetRecyclerView();
                        deleteSnackbar = null;
                    }
                    notifyDataSetChanged();
                }
            });

            bubble.setOnLongClickListener(v -> {
                if (deleteSnackbar == null)
                    deleteSnackbar = DeleteSnackbar.make(savedMessageActivity.findViewById(R.id.snackBar_location), DeleteSnackbar.LENGTH_INDEFINITE, messageList, savedMessageActivity.getUserCollection());
                deleteSnackbar.show();
                savedMessageActivity.setRecyclerViewAboveSnackBar();
                message.setSelected(true);
                notifyDataSetChanged();
                return true;
            });
        }
    }

    public SavedMessageAdapter(ArrayList<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
        this.contactColor = new HashMap<>();
        this.random = new Random();
        savedMessageActivity = ((SavedMessageActivity) context);
    }

    @NonNull
    @Override
    public SavedMessageAdapter.SavedMessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.speech_bubble, viewGroup, false);

        return new SavedMessageAdapter.SavedMessageViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(SavedMessageAdapter.SavedMessageViewHolder savedMessageViewHolder, int i) {
        Message message = messageList.get(i);
        savedMessageViewHolder.message = message;
        savedMessageViewHolder.timestamp.setText(message.getTime());
        if (message.isSelected())
            savedMessageViewHolder.bubble.getBackground().setTint(context.getResources().getColor(R.color.accent));
        else
            savedMessageViewHolder.bubble.getBackground().setTintList(null);

        String temp = message.getContactName();
        if (message.isFromGroup())  // If message is group
            temp = message.getGroupName() + " : " + temp; // debugger complained

        RelativeLayout.LayoutParams timestampLayoutParams = (RelativeLayout.LayoutParams) savedMessageViewHolder.timestamp.getLayoutParams();
        if (temp.length() > message.getMessageContent().length()) {
            timestampLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.incoming_bubble_title);
            timestampLayoutParams.addRule(RelativeLayout.END_OF, R.id.incoming_bubble_title);
        } else {
            timestampLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.incoming_bubble_text);
            timestampLayoutParams.addRule(RelativeLayout.END_OF, R.id.incoming_bubble_text);
        }
        savedMessageViewHolder.title.setVisibility(View.VISIBLE);
        savedMessageViewHolder.title.setText(temp);
        if (!contactColor.containsKey(message.getContactName())){
            contactColor.put(message.getContactName(), Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }
        savedMessageViewHolder.title.setTextColor(contactColor.get(message.getContactName()));


        savedMessageViewHolder.content.setText(message.getMessageContent());

        if (message.getMessageContent().length() > 100) {
            RelativeLayout.LayoutParams sourceIconLayoutParams = (RelativeLayout.LayoutParams) savedMessageViewHolder.sourceIcon.getLayoutParams();
            sourceIconLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.incoming_bubble_text);
            sourceIconLayoutParams.addRule(RelativeLayout.BELOW, 0);
            savedMessageViewHolder.sourceIcon.setLayoutParams(sourceIconLayoutParams);
        }
        switch (message.getSourceApp()) {
            case "WhatsApp":
                savedMessageViewHolder.sourceIcon.setImageResource(R.mipmap.whatsapp_logo_foreground);
                break;
            case "Telegram":
                savedMessageViewHolder.sourceIcon.setImageResource(R.mipmap.telegram_logo_foreground);
                break;
        }
    }

    private boolean someAreSelected() {
        for (Message message : messageList)
            if (message.isSelected())
                return true;
        return false;
    }

    public boolean deleteSnackbarExist() {
        return deleteSnackbar != null && deleteSnackbar.isShown();
    }

    public DeleteSnackbar getDeleteSnackbar() {
        return deleteSnackbar;
    }

    public ArrayList<Message> getMessageList(){
        return this.messageList;
    }


}
