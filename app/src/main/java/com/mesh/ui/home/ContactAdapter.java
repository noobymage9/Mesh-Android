package com.mesh.ui.home;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

        ArrayList<String> sourceApps = dbManager.getContactMostUsedSourceApps(contact.getID());
        ImageView[] tempImages = new ImageView[sourceApps.size()];
        for (int j = 0; j < sourceApps.size(); j++) {

            tempImages[j] = new ImageView(context);
            switch (sourceApps.get(j)) {
                case "WhatsApp":
                    tempImages[j].setImageDrawable(context.getResources().getDrawable(R.mipmap.whatsapp_logo_foreground));
                    break;
                case "Telegram":
                    tempImages[j].setImageDrawable(context.getResources().getDrawable(R.mipmap.telegram_logo_foreground));
                    break;
                default:
                    break;
            }
            tempImages[j].setVisibility(View.VISIBLE);
            tempImages[j].setId(j);
            RelativeLayout.LayoutParams tempLayout = new RelativeLayout.LayoutParams(45, 45);
            if (j != 0) {
                tempLayout.addRule(RelativeLayout.LEFT_OF, tempImages[j - 1].getId());
                tempLayout.addRule(RelativeLayout.START_OF, tempImages[j - 1].getId());
            } else {
                tempLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
            ((ViewGroup) contactViewHolder.sourceApp).addView(tempImages[j], tempLayout);

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