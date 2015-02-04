package com.jayway.android.advanced.examples.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * A custom view showing a Circle with centered text
 * 
 * @author Rasmus Gohs
 * 
 */
public class Circle extends View {

	private String text = "";
	private float width, height;
	private final Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
	private int circleColor = Color.BLUE;
	private int textColor = Color.BLACK;
	private float circleCenterY, circleCenterX;
	private float textYpos;
	private float radius = 0;

	/**
	 * 
	 * @param context
	 * @param attrs
	 *            a set of style attributes we can set from XML
	 * @param defStyle
	 *            resource id to a theme we can set from XML
	 */
	public Circle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();

	}

	/**
	 * Called from xml if no defstyle is set. Default defstyle value is 0
	 * 
	 * @param context
	 * @param attrs
	 */
	public Circle(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public void setText(String text) {
		this.text = text;
		requestLayout();
	}

	public void setTextSize(float sizeInPixels) {
		textPaint.setTextSize(sizeInPixels);
		requestLayout();
	}

	private void init() {
		circlePaint.setColor(circleColor);
		textPaint.setColor(textColor);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setTextSize(25);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		// Measure the text size and add padding
		int textWidth = (int) textPaint.measureText(text);
		int desiredWidth = textWidth + getPaddingLeft() + getPaddingRight();
		int desiredHeight = textWidth + getPaddingTop() + getPaddingBottom();

		// Measure Width
		if (widthMode == MeasureSpec.EXACTLY) {
			// Must be this size
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			// Can't be bigger than...
			width = Math.min(desiredWidth, widthSize);
		} else {
			// Fill the parent
			width = widthSize;
		}
	
		// Measure Height
		if (heightMode == MeasureSpec.EXACTLY) {
			// Must be this size
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			// Can't be bigger than...
			height = Math.min(desiredHeight, heightSize);
		} else {
			// Fill the parent
			height = heightSize;
		}

		// MUST CALL THIS
		setMeasuredDimension(width, height);
	}

	/**
	 * Called when the size of the view have been measured
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		width = w;
		height = h;

		radius = Math.min(width, height) / 2;
		circleCenterX = width / 2;
		circleCenterY = height / 2;
		
		textYpos = getTextYPosition();

		setGradientShader();
	}

	/**
	 * This is where the actual drawing of the view takes place. We are passed a
	 * Canvas object that we can draw on.
	 * 
	 * When ever the system invalidates the view onDraw is called. Keep this
	 * method short and sweet for optimal performance
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		int circleCenterX = canvas.getWidth()/2;
//		int circleCenterY = canvas.getHeight()/2;
//		int minDiameter = Math.min(canvas.getWidth(), canvas.getHeight());
//		int radius = minDiameter/2;
		canvas.drawCircle(circleCenterX, circleCenterY, radius, circlePaint);

		if (hasText()) {
			canvas.drawText(text, circleCenterX, getTextYPosition(), textPaint);
		}
	}

	private boolean hasText() {
		return text != null && text.length() > 0;
	}

	/**
	 * set the x and y (upper left corner) of the text
	 * 
	 * @param bounds
	 *            the bounds of the text
	 */
	private float getTextYPosition() {
		Rect bounds = new Rect();
		textPaint.getTextBounds(text, 0, text.length(), bounds);
		return circleCenterY + (bounds.height() / 2);
	}

	/**
	 * Set a nice gradient shader on the circle
	 */
	private void setGradientShader() {
		Shader gradientShader = new LinearGradient(0, 0, width, height, Color.GRAY, circleColor, TileMode.CLAMP);
		circlePaint.setShader(gradientShader);
	}

	/**
	 * Set the current animation progress value so we can animate the pulsation
	 * 
	 * @param value
	 */
	public void setAnimationValue(float value) {
		int colorDelta = Color.BLUE - Color.BLACK;
		circleColor = (int) (Color.BLUE + (colorDelta * value));
		setGradientShader();
		invalidate();
	}
}
