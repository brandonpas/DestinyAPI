package com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.model.GetPublicMilestonesResponse;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.model.PublicMilestoneObject;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.viewmodel.MainActivityViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private String TAG = "MainActivity";
    private ArrayList<PublicMilestoneObject> milestoneArray = new ArrayList<>();
    private RecyclerView milestoneRecylerView;
    private PublicMilestonesAdapter milestonesAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setupRecyclerView();
        initViewModels();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Initialize the ViewModels for this View.
     */
    void initViewModels() {
        MainActivityViewModel model = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        model.getMilestonesResponse().observe(this, new Observer<GetPublicMilestonesResponse>() {
            @Override
            public void onChanged(@Nullable GetPublicMilestonesResponse response) {
                if (response == null) {
                    showToast("Error contacting server", Toast.LENGTH_SHORT);
                    return;
                }
                if (!response.getErrorCode().equals("1")) {
                    showToast(response.getMessage(), Toast.LENGTH_SHORT);
                } else {
                    updateMilestonesAdapter(response.getMilestoneArray());
                }
            }
        });
    }

    /**
     * Initialize the RecyclerView and adapter for the PublicMilestones list.
     */
    void setupRecyclerView() {
        milestoneRecylerView = findViewById(R.id.public_milestones_recycler_view);
        milestonesAdapter = new PublicMilestonesAdapter(milestoneArray);
        milestoneRecylerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        milestoneRecylerView.setAdapter(milestonesAdapter);
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
     * Update the milestones adapter with the provided list.
     * @param list The updated list from the API
     */
    void updateMilestonesAdapter(ArrayList<PublicMilestoneObject> list) {
        milestonesAdapter.updateList(list);
    }
}
