package org.suirui.huijian.tv.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class MyRadioButton extends android.support.v7.widget.AppCompatRadioButton {
	private Context context;

	private Drawable mButtonDrawable;
	private int mButtonResource;

	public MyRadioButton(Context context) {
		super(context);
		this.context = context;
	}

	public MyRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	public void setButtonDrawable(int resid) {
		if (resid != 0 && resid == mButtonResource) {
			return;
		}

		mButtonResource = resid;

		Drawable d = null;
		if (mButtonResource != 0) {
			d = getResources().getDrawable(mButtonResource);
		}
		setButtonDrawable(d);
	}

	@Override
	public void setButtonDrawable(Drawable d) {
		if (d != null) {
			if (mButtonDrawable != null) {
				mButtonDrawable.setCallback(null);
				unscheduleDrawable(mButtonDrawable);
			}
			d.setCallback(this);
			d.setState(getDrawableState());
			d.setVisible(getVisibility() == VISIBLE, false);
			mButtonDrawable = d;
			mButtonDrawable.setState(null);
			setMinHeight(mButtonDrawable.getIntrinsicHeight());
		}

		refreshDrawableState();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable[] drawables = getCompoundDrawables();
		if (drawables != null) {
			Drawable drawableTop = drawables[1];
			if (drawableTop != null) {
				float textHeight = measureHeight(getText().toString());
				int drawablePadding = getCompoundDrawablePadding();
				int drawableHeight = drawableTop.getIntrinsicHeight();
				float bodyHeight = textHeight + drawableHeight
						+ drawablePadding;
				setPadding(0, (int) (getHeight() - bodyHeight), 0, 0);
				canvas.translate(0, 0 - (getHeight() - bodyHeight) / 2);
			}
		}
		super.onDraw(canvas);

	}
	
	//��ȡ�ı��߶�
	public int measureHeight(String text) {
		// Rect result = new Rect();
		// // Measure the text rectangle to get the height
		// getPaint().getTextBounds(text, 0, text.length(), result);
		// return result.height();
		Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();

		return (int) (fontMetrics.descent - fontMetrics.ascent + fontMetrics.leading);
	}
}