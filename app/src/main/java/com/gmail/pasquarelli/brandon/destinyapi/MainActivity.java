package com.gmail.pasquarelli.brandon.destinyapi;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.pasquarelli.brandon.destinyapi.api.ApiUtility;
import com.gmail.pasquarelli.brandon.destinyapi.data.GetPublicMilestonesResponse;
import com.gmail.pasquarelli.brandon.destinyapi.data.PublicMilestoneObject;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
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
     * Call the API Destiny2.GetPublicMilestones. When the response is received, updates the
     * RecyclerView list with the results.
     */
    void fetchData() {
        ApiUtility.getService().getPublicMilestones()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetPublicMilestonesResponse>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) { }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull GetPublicMilestonesResponse response) {
                        Log.v(TAG,"onNext called.");
                        if (!response.getErrorCode().equals("1")) {
                            showToast(response.getMessage(),Toast.LENGTH_SHORT);
                        } else {
                            updateMilestonesAdapter(response.getMilestoneArray());
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.v(TAG,"onError called.");
                    }

                    @Override
                    public void onComplete() {
                        Log.v(TAG,"onComplete called.");
                    }
                });
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
