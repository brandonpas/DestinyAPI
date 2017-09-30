package com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;

import com.gmail.pasquarelli.brandon.destinyapi.api.ApiUtility;
import com.gmail.pasquarelli.brandon.destinyapi.database.ContentDatabase;
import com.gmail.pasquarelli.brandon.destinyapi.database.milestones.entity.ContentMilestoneEntity;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.model.GetPublicMilestonesResponse;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.model.PublicMilestoneObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivityViewModel extends ViewModel {

    private String TAG = "MainActivityVM";
    private MutableLiveData<GetPublicMilestonesResponse> milestonesResponse;
    private ArrayList<PublicMilestoneObject> milestoneArray = new ArrayList<>();

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

    public void testDatabase(ContentDatabase db) {
        db.contentMilestoneDao()
                .getMilestoneFromList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<List<ContentMilestoneEntity>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<ContentMilestoneEntity> contentMilestoneEntities) {
                        
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                })




//                .subscribe(new Observer<List<ContentMilestoneEntity>>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@NonNull List<ContentMilestoneEntity> contentMilestoneEntities) {
//                        Log.v(TAG,"testDatabase onNext");
//                        testList.setValue(contentMilestoneEntities);
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        Log.v(TAG,"testDatabase onError");
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.v(TAG,"testDatabase onComplete");
//                    }
//                });
    }
}
