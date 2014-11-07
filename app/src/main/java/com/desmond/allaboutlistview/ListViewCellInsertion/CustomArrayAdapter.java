package com.desmond.allaboutlistview.ListViewCellInsertion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.desmond.allaboutlistview.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by desmond on 2/11/14.
 */
public class CustomArrayAdapter extends ArrayAdapter<ListItemObject> {

    HashMap<ListItemObject, Integer> mIdMap = new HashMap<ListItemObject, Integer>();
    List<ListItemObject> mData;
    Context mContext;
    int mLayoutViewResourceId;
    int mCounter;

    public CustomArrayAdapter(Context context, int layoutViewResourceId, List<ListItemObject> data) {
        super(context, layoutViewResourceId, data);
        mData = data;
        mContext = context;
        mLayoutViewResourceId = layoutViewResourceId;
        updateStableIds();
    }

    @Override
    public long getItemId(int position) {
        ListItemObject item = mData.get(position);
        if (mIdMap.containsKey(item)) {
            return mIdMap.get(item);
        }

        return -1;
    }

    public void updateStableIds() {
        mIdMap.clear();
        mCounter = 0;
        for (int i = 0; i < mData.size(); i++) {
            mIdMap.put(mData.get(i), mCounter++);
        }
    }

    public void addStableIdforDataAtPosition(int position) {
        mIdMap.put(mData.get(position), mCounter++);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ListItemObject obj = mData.get(position);

        if (convertView == null) {
            convertView =
                    LayoutInflater.from(mContext).inflate(mLayoutViewResourceId, parent, false);
        }

        convertView.setLayoutParams(
                new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, obj.getHeight())
        );

        ImageView imgView = (ImageView) convertView.findViewById(R.id.image_view);
        TextView textView = (TextView) convertView.findViewById(R.id.text_view);

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                obj.getImgResource(), null);

        textView.setText(obj.getTitle());
        imgView.setImageBitmap(getCroppedBitmap(bitmap));
        return convertView;
    }

    /**
     * Returns a circular cropped version fo the bitmap passed in
     * @param bitmap
     * @return
     */
    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        int halfWidth = bitmap.getWidth() / 2;
        int halfHeight = bitmap.getHeight() / 2;

        canvas.drawCircle(halfWidth, halfHeight, Math.max(halfWidth, halfHeight), paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
