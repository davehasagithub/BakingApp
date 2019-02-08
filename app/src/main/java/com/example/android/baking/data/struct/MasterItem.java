package com.example.android.baking.data.struct;

import com.example.android.baking.ui.masterdetail.MasterAdapter;

import java.util.List;

import androidx.core.util.ObjectsCompat;

public abstract class MasterItem {
    private final int viewType;

    MasterItem(int viewType) {
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }

    @Override
    public boolean equals(Object o) {
        return this.getClass().equals(o.getClass());
    }

    public abstract int getId();

    public static class MasterItemIngredientsButton extends MasterItem {
        private final List<IngredientDb> ingredients;
        public MasterItemIngredientsButton(List<IngredientDb> ingredients) {
            super(MasterAdapter.VIEW_TYPE_INGREDIENTS_BUTTON);
            this.ingredients = ingredients;
        }

        @Override
        public int getId() {
            return -1234;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            MasterItemIngredientsButton that = (MasterItemIngredientsButton) o;
            return ObjectsCompat.equals(ingredients, that.ingredients);
        }

        @Override
        public int hashCode() {
            return ObjectsCompat.hash(ingredients);
        }

        public List<IngredientDb> getIngredients() {
            return ingredients;
        }
    }

    public static class MasterItemStep extends MasterItem {
        private final StepDb step;
        public MasterItemStep(StepDb step) {
            super(MasterAdapter.VIEW_TYPE_STEP);
            this.step = step;
        }

        @Override
        public int getId() {
            return step.getId();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            MasterItemStep that = (MasterItemStep) o;
            return ObjectsCompat.equals(step, that.step);
        }

        @Override
        public int hashCode() {
            return ObjectsCompat.hash(step);
        }

        public StepDb getStep() {
            return step;
        }
    }
}
