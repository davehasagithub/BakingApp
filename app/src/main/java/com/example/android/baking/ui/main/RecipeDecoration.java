package com.example.android.baking.ui.main;

import android.graphics.Rect;
import android.view.View;

import com.example.android.baking.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

class RecipeDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        if (layoutManager != null) {

            // https://stackoverflow.com/a/49915897
            int position = parent.getChildViewHolder(view).getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                position = parent.getChildViewHolder(view).getOldPosition();
            }

            if (position != RecyclerView.NO_POSITION) {
                int padding = parent.getContext().getResources().getDimensionPixelSize(R.dimen.master_padding);
                int spanCount = layoutManager.getSpanCount();
                int column = position % spanCount;

                outRect.left = padding - column * padding / spanCount;
                outRect.right = (column + 1) * padding / spanCount;

                outRect.top = (position < spanCount ? padding : 0);
                outRect.bottom = padding;
            }
        }
    }
}
