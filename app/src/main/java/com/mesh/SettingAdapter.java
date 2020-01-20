package com.mesh;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.mesh.Database.DBManager;
import com.mesh.message.MessageActivity;

import java.util.ArrayList;
import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int numberOfSetting;

    public class SortOrderViewHolder extends RecyclerView.ViewHolder {
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

    public class DeleteNotificationViewHolder extends RecyclerView.ViewHolder {
        protected TextView deleteNotificationName;
        protected Switch deleteNotificationResult;

        public DeleteNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteNotificationName = itemView.findViewById(R.id.delete_notification_text);
            deleteNotificationResult = itemView.findViewById(R.id.delete_notification_result);
            deleteNotificationResult.setOnCheckedChangeListener((buttonView, isChecked) -> {
                DBManager dbManager = new DBManager(context);
                dbManager.open();
                dbManager.updateDeleteNotficationsSetting(isChecked);
                dbManager.close();
            });
        }
    }

    public class ResetViewHolder extends  RecyclerView.ViewHolder {
        protected Button resetButton;

        public ResetViewHolder(@NonNull View itemView) {
            super(itemView);
            resetButton = itemView.findViewById(R.id.reset_button);
            resetButton.setOnClickListener(v -> {
                DBManager dbManager = new DBManager(context);
                dbManager.open();
                dbManager.restoreDefaultSettings();
                dbManager.close();
            });
        }
    }

    public SettingAdapter(int numberOfSetting, Context context){
        this.numberOfSetting = numberOfSetting;
        this.context = context;
    }

    @Override
    public int getItemViewType(int numberOfSetting) {
        return numberOfSetting % 3;
    }

    @Override
    public int getItemCount() {
        return numberOfSetting;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        switch(viewHolder.getItemViewType()){
            case 0:
                SortOrderViewHolder sortOrderViewHolder = (SortOrderViewHolder) viewHolder;
                return;
            case 1:
                DeleteNotificationViewHolder deleteNotificationViewHolder = (DeleteNotificationViewHolder) viewHolder;
                DBManager dbManager = new DBManager(context);
                dbManager.open();
                deleteNotificationViewHolder.deleteNotificationResult.setChecked(dbManager.getDeleteNotificationSetting());
                dbManager.close();
                return;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        switch (i) {
            case 0:
                View sortOrderItemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sort_card, viewGroup,false);
                return new SortOrderViewHolder(sortOrderItemView);
            case 1:
                View deleteNotificationView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.delete_notification_card, viewGroup,false);
                return new DeleteNotificationViewHolder(deleteNotificationView);
            case 2:
                View resetButtonView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reset_to_default_button, viewGroup, false);
                return new ResetViewHolder(resetButtonView);
            default: return null;
        }
    }
}
