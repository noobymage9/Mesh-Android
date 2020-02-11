package com.mesh.message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.R;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    public class TagViewHolder extends RecyclerView.ViewHolder{
        private CheckBox tags;
        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tags = itemView.findViewById(R.id.tag_items);
        }
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.speech_bubble, parent, false);

        return new TagAdapter.TagViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
