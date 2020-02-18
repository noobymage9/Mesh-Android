package com.mesh.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mesh.Database.DBManager;
import com.mesh.message.MessageActivity;
import com.mesh.R;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private ArrayList<Contact> contactList;
    private Context context;
    public static final String CONTACT_PARCEL = "Contact Parcel";

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
            sourceApp = v.findViewById(R.id.source_apps);
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

        if (!dbManager.isGroup(contact.getID())) {
            ArrayList<String> sourceApps = dbManager.getContactMostUsedSourceApps(contact.getID());
            for (String sourceApp : sourceApps) {
                ImageView temp = new ImageView(context);
                temp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                switch (sourceApp) {
                    case "WhatsApp":
                        temp.setImageDrawable(context.getResources().getDrawable(R.mipmap.whatsapp_logo_foreground));
                        break;
                    case "Telegram":
                        temp.setImageDrawable(context.getResources().getDrawable(R.mipmap.telegram_logo_foreground));
                        break;
                    default:
                        break;
                }
                temp.setVisibility(View.VISIBLE);
                ((ViewGroup) contactViewHolder.sourceApp).addView(temp);
            }
        } else {
            background.setBackground(context.getResources().getDrawable(R.drawable.group_background));
        }
        dbManager.close();
        //contactViewHolder.icon.setImageBitmap(ci.icon);
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.home_card, viewGroup, false);
        return new ContactViewHolder(itemView);
    }
}