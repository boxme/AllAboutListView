package com.desmond.allaboutlistview.ListViewExpandingCells;

/**
 * This custom object is used to populate the list adapter. It contains a reference
 * to an image, title, and the extra text to be displayed. Furthermore, it keeps track
 * of the current state (collapsed/expanded) of the corresponding item in the list,
 * as well as store the height of the cell in its collapsed state.
 */
public class ExpandableListItem implements OnSizeChangedListener {

    private String mTitle;
    private String mText;
    private boolean mIsExpanded;
    private int mImgResource;
    private int mCollapsedHeight;
    private int mExpandedHeight;

    public ExpandableListItem(String title, int imgResource, int collapsedHeight, String text) {
        mTitle = title;
        mImgResource = imgResource;
        mCollapsedHeight = collapsedHeight;
        mIsExpanded = false;
        mText = text;
        mExpandedHeight = -1;
    }

    public boolean isExpanded() {
        return mIsExpanded;
    }

    public void setExpanded(boolean isExpanded) {
        mIsExpanded = isExpanded;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getImgResource() {
        return mImgResource;
    }

    public int getCollapsedHeight() {
        return mCollapsedHeight;
    }

    public void setCollapsedHeight(int collapsedHeight) {
        mCollapsedHeight = collapsedHeight;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public int getExpandedHeight() {
        return mExpandedHeight;
    }

    public void setExpandedHeight(int expandedHeight) {
        mExpandedHeight = expandedHeight;
    }

    @Override
    public void onSizeChanged(int newHeight) {
        setExpandedHeight(newHeight);
    }
}
