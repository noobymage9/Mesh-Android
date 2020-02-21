package com.mesh.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.Database.DBManager;
import com.mesh.message.MessageActivity;
import com.mesh.R;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private ArrayList<Contact> contactList;
    private Context context;
    public static final String CONTACT_PARCEL = "Contact Parcel";
    private final int SOURCE_APP_IMAGE_SIZE = 20;
    private int imageActualSize;

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView timestamp;
        protected View sourceApp;
        protected ImageView icon;

        public ContactViewHolder(final View v) {  //
            super(v);
            name = v.findViewById(R.id.contact_name);
            icon = v.findViewById(R.id.contact_icon);
            timestamp = v.findViewById(R.id.contact_timestamp);
            sourceApp = v.findViewById(R.id.source_app);
            v.setOnClickListener(view -> {
                Intent intent = new Intent(v.getContext(), MessageActivity.class);
                intent.putExtra(CONTACT_PARCEL, contactList.get(getAdapterPosition()));
                v.getContext().startActivity(intent);
            });
        }
    }

    public ContactAdapter(ArrayList<Contact> contactList, Context context) {
        this.contactList = contactList;
        this.context = context;
        imageActualSize = getSizeInDP(SOURCE_APP_IMAGE_SIZE);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        contactViewHolder.setIsRecyclable(false);
        View background = contactViewHolder.itemView.findViewById(R.id.inner_background);
        DBManager dbManager = new DBManager(context);
        dbManager.open();
        Contact contact = contactList.get(i);
        String contactName = contact.getName();
        contactViewHolder.timestamp.setText(dbManager.getContactLatestMessageTime(contact.getID()));
        if (contactName.length() > 15) {
            contactName = contactName.substring(0, 15);
            contactName += "...";
        }
        contactViewHolder.name.setText(contactName);

        ArrayList<String> sourceApps = dbManager.getContactMostUsedSourceApps(contact.getID());
        int j = 0;
        while (j <sourceApps.size()) {
            Drawable temp = null;
            switch (sourceApps.get(j)){
                case "WhatsApp": temp = context.getResources().getDrawable(R.mipmap.whatsapp_logo_foreground);
                break;
                case "Telegram": temp = context.getResources().getDrawable(R.mipmap.telegram_logo_foreground);
                break;
                default: break;
            }
            switch (j) {
                case 0: contactViewHolder.sourceApp.findViewById(R.id.first_source_app).setBackground(temp);
                break;
                case 1: contactViewHolder.sourceApp.findViewById(R.id.second_source_app).setBackground(temp);
                break;
                case 2: contactViewHolder.sourceApp.findViewById(R.id.third_source_app).setBackground(temp);
                break;
                default: break;
            }
            j++;
        }
        if (dbManager.isGroup(contact.getID())) {
            contactViewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.group_icon));
        } else {
            contactViewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.individual_icon));
        }
        dbManager.close();
        //contactViewHolder.icon.setImageBitmap(ci.icon);
    }


    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.home_card, viewGroup, false);
        return new ContactViewHolder(itemView);
    }

    private int getSizeInDP(int size) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (size*scale + 0.5f);
    }
}