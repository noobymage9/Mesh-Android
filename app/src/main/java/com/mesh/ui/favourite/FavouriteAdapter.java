package com.mesh.ui.favourite;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mesh.BaseAdapter;
import com.mesh.Database.DBManager;
import com.mesh.Image;
import com.mesh.MainActivity;
import com.mesh.R;
import com.mesh.message.MessageActivity;
import com.mesh.ui.favourite.FavouriteFragment;
import com.mesh.ui.home.Contact;
import com.mesh.ui.home.ConversationAdapter;

import java.util.ArrayList;

public class FavouriteAdapter extends BaseAdapter<FavouriteAdapter.FavouriteViewHolder> {

    private ArrayList<Contact> contactList;
    private final int SOURCE_APP_IMAGE_SIZE = 20;
    private FavouriteFragment favouriteFragment;
    private int imageActualSize;
    private Contact currentContactClicked;

    public class FavouriteViewHolder extends RecyclerView.ViewHolder {
        protected Contact contact;
        protected TextView name;
        TextView timestamp;
        View sourceApp;
        protected ImageView icon;

        FavouriteViewHolder(final View itemView) {  //
            super(itemView);
            name = itemView.findViewById(R.id.contact_name);
            icon = itemView.findViewById(R.id.contact_icon);
            icon.setOnClickListener(v -> {
                favouriteFragment.getImagePickerDialog().show(favouriteFragment.getParentFragmentManager(), MainActivity.ImagePickerFragmentTag);
                currentContactClicked = contact;

            });

            timestamp = itemView.findViewById(R.id.contact_timestamp);
            sourceApp = itemView.findViewById(R.id.source_app);
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(itemView.getContext(), MessageActivity.class);
                intent.putExtra(ConversationAdapter.CONVERSATION_PARCEL, contactList.get(getAdapterPosition()));
                itemView.getContext().startActivity(intent);
            });
        }
    }

    public FavouriteAdapter(ArrayList<Contact> contactList, FavouriteFragment favouriteFragment) {
        this.contactList = contactList;
        this.favouriteFragment = favouriteFragment;
        imageActualSize = getSizeInDP(SOURCE_APP_IMAGE_SIZE);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(FavouriteViewHolder contactViewHolder, int i) {
        contactViewHolder.setIsRecyclable(false);
        DBManager dbManager = new DBManager(favouriteFragment.getContext());
        dbManager.open();
        contactViewHolder.contact = contactList.get(i);
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
        while (j < sourceApps.size()) {
            ImageView sourceApp = null;
            switch (j) {
                case 0:
                    sourceApp = contactViewHolder.sourceApp.findViewById(R.id.first_source_app);
                    break;
                case 1:
                    sourceApp = contactViewHolder.sourceApp.findViewById(R.id.second_source_app);
                    break;
                case 2:
                    sourceApp = contactViewHolder.sourceApp.findViewById(R.id.third_source_app);
                    break;
                default:
                    break;
            }
            Image.setSource(sourceApps.get(j), favouriteFragment, sourceApp);
            j++;
        }
        if (dbManager.isGroup(contact.getID())) {
            Glide.with(favouriteFragment).load(contact.getProfilePic()).apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.all_group).into(contactViewHolder.icon);
        } else {
            Glide.with(favouriteFragment).load(contact.getProfilePic()).apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.all_individual).into(contactViewHolder.icon);
        }
        dbManager.close();
    }


    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_home, viewGroup, false);
        return new FavouriteViewHolder(itemView);
    }

    public int getSizeInDP(int size) {
        float scale = favouriteFragment.getResources().getDisplayMetrics().density;
        return (int) (size * scale + 0.5f);
    }


    public ArrayList<Contact> getContactList() {
        return contactList;
    }

    public Contact getCurrentContactClicked() {
        return currentContactClicked;
    }
}