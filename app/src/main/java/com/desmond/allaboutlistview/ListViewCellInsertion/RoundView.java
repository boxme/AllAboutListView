package com.desmond.allaboutlistview.ListViewCellInsertion;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * This round view draws a circle from which the image pops out of and into
 * the corresponding cell in the list
 */
public class RoundView extends View {

    private final int STROKE_WIDTH = 6;
    private final int RADIUS = 20;
    private final int ANIMATION_DURATION = 300;
    private final float SCALE_FACTOR = 0.3f;

    private Paint mPaint;

    public RoundView(Context context) {
        super(context);
        init();
    }

    public RoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(STROKE_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, RADIUS, mPaint);
    }

    public ObjectAnimator getScalingAnimator() {

        PropertyValuesHolder imViewScaleX =
                PropertyValuesHolder.ofFloat("scaleX", SCALE_FACTOR);

        PropertyValuesHolder imViewScaleY =
                PropertyValuesHolder.ofFloat("scaleY", SCALE_FACTOR);

        ObjectAnimator imgViewScaleAnimator =
                ObjectAnimator.ofPropertyValuesHolder(this, imViewScaleX, imViewScaleY);

        imgViewScaleAnimator.setRepeatCount(1);
        imgViewScaleAnimator.setRepeatMode(ValueAnimator.REVERSE);
        imgViewScaleAnimator.setDuration(ANIMATION_DURATION);

        return imgViewScaleAnimator;
    }
}
