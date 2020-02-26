package com.mesh.ui.search;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchBubbleViewHolder> {

    private ArrayList<Message> messageList;
    private Context context;
    private HashMap<String, Integer> contactColor;

    public class SearchBubbleViewHolder extends RecyclerView.ViewHolder {

        protected TextView messageContent, messageTimeStamp, messageContactName;
        protected View sourceApp;

        public SearchBubbleViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.message_content);
            messageTimeStamp = itemView.findViewById(R.id.message_timestamp);
            messageContactName = itemView.findViewById(R.id.message_contact_name);


        }
    }




    public SearchAdapter(ArrayList<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        if (messageList != null)
            return messageList.size();
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(SearchBubbleViewHolder searchBubbleViewHolder, int i) {
        Message message = messageList.get(i);
        searchBubbleViewHolder.messageContent.setText(message.getMessageContent());
        searchBubbleViewHolder.messageTimeStamp.setText(message.getTime());
        searchBubbleViewHolder.messageContactName.setText(message.getContactName());
    }

    @NonNull
    @Override
    public SearchBubbleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.search_card, viewGroup, false);

        return new SearchBubbleViewHolder(itemView);
    }

    private boolean someAreSelected() {
        for (Message message : messageList)
            if (message.isSelected())
                return true;
        return false;
    }

    public ArrayList<Message> getMessageList(){
        return this.messageList;
    }
}