package com.example.android.baking.ui.masterdetail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.example.android.baking.R;
import com.example.android.baking.data.struct.MasterItem;
import com.example.android.baking.ui.masterdetail.MasterAdapter.ViewHolder;
import com.example.android.baking.data.struct.MasterItem.MasterItemStep;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class MasterAdapter extends ListAdapter<MasterItem, ViewHolder> {
    public static final int VIEW_TYPE_INGREDIENTS_BUTTON = 1;
    public static final int VIEW_TYPE_STEP = 2;

    public static final int PAYLOAD_UPDATE_BACKGROUND = 0;

    private MasterAdapterCallback masterAdapterCallback;

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
        int layoutId;
        if (viewType == VIEW_TYPE_INGREDIENTS_BUTTON) {
            layoutId = R.layout.ingredients_button_row;
        } else if (viewType == VIEW_TYPE_STEP) {
            layoutId = R.layout.master_row;
        } else {
            throw new IllegalArgumentException("unhandled view type " + viewType);
        }

        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), layoutId, viewGroup, false));
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        final ViewDataBinding binding;

        ViewHolder(ViewDataBinding binding) {
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
        viewHolder.binding.getRoot().setBackgroundColor(selected ? 0xff009900 : 0xff999999);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        MasterItem item = getItem(position);
        if (item != null) {
            if (item instanceof MasterItemStep) {
                viewHolder.binding.setVariable(BR.step, ((MasterItemStep) item).getStep());
                viewHolder.binding.executePendingBindings();
            }
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
