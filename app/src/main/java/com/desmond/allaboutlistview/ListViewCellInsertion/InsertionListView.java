package com.desmond.allaboutlistview.ListViewCellInsertion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.desmond.allaboutlistview.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This ListView displays a set of ListItemObjects. By calling addRow with a new
 * ListItemObject, it is added to the top of the ListView and the new row is animated
 * in. If the ListView content is at the top (the scroll offset is 0), the animation of
 * the new row is accompanied by an extra image animation that pops into place in its
 * corresponding item in the ListView.
 */
public class InsertionListView extends ListView {

    private static final int NEW_ROW_DURATION = 500;
    private static final int OVERSHOOT_INTERPOLATOR_TENSION = 5;

    private final OvershootInterpolator sOvershootInterpolator =
            new OvershootInterpolator(OVERSHOOT_INTERPOLATOR_TENSION);

    private RelativeLayout mLayout;

    private OnRowAdditionAnimationListener mRowAdditionAnimationListener;

    private List<ListItemObject> mData;
    private List<BitmapDrawable> mCellBitmapDrawables;

    public InsertionListView(Context context) {
        super(context);
        init();
    }

    public InsertionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InsertionListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setDivider(null);

        // To contain screenshot drawable of the cells that were visible before the
        // data set change, but not after
        mCellBitmapDrawables = new ArrayList<BitmapDrawable>();
    }

    /**
     * Modifies the underlying data set and adapter through the addition of the new object
     * to the first item of the listView. The new cell is then animated into place from
     * above the bounds of the ListView
     */
    public void addRow(ListItemObject newObj) {
        final CustomArrayAdapter adapter = (CustomArrayAdapter) getAdapter();

        // Stores the starting bounds and the corresponding bitmap drawables
        // of every cell present in the ListView (screenshot) before the data set change takes place
        final HashMap<Long, Rect> listViewItemBounds = new HashMap<Long, Rect>();
        final HashMap<Long, BitmapDrawable> listViewItemDrawables =
                new HashMap<Long, BitmapDrawable>();

        int firstVisiblePosition = getFirstVisiblePosition();
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            int position = firstVisiblePosition + i;
            long itemID = adapter.getItemId(position);
            Rect startRect = new Rect(child.getLeft(), child.getTop(),
                    child.getRight(), child.getBottom());
            listViewItemBounds.put(itemID, startRect);
            listViewItemDrawables.put(itemID, getBitmapDrawableFromView(child));
        }

        // Adds the new object to the data set, thereby modifying the adapter
        // as wel as adding a stable ID for that specified object
        mData.add(0, newObj);
        adapter.addStableIdforDataAtPosition(0);
        adapter.notifyDataSetChanged();

        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);

                ArrayList<Animator> animations = new ArrayList<Animator>();

                final View newCell = getChildAt(0);
                final ImageView imgView = (ImageView) newCell.findViewById(R.id.image_view);
                final ImageView copyImgView = new ImageView(getContext());

                int firstVisiblePosition = getFirstVisiblePosition();
                final boolean shouldAnimateInNewRow = shouldAnimateInNewRow();
                final boolean shouldAnimateInImage = shouldAnimateInNewImage();

                if (shouldAnimateInNewRow) {
                    // Fades in the text of the first cell
                    TextView textView = (TextView) newCell.findViewById(R.id.text_view);
                    ObjectAnimator textAnimator =
                            ObjectAnimator.ofFloat(textView, "alpha", 0.0f, 1.0f);
                    animations.add(textAnimator);

                    // Animates in the extra hover view corresponding to the image in the top
                    // row of the ListView
                    if (shouldAnimateInImage) {

                        // Final width & height of the animated image should animate to
                        int width = imgView.getWidth();
                        int height = imgView.getHeight();

                        // Location of new cell
                        Point childLoc = getLocationOnScreen(newCell);
                        Point layoutLoc = getLocationOnScreen(mLayout);

                        // New item
                        ListItemObject obj = mData.get(0);
                        // Get a circular bitmap
                        Bitmap bitmap = CustomArrayAdapter.getCroppedBitmap(
                                BitmapFactory.decodeResource(getContext().getResources(),
                                        obj.getImgResource(), null)
                        );
                        copyImgView.setImageBitmap(bitmap);

                        imgView.setVisibility(View.INVISIBLE);

                        copyImgView.setScaleType(ImageView.ScaleType.CENTER);

                        ObjectAnimator imgViewTranslation =
                                ObjectAnimator.ofFloat(copyImgView, "y", childLoc.y - layoutLoc.y);

                        ObjectAnimator imgViewScaleY =
                                ObjectAnimator.ofFloat(copyImgView, "scaleY", 0, 1.0f);
                        ObjectAnimator imgViewScaleX =
                                ObjectAnimator.ofFloat(copyImgView, "scaleX", 0, 1.0f);

                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(imgViewScaleX, imgViewScaleY);
                        animatorSet.setInterpolator(sOvershootInterpolator);

                        animations.add(imgViewTranslation);
                        animations.add(animatorSet);

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                width, height
                        );

                        // Add the animated imageView to the parent layout
                        mLayout.addView(copyImgView, params);
                    }
                }

                // Loops through all the current visible cells in the ListView and animates all
                // of them into their post layout positions from their original positions
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = adapter.getItemId(position);
                    Rect startRect = listViewItemBounds.get(itemId);

                    // getTop() of the new position
                    int top = child.getTop();
                    int delta;
                    ObjectAnimator animation;
                    if (startRect != null) {
                        // If the cell was visible before the data set change and after
                        // the change, then animate the cell between the two positions
                        int startTop = startRect.top;
                        delta = startTop - top;
                    }
                    else  {
                        // If the cell was not visible (or present) before the data set
                        // change but is visible after the change, then use its height
                        // to determine the delta by which it should be animated
                        int childHeight = child.getHeight() + getDividerHeight();
                        // i == 0 will be animate from outside of screen
                        int startTop = top + (i > 0 ? childHeight : -childHeight);
                        delta = startTop - top;
                    }
                    animation = ObjectAnimator.ofFloat(child, "translationY", delta, 0);
                    animations.add(animation);

                    // Items left will be those were visible before the data set changed, but
                    // not after
                    listViewItemBounds.remove(itemId);
                    listViewItemDrawables.remove(itemId);
                }

                 // Loops through all the cells that were visible before the data set
                 // changed but not after, and keeps track of their corresponding
                 // drawables. The bounds of each drawable are then animated from the
                 // original state to the new one (off the screen). By storing all
                 // the drawables that meet this criteria, they can be redrawn on top
                 // of the ListView via dispatchDraw as they are animating.
                for (Long itemId : listViewItemBounds.keySet()) {
                    // Screen shot drawable of the row
                    BitmapDrawable bitmapDrawable = listViewItemDrawables.get(itemId);
                    Rect startBounds = listViewItemBounds.get(itemId);
                    // Position the drawable to the original position
                    bitmapDrawable.setBounds(startBounds);

                    int childHeight = startBounds.bottom - startBounds.top + getDividerHeight();
                    // New position after animating down
                    Rect endBounds = new Rect(startBounds);
                    endBounds.offset(0, childHeight);

                    ObjectAnimator animation = ObjectAnimator.ofObject(bitmapDrawable,
                            "bounds", sBoundsEvaluator, startBounds, endBounds);
                    animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        private Rect mLastBound = null;
                        private Rect mCurrentBound = new Rect();
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            Rect bounds = (Rect)valueAnimator.getAnimatedValue();
                            mCurrentBound.set(bounds);
                            if (mLastBound != null) {
                                mCurrentBound.union(mLastBound);
                            }
                            mLastBound = bounds;
                            invalidate(mCurrentBound);
                        }
                    });

                    listViewItemBounds.remove(itemId);
                    listViewItemDrawables.remove(itemId);

                    mCellBitmapDrawables.add(bitmapDrawable);

                    animations.add(animation);
                }

                // Animate all the cells from their old position to their new position
                // at the same time
                setEnabled(false);
                mRowAdditionAnimationListener.onRowAdditionAnimationStart();
                AnimatorSet set = new AnimatorSet();
                set.setDuration(NEW_ROW_DURATION);
                set.playTogether(animations);
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mCellBitmapDrawables.clear();
                        imgView.setVisibility(View.VISIBLE);
                        mLayout.removeView(copyImgView);
                        mRowAdditionAnimationListener.onRowAdditionAnimationEnd();
                        setEnabled(true);
                        invalidate();
                    }
                });

                // Start all cells animations
                set.start();

                listViewItemBounds.clear();
                listViewItemDrawables.clear();

                // Return false to cancel all the current drawing pass
                return true;
            }
        });
    }

    /**
     * By overriding dispatchDraw, the BitmapDrawables of all the cells that were on the
     * screen before (but not after) the layout are drawn and animated off the screen.
     *
     * dispatchDraw is called by the draw to draw the child views
     * This may be overridden by derived classes to gain control just before its children are drawn
     * (but after its own view has been drawn).
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mCellBitmapDrawables.size() > 0) {
            for (BitmapDrawable bitmapDrawable : mCellBitmapDrawables) {

                // Draw the screen shot to the canvas
                bitmapDrawable.draw(canvas);
            }
        }
    }

    /**
     * Returns a bitmap drawable showing a screenshot of the view passed in.
     */
    private BitmapDrawable getBitmapDrawableFromView(View v) {
        Bitmap bitmap  = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // Redraw the view and its children to the canvas
        v.draw(canvas);
        return new BitmapDrawable(getResources(), bitmap);
    }

    /**
     * Animate the row if new row is visible in the listView
     * @return
     */
    public boolean shouldAnimateInNewRow() {
        int firstVisiblePosition = getFirstVisiblePosition();
        return (firstVisiblePosition == 0);
    }

    /**
     * Animate the image view only if
     * 1) it's the only child in the list view
     * 2) it's getTop() is visible
     * @return
     */
    public boolean shouldAnimateInNewImage() {
        if (getChildCount() == 0) {
            return true;
        }
        boolean shouldAnimateInNewRow = shouldAnimateInNewRow();
        View topCell = getChildAt(0);
        return (shouldAnimateInNewRow && topCell.getTop() == 0);
    }

    /**
     * Returns the absolute x,y coordinates of the view relative to the top left
     * corner of the phone screen
     */
    public Point getLocationOnScreen(View v) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);

        int[] location = new int[2];
        v.getLocationOnScreen(location);

        return new Point(location[0], location[1]);
    }

    /** Setter for the underlying data set controlling the adapter. */
    public void setData(List<ListItemObject> data) {
        mData = data;
    }

    /**
     * Setter for the parent RelativeLayout of this ListView. A reference to this
     * ViewGroup is required in order to add the custom animated overlaying bitmap
     * when adding a new row.
     */
    public void setLayout(RelativeLayout layout) {
        mLayout = layout;
    }

    public void setRowAdditionAnimationListener(OnRowAdditionAnimationListener
                                                        rowAdditionAnimationListener) {
        mRowAdditionAnimationListener = rowAdditionAnimationListener;
    }

    /**
     * This TypeEvaluator is used to animate the position of a BitmapDrawable
     * by updating its bounds.
     */
    static final TypeEvaluator<Rect> sBoundsEvaluator = new TypeEvaluator<Rect>() {
        public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
            return new Rect(interpolate(startValue.left, endValue.left, fraction),
                    interpolate(startValue.top, endValue.top, fraction),
                    interpolate(startValue.right, endValue.right, fraction),
                    interpolate(startValue.bottom, endValue.bottom, fraction));
        }

        public int interpolate(int start, int end, float fraction) {
            return (int)(start + fraction * (end - start));
        }
    };
}
