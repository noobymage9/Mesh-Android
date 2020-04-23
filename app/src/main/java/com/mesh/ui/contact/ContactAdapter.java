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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mesh.Database.DBManager;
import com.mesh.Image;
import com.mesh.R;
import com.mesh.message.Message;
import com.mesh.message.MessageActivity;
import com.mesh.ui.home.Contact;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactBubbleViewHolder> {

    private ArrayList<Contact> contactList;
    private ContactFragment contactFragment;
    private HashMap<String, Integer> contactColor;

    public class ContactBubbleViewHolder extends RecyclerView.ViewHolder {

        protected TextView contactName;
        protected ImageView contactIcon;
        protected Contact contact;
        public ContactBubbleViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            contactIcon = itemView.findViewById(R.id.contact_icon);
        }
    }




    public ContactAdapter(ArrayList<Contact> contactList, ContactFragment contactFragment) {
        this.contactList = contactList;
        this.contactFragment = contactFragment;
    }

    @Override
    public int getItemCount() {
        if (contactList != null)
            return contactList.size();
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ContactBubbleViewHolder contactBubbleViewHolder, int i) {
        Contact contact = contactList.get(i);
        contactBubbleViewHolder.contact = contact;
        String contactName = contact.getName();
        if (contactName.length() > 15) {
            contactName = contactName.substring(0, 15);
            contactName += "...";
        }
        contactBubbleViewHolder.contactName.setText(contactName);
        DBManager dbManager = new DBManager(contactFragment.getContext());
        dbManager.open();
        if (dbManager.isGroup(contact.getID())) {
            Glide.with(contactFragment).load(contact.getProfilePic()).apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.all_group).into(contactBubbleViewHolder.contactIcon);
        } else {
            Glide.with(contactFragment).load(contact.getProfilePic()).apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.all_individual).into(contactBubbleViewHolder.contactIcon);
        }
        dbManager.close();
    }

    @NonNull
    @Override
    public ContactBubbleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_contact, viewGroup, false);

        return new ContactBubbleViewHolder(itemView);
    }


    public ArrayList<Contact> getContactList(){
        return contactList;
    }
}