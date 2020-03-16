package com.mesh.ui.home;

import android.content.Intent;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mesh.Database.DBManager;
import com.mesh.MainActivity;
import com.mesh.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ItemDragAndDropCallback extends ItemTouchHelper.Callback {

    private final RecyclerView recyclerView;
    private HomeFragment homeFragment;
    private View folder;
    private int folderPosition = -1;
    private int draggedFolderPosition = -1;

    ItemDragAndDropCallback(HomeFragment homeFragment, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.homeFragment = homeFragment;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP | ItemTouchHelper.DOWN);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        int from = viewHolder.getAdapterPosition();
        int to = target.getAdapterPosition();
        recyclerView.getAdapter().notifyItemMoved(from, to); // Allow swapping in Merge Mode to prevent issues with drag-scrolling

        if (!homeFragment.isMerge()) {
            swap(from, to);
        }
        return true;
    }

    private void swap(int from, int to) {
        DBManager dbManager = new DBManager(homeFragment.getContext());
        dbManager.open();
        ArrayList<Contact> contactList = ((ContactAdapter) recyclerView.getAdapter()).getContactList();
        Collections.swap(contactList, from, to);
        dbManager.swapContactPositions(contactList.get(from).getID(), contactList.get(to).getID());
        dbManager.updateCustomContactOrderSetting(true);
        dbManager.close();
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // nothing
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            draggedFolderPosition = viewHolder.getAdapterPosition();
            if (folder != null) {
                folder = null;
                folderPosition = -1;
            }
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {

            if (homeFragment.isMerge()) { // To prevent swapping
                if (folder != null && homeFragment.getMergeSnackbar() == null) {
                    homeFragment.displaySnackBar(draggedFolderPosition, folderPosition);
                    folder = null;
                    folderPosition = -1;
                    // You can remove item from the list here and add it to the folder
                    // Remember to notify RecyclerView about it
                    // recyclerView.getAdapter().notifyItemRemoved(draggedFolderPosition);
                } else
                    homeFragment.reset();
            }
        }
    }

    @Override
    public void onChildDraw(
            @NonNull Canvas c,
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder,
            float dX,
            float dY,
            int actionState,
            boolean isCurrentlyActive
    ) {
        if (homeFragment.isMerge()) {
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive) {

                float itemTopPosition = viewHolder.itemView.getTop() + dY + viewHolder.itemView.getHeight() / 2;
                float itemBottomPosition = viewHolder.itemView.getBottom() + dY - viewHolder.itemView.getHeight() / 2;

                // Find itemHolder under the dragged
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    View child = recyclerView.getChildAt(i);
                    RecyclerView.ViewHolder childView = recyclerView.getChildViewHolder(child);

                    // Exclude dragged item from detection
                    if (!child.equals(viewHolder.itemView)) {

                        // Accept folder which encloses item position
                        boolean topWithinChild = child.getTop() < itemTopPosition && itemTopPosition < child.getBottom();
                        boolean bottomWithinChild = child.getBottom() > itemBottomPosition && itemBottomPosition > child.getTop();
                        if (topWithinChild || bottomWithinChild) {
                            folder = child;
                            folderPosition = i;
                            if (!((ContactAdapter.ContactViewHolder) childView).getExpanded()) {
                                expand(child);
                                ((ContactAdapter.ContactViewHolder) childView).setExpanded(true);
                            }
                        } else {
                            if (((ContactAdapter.ContactViewHolder) childView).getExpanded()) {
                                shrink(child);
                                folder = null;
                                ((ContactAdapter.ContactViewHolder) childView).setExpanded(false);
                                folderPosition = 1;
                            }
                        }
                    }
                }
            } else { // Everytime it is drawing, it checks all itemHolder and shrink if necessary. Refactor if in future, size gets too big
                folder = null;
                folderPosition = -1;
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    View child = recyclerView.getChildAt(i);
                    RecyclerView.ViewHolder childView = recyclerView.getChildViewHolder(child);
                    if (((ContactAdapter.ContactViewHolder) childView).getExpanded()) {
                        shrink(child);
                        ((ContactAdapter.ContactViewHolder) childView).setExpanded(false);
                    }
                }

            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void shrink(View view) {
        ScaleAnimation shrink = new ScaleAnimation(
                1.1f, 1.0f,
                1.1f, 1.0f,
                Animation.RELATIVE_TO_SELF, (float) 0.5,
                Animation.RELATIVE_TO_SELF, (float) 0.5);

        shrink.setDuration(250);
        shrink.setFillAfter(true);
        view.startAnimation(shrink);
    }

    private void expand(View view) {
        ScaleAnimation expand = new ScaleAnimation(
                1.0f, 1.1f,
                1.0f, 1.1f,
                Animation.RELATIVE_TO_SELF, (float) 0.5,
                Animation.RELATIVE_TO_SELF, (float) 0.5);

        expand.setDuration(250);
        expand.setFillAfter(true);
        view.startAnimation(expand);

    }

    // Change scroll style
    @Override
    public int interpolateOutOfBoundsScroll(@NonNull RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
        return super.interpolateOutOfBoundsScroll(recyclerView, viewSize * 2, viewSizeOutOfBounds, totalSize, msSinceStartScroll);
    }
}