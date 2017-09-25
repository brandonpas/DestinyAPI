package com.gmail.pasquarelli.brandon.destinyapi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.pasquarelli.brandon.destinyapi.data.PublicMilestonesObject;

import java.util.ArrayList;

class PublicMilestonesAdapter extends RecyclerView.Adapter {

    private ArrayList<PublicMilestonesObject> milestonesArray;

    PublicMilestonesAdapter(ArrayList<PublicMilestonesObject> milestones) {
        milestonesArray = milestones;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.public_milestone_row_item, parent, false);
        return new PublicMilestonesViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView milestoneDescription = holder.itemView.findViewById(R.id.milestone_description);

        PublicMilestonesObject milestone = milestonesArray.get(position);
        String description = "Hashcode for milestone: " + milestone.getMilestoneHash();
        milestoneDescription.setText(description);
    }

    @Override
    public int getItemCount() {
        return milestonesArray.size();
    }

    void updateList(ArrayList<PublicMilestonesObject> newList) {
        milestonesArray.clear();
        milestonesArray.addAll(newList);
        notifyDataSetChanged();
    }

    private class PublicMilestonesViewHolder extends RecyclerView.ViewHolder {
        PublicMilestonesViewHolder(View itemView) {
            super(itemView);
        }
    }
}
