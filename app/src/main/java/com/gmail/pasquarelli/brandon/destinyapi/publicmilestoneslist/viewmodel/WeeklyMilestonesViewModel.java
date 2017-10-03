package com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.api.ApiUtility;
import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseManager;
import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;
import com.gmail.pasquarelli.brandon.destinyapi.database.databases.AppDatabase;
import com.gmail.pasquarelli.brandon.destinyapi.database.databases.ContentDatabase;
import com.gmail.pasquarelli.brandon.destinyapi.database.milestones.entity.AppMilestoneEntity;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.model.GetPublicMilestonesResponse;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class WeeklyMilestonesViewModel extends ViewModel {

    private String TAG = "MainActivityVM";
    private MutableLiveData<GetPublicMilestonesResponse> milestonesResponse;
    private MutableLiveData<String> toastMessage;

    private ArrayList<AppMilestoneEntity> milestoneArray = new ArrayList<>();


    /**
     * Used to retrieve an observable instance of the {@link GetPublicMilestonesResponse} object.
     * @return An "observable" instance of {@link GetPublicMilestonesResponse}
     * using the Android Architecture Component {@link LiveData}
     */
    public LiveData<GetPublicMilestonesResponse> getMilestonesResponse() {
        if (milestonesResponse == null) {
            milestonesResponse = new MutableLiveData<>();
        }
        return milestonesResponse;
    }

    /**
     * Used as a way for the ViewModel to emit messages to the Activity (or other observers),
     * without knowledge of who is consuming them.
     * @return An observable string
     */
    public LiveData<String> getToastMessage() {
        if (toastMessage == null) {
            toastMessage = new MutableLiveData<>();
        }
        return toastMessage;
    }

    /**
     * Used by the PublicMilestonesAdapter to retrieve the milestone array. This is a separate
     * call for the adapter so that we don't call the API for each item in the list when building
     * the row's layout.
     * @return ArrayList of PublicMilestoneObject
     */
    public ArrayList<AppMilestoneEntity> getMilestonesArray() {
        return milestoneArray;
    }

    /**
     * Asynchronously move the database to the Room directory.
     * This really should only be called the very first time the app is run. Show
     * a message if the database failed to move.
     */
    public void relocateDatabase(final Context context, final SharedPreferences preferences) {
        String sourceDbLocation = context.getString(R.string.prepackaged_db_location);
        moveDatabase(context, sourceDbLocation)
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
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(context.getString(R.string.prepackaged_db_relocated), true);
                        editor.apply();

                        populateAppDatabase(context);
                        // Since the database didn't exist before, now perform any queries
                        // that may have been missed prior.
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.v(TAG, "relocateDatabase onError");
                        toastMessage.setValue(context.getString(R.string.relocate_db_fail_toast));

                        // This error needs to be sent to a crash reporting monitor like
                        // Crashlytics or Firebase.
                    }
                });
    }

    /**
     * Use this function to move the database from some location to the directory that
     * the Room library expects.
     * @param context Application Context
     */
    private Completable moveDatabase(final Context context, final String sourceDatabaseLocation){
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                DatabaseManager.moveDatabaseFromAssets(context, sourceDatabaseLocation);
            }
        });
    }

    /**
     * This is called from the WeeklyMilestonesActivity and will occur on a background thread; for this reason,
     * its safe to use the synchronous calls from the data access objects.
     */
    private void populateAppDatabase(final Context context) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {

                AppDatabase appDatabase = Room.databaseBuilder(context,
                        AppDatabase.class, DatabaseStructure.APP_DB_NAME).build();

                ContentDatabase contentDatabase = Room.databaseBuilder(context,
                        ContentDatabase.class, DatabaseStructure.CONTENT_DB_NAME).build();

                // Convert Milestone data from the manifest to our structure.
                DatabaseManager.ConvertMilestoneData(appDatabase, contentDatabase);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    /**
     * Call the API to retreive the current milestones for the week.
     * <p>
     * This function will occur on a background thread.
     */
    public void retrieveMilestoneDetails(final AppDatabase appDatabase) {

        // Call API to retrieve updated list
        ApiUtility.getService().getPublicMilestones()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetPublicMilestonesResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) { }

                    @Override
                    public void onNext(@NonNull GetPublicMilestonesResponse getPublicMilestonesResponse) {
                        Log.v(TAG,"retrieveMilestoneDetails onNext");
                        updateList(appDatabase, getPublicMilestonesResponse);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.v(TAG,"retrieveMilestoneDetails onError");
                        e.printStackTrace();
                        milestonesResponse.setValue(null);
                    }

                    @Override
                    public void onComplete() {
                        Log.v(TAG,"retrieveMilestoneDetails onComplete");
                    }
                });

    }

    /**
     * Called when we receive the response from the API. This will extract the milestone hashcodes
     * and query our app database for the details for each milestone.
     * <p/>
     * ** This feels clunky and might need to be revisited at some point. **
     * @param appDatabase Our app database.
     * @param apiResponse The response from the API endpoint.
     */
    private void updateList(final AppDatabase appDatabase, final GetPublicMilestonesResponse apiResponse){
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                milestoneArray.clear();
                // If we received results
                if (apiResponse != null && apiResponse.getErrorCode().equals("1")){
                    milestoneArray.addAll(appDatabase.appMilestoneDao()
                            .getMilestoneFromList(apiResponse.getMilestonesHash()));
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) { }

            @Override
            public void onComplete() {
                Log.v(TAG,"updateList onComplete");

                // This will trigger the Activity to call notifyDataSetChanged
                milestonesResponse.setValue(apiResponse);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.v(TAG,"updateList onError");
                e.printStackTrace();
            }
        });
    }
}
