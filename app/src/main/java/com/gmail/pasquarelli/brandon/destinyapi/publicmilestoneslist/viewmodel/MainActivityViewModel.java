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
import io.reactivex.CompletableObserver;
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
     * Used by the PublicMilestonesAdapter to retrieve the milestone array. This is a separate
     * call for the adapter so that we don't call the API for each item in the list when building
     * the row's layout.
     * @return ArrayList of PublicMilestoneObject
     */
    public ArrayList<AppMilestoneEntity> getMilestonesArray() {
        return milestoneArray;
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

    /**
     * Call the API to retreive the current milestones for the week. 
     * <p>
     * This function will occur on a background thread.
     */
    public void retrieveMilestoneDetails(final AppDatabase appDatabase) {
        milestoneArray.clear();

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
    void updateList(final AppDatabase appDatabase, final GetPublicMilestonesResponse apiResponse){
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
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
