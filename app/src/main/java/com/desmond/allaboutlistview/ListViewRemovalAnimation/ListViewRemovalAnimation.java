package com.desmond.allaboutlistview.ListViewRemovalAnimation;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;

import com.desmond.allaboutlistview.Cheeses;
import com.desmond.allaboutlistview.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewRemovalAnimation extends ActionBarActivity {

    StableArrayAdapter mAdapter;
    ListView mListView;
    BackgroundContainer mBackgroundContainer;
    boolean mSwiping = false;
    boolean mItemPressed = false;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_removal_animation);

        mBackgroundContainer = (BackgroundContainer) findViewById(R.id.listViewBackground);
        mListView = (ListView) findViewById(R.id.listview);
        final ArrayList<String> cheeseList = new ArrayList<String>();
        for (int i = 0; i < Cheeses.sCheeseStrings.length; ++i) {
            cheeseList.add(Cheeses.sCheeseStrings[i]);
        }

//        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
//                mListView, new SwipeDismissListViewTouchListener.DismissCallbacks() {
//            @Override
//            public boolean canDismiss(int position) {
//                return true;
//            }
//
//            @Override
//            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
//                StableArrayAdapter adapter = (StableArrayAdapter) listView.getAdapter();
//                for (int reverseSortedPosition : reverseSortedPositions) {
//                    adapter.remove(cheeseList.get(reverseSortedPosition));
//                }
//                adapter.notifyDataSetChanged();
//            }
//        });
//        mListView.setOnTouchListener(touchListener);
//        mListView.setOnScrollListener(touchListener.makeScrollListener());

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ListViewRemovalAnimation", "item clicked");
            }
        });

        mAdapter = new StableArrayAdapter(this, R.layout.opaque_text_view, cheeseList,
                mTouchListener);
        mListView.setAdapter(mAdapter);
    }

    /**
     * Handle touch events to fade/move dragged items as they are swiped out
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        float mDownX;
        private int mSwipeSlop = -1;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0) {
                mSwipeSlop =
                        ViewConfiguration.get(ListViewRemovalAnimation.this).getScaledTouchSlop();
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed) {
                        // Multi-item swipes not handled
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    break;

                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1);
                    v.setTranslationX(0);
                    mItemPressed = false;
                    break;

                case MotionEvent.ACTION_MOVE:
                {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        if (deltaXAbs > mSwipeSlop) {
                            mSwiping = true;
                            mListView.requestDisallowInterceptTouchEvent(true);
                            mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
                        }
                    }
                    if (mSwiping) {
                        v.setTranslationX(x - mDownX);
                        v.setAlpha(1 - deltaXAbs / v.getWidth());
                    }
                }
                    break;

                case MotionEvent.ACTION_UP:
                {
                    // User let go - figure out whether to animate the view out, or back into place
                    if (mSwiping) {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        float fractionCovered;
                        float endX;
                        float endAlpha;
                        final boolean remove;
                        if (deltaXAbs > v.getWidth() / 4) {
                            // Greater than a quarter of the width - animate it out
                            fractionCovered = deltaXAbs / v.getWidth();
                            endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                            endAlpha = 0;
                            remove = true;
                        } else {
                            // Not far enough - animate it back
                            fractionCovered = 1 - (deltaXAbs / v.getWidth());
                            endX = 0;
                            endAlpha = 1;
                            remove = false;
                        }
                        // Animate position and alpha of swiped item
                        // NOTE: This is a simplified version of swipe behavior, for the
                        // purposes of this demo about animation. A real version should use
                        // velocity (via the VelocityTracker class) to send the item off or
                        // back at an appropriate speed.
                        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                        mListView.setEnabled(false);
                        ViewCompat.animate(v).setDuration(duration)
                                .alpha(endAlpha).translationX(endX)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Restore animated values
                                        v.setAlpha(1);
                                        v.setTranslationX(0);
                                        if (remove) {
                                            animateRemoval(mListView, v);
                                        } 
                                        else {
                                            mBackgroundContainer.hideBackground();
                                            mSwiping = false;
                                            mListView.setEnabled(true);
                                        }
                                    }
                                });
                    }
                }
                mItemPressed = false;
                break;

                default:
                    return false;
            }
            return true;
        }
    };

    /**
     * This method animates all other views in the ListView container (not including ignoreView)
     * into their final positions. It is called after ignoreView has been removed from the
     * adapter, but before layout has been run. The approach here is to figure out where
     * everything is now, then allow layout to run, then figure out where everything is after
     * layout, and then to run animations between all of those start/end positions.
     */
    private void animateRemoval(final ListView listView, View viewToRemove) {
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        View child;
        for (int i = 0; i < listView.getChildCount(); i++) {
            child = listView.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = mAdapter.getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        
        // Delete the item from the adapter
        int position = mListView.getPositionForView(viewToRemove);
        mAdapter.remove(mAdapter.getItem(position));
        
        final ViewTreeObserver observer = listView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listView.getFirstVisiblePosition();
                for (int i = 0; i < listView.getChildCount(); i++) {
                    final View child = listView.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = mAdapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();

                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            ViewCompat.animate(child)
                                    .setDuration(MOVE_DURATION)
                                    .translationY(0);
                            if (firstAnimation) {
                                ViewCompat.animate(child)
                                        .withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                mBackgroundContainer.hideBackground();
                                                mSwiping = false;
                                                mListView.setEnabled(true);
                                            }
                                        });
                                firstAnimation = false;
                            }
                        }
                    }
                    else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + listView.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        ViewCompat.animate(child)
                                .setDuration(MOVE_DURATION)
                                .translationY(0);
                        if (firstAnimation) {
                            ViewCompat.animate(child)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            mBackgroundContainer.hideBackground();
                                            mSwiping = false;
                                            mListView.setEnabled(true);
                                        }
                                    });
                            firstAnimation = false;
                        }
                    }
                }

                if (listView.getChildCount() == 0) {
                    mListView.setEnabled(true);
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
    }
}
