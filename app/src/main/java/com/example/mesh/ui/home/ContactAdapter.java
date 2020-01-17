package com.example.mesh.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mesh.message.MessageActivity;
import com.example.mesh.R;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private static List<String> contactNames;

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected ImageView icon;
        private final String CONTACT_PARCEL = "Contact Parcel";

        public ContactViewHolder(final View v) {  //
            super(v);
            name = v.findViewById(R.id.txtName);
            icon = v.findViewById(R.id.title);
            v.setOnClickListener(view -> {
                Intent intent = new Intent(v.getContext(), MessageActivity.class);
                intent.putExtra(CONTACT_PARCEL, contactNames.get(getAdapterPosition()));
                v.getContext().startActivity(intent);
            });
        }
    }

    public ContactAdapter(ArrayList<String> contactNames) {
        ContactAdapter.contactNames = contactNames;
    }

    @Override
    public int getItemCount() {
        return contactNames.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        String cn = contactNames.get(i);
        contactViewHolder.name.setText(cn);
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