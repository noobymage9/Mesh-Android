package com.mesh;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mesh.message.MessageActivity;

import java.util.ArrayList;
import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.SettingViewHolder> {
    private static List<String> contactNames;

    public static class SettingViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected ImageView icon;
        private final String CONTACT_PARCEL = "Contact Parcel";

        public SettingViewHolder(final View v) {  //
            super(v);
            name = v.findViewById(R.id.contact_name);
            icon = v.findViewById(R.id.contact_icon);
            v.setOnClickListener(view -> {
                Intent intent = new Intent(v.getContext(), MessageActivity.class);
                intent.putExtra(CONTACT_PARCEL, contactNames.get(getAdapterPosition()));
                v.getContext().startActivity(intent);
            });
        }
    }

    public SettingAdapter(ArrayList<String> contactNames) {
        SettingAdapter.contactNames = contactNames;
    }

    @Override
    public int getItemCount() {
        return contactNames.size();
    }

    @Override
    public void onBindViewHolder(SettingViewHolder contactViewHolder, int i) {
        String cn = contactNames.get(i);
        contactViewHolder.name.setText(cn);
        //contactViewHolder.icon.setImageBitmap(ci.icon);
    }

    @Override
    public SettingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);
        return new SettingViewHolder(itemView);
    }
}
