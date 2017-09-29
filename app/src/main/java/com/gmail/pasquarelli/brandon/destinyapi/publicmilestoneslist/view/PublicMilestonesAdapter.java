package com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.view;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.model.PublicMilestoneObject;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.viewmodel.MainActivityViewModel;

/**
 * Custom adapter for PublicMilestoneObject RecyclerView
 */
class PublicMilestonesAdapter extends RecyclerView.Adapter {

    private MainActivityViewModel viewModel;

    PublicMilestonesAdapter(MainActivity activity) {
        viewModel = ViewModelProviders.of(activity).get(MainActivityViewModel.class);
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

        PublicMilestoneObject milestone = viewModel.getMilestonesArray().get(position);
        String description = "Hashcode for milestone: " + milestone.getMilestoneHash();
        milestoneDescription.setText(description);
    }

    @Override
    public int getItemCount() {
        return viewModel.getMilestonesArray().size();
    }

    /**
     * Constructor for PublicMilestonesViewHolder
     */
    private class PublicMilestonesViewHolder extends RecyclerView.ViewHolder {
        PublicMilestonesViewHolder(View itemView) {
            super(itemView);
        }
    }
}
