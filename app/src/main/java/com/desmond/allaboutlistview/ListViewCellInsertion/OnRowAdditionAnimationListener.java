package com.desmond.allaboutlistview.ListViewCellInsertion;

/**
 * This listener is used to determine when the animation of a new row addition
 * begins and ends. The primary use of this interface is to create a callback
 * under which certain elements, such as the listview itself, can be disabled
 * to prevent unpredictable behaviour during the actual cell animation.
 */
public interface OnRowAdditionAnimationListener {
    public void onRowAdditionAnimationStart();
    public void onRowAdditionAnimationEnd();
}
