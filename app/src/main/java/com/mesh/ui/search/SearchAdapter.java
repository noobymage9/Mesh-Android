package com.mesh.ui.search;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.Database.DBManager;
import com.mesh.Image;
import com.mesh.R;
import com.mesh.message.Message;
import com.mesh.message.MessageActivity;
import com.mesh.ui.home.Contact;
import com.mesh.ui.home.ConversationAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchBubbleViewHolder> {

    private ArrayList<Message> messageList;
    private SearchFragment searchFragment;
    private HashMap<String, Integer> contactColor;

    public class SearchBubbleViewHolder extends RecyclerView.ViewHolder {

        protected TextView messageContent, messageTimeStamp, messageContactName;
        protected ImageView sourceApp;

        public SearchBubbleViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.message_content);
            messageTimeStamp = itemView.findViewById(R.id.message_timestamp);
            messageContactName = itemView.findViewById(R.id.message_contact_name);
            sourceApp = itemView.findViewById(R.id.first_source_app);
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(itemView.getContext(), MessageActivity.class);
                String name = messageContactName.getText().toString();
                DBManager dbManager = new DBManager(searchFragment.getContext());
                dbManager.open();
                Contact temp = new Contact(dbManager.getContactID(name), name);
                dbManager.close();
                intent.putExtra(ConversationAdapter.CONVERSATION_PARCEL, temp);
                itemView.getContext().startActivity(intent);

            });
        }
    }




    public SearchAdapter(ArrayList<Message> messageList, SearchFragment searchFragment) {
        this.messageList = messageList;
        this.searchFragment = searchFragment;
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
        Image.setSource(message.getSourceApp(), searchFragment, searchBubbleViewHolder.sourceApp);
    }

    @NonNull
    @Override
    public SearchBubbleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_search, viewGroup, false);

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