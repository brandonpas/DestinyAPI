package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.gmail.pasquarelli.brandon.destinyapi.MainApplication;
import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.model.InventoryProperties;
import com.gmail.pasquarelli.brandon.destinyapi.view.GridLayoutManager;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model.WeaponStat;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model.WeaponStatContainer;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.viewmodel.WeaponStatsViewModel;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WeaponStatsActivity extends AppCompatActivity {

    private String TAG = "WeaponStatActivity";
    private WeaponStatsViewModel statsViewModel;
    private GridLayout weaponLayout;
    private ProgressBar progressBar;
    GridLayoutManager layoutManager;
    private Spinner weaponSpinner;
    private int legendaryColor;
    private int exoticColor;
    private int labelSeparatorColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapon_stats);

        initViews();
        initViewModel();
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
     * Construct the view for each weapon stat container in the ArrayList returned.
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
                    public void onComplete() { }
                });
    }

    /**
     * Create the WeaponStat view and
     */
    private View getWeaponStatView(ViewGroup parent, WeaponStat stat) {
        View weaponView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weapon_item_row, null);

        if (stat == null)
            return weaponView;

        weaponView.setOnClickListener(view -> {
            Log.v(TAG, stat.getWeaponName() + " was clicked!");
        });
        TextView weaponName = weaponView.findViewById(R.id.weapon_name);
        TextView statValue = weaponView.findViewById(R.id.weapon_stat_value);

        if (stat.getTierType() == InventoryProperties.TIER_TYPE_SUPERIOR) {
            if (legendaryColor == 0)
                legendaryColor = getResources().getColor(R.color.legendary_item_purple);
            weaponView.setBackgroundColor(legendaryColor);
        }

        if (stat.getTierType() == InventoryProperties.TIER_TYPE_EXOTIC) {
            if (exoticColor == 0)
                exoticColor = getResources().getColor(R.color.exotic_item_yellow);
            weaponView.setBackgroundColor(exoticColor);
        }

        weaponName.setText(stat.getWeaponName());
        statValue.setText(String.valueOf(stat.getStatValue()));
        return weaponView;
    }

    /**
     * Create the Grid Layout Manager to be used for determining the number of columns
     * based on device orientation.
     */
    private void createLayoutManager() {
        layoutManager = new GridLayoutManager() {

            /**
             * Inflate and populate the WeaponStatContainer view within the GridLayout.
             * @param o In this case, the WeaponStatContainer
             * @return The WeaponStatContainer view with all items included.
             */
            @Override
            public View getItemView(Object o) {
                View gridItemView = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.weapon_stat_container, null);
                WeaponStatContainer statContainer = (WeaponStatContainer) o;

                TextView statLabel = gridItemView.findViewById(R.id.stat_label);
                LinearLayout weaponList = gridItemView.findViewById(R.id.weapon_list_for_stat_linear);

                for(WeaponStat stat : statContainer.getWeapons()){
                    View weaponView = getWeaponStatView(weaponList, stat);
                    weaponList.addView(weaponView);
                    View divider = new View(weaponList.getContext());
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 1);
                    divider.setLayoutParams(params);

                    if (labelSeparatorColor == 0)
                        labelSeparatorColor = weaponList.getContext().getResources().getColor(R.color.divider_dark);
                    divider.setBackgroundColor(labelSeparatorColor);
                    weaponList.addView(divider);
                }
                statLabel.setText(statContainer.getStatName());
                return gridItemView;
            }
        };
    }

    public static Intent getActivityIntent(Context context) {
        return new Intent(context, WeaponStatsActivity.class);
    }
}
