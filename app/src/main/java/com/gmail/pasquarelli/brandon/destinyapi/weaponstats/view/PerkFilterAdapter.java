package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.view.MultiSelectSpinner;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model.SocketFilterItem;

public class PerkFilterAdapter extends BaseAdapter {

    private MultiSelectSpinner spinnerList;

    public PerkFilterAdapter(MultiSelectSpinner spinner) {
        spinnerList = spinner;
    }

    @Override
    public int getCount() {
        return spinnerList.getItemCount();
    }

    @Override
    public Object getItem(int position) {
        return spinnerList.getItemAtPosition(position);
    }

    @Override
    public long getItemId(int position) {
        return spinnerList.getItemIdAtPosition(position);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View returnView = LayoutInflater.from(parent.getContext()).inflate(R.layout.weapon_perk_filter_row_item,
                parent, false);

        SocketFilterItem item = (SocketFilterItem) spinnerList.getItemAtPosition(position);
        TextView perkName = returnView.findViewById(R.id.filter_perk_name);
        TextView perkDescription = returnView.findViewById(R.id.filter_perk_description);
        CheckBox perkSelected = returnView.findViewById(R.id.filter_perk_selected);

        perkName.setText(item.getPerkName());
        perkDescription.setText(item.getPerkDescription());
        perkSelected.setChecked(spinnerList.itemSelectedAt(position));
        returnView.setOnClickListener(v -> {
            spinnerList.changeStateAtPosition(position);
            perkSelected.setChecked(spinnerList.itemSelectedAt(position));
        });

        return returnView;
    }
}
