package com.example.android.baking.ui.steps.detail;

import android.graphics.Rect;
import android.view.View;

import com.example.android.baking.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class DetailDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        // https://stackoverflow.com/a/49915897
        int position = parent.getChildViewHolder(view).getAdapterPosition();
        if (position == RecyclerView.NO_POSITION) {
            position = parent.getChildViewHolder(view).getOldPosition();
        }

        if (position != RecyclerView.NO_POSITION) {
            int padding = parent.getContext().getResources().getDimensionPixelSize(R.dimen.master_padding);
            outRect.left = padding;
            outRect.right = padding;
        }
    }
}
