package com.desmond.allaboutlistview.ListViewExpandingCells;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.desmond.allaboutlistview.R;

import java.util.ArrayList;
import java.util.List;

public class ExpandingCellsActivity extends ActionBarActivity {

    private final int CELL_DEFAULT_HEIGHT = 200;
    private final int NUM_OF_CELLS = 30;

    private ExpandingListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanding_cells);

        ExpandableListItem[] values = new ExpandableListItem[] {
                new ExpandableListItem("Chameleon", R.drawable.chameleon, CELL_DEFAULT_HEIGHT,
                        getResources().getString(R.string.short_lorem_ipsum)),
                new ExpandableListItem("Rock", R.drawable.rock, CELL_DEFAULT_HEIGHT,
                        getResources().getString(R.string.medium_lorem_ipsum)),
                new ExpandableListItem("Flower", R.drawable.flower, CELL_DEFAULT_HEIGHT,
                        getResources().getString(R.string.long_lorem_ipsum)),
        };

        List<ExpandableListItem> mData = new ArrayList<>();

        for (int i = 0; i < NUM_OF_CELLS; i++) {
            ExpandableListItem obj = values[i % values.length];
            mData.add(new ExpandableListItem(obj.getTitle(), obj.getImgResource(),
                    obj.getCollapsedHeight(), obj.getText()));
        }

        CustomArrayAdapter adapter = new CustomArrayAdapter(this, R.layout.expandable_list_view_item, mData);

        mListView = (ExpandingListView)findViewById(R.id.main_list_view);
        mListView.setAdapter(adapter);
        mListView.setDivider(null);
    }
}
