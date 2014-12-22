package com.desmond.allaboutlistview.ListViewExpandingCells;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A custom ListView which supports the preview of extra content corresponding
 * to each cell by clicking on the cell to hide and show the extra content
 */
public class ExpandingListView extends ListView {

    private boolean mShouldRemoveObserver = false;

    private List<View> mViewsToDraw = new ArrayList<>();

    private int[] mTranslate;

    public ExpandingListView(Context context) {
        super(context);
        init();
    }

    public ExpandingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpandingListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnItemClickListener(mItemClickListener);
    }

    /**
     * Listens for item clicks and expands or collapses the selected view depending on
     * its current state.
     */
    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ExpandableListItem viewObject =
                    (ExpandableListItem) getItemAtPosition(getPositionForView(view));

            if (!viewObject.isExpanded()) {
                // Not expanded yet
                expandView(view);
            }
            else {
                collapseView(view);
            }
        }
    };

    private void expandView(final View view) {

    }

    private void collapseView(final View view) {

    }
}
