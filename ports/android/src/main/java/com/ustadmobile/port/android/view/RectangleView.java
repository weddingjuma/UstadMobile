package com.ustadmobile.port.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.graphics.Paint.Style;
import android.view.View;

import com.ustadmobile.core.omr.AttendanceSheetImage;
import com.ustadmobile.core.omr.OMRRecognizer;

/**
 * Created by varuna on 22/02/16.
 */
public class RectangleView extends View{

    Paint paint;

    int parentWidth;
    int parentHeight;

    private int[] pageArea;
    private int[][] fpSearchAreas;

    public RectangleView(Context context) {
        super(context);
        paint = new Paint();
    }

    public RectangleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        paint = new Paint();
    }

    public RectangleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        paint.setStyle(Style.FILL);
        paint.setAlpha(128);

        //Right side (x axis) of the page area
        int pgAreaRight = pageArea[OMRRecognizer.X] + pageArea[OMRRecognizer.WIDTH];

        //Bottom side (y axis) of the page area
        int pgAreaBottom = pageArea[OMRRecognizer.Y] + pageArea[OMRRecognizer.HEIGHT];

        paint.setColor(Color.BLACK);
        paint.setAlpha(128);

        //top margin zone
        canvas.drawRect(0, 0, parentWidth, pageArea[OMRRecognizer.Y], paint);

        //left side
        canvas.drawRect(0, pageArea[OMRRecognizer.Y],
                pageArea[OMRRecognizer.X], pgAreaBottom, paint);

        //right side
        canvas.drawRect(pgAreaRight, pageArea[OMRRecognizer.Y],
                parentWidth, pgAreaBottom, paint);

        //bottom margin zone
        canvas.drawRect(0, pgAreaBottom, parentWidth, parentHeight, paint);



        paint.setStyle(Style.STROKE);
        paint.setAlpha(255);
        paint.setStrokeWidth(4);
        canvas.drawRect(pageArea[OMRRecognizer.X], pageArea[OMRRecognizer.Y],
                pgAreaRight, pgAreaBottom, paint);

        //paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
        int boxTop, boxLeft, boxBottom, boxRight;
        for(int i = 0; i < fpSearchAreas.length; i++) {
            boxLeft = Math.max(fpSearchAreas[i][OMRRecognizer.X], pageArea[OMRRecognizer.X]);
            boxTop = Math.max(fpSearchAreas[i][OMRRecognizer.Y], pageArea[OMRRecognizer.Y]);
            boxRight = Math.min(fpSearchAreas[i][OMRRecognizer.X] + fpSearchAreas[i][OMRRecognizer.WIDTH],
                    pgAreaRight);
            boxBottom = Math.min(fpSearchAreas[i][OMRRecognizer.Y] + fpSearchAreas[i][OMRRecognizer.HEIGHT],
                    pgAreaBottom);

            canvas.drawRect(boxLeft, boxTop, boxRight, boxBottom, paint);
        }

    }

    public int[] getPageArea() {
        return pageArea;
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        pageArea = OMRRecognizer.getExpectedPageArea(210, 297,
                parentWidth, parentHeight, 0.1f);
        fpSearchAreas = OMRRecognizer.getFinderPatternSearchAreas(
                pageArea, AttendanceSheetImage.DEFAULT_PAGE_DISTANCES,
                AttendanceSheetImage.DEFAULT_FINDER_PATTERN_SIZE* 3f);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

}