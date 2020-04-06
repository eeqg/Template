package com.wp.app.resource.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import cn.shyman.library.refresh.RecyclerAdapter;

/**
 * Created by wp on 2018/4/11.
 */

public class SpaceItemDecoration extends RecyclerAdapter.ItemDecoration {
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	public static final int GRID = 2;
	
	private int space;
	private int orientation = VERTICAL;
	private boolean includeEdge;
	private boolean dividerOnly;
	private int offsetPosition;
	
	@Target(ElementType.PARAMETER)
	@IntDef({HORIZONTAL, VERTICAL, GRID})
	public @interface Orientation {
	}
	
	public SpaceItemDecoration(Context context, int space) {
		this(context, space, VERTICAL);
	}
	
	public SpaceItemDecoration(Context context, int space, @Orientation int orientation) {
		this(context, space, orientation, false);
	}
	
	public SpaceItemDecoration(Context context, int space, @Orientation int orientation, boolean includeEdge) {
		this(context, space, orientation, includeEdge, false);
	}
	
	public SpaceItemDecoration(Context context, int space, @Orientation int orientation,
                               boolean includeEdge, boolean dividerOnly) {
		this.space = dp2px(context, space);
		this.orientation = orientation;
		this.includeEdge = includeEdge;
		this.dividerOnly = dividerOnly;
	}
	
	public SpaceItemDecoration(Context context, int space, @Orientation int orientation,
                               boolean includeEdge, boolean dividerOnly, int offsetPosition) {
		this.space = dp2px(context, space);
		this.orientation = orientation;
		this.includeEdge = includeEdge;
		this.dividerOnly = dividerOnly;
		this.offsetPosition = offsetPosition;
	}
	
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		if (isSkipDraw(view, parent, state)) {
			return;
		}
		
		int position = parent.getChildAdapterPosition(view) - offsetPosition;
		// LogUtils.d("-----position = " + position);
		if (position < 0) {
			return;
		}
		if (this.orientation == VERTICAL) { //LinearLayoutManager
			if (includeEdge && position == 0) {
				outRect.top = this.space;
			}
			outRect.bottom = this.space;
		} else if (this.orientation == HORIZONTAL) { //LinearLayoutManager
			if (includeEdge && position == 0) {
				outRect.left = this.space;
			}
			outRect.right = this.space;
		} else if (this.orientation == GRID) {
			int spanCount = 0, column = 0;
			if (parent.getLayoutManager() instanceof GridLayoutManager) {
				GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
				spanCount = layoutManager.getSpanCount();
				column = position % spanCount;
			} else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
				StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) parent.getLayoutManager();
				spanCount = layoutManager.getSpanCount();
				StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
				column = lp.getSpanIndex();
			}
			if (dividerOnly) {
				outRect.left = column == 0 ? 0 : this.space / 2;
				outRect.right = column == spanCount - 1 ? 0 : this.space / 2;
				if (position >= spanCount) {
					outRect.top = this.space;
				}
			} else {
				if (includeEdge || position < spanCount) {
					outRect.top = this.space;
				}
				outRect.bottom = this.space;
				outRect.left = column == 0 ? this.space : this.space / 2;
				outRect.right = column == spanCount - 1 ? this.space : this.space / 2;
			}
		}
	}
	
	private int dp2px(Context context, float dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, context.getResources().getDisplayMetrics());
	}
}
