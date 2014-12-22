package com.desmond.allaboutlistview.ListViewExpandingCells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.desmond.allaboutlistview.R;

import java.util.List;

/**
 * This is a custom array adapter used to populate the listview whose item
 * will expand to display extra content in addition to the default display.
 */
public class CustomArrayAdapter extends ArrayAdapter<ExpandableListItem> {

    private List<ExpandableListItem> mData;
    private int mLayoutViewResourceId;

    public CustomArrayAdapter(Context context, int layoutViewResourceId,
                              List<ExpandableListItem> data) {
        super(context, layoutViewResourceId, data);
        mData = data;
        mLayoutViewResourceId = layoutViewResourceId;
    }

    /**
     * Populates the item in the listview cell with the appropriate data. This method
     * sets the thumbnail image, the title, and the extra text. This method also updates
     * the layout parameters of the item's view so that the image and title are centered
     * in the bounds of the collapsed view, and such that the extra text is not displayed
     * in the collapsed state of the cell.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ExpandableListItem object = mData.get(position);

        if(convertView == null) {
            LayoutInflater inflater = ((FragmentActivity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(mLayoutViewResourceId, parent, false);
        }

        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.item_linear_layout);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams
                (AbsListView.LayoutParams.MATCH_PARENT, object.getCollapsedHeight());
        linearLayout.setLayoutParams(linearLayoutParams);

        ImageView imgView = (ImageView)convertView.findViewById(R.id.image_view);
        TextView titleView = (TextView)convertView.findViewById(R.id.title_view);
        TextView textView = (TextView)convertView.findViewById(R.id.text_view);

        titleView.setText(object.getTitle());
        imgView.setImageBitmap(getCroppedBitmap(BitmapFactory.decodeResource(getContext()
                .getResources(), object.getImgResource(), null)));
        textView.setText(object.getText());

        convertView.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT));

        ExpandingLayout expandingLayout = (ExpandingLayout)convertView.findViewById(R.id
                .expanding_layout);
        expandingLayout.setExpandedHeight(object.getExpandedHeight());
        expandingLayout.setSizeChangedListener(object);

        if (!object.isExpanded()) {
            expandingLayout.setVisibility(View.GONE);
        } else {
            expandingLayout.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    /**
     * Crops a circle out of the thumbnail photo
     */
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        int halfWidth = bitmap.getWidth()/2;
        int halfHeight = bitmap.getHeight()/2;

        canvas.drawCircle(halfWidth, halfHeight, Math.max(halfWidth, halfHeight), paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
