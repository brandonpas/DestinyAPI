package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.gmail.pasquarelli.brandon.destinyapi.MainApplication;
import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.viewmodel.WeaponStatsViewModel;

public class WeaponStatsActivity extends AppCompatActivity {

    private WeaponStatsViewModel statsViewModel;
    private GridView weaponGrid;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapon_stats);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        initViewModel();
    }

    void initViews() {
        weaponGrid = findViewById(R.id.weapon_grid);
        progressBar = findViewById(R.id.weapon_stat_query_progress);
        weaponGrid.setAdapter(new WeaponListAdapter(this));

        Spinner weaponSpinner = findViewById(R.id.weapon_type);
        weaponSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selection = getResources().getIntArray(R.array.weapon_list_ids)[position];
                MainApplication mainApplication = (MainApplication) getApplication();
                statsViewModel.getStats(mainApplication.getDatabase(), selection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void initViewModel() {
        statsViewModel = ViewModelProviders.of(this).get(WeaponStatsViewModel.class);
        statsViewModel.getContainersInitialized().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                hideProgress();
            }
        });
        showProgress();
        MainApplication mainApplication = (MainApplication) getApplication();
        statsViewModel.queryStats(mainApplication.getDatabase());
    }

    private void showProgress() {
        weaponGrid.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        weaponGrid.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
