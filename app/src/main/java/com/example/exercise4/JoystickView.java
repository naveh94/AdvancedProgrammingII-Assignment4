package com.example.exercise4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View implements Runnable {

    public interface OnMoveListener {
        void onMove(double angle, int length);
    }

    //Constants:
    private static final int LOOP_INTERVAL = 100;
    private static final int HANDLE_COLOR = Color.BLACK;
    private static final int BASE_COLOR = Color.TRANSPARENT;
    private static final int BORDER_COLOR = Color.BLACK;
    private static final int BORDER_WIDTH = 3;
    private static final double HANDLE_RATIO = 0.2;
    private static final double BASE_RATIO = 0.7;


    private Paint handlePaint;
    private Paint basePaint;
    private Paint borderPaint;


    private OnMoveListener onMoveListener;
    private Thread joystickThread = new Thread(this);

    private int centerX = 0;
    private int centerY = 0;
    private int positionX = 0;
    private int positionY = 0;
    private int handleRadios = 0;
    private int baseRadios = 0;


    public JoystickView(Context context) {
        super(context);

        handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handlePaint.setColor(HANDLE_COLOR);
        handlePaint.setStyle(Paint.Style.FILL);

        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setColor(BASE_COLOR);
        basePaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(BORDER_COLOR);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(BORDER_WIDTH);
    }

    private void resetPosition() {
        centerX = positionX = getWidth() / 2;
        centerY = positionY = getHeight() / 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resetPosition();

        int d = Math.min(w, h);
        handleRadios = (int) (d / 2 * HANDLE_RATIO);
        baseRadios = (int) (d / 2 * BASE_RATIO);
    }

    private double getAngle() {
        /*int angle = (int) Math.toDegrees(Math.atan2(centerY - positionY, positionX - centerX));
        return angle < 0 ? 360 + angle : angle;*/
        double angle = Math.atan2(centerY - positionY, positionX - centerX);
        Log.d("Joystick", "centerX="+centerX+", positionX="+positionX+
                ", centerY="+centerY+", positionY="+positionY+", angle="+angle);
        return angle;
    }

    private int getLength() {
        long length = Math.round ((100 * Math.sqrt((positionX - centerX) * (positionX - centerX) +
                (positionY - centerY) * (positionY - centerY)) / baseRadios));
        return (int)length;
    }

    private void drawBase(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, baseRadios + BORDER_WIDTH, borderPaint);
        canvas.drawCircle(centerX, centerY, baseRadios, basePaint);
    }

    private void drawHandle(Canvas canvas) {
        canvas.drawCircle(positionX, positionY, handleRadios, handlePaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBase(canvas);
        drawHandle(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        positionX = (int) event.getX();
        positionY = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_UP) {
            joystickThread.interrupt();
            resetPosition();
            if (onMoveListener != null) {
                onMoveListener.onMove(getAngle(), getLength());
            }
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (joystickThread != null && joystickThread.isAlive()) {
                joystickThread.interrupt();
            }
            joystickThread = new Thread(this);
            joystickThread.start();

            if (onMoveListener != null) {
                onMoveListener.onMove(getAngle(), getLength());
            }
        }

        double abs = Math.sqrt((positionX - centerX) * (positionX - centerX) +
                (positionY - centerY) * (positionY - centerY));

        if (abs > baseRadios) {
            positionX = (int) ((positionX - centerX) * baseRadios / abs + centerX);
            positionY = (int) ((positionY - centerY) * baseRadios / abs + centerY);
        }

        if (onMoveListener != null) {
            onMoveListener.onMove(getAngle(), getLength());
        }

        invalidate();
        return true;
    }

    public void setOnMoveListener(OnMoveListener onMoveListener) {
        this.onMoveListener = onMoveListener;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (onMoveListener != null) {
                        onMoveListener.onMove(getAngle(), getLength());
                    }
                }
            });
            try {
                Thread.sleep(LOOP_INTERVAL);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
