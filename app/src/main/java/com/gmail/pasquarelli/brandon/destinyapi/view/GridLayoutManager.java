package com.gmail.pasquarelli.brandon.destinyapi.view;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import java.util.ArrayList;

import io.reactivex.Observable;

public abstract class GridLayoutManager {

    public Observable<ArrayList<View>> constructViewsAsync(View parent, ArrayList<?> objects) {
        return Observable.fromCallable(() -> {
            ArrayList<View> returnList = new ArrayList<>();

            if (parent == null)
                throw new RuntimeException("parent is null");

            int width;
            int columnCount;
            int orientation = parent.getContext().getResources().getConfiguration().orientation;

            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                columnCount = 2;
            else
                columnCount = 3;

            GridLayout gridLayout = (GridLayout) parent;
            int defaultMargin = gridLayout.getPaddingLeft();
            width = Resources.getSystem().getDisplayMetrics().widthPixels - (defaultMargin * columnCount) - 8;
            gridLayout.setColumnCount(columnCount);
            for (Object o : objects) {
                View containerView = getItemView(o);
                if (containerView == null)
                    continue;

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.bottomMargin = 8;
                params.rightMargin = 8;
                params.width = width / columnCount;
                containerView.setLayoutParams(params);
                containerView.setPadding(0, 0, 0, 8);
                returnList.add(containerView);
            }
            return returnList;
        });
    }

    public void destructViews(ViewGroup parent) {
        if (parent != null) {
            parent.removeAllViews();
        }
    }

    public abstract View getItemView(Object o);
}
