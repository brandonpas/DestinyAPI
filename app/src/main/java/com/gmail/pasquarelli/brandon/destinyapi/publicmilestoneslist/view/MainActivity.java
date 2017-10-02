package com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.gmail.pasquarelli.brandon.destinyapi.database.databases.AppDatabase;
import com.gmail.pasquarelli.brandon.destinyapi.database.databases.ContentDatabase;
import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.model.GetPublicMilestonesResponse;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.viewmodel.MainActivityViewModel;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    // ViewModels
    private MainActivityViewModel model;

    // Views and adapters
    private TextView mTextMessage;
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

        if (!isDatabaseInitialized()) {
            relocateDatabase();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchThisWeeksMilestones();
    }

    /**
     * Initialize the ViewModels for this View.
     */
    void initViewModels() {
        model = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        bind();
    }

    /**
     * Set observers/define actions when the observable emits a change.
     */
    void bind() {
        model.getMilestonesResponse().observe(this, new Observer<GetPublicMilestonesResponse>() {
            @Override
            public void onChanged(@Nullable GetPublicMilestonesResponse response) {
                Log.v(TAG,"onChanged Called");
                milestonesAdapter.notifyDataSetChanged();
                if (response == null) {
                    showToast("Error contacting server", Toast.LENGTH_SHORT);
                } else if (!response.getErrorCode().equals("1")) {
                    showToast(response.getMessage(), Toast.LENGTH_SHORT);
                }
            }
        });

    }

    /**
     * Initialize the RecyclerView and adapter for the PublicMilestones list.
     */
    void setupRecyclerView() {
        milestoneRecylerView = findViewById(R.id.public_milestones_recycler_view);
        milestonesAdapter = new PublicMilestonesAdapter(this);
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
     * Check if the pre-packaged database has been copied to expected directory
     * so that Room can access it.
     * @return
     */
    boolean isDatabaseInitialized() {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        return preferences.getBoolean(getString(R.string.prepackaged_db_relocated), false);
    }

    /**
     * Call the ViewModel to asynchronously move the database to the Room directory.
     * This really should only be called the very first time the app is run. Show
     * a message if the database failed to move.
     */
    void relocateDatabase() {
        String sourceDbLocation = getString(R.string.prepackaged_db_location);
        model.moveDatabase(getApplicationContext(), sourceDbLocation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) { }

                    @Override
                    public void onComplete() {
                        Log.v(TAG, "relocateDatabase onComplete");

                        // Update storage so we know we've relocated the database and don't
                        // repeat on each startup.
                        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(getString(R.string.prepackaged_db_relocated), true);
                        editor.apply();

                        populateAppDatabase();
                        // Since the database didn't exist before, now perform any queries
                        // that may have been missed prior.
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.v(TAG, "relocateDatabase onError");
                        showToast(getString(R.string.relocate_db_fail_toast), Toast.LENGTH_SHORT);

                        // This error needs to be sent to a crash reporting app like
                        // Crashlytics or Firebase.
                    }
                });
    }

    void populateAppDatabase() {
        AppDatabase appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, DatabaseStructure.APP_DB_NAME).build();

        ContentDatabase contentDatabase = Room.databaseBuilder(getApplicationContext(),
                ContentDatabase.class, DatabaseStructure.CONTENT_DB_NAME).build();

        model.populateAppDatabase(appDatabase, contentDatabase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) { }

                    @Override
                    public void onComplete() {
                        Log.v(TAG,"PopulateAppDB onComplete");
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.v(TAG,"PopulateAppDB onError");
                        e.printStackTrace();
                    }
                });
    }

    /**
     * Invoke the API to retrieve and convert milestone data to usable list.
     * When complete, notify the adapter that the data-set changed.
     */
    void fetchThisWeeksMilestones() {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, DatabaseStructure.APP_DB_NAME).build();
        model.retrieveMilestoneDetails(db);
    }


}
