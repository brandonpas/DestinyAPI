package com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;
import com.gmail.pasquarelli.brandon.destinyapi.database.databases.ContentDatabase;
import com.gmail.pasquarelli.brandon.destinyapi.api.response_models.PublicMilestonesResponse;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.viewmodel.WeeklyMilestonesViewModel;

import java.util.Locale;

public class WeeklyMilestonesActivity extends AppCompatActivity {

    private String TAG = "WeeklyMilestones";

    // ViewModels
    private WeeklyMilestonesViewModel model;

    // Views and adapters
    private PublicMilestonesAdapter milestonesAdapter;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    // Load fragment 1
                    return true;
                case R.id.navigation_dashboard:
                    // Load fragment 2
                    return true;
                case R.id.navigation_notifications:
                    // Load fragment 3
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.milestone_list);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setupRecyclerView();
        initViewModels();
        setFonts();
        fetchThisWeeksMilestones();
    }

    /**
     * Set the appropriate typeface for TextViews
     */
    public void setFonts() {
        AssetManager am = getApplicationContext().getAssets();
        // Create Typeface objects
        Typeface headingTypeFace = Typeface.createFromAsset(am,
                String.format(Locale.US, "font/%s", "NHaasGroteskDSStd-55Rg.otf"));

        // Retrieve Views
        TextView milestoneHeading = findViewById(R.id.milestone_label);

        // Set Typeface
        milestoneHeading.setTypeface(headingTypeFace);
    }

    /**
     * Initialize the ViewModels for this View.
     */
    void initViewModels() {
        model = ViewModelProviders.of(this).get(WeeklyMilestonesViewModel.class);
        bind();
    }

    /**
     * Set observers/define actions when the observable emits a change.
     */
    void bind() {
        model.getMilestonesResponse().observe(this, new Observer<PublicMilestonesResponse>() {
            @Override
            public void onChanged(@Nullable PublicMilestonesResponse response) {
                Log.v(TAG,"onChanged Called");
                milestonesAdapter.notifyDataSetChanged();
                if (response == null) {
                    showToast("Error contacting server", Toast.LENGTH_SHORT);
                } else if (!response.getErrorCode().equals("1")) {
                    showToast(response.getMessage(), Toast.LENGTH_SHORT);
                }
            }
        });

        model.getToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String message) {
                showToast(message, Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * Initialize the RecyclerView and adapter for the PublicMilestones list.
     */
    void setupRecyclerView() {
        RecyclerView milestoneRecyclerView = findViewById(R.id.public_milestones_recycler_view);
        milestonesAdapter = new PublicMilestonesAdapter(this);
        milestoneRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        milestoneRecyclerView.setAdapter(milestonesAdapter);
    }

    /**
     * Display a pop-up message
     * @param message Message to be displayed {@link android.widget.Toolbar Toolbar}
     * @param duration Duration integer from {@link android.widget.Toast}
     */
    void showToast(String message, int duration) {
        Toast.makeText(getApplicationContext(), message, duration).show();
    }

    /**
     * Invoke the API to retrieve and convert milestone data to usable list.
     * When complete, notify the adapter that the data-set changed.
     */
    void fetchThisWeeksMilestones() {
        ContentDatabase db = Room.databaseBuilder(getApplicationContext(),
                ContentDatabase.class, DatabaseStructure.CONTENT_DB_NAME).build();
        model.retrieveMilestoneDetails(db);
    }

    /**
     * Retrieve the Intent to launch this activity
     * @param context Context of originating activity
     * @return An Intent object
     */
    public static Intent getIntent(Context context) {
        return new Intent(context, WeeklyMilestonesActivity.class);
    }


}
