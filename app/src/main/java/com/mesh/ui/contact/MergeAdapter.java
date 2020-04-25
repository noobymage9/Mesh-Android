package com.mesh.ui.contact;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mesh.Database.DBManager;
import com.mesh.MainActivity;
import com.mesh.R;
import com.mesh.message.MessageActivity;
import com.mesh.ui.home.Contact;

import java.util.ArrayList;

public class MergeAdapter extends RecyclerView.Adapter<MergeAdapter.MergeItemViewHolder> {

    private ArrayList<Contact> mergedContactList;
    private ContactDetailActivity contactDetailActivity;

    public class MergeItemViewHolder extends RecyclerView.ViewHolder {

        protected TextView contactName;
        protected TextView unmerge;
        protected Contact contact;

        public MergeItemViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            unmerge = itemView.findViewById(R.id.unmerge);
            unmerge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBManager dbManager = new DBManager(contactDetailActivity);
                    dbManager.open();
                    dbManager.unMergeContacts(contact.getID(), contactDetailActivity.getParentContact().getID());
                    dbManager.close();
                    mergedContactList.remove(contact);
                    notifyDataSetChanged();
                    Intent intent = new Intent(ContactViewModel.RECEIVE_JSON);
                    LocalBroadcastManager.getInstance(contactDetailActivity).sendBroadcast(intent);
                }
            });
        }
    }




    public MergeAdapter(ArrayList<Contact> mergedContactList, ContactDetailActivity contactDetailActivity) {
        this.mergedContactList = mergedContactList;
        this.contactDetailActivity = contactDetailActivity;
    }

    @Override
    public int getItemCount() {
        if (mergedContactList != null)
            return mergedContactList.size();
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MergeItemViewHolder mergeItemViewHolder, int i) {
        Contact contact = mergedContactList.get(i);
        mergeItemViewHolder.contact = contact;
        String contactName = contact.getName();
        if (contactName.length() > 15) {
            contactName = contactName.substring(0, 15);
            contactName += "...";
        }
        mergeItemViewHolder.contactName.setText(contactName);
    }

    @NonNull
    @Override
    public MergeItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_merged_contact, viewGroup, false);

        return new MergeItemViewHolder(itemView);
    }


    public ArrayList<Contact> getContactList(){
        return mergedContactList;
    }
}