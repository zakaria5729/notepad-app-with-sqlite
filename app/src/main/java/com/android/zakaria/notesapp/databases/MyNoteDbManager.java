package com.android.zakaria.notesapp.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.android.zakaria.notesapp.models.MyNote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyNoteDbManager {
    private DatabaseHelper databaseHelper;
    private Context context;

    public MyNoteDbManager(Context context) {
        this.context = context;
        this.databaseHelper = DatabaseHelper.getDatabaseHelperInstance(context);
    }

    public long addNote(MyNote myNote) {
        long insert = -1;
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigDB.COLUMN_TITLE, myNote.getTitle());
        contentValues.put(ConfigDB.COLUMN_NOTE, myNote.getNote());
        contentValues.put(ConfigDB.COLUMN_DATE, myNote.getDate());
        contentValues.put(ConfigDB.COLUMN_IMAGE_PATH, myNote.getImagePath());

        try {
            insert = sqLiteDatabase.insert(ConfigDB.TABLE_NAME, null, contentValues);
        } catch (SQLiteException ex) {
            Toast.makeText(context, "Operation failed: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }
        return insert;
    }

    public MyNote getSingleNote (int id){
        MyNote myNote = null;
        Cursor cursor = null;
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        try {
            cursor = sqLiteDatabase.query(ConfigDB.TABLE_NAME, null, ConfigDB.COLUMN_ID+" = ? ", new String[]{String.valueOf(id)}, null, null, null);

            if (cursor != null) {
                if (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndex(ConfigDB.COLUMN_TITLE));
                    String notes = cursor.getString(cursor.getColumnIndex(ConfigDB.COLUMN_NOTE));
                    String date = cursor.getString(cursor.getColumnIndex(ConfigDB.COLUMN_DATE));
                    String imagePath = cursor.getString(cursor.getColumnIndex(ConfigDB.COLUMN_IMAGE_PATH));
                    myNote = new MyNote(id, title, notes, date, imagePath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            sqLiteDatabase.close();
        }

        return myNote;
    }

    public List<MyNote> getAllNotes() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = sqLiteDatabase.query(ConfigDB.TABLE_NAME, null, null, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    List<MyNote> myNotesList = new ArrayList<>();

                        do {
                            int id = cursor.getInt(cursor.getColumnIndex(ConfigDB.COLUMN_ID));
                            String title = cursor.getString(cursor.getColumnIndex(ConfigDB.COLUMN_TITLE));
                            String note = cursor.getString(cursor.getColumnIndex(ConfigDB.COLUMN_NOTE));
                            String date = cursor.getString(cursor.getColumnIndex(ConfigDB.COLUMN_DATE));
                            String imagePath = cursor.getString(cursor.getColumnIndex(ConfigDB.COLUMN_IMAGE_PATH));

                            MyNote myNote = new MyNote(id, title, note, date, imagePath);
                            myNotesList.add(myNote);
                        } while (cursor.moveToNext());

                    return myNotesList;
                    }
                }
            } catch (SQLiteException ex) {
            Toast.makeText(context, "Operation failed: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }

    public long updateNote(MyNote myNote) {
        long rowCount = 0;
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigDB.COLUMN_TITLE, myNote.getTitle());
        contentValues.put(ConfigDB.COLUMN_NOTE, myNote.getNote());
        contentValues.put(ConfigDB.COLUMN_DATE, myNote.getDate());
        contentValues.put(ConfigDB.COLUMN_IMAGE_PATH, myNote.getImagePath());

        try {
            rowCount = sqLiteDatabase.update(ConfigDB.TABLE_NAME, contentValues, ConfigDB.COLUMN_ID + " = ? ", new String[]{String.valueOf(myNote.getId())});
        } catch (SQLiteException ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return rowCount;
    }

    public long deleteNote(int id) {
        long deletedRowCount = -1;
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            deletedRowCount = sqLiteDatabase.delete(ConfigDB.TABLE_NAME, ConfigDB.COLUMN_ID + " = ? ", new String[]{String.valueOf(id)});
        } catch (SQLiteException ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return deletedRowCount;
    }

    public boolean deleteAllNotes() {
        boolean deleteStatus = false;
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            sqLiteDatabase.delete(ConfigDB.TABLE_NAME, null, null);
            long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, ConfigDB.TABLE_NAME);

            if (count == 0) {
                deleteStatus = true;
            }
        } catch (SQLiteException ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return deleteStatus;
    }

    public long addNoteToTrash(MyNote myNote) {
        long insert = -1;
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigDB.TRASH_COLUMN_TITLE, myNote.getTitle());
        contentValues.put(ConfigDB.TRASH_COLUMN_NOTE, myNote.getNote());
        contentValues.put(ConfigDB.TRASH_COLUMN_DATE, myNote.getDate());
        contentValues.put(ConfigDB.TRASH_COLUMN_IMAGE_PATH, myNote.getImagePath());

        try {
            insert = sqLiteDatabase.insert(ConfigDB.TRASH_TABLE_NAME, null, contentValues);
        } catch (SQLiteException ex) {
            Toast.makeText(context, "Operation failed: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }
        return insert;
    }

    public List<MyNote> getAllNotesForTrash() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = sqLiteDatabase.query(ConfigDB.TRASH_TABLE_NAME, null, null, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    List<MyNote> myNotesListForTrash = new ArrayList<>();

                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(ConfigDB.TRASH_COLUMN_ID));
                        String title = cursor.getString(cursor.getColumnIndex(ConfigDB.TRASH_COLUMN_TITLE));
                        String note = cursor.getString(cursor.getColumnIndex(ConfigDB.TRASH_COLUMN_NOTE));
                        String date = cursor.getString(cursor.getColumnIndex(ConfigDB.TRASH_COLUMN_DATE));
                        String imagePath = cursor.getString(cursor.getColumnIndex(ConfigDB.TRASH_COLUMN_IMAGE_PATH));

                        MyNote myNote = new MyNote(id, title, note, date, imagePath);
                        myNotesListForTrash.add(myNote);
                    } while (cursor.moveToNext());

                    return myNotesListForTrash;
                }
            }
        } catch (SQLiteException ex) {
            Toast.makeText(context, "Operation failed: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            sqLiteDatabase.close();
        }
        return Collections.emptyList();
    }

    public long deleteTrashNote(long itemId) {
        long deleteStatus = -1;
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            deleteStatus = sqLiteDatabase.delete(ConfigDB.TRASH_TABLE_NAME, ConfigDB.TRASH_COLUMN_ID + " = ? ", new String[]{String.valueOf(itemId)});
        } catch (SQLiteException ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            sqLiteDatabase.close();
        }

        return deleteStatus;
    }

    public boolean deleteAllNotesFromTrash() {
        boolean deleteStatus = false;
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            sqLiteDatabase.delete(ConfigDB.TRASH_TABLE_NAME, null, null);
            long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, ConfigDB.TRASH_TABLE_NAME);

            if (count == 0) {
                deleteStatus = true;
            }
        } catch (SQLiteException ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return deleteStatus;
    }
}
