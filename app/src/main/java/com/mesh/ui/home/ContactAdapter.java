package com.mesh.ui.home;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mesh.Database.DBManager;
import com.mesh.message.MessageActivity;
import com.mesh.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private static List<String> contactNames;
    private Context context;

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected ImageView icon;
        protected TextView timestamp;
        private final String CONTACT_PARCEL = "Contact Parcel";

        public ContactViewHolder(final View v) {  //
            super(v);
            name = v.findViewById(R.id.contact_name);
            icon = v.findViewById(R.id.contact_icon);
            timestamp = v.findViewById(R.id.contact_timestamp);
            v.setOnClickListener(view -> {
                Intent intent = new Intent(v.getContext(), MessageActivity.class);
                intent.putExtra(CONTACT_PARCEL, contactNames.get(getAdapterPosition()));
                v.getContext().startActivity(intent);
            });
        }
    }

    public ContactAdapter(ArrayList<String> contactNames, Context context) {
        ContactAdapter.contactNames = contactNames;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return contactNames.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        DBManager dbManager = new DBManager(context);
        dbManager.open();
        String contactName = contactNames.get(i);
        contactViewHolder.timestamp.setText(dbManager.getLatestMessage(contactName).getTime());
        if (contactName.length() > 15) {
            contactName = contactName.substring(0, 15);
            contactName += "...";
        }
        contactViewHolder.name.setText(contactName);
        dbManager.close();
        //contactViewHolder.icon.setImageBitmap(ci.icon);
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);
        return new ContactViewHolder(itemView);
    }
}