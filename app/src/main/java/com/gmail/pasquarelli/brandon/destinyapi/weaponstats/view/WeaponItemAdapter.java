package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model.WeaponStat;

import java.util.ArrayList;


public class WeaponItemAdapter extends BaseAdapter {

    private ArrayList<WeaponStat> weaponStats;

    public WeaponItemAdapter(ArrayList<WeaponStat> list) {
        weaponStats = list;
    }

    @Override
    public int getCount() {
        return weaponStats.size();
    }

    @Override
    public WeaponStat getItem(int position) {
        return weaponStats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View weaponItemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.weapon_item_row, parent, false);

        WeaponStat stat = weaponStats.get(position);
        if (stat == null)
            return weaponItemView;

        TextView weaponName = weaponItemView.findViewById(R.id.weapon_name);
        TextView statValue = weaponItemView.findViewById(R.id.weapon_stat_value);

        weaponName.setText(stat.getWeaponName());
        statValue.setText(String.valueOf(stat.getStatValue()));
        return weaponItemView;
    }
}
