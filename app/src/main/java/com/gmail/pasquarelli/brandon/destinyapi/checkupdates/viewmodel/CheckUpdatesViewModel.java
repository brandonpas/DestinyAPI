package com.gmail.pasquarelli.brandon.destinyapi.checkupdates.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.api.ApiUtility;
import com.gmail.pasquarelli.brandon.destinyapi.api.response_models.ManifestResponse;
import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseManager;
import com.gmail.pasquarelli.brandon.destinyapi.model.ManifestDefinition;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class CheckUpdatesViewModel extends ViewModel {

    private String TAG = "CheckUpdateVM";
    private MutableLiveData<String> version;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<Integer> progressPercent;
    private boolean relocated;
    private String defaultLocale;

    public LiveData<String> getVersionNumber() {
        if (version == null) {
            version = new MutableLiveData<>();
        }
        return version;
    }

    public LiveData<String> getErrorMessage() {
        if (errorMessage == null) {
            errorMessage = new MutableLiveData<>();
        }
        return errorMessage;
    }

    public LiveData<Integer> getProgressPercent() {
        if (progressPercent == null) {
            progressPercent = new MutableLiveData<>();
        }
        return progressPercent;
    }

    public void setRelocatedValue(boolean value) {
        relocated = value;
    }

    public Boolean getRelocatedValue() {
        return relocated;
    }

    public void setDefaultLocale(String value) {
        defaultLocale = value;
    }

    public void checkDatabaseVersion(final Context context, final String currentVersion) {

        ApiUtility.getService()
                .getManifest()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ManifestResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        progressPercent.setValue(10);
                    }

                    @Override
                    public void onSuccess(@NonNull ManifestResponse manifestResponse) {
                        Log.v(TAG, "checkDatabaseVersion onSuccess");
                        ManifestDefinition manifest = manifestResponse.getManifest();
                        progressPercent.setValue(20);
                        // The database hasn't been relocated and either we didn't receive a response,
                        // we encountered a network/server error, or we found that the database is the
                        // current version so just move the packaged database instead of re-downloading, etc.
                        if (!relocated) {
                            if (manifest == null ||                     // possibly a server error
                                    manifest.getVersion() == null ||    // definitely a server error
                                    manifest.getVersion().equals(currentVersion)) {     //  packaged db is current
                                relocateDatabase(context, currentVersion);
                            } else {
                                // We have the manifest version and its been updated,
                                // but we have not yet relocated the packaged database. In this case,
                                // just download and extract which will extract to the proper directory
                                progressPercent.setValue(60);
                                updateDatabase(context, manifest, currentVersion);
                            }
                            return;
                        }

                        // We've previously initialized the database, so update here if necessary.
                        if (manifest != null) {
                            if (!manifest.getVersion().equals(currentVersion)) {
                                progressPercent.setValue(60);
                                updateDatabase(context, manifest, currentVersion);
                            } else {
                                // Content database is current, proceed.
                                version.setValue(currentVersion);
                                progressPercent.setValue(100);
                            }
                        } else {
                            // Either we didn't get a response for some reason,
                            // or we did and the version is the same
                            progressPercent.setValue(100);
                            version.setValue(currentVersion);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.v(TAG, "checkDatabaseVersion onError");
                        version.setValue(currentVersion);
                        progressPercent.setValue(100);
                    }
                });


    }

    private void updateDatabase(final Context context, ManifestDefinition manifest, String currentVersion) {
        Log.v(TAG,"Locale retrieved: " + defaultLocale);
        Log.v(TAG,"Updating database...");
        String databaseUrl = manifest.getWorldContentPaths().get(defaultLocale);
        String contentVersion = manifest.getVersion();

        // If locale not part of the manifest, default to English
        if (databaseUrl == null)
            databaseUrl = manifest.getWorldContentPaths().get("en");
        Log.v(TAG, "current: " + currentVersion + " manfiest version: " + manifest.getVersion());
        updateDatabase(context, databaseUrl, currentVersion, contentVersion);
    }

    private void updateDatabase(final Context context, String databaseEndpoint, final String currentVersion,
                        final String newVersion) {
        try {
            DatabaseManager.downloadDatabase(context,
                    ApiUtility.getBungieBaseUrl() + databaseEndpoint)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) { }

                        @Override
                        public void onComplete() {
                            Log.v(TAG,"updateDatabase onSuccess");


                            // Update storage so we know we've relocated the database and don't
                            // repeat on each startup.
                            relocated = true;
                            version.setValue(newVersion);
                            progressPercent.setValue(100);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.v(TAG, "updateDatabase onError");
                            version.setValue(currentVersion);
                            progressPercent.setValue(100);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, "updateDatabase catch error");
            version.setValue(currentVersion);
        }
    }
    /**
    * Asynchronously move the database to the Room directory. Only called the very
    * first time the app is run. Show a message if the database failed to move.
    */
    private void relocateDatabase(final Context context, final String currentVersion) {
        Log.v(TAG,"Relocating database...");
        moveDatabase(context)
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
                        relocated = true;

                        //Notify observer that version has been set so app progresses to next screen.
                        version.setValue(currentVersion);
                        progressPercent.setValue(100);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.v(TAG, "relocateDatabase onError");
                        e.printStackTrace();

                        errorMessage.setValue(context.getString(R.string.relocate_db_failed));
                        progressPercent.setValue(100);
                        // This error needs to be sent to a crash reporting monitor tool like
                        // Crashlytics or Firebase.

                    }
                });
    }

    /**
     * Use this function to move the database from some location to the directory that
     * the Room library expects.
     * @param context Application Context
     */
    private Completable moveDatabase(final Context context){
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                boolean success = DatabaseManager.moveDatabaseFromAssets(context);
                if (!success) {
                    // Trigger an error so the Completable calls onError
                    // instead of assuming that it was successful and preventing this process from
                    // running again.
                    throw new Exception("Database copy failed");
                }
            }
        });
    }
}
