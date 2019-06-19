package com.example.exercise4.View.ViewObjects;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
    private static final int BORDER_WIDTH = 3;
    private static final double HANDLE_RATIO = 0.2;
    private static final double BASE_RATIO = 0.7;

    private Paint handlePaint;
    private Paint handleReflectionPaint;
    private Paint arrowPaint;
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
     * JoystickView's constructor.
     * @param context Context - the context on which this view is shown.
     */
    public JoystickView(Context context) {
        super(context);
        initPaints();
    }

    /**
     *  Creates the paints which will be used for drawing the joystick.
     */
    private void initPaints() {
        handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handlePaint.setColor(Color.BLACK);
        handlePaint.setStyle(Paint.Style.FILL);

        handleReflectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handleReflectionPaint.setColor(Color.GRAY);
        handleReflectionPaint.setAlpha(300);
        handleReflectionPaint.setDither(true);
        handleReflectionPaint.setStyle(Paint.Style.FILL);

        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setStyle(Paint.Style.FILL);

        arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arrowPaint.setStyle(Paint.Style.FILL);
        arrowPaint.setAlpha(180);

        baseMidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baseMidPaint.setColor(Color.rgb(15,15,15));
        baseMidPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.LTGRAY);
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
     * @param w new width
     * @param h new height
     * @param oldw old width
     * @param oldh old height
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
        return Math.atan2(centerY - positionY, positionX - centerX);
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
     * Initialize the shaders for the Paints before drawing them.
     * (some shaders are dependent on the object coordinates, and therefore cannot be initialized
     * at the constructor).
     */
    private void initShaders() {
        int r = handleRadios / 3;
        int[] c = {Color.BLACK, Color.DKGRAY};
        int[] c2 = {Color.WHITE, Color.BLACK};
        basePaint.setShader(new RadialGradient(centerX, centerY, baseRadios, c,
                null, Shader.TileMode.MIRROR));
        handlePaint.setShader(new RadialGradient(positionX - r,
                positionY - r, handleRadios + r, Color.GRAY,
                Color.BLACK, Shader.TileMode.MIRROR));
        arrowPaint.setShader(new RadialGradient(positionX, positionY,
                baseRadios + handleRadios, c2,null, Shader.TileMode.CLAMP));
    }

    /**
     * Draw the base of the Joystick on given canvas.
     * @param canvas Canvas
     */
    private void drawBase(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, baseRadios + BORDER_WIDTH * 2, borderPaint);
        int midRad = baseRadios / 2;
        canvas.drawCircle(centerX, centerY, baseRadios, basePaint);
        canvas.drawCircle(centerX, centerY, midRad, baseMidPaint);
        drawArrows(canvas);
    }

    /**
     * draw the arrows on the joystick base.
     * @param canvas context's canvas
     */
    private void drawArrows(Canvas canvas) {
        Path arrow = new Path();
        int arrowLine = baseRadios / 4, arrowDistance = handleRadios * 3;
        // Down:
        arrow.moveTo(centerX, centerY + arrowDistance);
        arrow.rLineTo(-arrowLine, -arrowLine);
        arrow.rLineTo(arrowLine * 2, 0);
        arrow.close();
        // UP:
        arrow.moveTo(centerX, centerY - arrowDistance);
        arrow.rLineTo(-arrowLine, arrowLine);
        arrow.rLineTo(arrowLine * 2, 0);
        arrow.close();
        // Left:
        arrow.moveTo(centerX - arrowDistance, centerY);
        arrow.rLineTo(arrowLine, -arrowLine);
        arrow.rLineTo(0, arrowLine * 2);
        arrow.close();
        // Right:
        arrow.moveTo(centerX + arrowDistance, centerY);
        arrow.rLineTo(-arrowLine,-arrowLine);
        arrow.rLineTo(0, arrowLine * 2);
        canvas.drawPath(arrow, arrowPaint);
    }

    /**
     * Draw the handle of the Joystick on given canvas
     * @param canvas Canvas
     */
    private void drawHandle(Canvas canvas) {
        int r = handleRadios / 3;
        canvas.drawCircle(positionX, positionY, handleRadios, handlePaint);
        canvas.drawCircle(positionX - r, positionY - r, r, handleReflectionPaint);
    }

    /**
     * Overriding the View's onDraw method.
     * Draw the joystick on the canvas.
     * @param canvas Canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        initShaders();
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

        performClick();
        invalidate();
        return true;
    }

    /**
     * Calling onMoveListener.onMove when clicked.
     * @return Boolean if onMoveListener exists return true, else returns false.
     */
    @Override
    public boolean performClick() {
        super.performClick();
        if (onMoveListener != null) {
            onMoveListener.onMove(getAngle(), getDistance());
            return true;
        }
        return false;
    }

    /**
     * Set the joystick onMoveListener to given OnMoveListener object.
     * @param onMoveListener OnMoveListener
     */
    public void setOnMoveListener(OnMoveListener onMoveListener) {
        this.onMoveListener = onMoveListener;
    }
}
