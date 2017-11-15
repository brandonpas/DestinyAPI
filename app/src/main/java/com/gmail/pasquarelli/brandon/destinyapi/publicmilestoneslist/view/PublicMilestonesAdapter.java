package com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.model.MilestoneDefinition;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.viewmodel.WeeklyMilestonesViewModel;
import com.gmail.pasquarelli.brandon.destinyapi.utils.Conversions;

import java.util.Locale;

import io.reactivex.subjects.AsyncSubject;

/**
 * Custom adapter for PublicMilestoneObject RecyclerView
 */
class PublicMilestonesAdapter extends RecyclerView.Adapter {

    private Typeface bodyTypeFace;
    private Typeface subHeadingTypeFace;
    private WeeklyMilestonesViewModel viewModel;

    private PublicMilestonesAdapter() { }

    PublicMilestonesAdapter(WeeklyMilestonesActivity activity) {
        viewModel = ViewModelProviders.of(activity).get(WeeklyMilestonesViewModel.class);
        AssetManager am = activity.getApplicationContext().getAssets();
        bodyTypeFace = Typeface.createFromAsset(am,
                String.format(Locale.US, "font/%s", "Adobe Garamond Pro Regular.ttf"));
        subHeadingTypeFace = Typeface.createFromAsset(am,
                String.format(Locale.US, "font/%s", "NHaasGroteskDSStd-55Rg.otf"));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.public_milestone_row_item, parent, false);
        return new PublicMilestonesViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MilestoneDefinition milestone = viewModel.getMilestoneAt(position);
        if (milestone == null)
            return;

        View itemView = holder.itemView;
        TextView milestoneName = itemView.findViewById(R.id.milestone_name);
        TextView milestoneDescription = itemView.findViewById(R.id.milestone_description);
        ImageView milestoneImage = itemView.findViewById(R.id.milestone_image);

        // Set typefaces
        milestoneName.setTypeface(subHeadingTypeFace);
        milestoneDescription.setTypeface(bodyTypeFace);

        // Utilize the Glide library to download image via the URL
        // and insert into the ImageView
        Glide.with(itemView.getContext())
                .load(milestone.displayProperties.getIconUrl())
                .into(milestoneImage);


        milestoneName.setText(milestone.displayProperties.name);
        milestoneDescription.setText(milestone.displayProperties.description);
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
