package com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.view;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.database.milestones.entity.AppMilestoneEntity;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.viewmodel.WeeklyMilestonesViewModel;

/**
 * Custom adapter for PublicMilestoneObject RecyclerView
 */
class PublicMilestonesAdapter extends RecyclerView.Adapter {

    private WeeklyMilestonesViewModel viewModel;

    PublicMilestonesAdapter(WeeklyMilestonesActivity activity) {
        viewModel = ViewModelProviders.of(activity).get(WeeklyMilestonesViewModel.class);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.public_milestone_row_item, parent, false);
        return new PublicMilestonesViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        View itemView = holder.itemView;
        TextView milestoneName = itemView.findViewById(R.id.milestone_name);
        TextView milestoneDescription = itemView.findViewById(R.id.milestone_description);
        ImageView milestoneImage = itemView.findViewById(R.id.milestone_image);


        AppMilestoneEntity milestone = viewModel.getMilestonesArray().get(position);

        // Utilize the Glide library to download image via the URL
        // and insert into the ImageView
        if (milestone.displayProperties != null) {
            Glide.with(itemView.getContext())
                    .load(milestone.getIconUrl())
                    .into(milestoneImage);
        }

        milestoneName.setText(milestone.getName());
        milestoneDescription.setText(milestone.getDescription());
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
