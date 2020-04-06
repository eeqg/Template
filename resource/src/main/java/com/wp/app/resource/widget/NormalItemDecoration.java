package com.wp.app.resource.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.wp.app.resource.R;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import cn.shyman.library.refresh.RecyclerAdapter;

/**
 * Created by wp on 2018/4/11.
 */

public class NormalItemDecoration extends RecyclerAdapter.ItemDecoration {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private Paint dividerPaint;
    private Paint dividerBgPaint;
    private int dividerSize;
    private int leftOffset, rightOffset;

    private int orientation = HORIZONTAL;

    @Target(ElementType.PARAMETER)
    @IntDef({HORIZONTAL, VERTICAL})
    public @interface Orientation {
    }

    public NormalItemDecoration(Context context) {
        this(context, 0);
    }

    public NormalItemDecoration(Context context, int offset) {
        this(context, offset, offset, HORIZONTAL);
    }

    public NormalItemDecoration(Context context, int leftOffset, int rightOffset) {
        this(context, leftOffset, rightOffset, HORIZONTAL);
    }

    public NormalItemDecoration(Context context, int leftOffset, int rightOffset, @Orientation int orientation) {
        this.leftOffset = dp2px(context, leftOffset);
        this.rightOffset = dp2px(context, rightOffset);
        this.orientation = orientation;

        this.dividerBgPaint = new Paint();
        this.dividerBgPaint.setAntiAlias(true);
        this.dividerBgPaint.setColor(ContextCompat.getColor(context, R.color.colorWhite));

        this.dividerPaint = new Paint();
        this.dividerPaint.setAntiAlias(true);
        this.dividerPaint.setColor(ContextCompat.getColor(context, R.color.colorDivider));
        this.dividerSize = context.getResources().getDimensionPixelSize(R.dimen.dimenDivider);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (isSkipDraw(view, parent, state)) {
            return;
        }

        if (this.orientation == HORIZONTAL) {
            outRect.bottom = this.dividerSize;
        } else {
            outRect.left = this.dividerSize;
        }
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int index = 0; index < childCount; index++) {
            View view = parent.getChildAt(index);
            if (isSkipDraw(view, parent, state)) {
                continue;
            }

            int left = 0, top = 0, right = 0, bottom = 0;
            if (this.orientation == HORIZONTAL) {
                left = view.getLeft() + leftOffset;
                top = view.getBottom();
                right = view.getRight() - rightOffset;
                bottom = top + this.dividerSize;
                canvas.drawRect(view.getLeft(), top, view.getRight(), bottom, this.dividerBgPaint);
            } else {
                left = view.getLeft() - this.dividerSize;
                top = view.getTop() + leftOffset;
                right = view.getLeft();
                bottom = view.getBottom() - rightOffset;
                canvas.drawRect(left, view.getTop(), right, view.getBottom(), this.dividerBgPaint);
            }
            canvas.drawRect(left, top, right, bottom, this.dividerPaint);
        }
    }

    private int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
