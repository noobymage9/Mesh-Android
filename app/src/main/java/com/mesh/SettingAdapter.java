package com.mesh;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.mesh.message.MessageActivity;

import java.util.ArrayList;
import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class SortOrderViewHolder extends RecyclerView.ViewHolder {
        protected TextView settingName;
        protected TextView settingResult;

        public SortOrderViewHolder(@NonNull View itemView) {  //
            super(itemView);
            settingName = itemView.findViewById(R.id.sort_text);
            settingResult = itemView.findViewById(R.id.sort_result);
            itemView.setOnClickListener(view -> {
            });
        }
    }

    public static class DeleteNotificationViewHolder extends RecyclerView.ViewHolder {
        protected TextView deleteNotificationName;
        protected Switch deleteNotificationResult;

        public DeleteNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteNotificationName = itemView.findViewById(R.id.delete_notification_text);
            deleteNotificationResult = itemView.findViewById(R.id.delete_notification_result);
        }
    }

    public SettingAdapter(){}

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public int getItemCount() {

        return 2;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        switch(viewHolder.getItemViewType()) {
            case 0:
                SortOrderViewHolder sortOrderViewHolder = (SortOrderViewHolder) viewHolder;
                break;
            case 1:
                DeleteNotificationViewHolder deleteNotificationViewHolder = (DeleteNotificationViewHolder) viewHolder;
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        switch (i) {
            case 0:
                View sortOrderItemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sort_card,viewGroup,false);
                return new SortOrderViewHolder(sortOrderItemView);
            case 1:
                View deleteNotificationView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.delete_notification_card,viewGroup,false);
                return new DeleteNotificationViewHolder(deleteNotificationView);
            default: return null;
        }
    }
}
