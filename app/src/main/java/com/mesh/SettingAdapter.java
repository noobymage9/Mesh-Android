package com.mesh;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.Database.DBManager;
import com.mesh.Database.SortSetting;

public class SettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int numberOfSetting;

    public class SortOrderViewHolder extends RecyclerView.ViewHolder {
        TextView settingName;
        TextView settingResult;

        SortOrderViewHolder(@NonNull View itemView) {  //
            super(itemView);
            settingName = itemView.findViewById(R.id.sort_text);
            settingResult = itemView.findViewById(R.id.sort_result);
            itemView.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.setting_sort);

                builder.setItems(R.array.sort_array, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            DBManager dbManager = new DBManager(context);
                            dbManager.open();
                            dbManager.updateContactSortSetting(SortSetting.Recency);
                            settingResult.setText(R.string.default_sort);
                            dbManager.updateCustomContactOrderSetting(false);
                            dbManager.close();
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(MainActivity.RECEIVE_JSON));
                            break;
                        case 1:
                            DBManager dbManager1 = new DBManager(context);
                            dbManager1.open();
                            dbManager1.updateContactSortSetting(SortSetting.Frequency);
                            settingResult.setText(R.string.setting_sort_frequency);
                            dbManager1.updateCustomContactOrderSetting(false);
                            dbManager1.close();
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(MainActivity.RECEIVE_JSON));
                            break;
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            });
        }
    }

    public class DeleteNotificationViewHolder extends RecyclerView.ViewHolder {
        TextView deleteNotificationName;
        Switch deleteNotificationResult;

        DeleteNotificationViewHolder(@NonNull View itemView) {
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

    public class ExtraViewHolder extends  RecyclerView.ViewHolder {
        Button resetButton, backupButton;

        ExtraViewHolder(@NonNull View itemView) {
            super(itemView);
            resetButton = itemView.findViewById(R.id.reset_button);
            resetButton.setOnClickListener(v -> {
                DBManager dbManager = new DBManager(context);
                dbManager.open();
                dbManager.restoreDefaultSettings();
                notifyDataSetChanged();
                dbManager.close();
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(MainActivity.RECEIVE_JSON));
            });
            backupButton = itemView.findViewById(R.id.backup_button);
            backupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 7/3/2020 back up to firebase
                }
            });

        }
    }

    SettingAdapter(int numberOfSetting, Context context){
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
                DBManager dbManager = new DBManager(context);
                dbManager.open();
                SortOrderViewHolder sortOrderViewHolder = (SortOrderViewHolder) viewHolder;
                switch (dbManager.getContactSortSetting()) {
                    case Recency:
                        sortOrderViewHolder.settingResult.setText(R.string.default_sort);
                        break;
                    case Frequency:
                        sortOrderViewHolder.settingResult.setText(R.string.setting_sort_frequency);
                        break;
                    default:
                        sortOrderViewHolder.settingResult.setText(R.string.setting_sort_unknown);
                        break;
                }
                dbManager.close();
                break;
            case 1:
                DBManager dbManager1 = new DBManager(context);
                dbManager1.open();
                DeleteNotificationViewHolder deleteNotificationViewHolder = (DeleteNotificationViewHolder) viewHolder;
                deleteNotificationViewHolder.deleteNotificationResult.setChecked(dbManager1.getDeleteNotificationSetting());
                dbManager1.close();
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        switch (i) {
            case 0:
                View sortOrderItemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sort_card, viewGroup,false);
                return new SortOrderViewHolder(sortOrderItemView);
            case 1:
                View deleteNotificationView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.delete_notification_card, viewGroup,false);
                return new DeleteNotificationViewHolder(deleteNotificationView);
            case 2:
                View resetButtonView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.extras, viewGroup, false);
                return new ExtraViewHolder(resetButtonView);
            default: return null;
        }
    }

}
