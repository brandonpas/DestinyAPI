package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model.SocketFilterItem;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.viewmodel.WeaponStatsViewModel;


public class PerkFilterAdapter extends ArrayAdapter {

    private WeaponStatsViewModel weaponStatsViewModel;

    public PerkFilterAdapter(@NonNull Context context, int resource, WeaponStatsViewModel viewModel) {
        super(context, resource);
        weaponStatsViewModel = viewModel;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View returnView = LayoutInflater.from(parent.getContext()).inflate(R.layout.weapon_perk_filter_row_item,
                parent, false);

        SocketFilterItem item = weaponStatsViewModel.getSocketFilterItemAt(position);
        TextView perkName = returnView.findViewById(R.id.filter_perk_name);
        TextView perkDescription = returnView.findViewById(R.id.filter_perk_description);

        perkName.setText(item.getPerkName());
        perkDescription.setText(item.getPerkDescription());

        return returnView;
    }
}
