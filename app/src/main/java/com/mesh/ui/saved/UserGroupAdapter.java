package com.mesh.ui.saved;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.R;
import com.mesh.message.UserCollection;
import com.mesh.ui.home.Contact;
import com.mesh.ui.home.ContactAdapter;

import java.util.ArrayList;

public class UserGroupAdapter extends RecyclerView.Adapter<UserGroupAdapter.UserGroupViewHolder> {

    private ArrayList<UserCollection> userCollectionList;
    private Context context;

    public class UserGroupViewHolder extends RecyclerView.ViewHolder{
        private TextView name;

        public UserGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.usercollection_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public UserGroupAdapter(ArrayList<UserCollection> userCollectionList, Context context) {
        this.userCollectionList = userCollectionList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.saved_card, parent, false);
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
