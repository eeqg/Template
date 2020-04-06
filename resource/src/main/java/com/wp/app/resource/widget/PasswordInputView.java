package com.wp.app.resource.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.DigitsKeyListener;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.wp.app.resource.R;


public class PasswordInputView extends AppCompatEditText {
	
	private static final String ACCEPTED = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	private int mItemLength;
	private Drawable mItemSrc;
	private Drawable mItemBackground;
	private int mItemPadding;
	private Drawable mDivider;
	
	private int mItemWidth;
	private int mItemHeight;
	private int mDividerWidth;
	
	public PasswordInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PasswordInputView);
		mItemLength = a.getInt(R.styleable.PasswordInputView_piv_itemLength, 6);
		mItemSrc = a.getDrawable(R.styleable.PasswordInputView_piv_itemSrc);
		mItemBackground = a.getDrawable(R.styleable.PasswordInputView_piv_itemBackground);
		mItemPadding = a.getDimensionPixelSize(R.styleable.PasswordInputView_piv_itemPadding, 0);
		if (mItemSrc != null) {
			mItemWidth = mItemSrc.getIntrinsicWidth();
			mItemHeight = mItemSrc.getIntrinsicHeight();
		}
		mDivider = a.getDrawable(R.styleable.PasswordInputView_piv_divider);
		if (mDivider != null) {
			mDividerWidth = mDivider.getIntrinsicWidth();
		}
		mDividerWidth = a.getDimensionPixelSize(R.styleable.PasswordInputView_piv_dividerWidth, mDividerWidth);
		a.recycle();
		
		setCursorVisible(false);
		setFilters(new InputFilter[]{new InputFilter.LengthFilter(mItemLength)});
		setKeyListener(DigitsKeyListener.getInstance(ACCEPTED));
		setInputType(EditorInfo.TYPE_CLASS_NUMBER);
	}
	
	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		invalidate();
	}
	
	@Override
	protected void onSelectionChanged(int selStart, int selEnd) {
		super.onSelectionChanged(selStart, selEnd);
		
		int length = getText().length();
		if (selStart != length || selEnd != length) {
			setSelection(length);
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mItemSrc == null) {
			setMeasuredDimension(0, 0);
			return;
		}
		
		// int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		// int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int measureWidth = (mItemWidth + mItemPadding * 2) * mItemLength
				+ mDividerWidth * (mItemLength - 1);
		int measureHeight = mItemHeight + mItemPadding * 2;
		
		setMeasuredDimension(measureWidth, measureHeight);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int textLength = length();
		
		int index = 0;
		for (; index < textLength; index++) {
			int left = index * (mItemWidth + mItemPadding * 2) + mDividerWidth * index;
			mItemSrc.setBounds(
					left + mItemPadding,
					mItemPadding,
					left + mItemWidth + mItemPadding,
					mItemHeight + mItemPadding);
			mItemSrc.draw(canvas);
			
			if (index != 0) {
				mDivider.setBounds(left, 0, left + mDividerWidth, mItemHeight + mItemPadding * 2);
				mDivider.draw(canvas);
			}
		}
		for (; index < mItemLength; index++) {
			int left = index * (mItemWidth + mItemPadding * 2) + mDividerWidth * index;
			if (index != 0) {
				mDivider.setBounds(left, 0, left + mDividerWidth, mItemHeight + mItemPadding * 2);
				mDivider.draw(canvas);
			}
		}
	}
	
	@Override
	public boolean isSuggestionsEnabled() {
		return false;
	}
	
	@Override
	protected MovementMethod getDefaultMovementMethod() {
		return new ArrowKeyMovementMethod() {
			@Override
			protected boolean handleMovementKey(TextView widget, Spannable buffer, int keyCode, int movementMetaState, KeyEvent event) {
				return false;
			}
			
			@Override
			protected boolean left(TextView widget, Spannable buffer) {
				return false;
			}
			
			@Override
			protected boolean right(TextView widget, Spannable buffer) {
				return false;
			}
			
			@Override
			protected boolean up(TextView widget, Spannable buffer) {
				return false;
			}
			
			@Override
			protected boolean down(TextView widget, Spannable buffer) {
				return false;
			}
			
			@Override
			protected boolean pageUp(TextView widget, Spannable buffer) {
				return false;
			}
			
			@Override
			protected boolean pageDown(TextView widget, Spannable buffer) {
				return false;
			}
			
			@Override
			protected boolean top(TextView widget, Spannable buffer) {
				return false;
			}
			
			@Override
			protected boolean bottom(TextView widget, Spannable buffer) {
				return false;
			}
			
			@Override
			protected boolean lineStart(TextView widget, Spannable buffer) {
				return false;
			}
			
			@Override
			protected boolean lineEnd(TextView widget, Spannable buffer) {
				return false;
			}
			
			@Override
			protected boolean home(TextView widget, Spannable buffer) {
				return false;
			}
			
			@Override
			protected boolean end(TextView widget, Spannable buffer) {
				return false;
			}
			
			@Override
			public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
				return false;
			}
			
			@Override
			public boolean canSelectArbitrarily() {
				return false;
			}
			
			@Override
			public void initialize(TextView widget, Spannable text) {
				setSelection(text.length());
			}
			
			@Override
			public void onTakeFocus(TextView view, Spannable text, int dir) {
			}
		};
	}
}
