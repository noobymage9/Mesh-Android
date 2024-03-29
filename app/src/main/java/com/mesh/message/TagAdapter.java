package com.mesh.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.R;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    private ArrayList<UserCollection> userCollections;
    private Context context;

    class TagViewHolder extends RecyclerView.ViewHolder{
        private CheckBox tags;
        private UserCollection userCollection;

        TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tags = itemView.findViewById(R.id.tag_items);
            tags.setOnCheckedChangeListener((buttonView, isChecked) -> userCollection.setSelected(isChecked));
        }
    }

    TagAdapter(ArrayList<UserCollection> userCollections, Context context) {
        this.userCollections = userCollections;
        this.context = context;
    }


    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_savedtag, parent, false);

        return new TagAdapter.TagViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.userCollection = userCollections.get(position);
        holder.tags.setText(userCollections.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return userCollections.size();
    }
}
