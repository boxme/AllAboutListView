package com.desmond.allaboutlistview.ListViewExpandingCells;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * This layout is used to contain the extra information that will be displayed
 * when a certain cell is expanded. The custom relative layout is created in
 * order to achieve a fading effect of this layout's contents as it is being
 * expanded or collapsed as opposed to just fading the content in(out) after(before)
 * the cell expands(collapse).
 *
 * During expansion, layout takes place so the full contents of this layout can
 * be displayed. When the size changes to display the full contents of the layout,
 * its height is stored. When the view is collapsing, this layout's height becomes 0
 * since it is no longer in the visible part of the cell. By overriding onMeasure, and
 * setting the height back to its max height, it is still visible during the collapse
 * animation, and so, a fade out effect can be achieved.
 */
public class ExpandingLayout extends RelativeLayout {

    private OnSizeChangedListener mSizeChangedListener;
    private int mExpandedHeight = -1;

    public ExpandingLayout(Context context) {
        super(context);
    }

    public ExpandingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mExpandedHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mExpandedHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mExpandedHeight = h;
        // Notifies the list data object corresponding to this layout that its size has changed
        mSizeChangedListener.onSizeChanged(h);
    }

    public int getExpandedHeight() {
        return mExpandedHeight;
    }

    public void setExpandedHeight(int expandedHeight) {
        mExpandedHeight = expandedHeight;
    }

    public void setSizeChangedListener(OnSizeChangedListener listener) {
        mSizeChangedListener = listener;
    }
}
