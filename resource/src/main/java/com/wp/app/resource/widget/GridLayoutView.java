package com.wp.app.resource.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wp.app.resource.R;

import java.util.List;

/**
 * Created by wp on 2019/4/25.
 */
public class GridLayoutView extends FrameLayout {
    private final String TAG = GridLayoutView.class.getSimpleName();

    private final int column;
    private final int row;
    private float rowSpace, colSpace;
    private RecyclerView recyclerView;

    public GridLayoutView(@NonNull Context context) {
        this(context, null);
    }

    public GridLayoutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridLayoutView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GridLayoutView);
        column = a.getInt(R.styleable.GridLayoutView_grid_col, 3);
        row = a.getInt(R.styleable.GridLayoutView_grid_row, -1);
        rowSpace = a.getLayoutDimension(R.styleable.GridLayoutView_grid_space_row, 0);
        colSpace = a.getLayoutDimension(R.styleable.GridLayoutView_grid_space_col, 0);
        a.recycle();

        initView();
    }

    private void initView() {
        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), column));
        recyclerView.addItemDecoration(new ItemDecoration());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(recyclerView, params);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setAdapter(GridItemListAdapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    public static abstract class GridItemListAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {
        private List<T> dataList;

        private OnItemClickListener itemClickListener;

        protected GridItemListAdapter() {
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            itemClickListener = listener;
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemViewHolder itemViewHolder = onCreateItemHolder();
            final RecyclerViewHolder holder = new RecyclerViewHolder(itemViewHolder.onCreateView(parent));
            holder.itemViewHolder = itemViewHolder;
            itemViewHolder.onViewCreated(holder.itemView);
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(holder.getLayoutPosition());
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {
            // LogUtils.d("-----onBindViewHolder()---position = " + position);
            holder.itemViewHolder.onBind(position);
        }

        @Override
        public int getItemCount() {
            // LogUtils.d("-----" + (dataList != null ? dataList.size() : 0));
            return dataList != null ? dataList.size() : 0;
        }

        protected abstract ItemViewHolder onCreateItemHolder();

        public T getItem(int position) {
            return dataList != null ? dataList.get(position) : null;
        }

        public void setDataList(List<T> list) {
            if (dataList != null) {
                dataList.clear();
            }
            this.dataList = list;
            // LogUtils.e("-----", dataList.size() + "");
            notifyDataSetChanged();
        }

        public List<T> getDataList() {
            return dataList;
        }
    }

    public abstract static class ItemViewHolder {

        protected abstract View onCreateView(ViewGroup parent);

        protected void onViewCreated(View itemView) {
        }

        protected abstract void onBind(int position);
    }

    private static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private ItemViewHolder itemViewHolder;

        RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private class ItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int spanCount = 0, columnIndex = 0, rowIndex = 0;
            if (parent.getLayoutManager() instanceof GridLayoutManager) {
                GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
                spanCount = layoutManager.getSpanCount();
                columnIndex = position % spanCount;
                rowIndex = position / GridLayoutView.this.column;
            }
            if (rowIndex > 0) outRect.top = (int) rowSpace;
            outRect.left = (int) (columnIndex == 0 ? 0 : colSpace / 2);
            outRect.right = (int) (columnIndex == spanCount - 1 ? 0 : colSpace / 2);
        }
    }
}
