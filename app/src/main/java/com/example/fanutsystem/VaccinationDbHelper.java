package com.example.fanutsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VaccinationDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "fanut_vaccination.db";
    private static final int DB_VERSION = 2;

    static final String TABLE_STATUS = "vaccine_status";
    static final String COL_CHILD_ID = "child_id";
    static final String COL_CODE = "vaccine_code";
    static final String COL_COMPLETED = "completed";
    static final String COL_GIVEN_AT = "given_at";

    private static final String CREATE_STATUS = "CREATE TABLE " + TABLE_STATUS + " ("
            + COL_CHILD_ID + " TEXT NOT NULL,"
            + COL_CODE + " TEXT NOT NULL,"
            + COL_COMPLETED + " INTEGER NOT NULL DEFAULT 0,"
            + COL_GIVEN_AT + " INTEGER NOT NULL DEFAULT 0,"
            + "PRIMARY KEY(" + COL_CHILD_ID + "," + COL_CODE + "));";

    private static VaccinationDbHelper instance;

    public static synchronized VaccinationDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new VaccinationDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private VaccinationDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STATUS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_STATUS + " ADD COLUMN " + COL_GIVEN_AT
                    + " INTEGER NOT NULL DEFAULT 0");
        }
    }

    public boolean isCompleted(String childId, String vaccineCode) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLE_STATUS,
                new String[]{COL_COMPLETED},
                COL_CHILD_ID + "=? AND " + COL_CODE + "=?",
                new String[]{childId, vaccineCode},
                null, null, null)) {
            if (c.moveToFirst()) {
                return c.getInt(0) != 0;
            }
        }
        return false;
    }

    public long getGivenAtMillis(String childId, String vaccineCode) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLE_STATUS,
                new String[]{COL_GIVEN_AT},
                COL_CHILD_ID + "=? AND " + COL_CODE + "=?",
                new String[]{childId, vaccineCode},
                null, null, null)) {
            if (c.moveToFirst()) {
                return c.getLong(0);
            }
        }
        return 0L;
    }

    public void setCompleted(String childId, String vaccineCode, boolean completed) {
        SQLiteDatabase db = getWritableDatabase();
        long existingGiven = getGivenAtMillis(childId, vaccineCode);
        ContentValues cv = new ContentValues();
        cv.put(COL_CHILD_ID, childId);
        cv.put(COL_CODE, vaccineCode);
        cv.put(COL_COMPLETED, completed ? 1 : 0);
        if (completed) {
            cv.put(COL_GIVEN_AT, existingGiven > 0 ? existingGiven : System.currentTimeMillis());
        } else {
            cv.put(COL_GIVEN_AT, 0);
        }
        db.insertWithOnConflict(TABLE_STATUS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }
}
