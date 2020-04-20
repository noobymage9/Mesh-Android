package com.mesh.ui.saved;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.mesh.Database.DBManager;
import com.mesh.MainActivity;
import com.mesh.R;
import com.mesh.message.Message;
import com.mesh.message.UserCollection;

import java.util.List;

final class DeleteSnackbar extends BaseTransientBottomBar<DeleteSnackbar> {

    private DeleteSnackbar(@NonNull ViewGroup parent, @NonNull View content, @NonNull com.google.android.material.snackbar.ContentViewCallback contentViewCallback) {
        super(parent, content, contentViewCallback);
    }

    private static class ContentViewCallback implements com.google.android.material.snackbar.ContentViewCallback {

        // view inflated from custom layout
        private View content;

        ContentViewCallback(View content) {
            this.content = content;
        }

        @Override
        public void animateContentIn(int delay, int duration) {
            // add custom *in animations for your views
            // e.g. original snackbar uses alpha animation, from 0 to 1
            // ViewCompat.setScaleY(content, 0f); Deprecated
            ViewCompat.animate(content)
                    .scaleY(1f).setDuration(duration)
                    .setStartDelay(delay);
        }

        @Override
        public void animateContentOut(int delay, int duration) {
            // add custom *out animations for your views
            // e.g. original snackbar uses alpha animation, from 1 to 0
            // ViewCompat.setScaleY(content, 1f); Deprecated
            ViewCompat.animate(content)
                    .scaleY(0f)
                    .setDuration(duration)
                    .setStartDelay(delay);
        }
    }

    static DeleteSnackbar make(ViewGroup parent, @Duration int duration, List<Message> messageList, UserCollection userCollection) {
        // inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View content = inflater.inflate(R.layout.layout_snackbar_message, parent, false);
        Button save = content.findViewById(R.id.save_button);
        save.setVisibility(View.GONE);

        // create snackbar with custom view
        ContentViewCallback callback = new ContentViewCallback(content);
        DeleteSnackbar DeleteSnackbar = new DeleteSnackbar(parent, content, callback);
        DeleteSnackbar.getView().setPadding(0, 0, 0, 0);

        Button delete = content.findViewById(R.id.delete_button);
        delete.setOnClickListener(v -> {
            DBManager dbManager = new DBManager(parent.getContext());
            dbManager.open();
            for (Message message : messageList)
                if (message.isSelected())
                    dbManager.delete(message.getTagID(), message.getID());
            dbManager.close();
            DeleteSnackbar.dismiss();
            LocalBroadcastManager.getInstance(parent.getContext()).sendBroadcast(new Intent(MainActivity.RECEIVE_JSON));
        });

        DeleteSnackbar.setDuration(duration);
        return DeleteSnackbar;
    }
}
