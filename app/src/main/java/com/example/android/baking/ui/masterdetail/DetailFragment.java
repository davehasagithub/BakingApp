package com.example.android.baking.ui.masterdetail;

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
import com.example.android.baking.ui.masterdetail.DetailAdapter.DetailAdapterCallback;
import com.example.android.baking.utilities.SnapOnScrollListener;
import com.example.android.baking.utilities.SnapOnScrollListener.OnSnapPositionChangeListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
        //getLifecycle().addObserver(adapter);
        adapter.setHasStableIds(true);

        Fragment parentFragment = getParentFragment();
        if (parentFragment != null) {
            masterDetailFragmentViewModel = ViewModelProviders.of(parentFragment).get(MasterDetailFragmentViewModel.class);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        binding.recyclerviewMasterItems.setHasFixedSize(true);
        binding.recyclerviewMasterItems.setAdapter(adapter);
        binding.recyclerviewMasterItems.addItemDecoration(new DetailDecoration());
        binding.recyclerviewMasterItems.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        Integer position = masterDetailFragmentViewModel.getMasterItemIndexLiveData().getValue();
        if (position != null && position != -1) {
            // after activity killed the initial app restore does a smooth scroll. why?
            binding.recyclerviewMasterItems.scrollToPosition(position);
        }

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.recyclerviewMasterItems);
        SnapOnScrollListener snapOnScrollListener = new SnapOnScrollListener(snapHelper, new OnSnapPositionChangeListener() {
            @Override
            public void onSnapPositionChange(int newPosition) {
                Timber.v("onSnapPositionChange %d", newPosition);
                if (masterDetailFragmentViewModel.initiateManualScroll(newPosition)) {
                    Timber.v("initiateManualScroll %d", newPosition);
                    List<MasterItem> masterItems = masterDetailFragmentViewModel.getMasterItemsLiveData().getValue();
                    if (masterItems != null && newPosition < masterItems.size()) {
                        masterDetailFragmentViewModel.setMasterItemId(masterItems.get(newPosition).getId());

                        // updateSelectedPosition(newPosition);
                    }
                } else {
                    Timber.v("initiateManualScroll %d [ignored]", newPosition);
                }
            }
        });
        binding.recyclerviewMasterItems.addOnScrollListener(snapOnScrollListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addViewModelObservers();
    }

    private void addViewModelObservers() {
        masterDetailFragmentViewModel.getMasterItemsLiveData().observe(getViewLifecycleOwner(), new Observer<List<MasterItem>>() {
                    @Override
                    public void onChanged(List<MasterItem> masterItems) {
                        if (adapter != null) {
                            adapter.submitList(masterItems);
                        }
                    }
                }
        );

        masterDetailFragmentViewModel.getMasterItemIndexLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(final Integer newPosition) {
                Timber.v("onChanged %d", newPosition);
                if (masterDetailFragmentViewModel.initiateManualTap(newPosition)) {
                    Timber.v("initiateManualTap %d", newPosition);
                    if (newPosition != -1) {

                        binding.recyclerviewMasterItems.stopScroll();
                        binding.recyclerviewMasterItems.scrollToPosition(newPosition);

                        binding.recyclerviewMasterItems.post(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });

                    }
                } else {
                    Timber.d("onChanged %d [ignored]", newPosition);
                }

                updateSelectedPosition(newPosition);

            }
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
