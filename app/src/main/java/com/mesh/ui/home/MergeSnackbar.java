package com.mesh.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.mesh.Database.DBManager;
import com.mesh.MainActivity;
import com.mesh.R;
import com.mesh.message.Message;
import com.mesh.message.UserCollection;

import java.util.List;

final class MergeSnackbar extends BaseTransientBottomBar<MergeSnackbar> {

    private MergeSnackbar(@NonNull ViewGroup parent, @NonNull View content, @NonNull com.google.android.material.snackbar.ContentViewCallback contentViewCallback) {
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

    static MergeSnackbar make(ViewGroup parent, @Duration int duration, int from, int to, RecyclerView recyclerView) {
        // inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View content = inflater.inflate(R.layout.save_delete_bar, parent, false);
        Button save = content.findViewById(R.id.save_button);
        save.setVisibility(View.GONE);

        // create snackbar with custom view
        ContentViewCallback callback = new ContentViewCallback(content);
        MergeSnackbar mergeSnackbar = new MergeSnackbar(parent, content, callback);
        mergeSnackbar.getView().setPadding(0, 0, 0, 0);

        Button merge = content.findViewById(R.id.delete_button);
        merge.setText("Merge");
        merge.setOnClickListener(v -> {
            ((ContactAdapter) recyclerView.getAdapter()).merge(from, to);
            mergeSnackbar.dismiss();
        });

        mergeSnackbar.setDuration(duration);
        return mergeSnackbar;
    }
}
