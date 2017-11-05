package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.model.InventoryProperties;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model.WeaponStat;

import java.util.ArrayList;


public class WeaponItemAdapter extends BaseAdapter {

    private String TAG = "WeaponItemAdp";
    private ArrayList<WeaponStat> weaponStats;
    private int legendaryColor;
    private int exoticColor;

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
        View weaponItemView;

        if (convertView == null)
            weaponItemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.weapon_item_row, parent, false);
        else
            weaponItemView = convertView;

        WeaponStat stat = weaponStats.get(position);
        if (stat == null)
            return weaponItemView;

        TextView weaponName = weaponItemView.findViewById(R.id.weapon_name);
        TextView statValue = weaponItemView.findViewById(R.id.weapon_stat_value);

        if (stat.getTierType() == InventoryProperties.TIER_TYPE_SUPERIOR) {
            if (legendaryColor == 0)
                legendaryColor = weaponItemView.getResources().getColor(R.color.legendary_item_purple);
            weaponItemView.setBackgroundColor(legendaryColor);
        }

        if (stat.getTierType() == InventoryProperties.TIER_TYPE_EXOTIC) {
            if (exoticColor == 0)
                exoticColor = weaponItemView.getResources().getColor(R.color.exotic_item_yellow);
            weaponItemView.setBackgroundColor(exoticColor);
        }

        weaponName.setText(stat.getWeaponName());
        statValue.setText(String.valueOf(stat.getStatValue()));
        Log.v(TAG, "Loading position: " + position);
        return weaponItemView;
    }
}
