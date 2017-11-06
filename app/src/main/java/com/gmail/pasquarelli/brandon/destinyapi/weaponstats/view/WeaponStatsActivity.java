package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gmail.pasquarelli.brandon.destinyapi.MainApplication;
import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.view.GridLayoutManager;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model.WeaponStatContainer;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.viewmodel.WeaponStatsViewModel;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class WeaponStatsActivity extends AppCompatActivity {

    private String TAG = "WeaponStatActivity";
    private WeaponStatsViewModel statsViewModel;
    private GridLayout weaponLayout;
    private ProgressBar progressBar;
    GridLayoutManager layoutManager;
    private Spinner weaponSpinner;
    final private int HANDLER_MSG = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapon_stats);

        initViews();
        initViewModel();
    }

    protected void onStop(){
        super.onStop();
        layoutManager.destructViews(weaponLayout);
    }

    void initViews() {
        progressBar = findViewById(R.id.weapon_stat_query_progress);
        weaponLayout = findViewById(R.id.weapon_grid_layout);
        createLayoutManager();
    }

    void initViewModel() {
        statsViewModel = ViewModelProviders.of(this).get(WeaponStatsViewModel.class);
        statsViewModel.getContainersInitialized().observe(this, aBoolean -> {
            hideProgress();
            initWeaponSpinner();

            int position = weaponSpinner.getSelectedItemPosition();
            int selection = getResources().getIntArray(R.array.weapon_list_ids)[position];
            MainApplication mainApplication = (MainApplication) getApplication();
            statsViewModel.getStats(mainApplication.getDatabase(), selection);
        });
        statsViewModel.getWeaponStats().observe(this, this::createStatContainerViews);

        showProgress();
        MainApplication mainApplication = (MainApplication) getApplication();
        statsViewModel.queryStats(mainApplication.getDatabase());
    }

    private void initWeaponSpinner() {
        weaponSpinner = findViewById(R.id.weapon_type);
        weaponSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selection = getResources().getIntArray(R.array.weapon_list_ids)[position];
                MainApplication mainApplication = (MainApplication) getApplication();
                statsViewModel.getStats(mainApplication.getDatabase(), selection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void showProgress() {
        weaponLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        weaponLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Construct the view for each weapon stat container.
     * @param weaponStatContainers The list containing the WeaponStatContainers
     */
    private void createStatContainerViews(ArrayList<WeaponStatContainer> weaponStatContainers) {
        if (weaponLayout == null || weaponLayout.getWidth() <= 0)
            return;

        Log.v(TAG, "create container views. grid layout width: " + weaponLayout.getWidth());
        if (weaponStatContainers.size() <= 0)
            return;
            layoutManager.destructViews(weaponLayout);
            layoutManager.constructViews(weaponLayout, weaponStatContainers)
                    .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<View>>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(ArrayList<View> views) {
                        Log.v(TAG, "createStatContainers list size: " + views.size());
                        for (View view : views) {
                            weaponLayout.addView(view);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG, "createStatContainers onError");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });




//        ScrollView scrollView = findViewById(R.id.weapon_grid_scrollview);
//        GridLayout grid = findViewById(R.id.weapon_grid_layout);
//
//        ViewGroup.LayoutParams params = scrollView.getLayoutParams();
//        ViewGroup.LayoutParams gridParams = grid.getLayoutParams();
//
//        params.height = 1000;
//        gridParams.height = 1000;
//
//        grid.setLayoutParams(gridParams);
//        scrollView.setLayoutParams(params);
//
//        grid.invalidate();
//        grid.requestLayout();
//        scrollView.invalidate();
//        scrollView.requestLayout();
    }

    private void createLayoutManager() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        layoutManager = new GridLayoutManager() {
            @Override
            public View getItemView(Object o) {
                View gridItemView = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.weapon_stat_container, null);
                WeaponStatContainer statContainer = (WeaponStatContainer) o;

                TextView statLabel = gridItemView.findViewById(R.id.stat_label);
                ListView weaponList = gridItemView.findViewById(R.id.weapon_list_for_stat);
                weaponList.setAdapter(new WeaponItemAdapter(statContainer.getWeapons()));
                statLabel.setText(statContainer.getStatName());
                return gridItemView;
            }

            @Override
            public int getItemViewHeight(Object o) {
                int containerHeader = 36 * (int) metrics.density;
                WeaponStatContainer statContainer = (WeaponStatContainer) o;
                int listSize = statContainer.getWeaponListSize();
                if (listSize == 0)
                    return 0;
                else
                    return (containerHeader) + (listSize * 15 * (int) (metrics.density));
            }
        };
    }

    public static Intent getActivityIntent(Context context) {
        return new Intent(context, WeaponStatsActivity.class);
    }
}
