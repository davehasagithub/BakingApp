package com.example.android.baking.utilities;

import android.view.View;

import org.jetbrains.annotations.NotNull;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.SnapHelper;

// with help from https://medium.com/over-engineering/detecting-snap-changes-with-androids-recyclerview-snaphelper-9e9f5e95c424
public class SnapOnScrollListener extends RecyclerView.OnScrollListener {

    public interface OnSnapPositionChangeListener {
        void onSnapPositionChange(int position);
    }

    final private SnapHelper snapHelper;
    final private OnSnapPositionChangeListener onSnapPositionChangeListener;

    public SnapOnScrollListener(SnapHelper snapHelper, OnSnapPositionChangeListener onSnapPositionChangeListener) {
        this.snapHelper = snapHelper;
        this.onSnapPositionChangeListener = onSnapPositionChangeListener;
    }

    private int snapPosition = RecyclerView.NO_POSITION;

    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
        int snapPosition = getSnapPosition(recyclerView);
        boolean snapPositionChanged = this.snapPosition != snapPosition;
        if (snapPositionChanged) {
            if (onSnapPositionChangeListener != null) {
                onSnapPositionChangeListener.onSnapPositionChange(snapPosition);
            }
            this.snapPosition = snapPosition;
        }
    }

    private int getSnapPosition(RecyclerView recyclerView) {
        LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return RecyclerView.NO_POSITION;
        }
        View snapView = snapHelper.findSnapView(layoutManager);
        if (snapView == null) {
            return RecyclerView.NO_POSITION;
        }
        return layoutManager.getPosition(snapView);
    }
}
