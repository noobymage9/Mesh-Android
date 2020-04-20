package com.mesh.ui.saved;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.R;
import com.mesh.message.UserCollection;

import java.util.ArrayList;

public class UserGroupAdapter extends RecyclerView.Adapter<UserGroupAdapter.UserGroupViewHolder> {

    private ArrayList<UserCollection> userCollectionList;
    private Context context;
    static final String USER_COLLECTION_PARCEL = "User Collection";

    class UserGroupViewHolder extends RecyclerView.ViewHolder{
        private TextView name;

        UserGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.usercollection_name);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), SavedMessageActivity.class);
                intent.putExtra(USER_COLLECTION_PARCEL, userCollectionList.get(getAdapterPosition()));
                v.getContext().startActivity(intent);
            });
        }
    }

    UserGroupAdapter(ArrayList<UserCollection> userCollectionList, Context context) {
        this.userCollectionList = userCollectionList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_saved, parent, false);
        return new UserGroupAdapter.UserGroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserGroupViewHolder holder, int position) {
        holder.name.setText(userCollectionList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return userCollectionList.size();
    }

}
