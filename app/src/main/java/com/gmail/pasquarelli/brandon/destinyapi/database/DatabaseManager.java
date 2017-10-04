package com.gmail.pasquarelli.brandon.destinyapi.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.database.databases.AppDatabase;
import com.gmail.pasquarelli.brandon.destinyapi.database.databases.ContentDatabase;
import com.gmail.pasquarelli.brandon.destinyapi.database.milestones.dao.AppMilestoneDao;
import com.gmail.pasquarelli.brandon.destinyapi.database.milestones.entity.AppMilestoneEntity;
import com.gmail.pasquarelli.brandon.destinyapi.database.milestones.entity.ContentMilestoneEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static String TAG = "DatabaseManager";

    /**
     *
     * @param context Reference to the activity's context
     * @return
     */
    public static boolean moveDatabaseFromAssets(Context context) {
        boolean operationCompleted = false;
        try {

            final String filePath = "/data/data/" + context.getPackageName() + "/databases/";
            final String prepackagedDB = context.getString(R.string.prepackaged_db_location);
            InputStream existingDatabaseStream = context.getAssets().open(prepackagedDB);


            String newDatabaseName = filePath + DatabaseStructure.CONTENT_DB_NAME;
            File outputPath = new File(filePath);
            if (!outputPath.exists()) {
                outputPath.mkdir();
            }

            OutputStream newDatabaseStream = new FileOutputStream(newDatabaseName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = existingDatabaseStream.read(buffer)) > 0) {
                newDatabaseStream.write(buffer, 0, length);
            }

            newDatabaseStream.flush();
            existingDatabaseStream.close();
            newDatabaseStream.close();

            Log.v(TAG,newDatabaseName + " copied");
            operationCompleted = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return operationCompleted;
    }


    public static final Migration persistDatabaseProvided = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Don't do anything. This is to prevent Room from dropping our data
            // provided in the "content" database.
        }
    };

    /**
     * Query the ContentDatabase provided by Bungie and restructure to our format
     * in our own database.
     * @param appDatabase Our database
     * @param contentDatabase Database provided by Bungie
     */
    public static void ConvertMilestoneData(@NonNull final AppDatabase appDatabase,
                                            @NonNull final ContentDatabase contentDatabase) {

        AppMilestoneDao appDao = appDatabase.appMilestoneDao();
        Gson gson = new GsonBuilder().create();

        List<ContentMilestoneEntity> contentList =
                contentDatabase.contentMilestoneDao().getMilestoneFromList();

        List<AppMilestoneEntity> appMilestoneList = new ArrayList<>();

        for (ContentMilestoneEntity jsonMilestone : contentList) {
            Log.v(TAG, String.valueOf(jsonMilestone.getId()));
            AppMilestoneEntity appMilestone =
                    gson.fromJson(jsonMilestone.getJsonStream(), AppMilestoneEntity.class);
            appMilestoneList.add(appMilestone);
        }
        appDao.insertMilestoneList(appMilestoneList);
    }

}
