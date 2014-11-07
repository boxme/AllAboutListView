package com.desmond.allaboutlistview.ListViewCellInsertion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.desmond.allaboutlistview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This application creates a ListView to which new elements can be added from the
 * top. When a new element is added, it is animated from above the bounds
 * of the list to the top. When the list is scrolled all the way to the top and a new
 * element is added, the row animation is accompanied by an image animation that pops
 * out of the round view and pops into the correct position in the top cell.
 */

public class ListViewCellInsertionActivity extends ActionBarActivity
        implements OnRowAdditionAnimationListener {

    private ListItemObject mValues[];

    private InsertionListView mListView;

    private Button mButton;

    private Integer mItemNum = 0;

    private RoundView mRoundView;

    private int mCellHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_cell_insertion);

        mValues = new ListItemObject[] {
                new ListItemObject("Chameleon", R.drawable.chameleon, 0),
                new ListItemObject("Rock", R.drawable.rock, 0),
                new ListItemObject("Flower", R.drawable.flower, 0),
        };

        mCellHeight = (int)(getResources().getDimension(R.dimen.cell_height));

        List<ListItemObject> mData = new ArrayList<ListItemObject>();
        CustomArrayAdapter mAdapter = new CustomArrayAdapter(this, R.layout.list_view_item, mData);
        RelativeLayout mLayout = (RelativeLayout)findViewById(R.id.relative_layout);

        mRoundView = (RoundView)findViewById(R.id.round_view);
        mButton = (Button)findViewById(R.id.add_row_button);
        mListView = (InsertionListView) findViewById(R.id.insertion_listview);

        mListView.setAdapter(mAdapter);
        mListView.setData(mData);
        mListView.setLayout(mLayout);
        mListView.setRowAdditionAnimationListener(this);
    }

    public void addRow(View view) {
        mButton.setEnabled(false);

        mItemNum++;
        ListItemObject obj = mValues[mItemNum % mValues.length];
        final ListItemObject newObj = new ListItemObject(obj.getTitle(), obj.getImgResource(),
                mCellHeight);

        boolean shouldAnimateInNewImage = mListView.shouldAnimateInNewImage();
        if (!shouldAnimateInNewImage) {
            mListView.addRow(newObj);
            return;
        }

        mListView.setEnabled(false);
        ObjectAnimator animator = mRoundView.getScalingAnimator();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                mListView.addRow(newObj);
            }
        });
        animator.start();
    }

    @Override
    public void onRowAdditionAnimationStart() {
        mButton.setEnabled(false);
    }

    @Override
    public void onRowAdditionAnimationEnd() {
        mButton.setEnabled(true);
    }
}
