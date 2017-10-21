package com.gmail.pasquarelli.brandon.destinyapi.checkupdates.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.authentication.view.AuthenticateActivity;
import com.gmail.pasquarelli.brandon.destinyapi.checkupdates.viewmodel.CheckUpdatesViewModel;

import java.util.Locale;

/**
 * Entry point of app. This activity will only serve to validate database version,
 * and any other updates that must occur. App will then automatically redirect to the
 * home screen.
 */
public class CheckUpdatesActivity extends AppCompatActivity {

    String TAG = "CheckUpdateAct";
    CheckUpdatesViewModel checkUpdatesViewModel;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_updates);

        initViews();
        initViewModels();
        checkDatabaseVersion();
    }

    public void initViews() {
        progress = findViewById(R.id.progressBar);
    }

    /**
     * Initialize ViewModels
     */
    void initViewModels() {
        checkUpdatesViewModel = ViewModelProviders.of(this).get(CheckUpdatesViewModel.class);
        bind();
    }

    /**
     * Set observers
     */
    void bind() {
        checkUpdatesViewModel.getVersionNumber().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String version) {
                SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                String currentVersion = prefs.getString(getString(R.string.pref_current_db_version),
                        getString(R.string.db_version));
                SharedPreferences.Editor editor = prefs.edit();

                // If the version changed, store the updated version
                if (!currentVersion.equals(version)) {
                    editor.putString(getString(R.string.pref_current_db_version),version);
                }

                // Save whether the database was successfully relocated/downloaded.
                editor.putBoolean(getString(R.string.pref_packaged_db_relocated),
                        checkUpdatesViewModel.getRelocatedValue());
                editor.apply();
                // After database updated and new version stored, launch into the app
                launchApp();
            }
        });

        checkUpdatesViewModel.getProgressPercent().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null)
                    progress.setProgress(integer);
            }
        });
    }

    /**
     * Check the current database version and update if necessary
     */
    void checkDatabaseVersion() {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        String currentVersion = prefs.getString(getString(R.string.pref_current_db_version), getString(R.string.db_version));
        boolean relocated = prefs.getBoolean(getString(R.string.pref_packaged_db_relocated), false);

        String languageCode = Locale.getDefault().getLanguage();
        Log.v(TAG, "Language code: " + languageCode);
        Log.v(TAG, "Activity checkDBVersion, relocated: " + relocated);
        checkUpdatesViewModel.setRelocatedValue(relocated);
        checkUpdatesViewModel.setDefaultLocale(languageCode);
        checkUpdatesViewModel.checkDatabaseVersion(this, currentVersion);
    }

    /**
     * Get the intent for the next screen after verifying the database and finish this activity
     */
    void launchApp() {
        Intent intent = AuthenticateActivity.getIntent(this);
        startActivity(intent);
        finish();
    }

}
