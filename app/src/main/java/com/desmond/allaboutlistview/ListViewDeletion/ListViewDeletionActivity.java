package com.desmond.allaboutlistview.ListViewDeletion;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.desmond.allaboutlistview.Cheeses;
import com.desmond.allaboutlistview.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This example shows how animating ListView views can lead to artifacts if those views are
 * recycled before you animate them
 */
public class ListViewDeletionActivity extends ActionBarActivity {

    private final ArrayList<View> mCheckViews = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_deletion);

        final Button deleteButton = (Button) findViewById(R.id.deleteButton);
        final CheckBox usePositionsCB = (CheckBox) findViewById(R.id.usePositionsCB);
        final ListView listview = (ListView) findViewById(R.id.listview);
        final ArrayList<String> cheeseList = new ArrayList<String>();
        for (int i = 0; i < Cheeses.sCheeseStrings.length; ++i) {
            cheeseList.add(Cheeses.sCheeseStrings[i]);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_multiple_choice, cheeseList);
        listview.setAdapter(adapter);
        listview.setItemsCanFocus(false);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Clicking the delete button fades out the currently selected views
        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SparseBooleanArray checkItems = listview.getCheckedItemPositions();
                int numCheckedItems = checkItems.size();
                for (int i = numCheckedItems - 1; i >= 0; --i) {

                    if (!checkItems.valueAt(i)) {
                        continue;
                    }

                    int position = checkItems.keyAt(i);
                    final String item = adapter.getItem(position);

                    if (!usePositionsCB.isChecked()) {
                        // Remove the actual data after the time period that we're going to run
                        // the fading animation. This is part of the wrong way
                        v.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter.remove(item);
                            }
                        }, 300);
                    }
                    else {
                        // This is the correct way to do this: first see whether the item is
                        // actually visible, and don't bother animating it if it's not.
                        // Next, get the view associated with the item at the time of deletion
                        // (not some old view chosen when the item was clicked).
                        mCheckViews.clear();
                        int positionOnScreen = position - listview.getFirstVisiblePosition();
                        if (positionOnScreen >= 0 &&
                                positionOnScreen < listview.getChildCount()) {
                            final View view = listview.getChildAt(positionOnScreen);

                            // All set to fade this view out. Using ViewPropertyAnimator accounts
                            // for possible recycling of views during the animation itself
                            // (see the ListViewAnimations example for more on this).
                            ViewCompat.animate(view).alpha(0).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    view.setAlpha(1);
                                    adapter.remove(item);
                                }
                            });
                        }
                        else {
                            // Not animating the view, but don't delete it yet to avoid making
                            // the list shit due to offscreen deletion
                            v.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.remove(item);
                                }
                            }, 300);
                        }
                    }
                }

                // THIS IS THE WRONG WAY TO DO THIS
                // We're basing our decision of the views to be animated based on outdated
                // information at selection time. Then we're going ahead and running an animation
                // on those views even when the selected items might not even be in view (in which
                // case we'll probably be mistakenly fading out something else that is on the
                // screen and is re-using that recycled view).
                for (int i = 0; i < mCheckViews.size(); ++i) {
                    final View checkedView = mCheckViews.get(i);
                    ViewCompat.animate(checkedView).alpha(0).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            checkedView.setAlpha(1);
                        }
                    });
                }
                mCheckViews.clear();
                adapter.notifyDataSetChanged();
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean checked = listview.isItemChecked(position);
                if (checked) {
                    mCheckViews.add(view);
                }
                else {
                    mCheckViews.remove(view);
                }
            }
        });

    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
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
    }
}
