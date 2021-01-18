package com.example.smartgarden.customWidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

public class myImageView extends androidx.appcompat.widget.AppCompatImageView {
    Paint mPaint;
    Paint mTextPaint;
    Paint mCircle;

    int rowCarry;
    int columnCarry;
    int rowHeight;
    int columnWidth;

    int nColumn = 10;
    int nRow = 10;

    public int posRefX = 0, posRefY = 0;
    public int posActX = 0, posActY = 0;

    public int[] getSize(){
        int[] data = {this.nColumn, this.nRow};
        return data;
    }

    public int[] getPosition(float x, float y){
        int[] data = {0,0};
        data[0] = (int)(x-columnCarry)/columnWidth;
        data[1] = (int)(y-rowCarry)/rowHeight;
        return data;
    }

    public myImageView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    public myImageView(Context context, AttributeSet attributeSet, int defStyle){
        super(context, attributeSet, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rowCarry = this.getHeight() % nRow;
        columnCarry = this.getWidth() % nColumn;
        rowHeight = this.getHeight() / nRow;
        columnWidth = this.getWidth() / nColumn;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.RED);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(32);

        mCircle = new Paint();
        mCircle.setColor(Color.CYAN);
        mCircle.setStyle(Paint.Style.FILL_AND_STROKE);
        mCircle.setStrokeWidth(10);

        Rect rect = new Rect(0+columnCarry/2, 0+rowCarry/2, columnWidth*nColumn+columnCarry/2, rowHeight*nRow+rowCarry/2);
        canvas.drawRect(rect, mPaint);

        mPaint.setStrokeWidth(1);
        for(int i=1; i<nColumn; i++){
            canvas.drawText( String.valueOf(i), 16+columnWidth*i+columnCarry/2, 32+rowCarry/2, mTextPaint);
            canvas.drawLine(columnWidth*i+columnCarry/2, 0+rowCarry/2, columnWidth*i + columnCarry / 2, rowHeight*nRow+rowCarry/2, mPaint);
        }
        for(int i=1; i<nRow; i++){
            canvas.drawText(String.valueOf(i), 16+columnCarry/2, 32+rowHeight*i+columnCarry/2, mTextPaint);
            canvas.drawLine(0+columnCarry/2, rowHeight*i+rowCarry/2, columnWidth*nColumn+columnCarry/2, rowHeight*i+rowCarry/2, mPaint);
        }

        canvas.drawCircle(posRefX*columnWidth+columnCarry/2+columnWidth/2, posRefY*rowHeight+rowCarry/2+rowHeight/2,Math.min(rowHeight,columnWidth)/2-16,mCircle);
//        mCircle.setColor(Color.MAGENTA);
//        canvas.drawCircle(posActX*columnWidth+columnCarry/2+columnWidth/2, posActY*rowHeight+rowCarry/2+rowHeight/2,Math.min(rowHeight,columnWidth)/2-16,mCircle);
    }
}
