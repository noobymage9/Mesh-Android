package com.mesh.ui.home;

import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

public class ItemDragAndDropCallback extends ItemTouchHelper.Callback {

    private final RecyclerView recyclerView;
    private boolean first = true;
    HashMap<View, Boolean> booleanHashMap = new HashMap<>();

    ItemDragAndDropCallback(RecyclerView recyclerView) {
        // Choose drag and swipe directions
        // Up and down is chosen for dragging
        // Nothing is chosen for swiping
        this.recyclerView = recyclerView;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP | ItemTouchHelper.DOWN);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int from = viewHolder.getAdapterPosition();
        int to = target.getAdapterPosition();
        // You can reorder items here
        // Reorder items only when target is not a folder
        recyclerView.getAdapter().notifyItemMoved(from, to);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // nothing
    }

    // An item will be dropped into this folder
    private View folder;

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {

            // Here you are notified that the drag operation began

            if (folder != null) {
                //folder.setBackgroundResource(0); // Clear former folder background
                folder = null;
            }
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {

            // Here you are notified that the last operation ended

            if (folder != null) {
                // Set folder background to a color indicating
                // that an item was dropped into it
                /*
                folder.setBackgroundColor(
                        ContextCompat.getColor(
                                recyclerView.getContext(), android.R.color.holo_green_dark
                        )
                );
                */

                // You can remove item from the list here and add it to the folder
                // Remember to notify RecyclerView about it

                if (viewHolder != null)
                    recyclerView.getAdapter().notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        }
    }

    // This method gets called a lot, so don't do any expensive operations here
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
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive) {
            if (first) {
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    booleanHashMap.put(recyclerView.getChildAt(i), false);
                }
                first = false;
            }
            // Here you are notified that the drag operation is in progress

            if (folder != null) {
                //folder.setBackgroundResource(0); // Clear former folder background
                folder = null;
            }

            float itemTopPosition = viewHolder.itemView.getTop() + dY;
            float itemBottomPosition = viewHolder.itemView.getBottom() + dY;

            // Find folder under dragged item
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                View child = recyclerView.getChildAt(i);

                // Exclude dragged item from detection
                if (!child.equals(viewHolder.itemView)) {

                    // Accept folder which encloses item position
                    boolean topWithinChild = child.getTop() < itemTopPosition && itemTopPosition < child.getBottom();
                    boolean bottomWithinChild = child.getBottom() > itemBottomPosition && itemBottomPosition > child.getTop();
                    if (topWithinChild || bottomWithinChild) {

                        folder = child;
                        if (!booleanHashMap.get(child)) {
                            expand(child);
                        }
                        // Set folder background to a color indicating
                        // that an item will be dropped into it upon release
                        /*

                        folder.setBackgroundColor(
                                ContextCompat.getColor(
                                        recyclerView.getContext(), android.R.color.holo_green_light
                                )
                        );
                        */
                        /*


                         */

                       // break;
                    } else {
                        if (booleanHashMap.get(child)) {
                            shrink(child);
                        }
                        //break;
                    }
                }
            }
        } else {
            for (View view : booleanHashMap.keySet()) {
                if (booleanHashMap.get(view)) {
                    shrink(view);
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    public void shrink(View view) {
        ScaleAnimation shrink = new ScaleAnimation(
                1.1f, 1.0f,
                1.1f, 1.0f,
                Animation.RELATIVE_TO_SELF, (float) 0.5,
                Animation.RELATIVE_TO_SELF, (float) 0.5);

        shrink.setDuration(250);
        shrink.setFillAfter(true);
        booleanHashMap.put(view, false);
        view.startAnimation(shrink);
    }

    public void expand(View view) {
        ScaleAnimation expand = new ScaleAnimation(
                1.0f, 1.1f,
                1.0f, 1.1f,
                Animation.RELATIVE_TO_SELF, (float) 0.5,
                Animation.RELATIVE_TO_SELF, (float) 0.5);

        expand.setDuration(250);
        expand.setFillAfter(true);
        booleanHashMap.put(view, true);
        view.startAnimation(expand);

    }



}