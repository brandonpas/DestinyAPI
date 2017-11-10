package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import com.gmail.pasquarelli.brandon.destinyapi.view.MultiSelectSpinner;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model.SocketFilter;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model.WeaponStat;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model.WeaponStatContainer;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.viewmodel.WeaponStatsViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WeaponStatsActivity extends AppCompatActivity {

    // ViewModels
    private WeaponStatsViewModel statsViewModel;

    // Views and Layouts
    private GridLayout weaponLayout;
    private ProgressBar progressBar;
    GridLayoutManager layoutManager;
    private Spinner weaponSpinner;
    MultiSelectSpinner multiSelectSpinner;

    // Values
    private String TAG = "WeaponStatActivity";
    private int legendaryColor;
    private int exoticColor;
    private int labelSeparatorColor;
    private String[] selectedWeapons = new String[3];
    private HashMap<String, ArrayList<Object>> viewReference = new HashMap<>();
    private int[] selectionColors;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapon_stats);

        initViews();
        initViewModel();
    }

    void initViews() {
        Resources resources = getResources();
        legendaryColor = resources.getColor(R.color.legendary_item_purple);
        exoticColor = resources.getColor(R.color.exotic_item_yellow);
        labelSeparatorColor = resources.getColor(R.color.divider_dark);
        selectionColors = new int[] {
                resources.getColor(R.color.selected_weapon_color_1),
                resources.getColor(R.color.selected_weapon_color_2),
                resources.getColor(R.color.selected_weapon_color_3)
        };

        progressBar = findViewById(R.id.weapon_stat_query_progress);
        weaponLayout = findViewById(R.id.weapon_grid_layout);
        createLayoutManager();

//        String[] array = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};
        multiSelectSpinner = findViewById(R.id.multi_select_perk_filter);
//        multiSelectSpinner.setValueList(array);
//        multiSelectSpinner.setSelection(0);


//        multiSelectSpinner.getSelectedItemsObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DisposableObserver<Boolean[]>() {
//                    @Override
//                    public void onNext(Boolean[] booleans) {
//                        Log.v(TAG, "Multi-select spinner onNext");
//                        Log.v(TAG,"selected items: " + booleans.length);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.v(TAG, "Multi-select spinner onError");
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() { }
//                });
    }

    /**
     * Initialize the ViewModel and set observers
     */
    void initViewModel() {
        statsViewModel = ViewModelProviders.of(this).get(WeaponStatsViewModel.class);
        statsViewModel.getContainersInitialized().observe(this, aBoolean -> {
            initWeaponSpinner();

            int position = weaponSpinner.getSelectedItemPosition();
            int selection = getResources().getIntArray(R.array.weapon_list_ids)[position];
            MainApplication mainApplication = (MainApplication) getApplication();
            statsViewModel.getStats(mainApplication.getDatabase(), selection, null);
        });
        statsViewModel.getWeaponStats().observe(this, this::processStatsResults);

        showProgress();
        MainApplication mainApplication = (MainApplication) getApplication();
        statsViewModel.queryStats(mainApplication.getDatabase());
//        statsViewModel.getAllPerks(mainApplication.getDatabase());
        statsViewModel.getFilterList().observe(this, strings -> {
            multiSelectSpinner.setValueList(strings);
        });
    }

    /**
     * Initialize the weapon class selection spinner
     */
    private void initWeaponSpinner() {
        weaponSpinner = findViewById(R.id.weapon_type);
        weaponSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selection = getResources().getIntArray(R.array.weapon_list_ids)[position];
                showProgress();
                MainApplication mainApplication = (MainApplication) getApplication();
                statsViewModel.getStats(mainApplication.getDatabase(), selection, null);
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

    private void processStatsResults(ArrayList<WeaponStatContainer> stats) {
        updatePerkFilter();
        createStatContainerViews(stats);
    }

    private void updatePerkFilter() {


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
        viewReference.clear();
        layoutManager.constructViews(weaponLayout, weaponStatContainers)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<View>>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(ArrayList<View> views) {
                        hideProgress();
                        for (View view : views)
                            weaponLayout.addView(view);
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
    private View getWeaponStatView(ViewGroup parent, WeaponStat stat, String containerName) {
        View weaponView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weapon_item_row, null);

        ConstraintLayout itemView = weaponView.findViewById(R.id.weapon_item_info);

        if (stat == null)
            return weaponView;

        weaponView.setOnClickListener(view -> changeWeaponSelectState(stat) );
        TextView weaponName = weaponView.findViewById(R.id.weapon_name);
        TextView statValue = weaponView.findViewById(R.id.weapon_stat_value);

//        if (stat.getTierType() == InventoryProperties.TIER_TYPE_SUPERIOR) {
            itemView.setBackgroundColor(legendaryColor);
            weaponView.setBackgroundColor(legendaryColor);
//        }

        if (stat.getTierType() == InventoryProperties.TIER_TYPE_EXOTIC) {
//            itemView.setBackgroundColor(exoticColor);
//            weaponView.setBackgroundColor(exoticColor);
            weaponName.setTextColor(exoticColor);
        }
        ArrayList<Object> viewRef = viewReference.get(stat.getWeaponHash());
        if (viewRef == null)
            viewRef = new ArrayList<>();
        weaponView.setTag(containerName + "~" + stat.getWeaponName());
        viewRef.add(weaponView.getTag());
        viewReference.put(stat.getWeaponHash(), viewRef);
        weaponName.setText(stat.getWeaponName());
        statValue.setText(String.valueOf(stat.getStatValue()));
        return weaponView;
    }

    /**
     * Change the items selection state. If not yet selected, highlight it by creating a stroke around the view
     * for the weapon in each container. If already selected, remove the highlight
     * @param weapon
     */
    private void changeWeaponSelectState(WeaponStat weapon) {

        int availableIndex = -1;
        String weaponHash = weapon.getWeaponHash();
        if (weapon.getTierType() != InventoryProperties.TIER_TYPE_SUPERIOR)
            return;

        for (int selection=0; selection < selectedWeapons.length; selection++) {

            if (selectedWeapons[selection] == null) { availableIndex = selection;  continue; }

            // The selection isn't empty and its not the weapon selected, don't "deselect" another item
            if (!selectedWeapons[selection].equals(weaponHash))
                continue;

            // the user "deselected" an item. revert the color back, clear the selection index, and quit
            // since thats all we needed to do
            if (selectedWeapons[selection].equals(weaponHash)) {
                selectedWeapons[selection] = null;

                ArrayList<Object> views = viewReference.get(weapon.getWeaponHash());
                for (Object tag : views) {
                    View weaponView = weaponLayout.findViewWithTag(tag);
                    if (weapon.getTierType() == InventoryProperties.TIER_TYPE_SUPERIOR)
                        weaponView.setBackgroundColor(legendaryColor);

                    if (weapon.getTierType() == InventoryProperties.TIER_TYPE_EXOTIC)
                        weaponView.setBackgroundColor(exoticColor);
                }

                // we've found the previously selected item and have now completed "deselecting" it.
                return;
            }
        }

        // we haven't yet found the item and we have a place to
        if (availableIndex > -1) {
            selectedWeapons[availableIndex] = weaponHash;


            ArrayList<Object> views = viewReference.get(weapon.getWeaponHash());
            for (Object tag : views) {
                View weaponView = weaponLayout.findViewWithTag(tag);
                weaponView.setBackgroundColor(selectionColors[availableIndex]);
            }
        }
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
                    View weaponView = getWeaponStatView(weaponList, stat, statContainer.getStatName());
                    weaponList.addView(weaponView);
                    View divider = new View(weaponList.getContext());
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 1);
                    divider.setLayoutParams(params);

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
