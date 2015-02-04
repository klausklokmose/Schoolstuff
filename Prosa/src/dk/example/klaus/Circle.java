package dk.example.klaus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class Circle extends View{

	private final Paint paint = new Paint();
	private final Paint text = new Paint();
	private String myText;
	
	public Circle(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		myText = "My Circle";
		paint.setColor(Color.CYAN);
		paint.setStyle(Paint.Style.FILL);
		text.setColor(Color.BLACK);
		text.setTextSize(50);
		text.setTextAlign(Align.CENTER);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int circleCenterX = canvas.getWidth()/2;
		int circleCenterY = canvas.getHeight()/2;
		int minD = Math.min(circleCenterX, circleCenterY);
		int radius = minD/2;
		Rect bounds = new Rect();
		//TODO
		canvas.drawCircle(circleCenterX, circleCenterY, radius, paint);
		canvas.drawText(myText, circleCenterX, circleCenterY, text);
	}
}
