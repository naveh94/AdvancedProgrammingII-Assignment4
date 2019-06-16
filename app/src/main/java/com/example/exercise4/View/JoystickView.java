package com.example.exercise4.View;

import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;

/**
 * A custom View implementing a dynamic Joystick that can be moved by the user.
 * A OnMoveListener object can be bound in order to get notifications on joystick movements.
 */
public class JoystickView extends View {

    public interface OnMoveListener {
        void onMove(double angle, int length);
    }

    //Constants:
    private static final int HANDLE_COLOR = Color.BLACK;
    private static final int BASE_COLOR = Color.DKGRAY;
    private static final int BORDER_COLOR = Color.BLACK;
    private static final int BORDER_WIDTH = 3;
    private static final double HANDLE_RATIO = 0.2;
    private static final double BASE_RATIO = 0.7;

    private Paint handlePaint;
    private Paint handleReflPaint;
    private Paint basePaint;
    private Paint borderPaint;
    private Paint baseMidPaint;

    private OnMoveListener onMoveListener;

    private int centerX = 0;
    private int centerY = 0;
    private int positionX = 0;
    private int positionY = 0;
    private int handleRadios = 0;
    private int baseRadios = 0;

    /**
     * JoystickView's constructor. Creates the paints which will be used by the handle, the base
     * and the border.
     * @param context Context - the context on which this view is shown.
     */
    public JoystickView(Context context) {
        super(context);

        handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handlePaint.setColor(HANDLE_COLOR);
        handlePaint.setStyle(Paint.Style.FILL);
        handlePaint.setShadowLayer(handleRadios, 5, 5, Color.BLACK);

        handleReflPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handleReflPaint.setColor(Color.GRAY);
        handleReflPaint.setAlpha(300);
        handleReflPaint.setShadowLayer(handleRadios / 4, 5, 5, Color.GRAY);
        handleReflPaint.setDither(true);
        handleReflPaint.setStyle(Paint.Style.FILL);

        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setStyle(Paint.Style.FILL);

        baseMidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baseMidPaint.setColor(Color.rgb(15,15,15));
        baseMidPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(BORDER_COLOR);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(BORDER_WIDTH);

    }

    /**
     * Resets the handle's and the base's position to the middle of the screen.
     */
    private void resetPosition() {
        centerX = positionX = getWidth() / 2;
        centerY = positionY = getHeight() / 2;
    }

    /**
     * Overriding the View's onSizeChanged method.
     * Resets the position of the joystick to the middle of the screen, and change their size
     * according to the lower value between the width and the height.
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resetPosition();

        int d = Math.min(w, h);
        handleRadios = (int) (d / 2 * HANDLE_RATIO);
        baseRadios = (int) (d / 2 * BASE_RATIO);
    }

    /**
     * Get the angle on which the handle is currently on from the center of the base.
     * @return double - the angle
     */
    private double getAngle() {
        double angle = Math.atan2(centerY - positionY, positionX - centerX);
        return angle;
    }

    /**
     * Get the distance of the handle from the middle of the screen as a percent from 100%.
     * @return int - the distance percent as an integer 1-100
     */
    private int getDistance() {
        long distance = Math.round ((100 * Math.sqrt((positionX - centerX) * (positionX - centerX) +
                (positionY - centerY) * (positionY - centerY)) / baseRadios));
        return (int)distance;
    }

    /**
     * Draw the base of the Joystick on given canvas.
     * @param canvas Canvas
     */
    private void drawBase(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, baseRadios + BORDER_WIDTH, borderPaint);
        int[] c = {Color.BLACK, Color.DKGRAY};
        basePaint.setShader(new RadialGradient(centerX, centerY, baseRadios, c,
                null, Shader.TileMode.MIRROR));
        int midRad = baseRadios - (baseRadios / 2);
        canvas.drawCircle(centerX, centerY, baseRadios, basePaint);
        canvas.drawCircle(centerX, centerY, midRad, baseMidPaint);
    }

    /**
     * Draw the handle of the Joystick on given canvas
     * @param canvas Canvas
     */
    private void drawHandle(Canvas canvas) {
        int r = handleRadios / 3;
        handlePaint.setShader(new RadialGradient(positionX - r,
                positionY - r, handleRadios + r, Color.DKGRAY,
                Color.BLACK, Shader.TileMode.MIRROR));
        canvas.drawCircle(positionX, positionY, handleRadios, handlePaint);
        canvas.drawCircle(positionX - r, positionY - r, r, handleReflPaint);
    }

    /**
     * Overriding the View's onDraw method.
     * Draw the joystick on the canvas.
     * @param canvas Canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        drawBase(canvas);
        drawHandle(canvas);
    }

    /**
     * Overriding the View's onTouchEvent method.
     * Set the current position of the handle to the finger's position (while keeping it inside
     * the base's border). If the event is "ACTION_UP" resets the handle's position to the middle of
     * the screen. Invoke the onMoveListener if exists, with getAngle and getDistance as parameters.
     * @param event MotionEvent - the event occurred
     * @return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        positionX = (int) event.getX();
        positionY = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_UP) {
            resetPosition();
        }

        double abs = Math.sqrt((positionX - centerX) * (positionX - centerX) +
                (positionY - centerY) * (positionY - centerY));

        if (abs > baseRadios) {
            positionX = (int) ((positionX - centerX) * baseRadios / abs + centerX);
            positionY = (int) ((positionY - centerY) * baseRadios / abs + centerY);
        }

        if (onMoveListener != null) {
            onMoveListener.onMove(getAngle(), getDistance());
        }

        invalidate(); // Making sure the view is redrawn.
        return true;
    }

    /**
     * Set the joystick onMoveListener to given OnMoveListener object.
     * @param onMoveListener
     */
    public void setOnMoveListener(OnMoveListener onMoveListener) {
        this.onMoveListener = onMoveListener;
    }

    /***
     * Used when using the JoystickView as a layout object. As explained above,
     * currently isn't used.
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
}
