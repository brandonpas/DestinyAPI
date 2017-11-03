package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model.WeaponStatContainer;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.viewmodel.WeaponStatsViewModel;

import java.util.ArrayList;


public class WeaponListAdapter extends BaseAdapter {

    private String TAG = "WeaponListAdp";
    WeaponStatsViewModel statsViewModel;

    public WeaponListAdapter(AppCompatActivity activity) {
        statsViewModel = ViewModelProviders.of(activity).get(WeaponStatsViewModel.class);
        statsViewModel.getWeaponStats().observe(activity, new Observer<ArrayList<WeaponStatContainer>>() {
            @Override
            public void onChanged(@Nullable ArrayList<WeaponStatContainer> weaponStatContainers) {
                updateList();
            }
        });
    }

    @Override
    public int getCount() {
        return statsViewModel.getStatsListCount();
    }

    @Override
    public WeaponStatContainer getItem(int position) {
        return statsViewModel.getWeaponStatAt(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridItemView;

        if (convertView == null)
            gridItemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.weapon_stat_container, parent, false);
        else
            gridItemView = convertView;

        WeaponStatContainer statContainer = statsViewModel.getWeaponStatAt(position);
        if (statContainer == null)
            return LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.weapon_stat_container, parent, false);

        TextView statLabel = gridItemView.findViewById(R.id.stat_label);
        ListView weaponList = gridItemView.findViewById(R.id.weapon_list_for_stat);
        ViewGroup.LayoutParams params = weaponList.getLayoutParams();
        params.height = (statContainer.getWeaponListSize() * 72);
//        weaponList.setLayoutParams(params);
        weaponList.setAdapter(new WeaponItemAdapter(statContainer.getWeapons()));

        statLabel.setText(statContainer.getStatName());
        Log.v(TAG, "Loading position: " + position);
        return gridItemView;
    }

    private void updateList() {
        this.notifyDataSetChanged();
    }

}
