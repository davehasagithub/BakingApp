package com.example.android.baking.ui.masterdetail;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.baking.R;
import com.example.android.baking.data.struct.MasterItem;
import com.example.android.baking.databinding.DetailIngredientsBinding;
import com.example.android.baking.databinding.DetailStepBinding;
import com.example.android.baking.ui.masterdetail.DetailAdapter.ViewHolder;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class DetailAdapter extends ListAdapter<MasterItem, ViewHolder> implements DefaultLifecycleObserver {

    public static final int PAYLOAD_UPDATE_EXOPLAYER = 0;

    final private DetailAdapterCallback detailAdapterCallback;
    final private DataSource.Factory dataSourceFactory;
    final private SimpleExoPlayer player;
    private WeakReference<PlayerView> currentPlayerViewRef;

    public interface DetailAdapterCallback {
        boolean isActivePosition(int position);
    }

    @Override
    public void onDestroy(@NotNull LifecycleOwner owner) {
        Timber.d("player released");
        if (player != null) {
            player.release();
        }
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        Timber.d("player paused");
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    DetailAdapter(Context context, DetailAdapterCallback detailAdapterCallback, Lifecycle lifecycle) {
        super(DetailAdapter.DIFF_CALLBACK);

        this.detailAdapterCallback = detailAdapterCallback;
        this.dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "blah"));
        this.player = ExoPlayerFactory.newSimpleInstance(context);

        lifecycle.addObserver(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        int layoutId;
        if (viewType == MasterAdapter.VIEW_TYPE_INGREDIENTS_BUTTON) {
            layoutId = R.layout.detail_ingredients;
        } else if (viewType == MasterAdapter.VIEW_TYPE_STEP) {
            layoutId = R.layout.detail_step;
        } else {
            throw new IllegalArgumentException("unhandled view type " + viewType);
        }

        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), layoutId, viewGroup, false));
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ViewDataBinding binding;

        ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.get(0) instanceof Integer) {
            Integer payload = (Integer) payloads.get(0);
            if (payload == DetailAdapter.PAYLOAD_UPDATE_EXOPLAYER) {
                updateExoPlayer(holder, position);
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    private void updateExoPlayer(ViewHolder viewHolder, int position) {
        boolean selected = (detailAdapterCallback != null && detailAdapterCallback.isActivePosition(position));

        if (selected) {
            player.stop(true);
            player.setPlayWhenReady(false);
        }

        int viewType = getItemViewType(position);
        if (viewType == MasterAdapter.VIEW_TYPE_STEP) {
            DetailStepBinding binding = (DetailStepBinding) viewHolder.binding;
            String url = ((MasterItem.MasterItemStep) getItem(position)).getStep().getVideoUrl();
            boolean hasVideo = !TextUtils.isEmpty(url);
            binding.playerView.setVisibility(!hasVideo ? View.GONE : View.VISIBLE);
            if (selected) {
                if (hasVideo) {
                    player.prepare(new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url)));
                    binding.playerView.setControllerHideOnTouch(true);

                    PlayerView currentPlayerView = (currentPlayerViewRef == null ? null : currentPlayerViewRef.get());
                    if (currentPlayerView != null) {
                        if (currentPlayerView != binding.playerView) {
                            Timber.d("position %d switched target view", position);
                            PlayerView.switchTargetView(player, currentPlayerView, binding.playerView);
                        } else {
                            Timber.d("position %d existing target view", position);
                            binding.playerView.setPlayer(player);
                        }
                    } else {
                        Timber.d("position %d set player", position);
                        binding.playerView.setPlayer(player);
                    }
                    currentPlayerViewRef = new WeakReference<>(binding.playerView);
                }
            } else {
                if (binding.playerView.getPlayer() != null) {
                    Timber.d("position %d setplayer null", position);
                    binding.playerView.setPlayer(null);
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        MasterItem item = getItem(position);
        if (item != null) {
            Context context = viewHolder.binding.getRoot().getContext();
            int viewType = getItemViewType(position);
            if (viewType == MasterAdapter.VIEW_TYPE_STEP) {
                DetailStepBinding binding = (DetailStepBinding) viewHolder.binding;
                binding.setStep(((MasterItem.MasterItemStep) item).getStep());
                ((DetailStepBinding) viewHolder.binding).tvRecipeName.setTag("position" + position);
            } else if (viewType == MasterAdapter.VIEW_TYPE_INGREDIENTS_BUTTON) {
                DetailIngredientsBinding binding = (DetailIngredientsBinding) viewHolder.binding;

                IngredientsAdapter adapter = (IngredientsAdapter) binding.recyclerviewIngredients.getAdapter();
                if (adapter == null) {
                    adapter = new IngredientsAdapter();
                    adapter.setHasStableIds(true);
                    binding.recyclerviewIngredients.setAdapter(adapter);
                }
                adapter.submitList(((MasterItem.MasterItemIngredientsButton) item).getIngredients());

                binding.recyclerviewIngredients.setHasFixedSize(true);
                if (binding.recyclerviewIngredients.getItemDecorationCount() == 0) {
                    binding.recyclerviewIngredients.addItemDecoration(new IngredientsDecoration());
                }
                binding.recyclerviewIngredients.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            }
            updateExoPlayer(viewHolder, position);
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
