package com.example.android.baking.ui.steps;

import com.example.android.baking.data.struct.MasterItem;
import com.example.android.baking.utilities.Event;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

@SuppressWarnings("WeakerAccess")
public class MasterDetailFragmentViewModel extends ViewModel {

    boolean initialized = false;
    final private MutableLiveData<Integer> masterItemIdLiveData = new MutableLiveData<>();
    final private MutableLiveData<List<MasterItem>> masterItemsLiveData = new MutableLiveData<>();
    final private MutableLiveData<Integer> masterItemIndexLiveData = new MutableLiveData<>();
    final private MutableLiveData<Boolean> twoPaneLiveData = new MutableLiveData<>();
    final private MutableLiveData<Boolean> nextItemAvailableLiveData = new MutableLiveData<>();
    final private MutableLiveData<Boolean> previousItemAvailableLiveData = new MutableLiveData<>();
    final private MutableLiveData<Event<Boolean>> handleMasterItemClickLiveData = new MutableLiveData<>();
    private int manualTapPendingPosition = -1;
    private int manualScrollPendingPosition = -1;

    // these two methods are critical to synchronize competing interactions: [scrolling of details] and [tapping of master items].
    // 1- when the user scrolls the details, a scroll listener updates the position LiveData. an observer then tries to trigger another (programmatic) scroll to that position.
    // 2- when the user taps a master item, it updates the position LiveData causing an observer to scroll the details. this triggers the scroll listener which tries to again update the position.
    public boolean initiateManualTap(int pendingPosition) {
        if (manualScrollPendingPosition == -1) {
            manualTapPendingPosition = pendingPosition;
            return true;
        }
        if (pendingPosition == manualScrollPendingPosition) {
            manualScrollPendingPosition = -1;
        }
        return false;
    }

    public boolean initiateManualScroll(int pendingPosition) {
        if (manualTapPendingPosition == -1) {
            manualScrollPendingPosition = pendingPosition;
            return true;
        }
        if (pendingPosition == manualTapPendingPosition) {
            manualTapPendingPosition = -1;
        }
        return false;
    }

    final private LiveData<MasterItem> masterItemLiveData = Transformations.map(masterItemIdLiveData, currentMasterItemId -> {
        MasterItem returnItem = null;
        if (currentMasterItemId != null && currentMasterItemId != -1) {
            List<MasterItem> items = masterItemsLiveData.getValue();
            if (items != null) {
                for (int i = 0; i < items.size(); i++) {
                    MasterItem item = items.get(i);
                    if (item.getId() == currentMasterItemId) {
                        returnItem = item;
                        masterItemIndexLiveData.setValue(i);
                        previousItemAvailableLiveData.setValue(i > 0);
                        nextItemAvailableLiveData.setValue(i < items.size() - 1);
                        break;
                    }
                }
            }
        }
        if (returnItem == null) {
            masterItemIndexLiveData.setValue(-1);
        }
        return returnItem;
    });

    public void init(int masterItemId) {
        if (!initialized) {
            initialized = true;
            masterItemIdLiveData.setValue(masterItemId);
        }
    }

    public void triggerMasterItemLoad(MasterItem item) {
        masterItemIdLiveData.setValue(item == null ? -1 : item.getId());
        handleMasterItemClickLiveData.setValue(new Event<>(item != null));
    }

    public void clearMasterItem() {
        masterItemIdLiveData.setValue(null);
    }

    public int getMasterItemId() {
        Integer currentMasterItemId = masterItemIdLiveData.getValue();
        return (currentMasterItemId == null ? -1 : currentMasterItemId);
    }

    public LiveData<MasterItem> getMasterItemLiveData() {
        return masterItemLiveData;
    }

    public MutableLiveData<List<MasterItem>> getMasterItemsLiveData() {
        return masterItemsLiveData;
    }

    public MutableLiveData<Boolean> getNextItemAvailableLiveData() {
        return nextItemAvailableLiveData;
    }

    public MutableLiveData<Boolean> getPreviousItemAvailableLiveData() {
        return previousItemAvailableLiveData;
    }

    public MutableLiveData<Event<Boolean>> getHandleMasterItemClickLiveData() {
        return handleMasterItemClickLiveData;
    }

    public MutableLiveData<Boolean> getTwoPaneLiveData() {
        return twoPaneLiveData;
    }

    public void setTwoPane(boolean twoPane) {
        twoPaneLiveData.setValue(twoPane);
    }

    public boolean isTwoPane() {
        Boolean twoPane = twoPaneLiveData.getValue();
        return (twoPane == null ? false : twoPane);
    }

    public void setMasterItems(List<MasterItem> items) {
        masterItemsLiveData.setValue(items);
        masterItemIdLiveData.setValue(getMasterItemId());
    }

    public MutableLiveData<Integer> getMasterItemIndexLiveData() {
        return masterItemIndexLiveData;
    }

    public void selectFirstMasterItemIfNotSet() {
        MasterItem currentItem = masterItemLiveData.getValue();
        if (currentItem == null) {
            List<MasterItem> items = masterItemsLiveData.getValue();
            if (items != null) {
                MasterItem item = items.get(0);
                if (item != null) {
                    masterItemIdLiveData.setValue(item.getId());
                }
            }
        }
    }

    public void moveStep(boolean forward) {
        List<MasterItem> items = masterItemsLiveData.getValue();
        if (items != null) {
            MasterItem currentItem = masterItemLiveData.getValue();
            if (currentItem != null) {
                MasterItem previousItem = null;
                for (int i = 0; i < items.size(); i++) {
                    MasterItem item = items.get(i);
                    if (item.getId() == currentItem.getId()) {
                        if (forward && i + 1 < items.size()) {
                            masterItemIdLiveData.setValue(items.get(i + 1).getId());
                        } else if (!forward && previousItem != null) {
                            masterItemIdLiveData.setValue(previousItem.getId());
                        }
                        break;
                    }
                    previousItem = item;
                }
            }
        }
    }

    public void setMasterItemId(int masterItemId) {
        masterItemIdLiveData.postValue(masterItemId);
    }
}
