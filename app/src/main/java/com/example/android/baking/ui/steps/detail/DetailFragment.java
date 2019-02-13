package com.example.android.baking.ui.steps.detail;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.android.baking.R;
import com.example.android.baking.data.struct.MasterItem;
import com.example.android.baking.databinding.DetailFragmentBinding;
import com.example.android.baking.ui.steps.MasterDetailFragmentViewModel;
import com.example.android.baking.ui.steps.detail.DetailAdapter.DetailAdapterCallback;
import com.example.android.baking.utilities.SnapOnScrollListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class DetailFragment extends Fragment implements DetailAdapterCallback {

    private DetailFragmentBinding binding;
    private MasterDetailFragmentViewModel masterDetailFragmentViewModel;
    private DetailAdapter adapter;

    private int lastPosition = -1;

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        adapter = new DetailAdapter(context, this, getLifecycle());
        adapter.setHasStableIds(true);

        Fragment parentFragment = getParentFragment();
        if (parentFragment != null) {
            masterDetailFragmentViewModel = ViewModelProviders.of(parentFragment).get(MasterDetailFragmentViewModel.class);
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (getParentFragment() != null && getParentFragment().isRemoving()) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.hold);
        } else {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DetailFragmentBinding.inflate(inflater);
        binding.setMasterDetailFragmentViewModel(masterDetailFragmentViewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postponeEnterTransition();
        final ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (vto.isAlive()) {
                    vto.removeOnPreDrawListener(this);
                } else {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                startPostponedEnterTransition();
                binding.setLifecycleOwner(DetailFragment.this);
                return true;
            }
        });

        binding.recyclerviewDetailItems.setHasFixedSize(true);
        binding.recyclerviewDetailItems.setAdapter(adapter);
        binding.recyclerviewDetailItems.addItemDecoration(new DetailDecoration());
        binding.recyclerviewDetailItems.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        Integer position = masterDetailFragmentViewModel.getMasterItemIndexLiveData().getValue();
        if (position != null && position != -1) {
            binding.recyclerviewDetailItems.scrollToPosition(position);
        }

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.recyclerviewDetailItems);
        SnapOnScrollListener snapOnScrollListener = new SnapOnScrollListener(snapHelper, newPosition -> {
            if (masterDetailFragmentViewModel.initiateManualScroll(newPosition)) {
                Timber.d("initiateManualScroll %d", newPosition);
                List<MasterItem> masterItems = masterDetailFragmentViewModel.getMasterItemsLiveData().getValue();
                if (masterItems != null && newPosition < masterItems.size()) {
                    masterDetailFragmentViewModel.setMasterItemId(masterItems.get(newPosition).getId());
                }
            } else {
                Timber.d("initiateManualScroll %d [ignored]", newPosition);
            }
        });
        binding.recyclerviewDetailItems.addOnScrollListener(snapOnScrollListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addViewModelObservers();
    }

    private void addViewModelObservers() {
        masterDetailFragmentViewModel.getMasterItemsLiveData().observe(getViewLifecycleOwner(), masterItems -> {
                    if (adapter != null) {
                        adapter.submitList(masterItems);
                    }
                }
        );

        masterDetailFragmentViewModel.getMasterItemIndexLiveData().observe(getViewLifecycleOwner(), newPosition -> {
            if (masterDetailFragmentViewModel.initiateManualTap(newPosition)) {
                Timber.d("initiateManualTap %d", newPosition);
                if (newPosition != -1) {
                    binding.recyclerviewDetailItems.stopScroll();
                    binding.recyclerviewDetailItems.scrollToPosition(newPosition);
                }
            } else {
                Timber.d("initiateManualTap %d [ignored]", newPosition);
            }

            updateSelectedPosition(newPosition);
        });
    }

    private void updateSelectedPosition(int position) {
        int oldPosition = lastPosition;
        lastPosition = position;
        if (lastPosition != -1) {
            adapter.notifyItemChanged(lastPosition, DetailAdapter.PAYLOAD_UPDATE_EXOPLAYER);
        }
        if (oldPosition != -1) {
            adapter.notifyItemChanged(oldPosition, DetailAdapter.PAYLOAD_UPDATE_EXOPLAYER);
        }
    }

    @Override
    public boolean isActivePosition(int position) {
        return position == lastPosition;
    }
}
