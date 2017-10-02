package com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import com.gmail.pasquarelli.brandon.destinyapi.api.ApiUtility;
import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseManager;
import com.gmail.pasquarelli.brandon.destinyapi.database.databases.AppDatabase;
import com.gmail.pasquarelli.brandon.destinyapi.database.databases.ContentDatabase;
import com.gmail.pasquarelli.brandon.destinyapi.database.milestones.entity.AppMilestoneEntity;
import com.gmail.pasquarelli.brandon.destinyapi.database.milestones.entity.ContentMilestoneEntity;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.model.GetPublicMilestonesResponse;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.model.PublicMilestoneObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivityViewModel extends ViewModel {

    private String TAG = "MainActivityVM";
    private MutableLiveData<GetPublicMilestonesResponse> milestonesResponse;
    private ArrayList<PublicMilestoneObject> milestoneArray = new ArrayList<>();
//    private ArrayList<AppMilestoneEntity> milestoneArray = new ArrayList<>();


    private MutableLiveData<List<ContentMilestoneEntity>> testList;

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

    public LiveData<List<ContentMilestoneEntity>> getContentMilestones() {
        if (testList == null) {
            testList = new MutableLiveData<>();
        }
        return testList;
    }

    /**
     * Used by the PublicMilestonesAdapter to retrieve the milestone array. This is a separate
     * call for the adapter so that we don't call the API for each item in the list when building
     * the row's layout.
     * @return ArrayList of PublicMilestoneObject
     */
    public ArrayList<PublicMilestoneObject> getMilestonesArray() {
        return milestoneArray;
    }

    /**
     * Call the API Destiny2.GetPublicMilestones asynchronously. When the response is received, updates the
     * observable which emits the change to any observers.
     */
    public void fetchMilestonesResponse() {

        // Start by clearing stale results
        milestoneArray.clear();

        // Call API to retrieve updated list
        ApiUtility.getService().getPublicMilestones()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetPublicMilestonesResponse>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) { }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull GetPublicMilestonesResponse response) {
                        Log.v(TAG,"onNext called.");
                        milestonesResponse.setValue(response);
                        milestoneArray.addAll(response.getMilestoneArray());
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.v(TAG,"onError called.");
                        milestonesResponse.setValue(null);
                    }

                    @Override
                    public void onComplete() {
                        Log.v(TAG,"onComplete called.");
                    }
                });
    }

    /**
     * Use this function to move the database from some location to the directory that
     * the Room library expects.
     * @param context
     */
    public Completable moveDatabase(final Context context, final String sourceDatabaseLocation){
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                DatabaseManager.moveDatabaseFromAssets(context, sourceDatabaseLocation);
            }
        });
    }

    public void testDatabase(ContentDatabase db) {
        db.contentMilestoneDao()
                .getMilestoneFromListAsync()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<List<ContentMilestoneEntity>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) { }

                    @Override
                    public void onSuccess(@NonNull List<ContentMilestoneEntity> contentMilestoneEntities) {
                        Log.v(TAG,"testDatabase onSuccess. list size: " + contentMilestoneEntities.size());
                        testList.setValue(contentMilestoneEntities);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.v(TAG,"testDatabase onError");
                        e.printStackTrace();
                    }
                });
    }

    public Single<List<AppMilestoneEntity>> testAppDatabase(AppDatabase db) {
        return db.appMilestoneDao()
                .getAllMilestones();
    }


    /**
     * This is called from the MainActivity and will occur on a background thread; for this reason,
     * its safe to use the synchronous calls from the data access objects.
     * @param appDatabase Our representation of the Destiny JSON data.
     * @param contentDatabase The database returned from the 'GetDestinyManifest' call.
     * @return
     */
    public Completable populateAppDatabase(final AppDatabase appDatabase, final ContentDatabase contentDatabase) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {

                // Convert Milestone data from the manifest to our structure.
                DatabaseManager.ConvertMilestoneData(appDatabase, contentDatabase);
            }
        });
    }

//    public void retrieveMilestoneDetails()
}
