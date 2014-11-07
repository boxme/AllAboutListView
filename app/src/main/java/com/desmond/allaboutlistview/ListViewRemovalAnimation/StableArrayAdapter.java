package com.desmond.allaboutlistview.ListViewRemovalAnimation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by desmond on 1/11/14.
 */
public class StableArrayAdapter extends ArrayAdapter<String> {

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
    View.OnTouchListener mTouchListener;

    public StableArrayAdapter(Context context, int textViewResourceID,
                              List<String> objects, View.OnTouchListener listener) {
        super(context, textViewResourceID, objects);

        mTouchListener= listener;
        for (int i = 0; i < objects.size(); i++) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        String item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (view != convertView) {
            // Add touch listener to every new view to track swipe motion
//            view.setOnTouchListener(mTouchListener);
        }
        return view;
    }
}
