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

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    /* This class is to serve as a mechanism to produce the "cards" for the recyclerView. Cards that are scrolled out of screen
        are reused for the next card that is entering the screen. This sorts of save resources.
     */

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        //protected TextView content;
        protected ImageView icon;
        private final String CONTACT_PARCEL = "Contact Parcel";

        public ContactViewHolder(final View v) {  //
            super(v);
            name = (TextView) v.findViewById(R.id.txtName);
            icon = (ImageView) v.findViewById(R.id.title);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(v.getContext(), MessageActivity.class);
                    intent.putExtra(CONTACT_PARCEL, contactList.get(getAdapterPosition()));
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    private static List<ContactInfo> contactList;
    public ContactAdapter(List<ContactInfo> contactList) {
        this.contactList = contactList;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        ContactInfo ci = contactList.get(i);
        contactViewHolder.name.setText(ci.name);
        contactViewHolder.icon.setImageBitmap(ci.icon);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);

        return new ContactViewHolder(itemView);
    }

}