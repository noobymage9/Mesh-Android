package com.mesh.ui.home;

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
import com.mesh.Database.DBManager;
import com.mesh.Image;
import com.mesh.R;
import com.mesh.message.MessageActivity;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private ArrayList<Contact> contactList;
    private final String imagePickerFragmentTag = "image_picker_dialog";
    public static final String CONTACT_PARCEL = "Contact Parcel";
    private final int SOURCE_APP_IMAGE_SIZE = 20;
    private HomeFragment homeFragment;
    private int imageActualSize;
    private Contact currentContactClicked;

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        protected Contact contact;
        protected TextView name;
        TextView timestamp;
        View sourceApp;
        protected ImageView icon;
        boolean expanded = false;

        ContactViewHolder(final View itemView) {  //
            super(itemView);
            name = itemView.findViewById(R.id.contact_name);
            icon = itemView.findViewById(R.id.contact_icon);
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (homeFragment.isMerge()) {
                        if (homeFragment.getMergeSnackbar() == null || !homeFragment.getMergeSnackbar().isShown()) {
                            homeFragment.getImagePickerDialog().show(homeFragment.getParentFragmentManager(), imagePickerFragmentTag);
                            currentContactClicked = contact;
                        } else {
                            homeFragment.dismissSnack();
                        }
                    }
                }
            });
            icon.setOnLongClickListener(new View.OnLongClickListener() { // temporary way.
                @Override
                public boolean onLongClick(View v) {
                    DBManager dbManager = new DBManager(homeFragment.getContext());
                    dbManager.open();
                    dbManager.insertIcon(null, contact.getID() + "");
                    dbManager.close();
                    return false;
                }
            });

            timestamp = itemView.findViewById(R.id.contact_timestamp);
            sourceApp = itemView.findViewById(R.id.source_app);
            itemView.setOnClickListener(view -> {
                if (homeFragment.isMerge()) {
                    if (homeFragment.getMergeSnackbar() == null || !homeFragment.getMergeSnackbar().isShown()) {
                        Intent intent = new Intent(itemView.getContext(), MessageActivity.class);
                        intent.putExtra(CONTACT_PARCEL, contactList.get(getAdapterPosition()));
                        itemView.getContext().startActivity(intent);
                    } else {
                        homeFragment.dismissSnack();
                    }
                }
            });
            itemView.setOnTouchListener((v1, event) -> {
                if (!homeFragment.isMerge()) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        homeFragment.getItemTouchHelper().startDrag(ContactViewHolder.this);
                        return true;
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    itemView.performClick();
                    return true;
                }
                return false;
            });
        }

        public boolean getExpanded() {
            return this.expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }
    }

    ContactAdapter(ArrayList<Contact> contactList, HomeFragment homeFragment) {
        this.contactList = contactList;
        this.homeFragment = homeFragment;
        imageActualSize = getSizeInDP(SOURCE_APP_IMAGE_SIZE);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        contactViewHolder.setIsRecyclable(false);
        DBManager dbManager = new DBManager(homeFragment.getContext());
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
            Image.setSource(sourceApps.get(j), homeFragment, sourceApp);
            j++;
        }
        if (dbManager.isGroup(contact.getID())) {
            Glide.with(homeFragment).load(contact.getProfilePic()).apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.all_group).into(contactViewHolder.icon);
        } else {
            Glide.with(homeFragment).load(contact.getProfilePic()).apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.all_individual).into(contactViewHolder.icon);
        }
        dbManager.close();
    }


    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_home, viewGroup, false);
        return new ContactViewHolder(itemView);
    }

    public int getSizeInDP(int size) {
        float scale = homeFragment.getResources().getDisplayMetrics().density;
        return (int) (size * scale + 0.5f);
    }

    public void merge(int from, int to) {
        Contact dragged = contactList.get(from);
        Contact target = contactList.get(to);
        homeFragment.reset();
        //TODO merge the two contact in db
        //notifyItemRemoved(from);
    }

    public ArrayList<Contact> getContactList() {
        return contactList;
    }

    public Contact getCurrentContactClicked() {
        return currentContactClicked;
    }
}