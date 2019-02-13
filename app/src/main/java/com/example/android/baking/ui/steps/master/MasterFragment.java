package com.example.android.baking.ui.steps.master;

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
import com.example.android.baking.databinding.MasterFragmentBinding;
import com.example.android.baking.ui.steps.MasterDetailFragment;
import com.example.android.baking.ui.steps.MasterDetailFragmentViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MasterFragment extends Fragment implements MasterAdapter.MasterAdapterCallback {

    private MasterFragmentBinding binding;
    private MasterDetailFragmentViewModel masterDetailFragmentViewModel;
    private MasterAdapter adapter;

    private int lastPosition = -1;

    public static MasterFragment newInstance() {
        return new MasterFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new MasterAdapter(this);
        adapter.setHasStableIds(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MasterFragmentBinding.inflate(inflater);
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
                binding.setLifecycleOwner(MasterFragment.this);
                return true;
            }
        });

        binding.recyclerviewMasterItems.setHasFixedSize(true);
        binding.recyclerviewMasterItems.setAdapter(adapter);
        binding.recyclerviewMasterItems.addItemDecoration(new MasterDecoration());
        binding.recyclerviewMasterItems.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
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

        masterDetailFragmentViewModel.getMasterItemIndexLiveData().observe(getViewLifecycleOwner(), this::updateSelectedPosition);
    }

    private void updateSelectedPosition(int position) {
        int oldPosition = lastPosition;
        lastPosition = position;
        if (lastPosition != -1) {
            adapter.notifyItemChanged(lastPosition, MasterAdapter.PAYLOAD_UPDATE_BACKGROUND);

            if (masterDetailFragmentViewModel.isTwoPane()) {
                binding.recyclerviewMasterItems.scrollToPosition(lastPosition);
            }
        }
        if (oldPosition != -1) {
            adapter.notifyItemChanged(oldPosition, MasterAdapter.PAYLOAD_UPDATE_BACKGROUND);
        }
    }

    @Override
    public void onClickItem(int position, MasterItem item) {
        boolean isReclick = isActivePosition(position);
        if (!isReclick || !MasterDetailFragment.AUTO_SELECT_FIRST_ON_TWO_PANE) {
            masterDetailFragmentViewModel.triggerMasterItemLoad(isReclick ? null : item);
        }
    }

    @Override
    public boolean isActivePosition(int position) {
        return position == lastPosition;
    }
}
