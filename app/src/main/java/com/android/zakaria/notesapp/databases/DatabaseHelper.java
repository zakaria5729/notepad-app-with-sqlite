package com.android.zakaria.notesapp.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper databaseHelper;

    private static final String CREATE_MY_NOTES_TABLE = "CREATE TABLE " + ConfigDB.TABLE_NAME + "(" + ConfigDB.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ConfigDB.COLUMN_TITLE + " TEXT, " + ConfigDB.COLUMN_NOTE + " TEXT, " + ConfigDB.COLUMN_DATE + " TEXT, "+ConfigDB.COLUMN_IMAGE_PATH+" TEXT);";

    private static final String CREATE_MY_NOTES_TRASH_TABLE = "CREATE TABLE "+ ConfigDB.TRASH_TABLE_NAME+"("+ ConfigDB.TRASH_COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ ConfigDB.TRASH_COLUMN_TITLE+" TEXT, "+ ConfigDB.TRASH_COLUMN_NOTE+" TEXT, "+ConfigDB.TRASH_COLUMN_DATE+" TEXT, "+ConfigDB.TRASH_COLUMN_IMAGE_PATH+" TEXT);";

    private DatabaseHelper(Context context) {
        super(context, ConfigDB.DATABASE_NAME, null, ConfigDB.DATABASE_VERSION);
    }

    static synchronized DatabaseHelper getDatabaseHelperInstance(Context context) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MY_NOTES_TABLE);
        db.execSQL(CREATE_MY_NOTES_TRASH_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ConfigDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ConfigDB.TRASH_TABLE_NAME);
        onCreate(db);
    }
}
