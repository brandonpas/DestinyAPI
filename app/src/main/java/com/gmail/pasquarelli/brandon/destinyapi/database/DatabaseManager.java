package com.gmail.pasquarelli.brandon.destinyapi.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.util.Log;

import com.gmail.pasquarelli.brandon.destinyapi.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class DatabaseManager {
    private static String TAG = "DatabaseManager";

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
        }
    };

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

    public static Completable downloadDatabase(Context context, final String databaseUrl) throws Exception {
        final String filePath = "/data/data/" + context.getPackageName() + "/databases/";
        final String zippedDatabase = filePath + "temp_database.zip";
        final String unzippedDatabaseName = filePath + "temp_database.db";
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                boolean downloadComplete = false;
                InputStream download;
                FileOutputStream databaseFile;
                ZipInputStream zippedDatabaseFile;
                try {
                    File directory = new File(filePath);
                    if (!directory.exists())
                        directory.mkdirs();

                    download = new URL(databaseUrl).openStream();
                    databaseFile = new FileOutputStream(zippedDatabase);

                    byte[] buffer = new byte[1024];
                    int fileLength;
                    while ((fileLength = download.read(buffer)) > 0) {
                        databaseFile.write(buffer, 0, fileLength);
                        if(fileLength < buffer.length)
                            downloadComplete = true;
                    }

                    databaseFile.flush();
                    download.close();
                    databaseFile.close();

                    // At this point, the download is complete. Now delete the
                    // original database file, unzip the new one downloaded, and rename

                    zippedDatabaseFile = new ZipInputStream(new FileInputStream(zippedDatabase));
                    ZipEntry entry = zippedDatabaseFile.getNextEntry();
                    buffer = new byte[1024];
                    while (entry != null) {
                        File unzippedFile = new File(unzippedDatabaseName);

                        FileOutputStream unzippedFileStream = new FileOutputStream(unzippedFile);

                        while ((fileLength = zippedDatabaseFile.read(buffer)) > 0)
                            unzippedFileStream.write(buffer, 0 , fileLength);

                        unzippedFileStream.flush();
                        unzippedFileStream.close();
                        entry = zippedDatabaseFile.getNextEntry();
                    }
                    zippedDatabaseFile.closeEntry();
                    zippedDatabaseFile.close();

                    // Unzipped, all that's left to do is delete original and rename new db file
                    File originalFile = new File(filePath + DatabaseStructure.CONTENT_DB_NAME);
                    File newFile = new File(unzippedDatabaseName);

                    if (newFile.exists()) {
                        if (originalFile.exists())
                            originalFile.delete();

                        File renamedFile = new File(filePath + DatabaseStructure.CONTENT_DB_NAME);
                        newFile.renameTo(renamedFile);
                    }
                } catch (IOException e) {
                    // If we didn't finish downloading the temp file, delete it.
                    if (!downloadComplete) {
                        File checkTemp = new File(zippedDatabase);
                        if (checkTemp.exists())
                            checkTemp.delete();
                    }
                    e.printStackTrace();
                    throw new Exception(e.getMessage());
                }
            }
        });
    }

}
