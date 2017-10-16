package com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.gmail.pasquarelli.brandon.destinyapi.api.ApiUtility;
import com.gmail.pasquarelli.brandon.destinyapi.database.databases.ContentDatabase;
import com.gmail.pasquarelli.brandon.destinyapi.database.entity.ContentMilestoneEntity;
import com.gmail.pasquarelli.brandon.destinyapi.model.MilestoneDefinition;
import com.gmail.pasquarelli.brandon.destinyapi.api.response_models.PublicMilestonesResponse;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class WeeklyMilestonesViewModel extends ViewModel {

    private String TAG = "MilestonesVM";
    private MutableLiveData<PublicMilestonesResponse> milestonesResponse;
    private MutableLiveData<String> toastMessage;

    private ArrayList<MilestoneDefinition> milestoneArray = new ArrayList<>();


    /**
     * Used to retrieve an observable instance of the {@link PublicMilestonesResponse} object.
     * @return An "observable" instance of {@link PublicMilestonesResponse}
     * using the Android Architecture Component {@link LiveData}
     */
    public LiveData<PublicMilestonesResponse> getMilestonesResponse() {
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
    public ArrayList<MilestoneDefinition> getMilestonesArray() {
        return milestoneArray;
    }

    /**
     * Call the API to retrieve the current milestones for the week.
     * <p>
     * This function will occur on a background thread.
     */
    public void retrieveMilestoneDetails(final ContentDatabase appDatabase) {

        // Call API to retrieve updated list
        ApiUtility.getService().getPublicMilestones()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PublicMilestonesResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) { }

                    @Override
                    public void onNext(@NonNull PublicMilestonesResponse publicMilestonesResponse) {
                        Log.v(TAG,"retrieveMilestoneDetails onNext");
                        updateList(appDatabase, publicMilestonesResponse);
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
    private void updateList(final ContentDatabase appDatabase, final PublicMilestonesResponse apiResponse){
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                milestoneArray.clear();
                // If we received results
                if (apiResponse != null && apiResponse.getErrorCode().equals("1")){

                    List<ContentMilestoneEntity> dbMilestones = appDatabase.contentMilestoneDao()
                            .getMilestoneFromList(apiResponse.getMilestonesHash());

                    Gson gson = new Gson();
                    for (ContentMilestoneEntity entity : dbMilestones) {
                        Reader reader = new InputStreamReader(new ByteArrayInputStream(entity.json));
                        JsonReader jsonReader = new JsonReader(reader);

                        MilestoneDefinition milestone = gson.fromJson(jsonReader, MilestoneDefinition.class);
                        milestoneArray.add(milestone);
                    }
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
