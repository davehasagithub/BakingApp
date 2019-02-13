package com.example.android.baking.ui.steps.master;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.example.android.baking.R;
import com.example.android.baking.data.struct.MasterItem;
import com.example.android.baking.data.struct.MasterItem.MasterItemStep;
import com.example.android.baking.databinding.SharedMasterRowBinding;
import com.example.android.baking.ui.steps.master.MasterAdapter.ViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class MasterAdapter extends ListAdapter<MasterItem, ViewHolder> {
    public static final int VIEW_TYPE_INGREDIENTS_BUTTON = 1;
    public static final int VIEW_TYPE_STEP = 2;

    public static final int PAYLOAD_UPDATE_BACKGROUND = 0;

    final private MasterAdapterCallback masterAdapterCallback;

    MasterAdapter(MasterAdapterCallback masterAdapterCallback) {
        super(MasterAdapter.DIFF_CALLBACK);
        this.masterAdapterCallback = masterAdapterCallback;
    }

    public interface MasterAdapterCallback {
        void onClickItem(int position, MasterItem item);

        boolean isActivePosition(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.shared_master_row, viewGroup, false));
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        final SharedMasterRowBinding binding;

        ViewHolder(SharedMasterRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (masterAdapterCallback != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    MasterItem item = getItem(position);
                    masterAdapterCallback.onClickItem(position, item);
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.get(0) instanceof Integer) {
            Integer payload = (Integer) payloads.get(0);
            if (payload == MasterAdapter.PAYLOAD_UPDATE_BACKGROUND) {
                updateBackgroundColor(holder, position);
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    private void updateBackgroundColor(ViewHolder viewHolder, int position) {
        boolean selected = (masterAdapterCallback != null && masterAdapterCallback.isActivePosition(position));
        int color = ContextCompat.getColor(viewHolder.binding.getRoot().getContext(), selected ? R.color.stepCardBackgroundSelectedColor : R.color.stepCardBackgroundColor);
        viewHolder.binding.card.setCardBackgroundColor(color);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        MasterItem item = getItem(position);
        if (item != null) {
            Context context = viewHolder.binding.getRoot().getContext();
            if (item instanceof MasterItemStep) {
                viewHolder.binding.setText(((MasterItemStep) item).getStep().getCleanedShortDescription());
                viewHolder.binding.setImageUrl(((MasterItemStep) item).getStep().getThumbnailUrl());
                viewHolder.binding.setImageContentDescription(context.getString(R.string.accessibility_step_icon));
            } else {
                viewHolder.binding.setText(context.getString(R.string.ingredients_title));
                viewHolder.binding.setImageUrl(null);
                viewHolder.binding.setImageContentDescription(context.getString(R.string.accessibility_ingredient_icon));
            }
            viewHolder.binding.executePendingBindings();
            updateBackgroundColor(viewHolder, position);
        }
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    private static final DiffUtil.ItemCallback<MasterItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MasterItem>() {
                @Override
                public boolean areItemsTheSame(MasterItem oldMasterItem, MasterItem newMasterItem) {
                    return oldMasterItem.getId() == newMasterItem.getId();
                }

                @Override
                public boolean areContentsTheSame(MasterItem oldMasterItem, @NonNull MasterItem newMasterItem) {
                    return oldMasterItem.equals(newMasterItem);
                }
            };
}
