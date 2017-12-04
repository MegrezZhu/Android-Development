package com.zyuco.lab10.lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DBAdapter extends SQLiteOpenHelper {
    private static DBAdapter instance;

    private static final String TAG = "Lab10.DB";

    private static final int DB_VERSION = 1;
    public static final String DB_NAME = "Lab10.db";

    // table name
    static final private String TABLE_PERSON = "person";

    // sql to create table
    static final private String CREATE_TABLE_PERSON = "" +
        "CREATE TABLE IF NOT EXISTS " + TABLE_PERSON +
        "  (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
        "   name TEXT NOT NULL UNIQUE," +
        "   birthday TEXT," +
        "   gift TEXT);";

    private Context context;

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "db created");
        db.execSQL(CREATE_TABLE_PERSON);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSON);
        onCreate(db);
    }

    private DBAdapter(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public static synchronized DBAdapter getInstance(Context context) {
        if (instance == null) {
            context.deleteDatabase(DB_NAME); // FIXME: this is just for debugging
            instance = new DBAdapter(context.getApplicationContext());
        }
        return instance;
    }

    public class CRUD {
        void insert(Person person) {
            // TODO
        }
    }
}
