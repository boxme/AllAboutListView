package com.desmond.allaboutlistview.ListViewAnimations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import com.desmond.allaboutlistview.Cheeses;
import com.desmond.allaboutlistview.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This example shows how animating ListView views can lead to bugs if those views are
 * recycled before the animation has completed
 */
public class ListViewAnimationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_animation);

        final CheckBox vpaCB = (CheckBox) findViewById(R.id.vpaCB);
        final CheckBox setTransientStateCB = (CheckBox) findViewById(R.id.setTransientStateCB);
        final ListView listview = (ListView) findViewById(R.id.listview);
        final ArrayList<String> cheeseList = new ArrayList<String>();

        for (String cheeseString : Cheeses.sCheeseStrings) {
            cheeseList.add(cheeseString);
        }

        final StableArrayAdapter adapter = new StableArrayAdapter(
                this, android.R.layout.simple_list_item_1, cheeseList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);

                if (vpaCB.isChecked()) {
                    // Use ViewPropertyAnimator to animate the deletion will set the transientState
                    // flag internally. Doing so will free the view from view recycling
                    ViewCompat.animate(view).setDuration(1000).alpha(0)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    cheeseList.remove(item);
                                    adapter.notifyDataSetChanged();
                                    // Reset the view so that it can be seen when it's reused
                                    view.setAlpha(1);
                                }
                            });
                }
                else {
                    // Here's where the problem starts - this animation will animate a View object.
                    // But that View may get recycled if it is animated out of the container,
                    // and the animation will continue to fade a view that now contains unrelated
                    // content. Note that the deletion will still occur to correct item
                    ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 0);
                    anim.setDuration(1000);

                    if (setTransientStateCB.isChecked()) {
                        // Here's the correct way to do this: if you tell a view that it has
                        // transientState, then it won't be recycled until the
                        // transientState flag is reset.
                        // A different approach is to use ViewPropertyAnimator, which sets the
                        // transientState flag internally.
                        ViewCompat.setHasTransientState(view, true);
                    }
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            cheeseList.remove(item);
                            adapter.notifyDataSetChanged();
                            view.setAlpha(1);

                            // Reset the transientState flag
                            if (setTransientStateCB.isChecked()) {
                                ViewCompat.setHasTransientState(view, false);
                            }
                        }
                    });

                    anim.start();
                }
            }
        });
    }

    private static class StableArrayAdapter extends ArrayAdapter<String> {

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
