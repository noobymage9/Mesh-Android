package com.mesh.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.Database.DBManager;
import com.mesh.R;

import java.util.ArrayList;
import java.util.Collections;

public class DragSwipeController extends ItemTouchHelper.Callback {

    enum ButtonsState {
        GONE,
        VISIBLE
    }

    private final RecyclerView recyclerView;
    private HomeFragment homeFragment;
    private View folder;
    private int folderPosition = -1;
    private int draggedFolderPosition = -1;
    private boolean swapped = false;
    private boolean swipeBack = false;
    private ButtonsState buttonShowedState = ButtonsState.GONE;
    private int buttonWidth = 300;
    private RectF buttonInstance = null;
    private float defaultMargin;
    private int maxSlide;

    DragSwipeController(HomeFragment homeFragment, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.homeFragment = homeFragment;
        defaultMargin = homeFragment.getResources().getDimension(R.dimen._5sdp);
        maxSlide = (int) (buttonWidth - 5 * defaultMargin);

    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        int from = viewHolder.getAdapterPosition();
        int to = target.getAdapterPosition();
        recyclerView.getAdapter().notifyItemMoved(from, to); // Allow swapping in Merge Mode to prevent issues with drag-scrolling
        swapped = true;
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
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
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
        } else if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

        }
        else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {

            if (homeFragment.isMerge()) { // To prevent swapping
                if (folder != null && homeFragment.getMergeSnackbar() == null) {
                    homeFragment.displaySnackBar(draggedFolderPosition, folderPosition);
                    folder = null;
                    folderPosition = -1;
                    // You can remove item from the list here and add it to the folder
                    // Remember to notify RecyclerView about it
                    // recyclerView.getAdapter().notifyItemRemoved(draggedFolderPosition);
                } else if (swapped){
                    homeFragment.reset();
                }
            }
            swapped = false;
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
            if (isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (dX > maxSlide) dX = maxSlide;
                    setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
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
        drawButtons(c, viewHolder);
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
        float corners = homeFragment.getContactAdapter().getSizeInDP(20);

        View itemView = viewHolder.itemView;
        Paint p = new Paint();

        RectF leftButton = new RectF(itemView.getLeft(), itemView.getTop() + defaultMargin, itemView.getLeft() + buttonWidth, itemView.getBottom() - defaultMargin);
        p.setColor(homeFragment.getResources().getColor(R.color.BritishBlue));
        c.drawRoundRect(leftButton, corners, corners, p);
        drawIcon( c, leftButton, p);

        buttonInstance = null;
        if (buttonShowedState == ButtonsState.VISIBLE) buttonInstance = leftButton;
    }

    private void drawIcon(Canvas c, RectF button, Paint p) {
        float textSize = 100;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);
        Drawable temp1 = homeFragment.getContext().getDrawable(R.drawable.menu_favourite);
        Bitmap temp = HomeFragment.drawableToBitmap(temp1, + homeFragment.getContactAdapter().getSizeInDP(24));
        //temp1.setBounds((int) button.left + maxSlide / 3, (int) button.top + maxSlide / 3, (int) button.left + maxSlide / 3 + homeFragment.getContactAdapter().getSizeInDP(24), (int) button.top + maxSlide / 3 + homeFragment.getContactAdapter().getSizeInDP(24));
        //temp1.draw(c);
        c.drawText("\u2764", button.left + maxSlide / 3, button.top + 3 * maxSlide / 4, p);
        //c.drawBitmap(temp, button.left + maxSlide / 3, button.top + maxSlide / 3, null);
    }


    private void setTouchListener(Canvas c,
                                  RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  float dX, float dY,
                                  int actionState, boolean isCurrentlyActive) {

        // Forcibly reset all swiped
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            recyclerView.getChildAt(i).setPadding(0, 0, 0, 0);
        }

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if (swipeBack) {
                    if (dX >= maxSlide) buttonShowedState = ButtonsState.VISIBLE;
                    if (buttonShowedState != ButtonsState.GONE) {
                        viewHolder.itemView.setPadding(maxSlide, 0, 0, 0);
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }

    private void setTouchDownListener(final Canvas c,
                                      final RecyclerView recyclerView,
                                      final RecyclerView.ViewHolder viewHolder,
                                      final float dX, final float dY,
                                      final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            return false;
        });
    }

    private void setTouchUpListener(final Canvas c,
                                    final RecyclerView recyclerView,
                                    final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY,
                                    final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                viewHolder.itemView.setPadding(0, 0, 0, 0);
                onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        return false;
                    }
                });
                setItemsClickable(recyclerView, true);
                swipeBack = false;
                // on click listener for button for when u lift ur finger off the button
                if (buttonInstance.contains(event.getX(), event.getY())) {
                    DBManager dbManager = new DBManager(homeFragment.getContext());
                    dbManager.open();
                    dbManager.setFavouriteContact(homeFragment.getContactAdapter().getContactList().get(viewHolder.getAdapterPosition()).getID());
                    dbManager.close();
                }
                buttonShowedState = ButtonsState.GONE;
            }
            return false;
        });
    }

    private void setItemsClickable(RecyclerView recyclerView,
                                   boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
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