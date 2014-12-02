package com.desmond.allaboutlistview.ListViewDraggingAnimation;

import android.content.Context;
import android.os.Build;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by desmond on 1/12/14.
 */
public class StableArrayAdapter extends ArrayAdapter<String> {

    final int INVALID_ID = -1;

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

    public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        String item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        // A fix on android L, otherwise the draggable view won't work properly
        return Build.VERSION.SDK_INT < 20;
    }
}
