package com.mesh;

import androidx.recyclerview.widget.RecyclerView;

import com.mesh.ui.home.Contact;

public abstract class BaseAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    abstract public Contact getCurrentContactClicked();

    public void refresh(String filePath) {
        getCurrentContactClicked().setProfilePic(filePath);
        notifyDataSetChanged();
    }

}
