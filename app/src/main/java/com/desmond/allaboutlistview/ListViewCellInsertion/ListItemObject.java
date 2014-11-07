package com.desmond.allaboutlistview.ListViewCellInsertion;

/**
 * The data model for every cell in the ListView for this application. This model stores
 * a title, an image resource and a default cell height for every item in the ListView.
 */
public class ListItemObject {

    private String mTitle;
    private int mImgResource;
    private int mHeight;

    public ListItemObject(String title, int imgResource, int height) {
        mTitle = title;
        mImgResource = imgResource;
        mHeight = height;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getImgResource() {
        return mImgResource;
    }

    public int getHeight() {
        return mHeight;
    }
}
