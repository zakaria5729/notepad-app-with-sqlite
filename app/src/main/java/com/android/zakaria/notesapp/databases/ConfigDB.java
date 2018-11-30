package com.android.zakaria.notesapp.databases;

class ConfigDB {

    static final String DATABASE_NAME = "notes_db";
    static final int DATABASE_VERSION = 1;
    static final String TABLE_NAME = "notes_table";

    static final String COLUMN_ID = "id";
    static final String COLUMN_TITLE = "title";
    static final String COLUMN_NOTE = "note";
    static final String COLUMN_DATE = "date";
    static final String COLUMN_IMAGE_PATH = "image_path";


    static final String TRASH_TABLE_NAME = "notes_table_trash";

    static final String TRASH_COLUMN_ID = "trash_id";
    static final String TRASH_COLUMN_TITLE = "trash_title";
    static final String TRASH_COLUMN_NOTE = "trash_note";
    static final String TRASH_COLUMN_DATE = "trash_date";
    static final String TRASH_COLUMN_IMAGE_PATH = "trash_image_path";
}
