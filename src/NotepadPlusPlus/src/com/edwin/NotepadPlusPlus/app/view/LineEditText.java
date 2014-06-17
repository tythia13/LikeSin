package com.edwin.NotepadPlusPlus.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * A custom EditText that draws lines between each line of text that is
 * displayed.
 */
public class LineEditText extends EditText {
	private Rect mRect;
	private Paint mPaint;

	// We need this constructor for LayoutInflater
	public LineEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

		mRect = new Rect();
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(android.graphics.Color.GRAY);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int count = getLineCount();
		Rect r = mRect;
		Paint paint = mPaint;

		for (int i = 0; i < count; i++) {
			int baseline = getLineBounds(i, r);
			canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
		}
	}
}
