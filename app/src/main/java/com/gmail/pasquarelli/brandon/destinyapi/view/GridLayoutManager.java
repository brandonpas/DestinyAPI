package com.gmail.pasquarelli.brandon.destinyapi.view;

import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;

public abstract class GridLayoutManager {

    private String TAG = "GridLM";
    private int defaultMargin = 8;

    public Observable<ArrayList<View>> constructViews(ViewGroup parent, ArrayList<?> objectList) {
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
                width = parent.getWidth() - (defaultMargin * columnCount);
                Log.v(TAG, " columnCount: " + columnCount + ". Width: " + width);

                // The view may have changed (configuration change) and the layout manager can't calculate width,
                // so don't create any views otherwise they'll be the incorrect width;
                if (width <= 0)
                    return Observable.just(returnList);

                GridLayout gridLayout = (GridLayout) parent;
                gridLayout.setColumnCount(columnCount);

                for (Object o : objectList) {
                    View containerView = getItemView(o);
                    if (containerView == null)
                        continue;

                    // Define layout
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = width / columnCount;
                    params.height = getItemViewHeight(o);
//                    params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    Log.v(TAG, "containerView height: " + containerView.getHeight());
                    params.bottomMargin = 8;
                    params.rightMargin = 8;
                    containerView.setLayoutParams(params);
                    containerView.setPadding(0, 0, 0, 8);

                    // Add the view to the GridLayout
//            gridLayout.addView(containerView);
                    returnList.add(containerView);
                }
        return Observable.just(returnList);
    }

    public void destructViews(ViewGroup parent) {
        if (parent != null) {
            parent.removeAllViews();
        }
    }

    public abstract View getItemView(Object o);

    public abstract int getItemViewHeight(Object o);
}
