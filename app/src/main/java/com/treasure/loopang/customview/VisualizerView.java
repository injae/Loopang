package com.treasure.loopang.customview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaExtractor;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class VisualizerView extends FrameLayout {
    private static final int LINE_WIDTH = 1; // width of visualizer lines
    private static final int LINE_SCALE = 75; // scales visualizer lines
    private List<Float> amplitudes; // amplitudes for line lengths
    private int width; // width of this View
    private int height; // height of this View
    private Paint linePaint; // specifies line drawing characteristics

    // constructor
    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs); // call superclass constructor
        linePaint = new Paint(); // create Paint for lines
        linePaint.setColor(Color.BLACK); // set color to green
        linePaint.setStrokeWidth(LINE_WIDTH); // set stroke width
        amplitudes = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if( widthMode == MeasureSpec.UNSPECIFIED)
            width = 500;
        else if( widthMode == MeasureSpec.AT_MOST)
            width = widthMeasureSpec;
        else if( widthMode == MeasureSpec.EXACTLY)
            width = MeasureSpec.getSize(widthMeasureSpec);

        if( heightMode == MeasureSpec.UNSPECIFIED)
            height = 60;
        else if( heightMode == MeasureSpec.AT_MOST)
            height = heightMeasureSpec;
        else if( heightMode == MeasureSpec.EXACTLY)
            height = MeasureSpec.getSize(heightMeasureSpec);;

        setMeasuredDimension(width, height);
    }

    // called when the dimensions of the View change
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(w == 0 || h == 0) return;

        width = w; // new width of this View
        height = h; // new height of this View
        amplitudes = new ArrayList<>(width / LINE_WIDTH);
    }

    // clear all amplitudes to prepare for a new visualization
    public void clear() {
        amplitudes.clear();
    }

    // add the given amplitude to the amplitudes ArrayList
    public void addAmplitude(float amplitude) {
        amplitudes.add(amplitude); // add newest to the amplitudes ArrayList
        // if the power lines completely fill the VisualizerView
        if (amplitudes.size() * LINE_WIDTH >= width) {
           // amplitudes.remove(0); // remove oldest power value
        }
    }

    public void addAmplitudeAndInvalidate(float amplitude) {
        this.addAmplitude(amplitude);
        this.invalidate();
    }

    public List<Float> getAmplitudes() {
        return amplitudes;
    }

    public void setAmplitudes(List<Float> amplitudes) {
        this.amplitudes = amplitudes;
    }

    // draw the visualizer with scaled lines representing the amplitudes
    @Override
    public void onDraw(Canvas canvas) {
        int middle = height / 2; // get the middle of the View
        float curX =  0; // width - Float.intBitsToFloat(amplitudes.size())/ 2; // start curX at middle

        // for each item in the amplitudes ArrayList
        for (float power : amplitudes) {
            float scaledHeight = power / LINE_SCALE; // scale the power
            curX += LINE_WIDTH; // increase X by LINE_WIDTH

            // draw a line representing this item in the amplitudes ArrayList
            canvas.drawLine(curX, middle + scaledHeight / 2 + 1, curX, middle
                    - scaledHeight / 2 - 1, linePaint);
        }
    }

}