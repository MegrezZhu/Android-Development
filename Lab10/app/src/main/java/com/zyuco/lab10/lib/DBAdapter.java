package com.zyuco.lab10.lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBAdapter extends SQLiteOpenHelper {
    private static DBAdapter instance;
    private CRUD crud;

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
//            context.deleteDatabase(DB_NAME); // FIXME: this is just for debugging
            instance = new DBAdapter(context.getApplicationContext());
        }
        return instance;
    }

    // can only be invoked AFTER instanced
    public static synchronized DBAdapter getInstance() {
        return getInstance(null);
    }

    public CRUD getCRUD() {
        if (crud == null) crud = new CRUD();
        return crud;
    }

    public class CRUD {
        private SQLiteDatabase db = getWritableDatabase();

        private CRUD() {
        }

        public void insert(Person person) {
            ContentValues values = new ContentValues();
            values.put("name", person.name);
            values.put("birthday", person.birthday);
            values.put("gift", person.gift);

            db.insert(TABLE_PERSON, null, values);
        }

        public boolean duplicated(String name) {
            String[] columns = {"name", "birthday", "gift"};
            String[] args = {name};

            try (Cursor cursor = db.query(TABLE_PERSON, columns, "name = ?", args, null, null, null)) {
                return cursor.moveToFirst();
            }
        }

        public void update(Person person) {
            String whereClause = "name = ?";
            String[] whereArgs = {person.name};

            ContentValues values = new ContentValues();
            values.put("birthday", person.birthday);
            values.put("gift", person.gift);

            db.update(TABLE_PERSON, values, whereClause, whereArgs);
        }

        public void remove(String name) {
            String whereClause = "name = ?";
            String[] whereArgs = {name};

            db.delete(TABLE_PERSON, whereClause, whereArgs);
        }

        public List<Map<String, String>> getAll() {
            String[] columns = {"name", "birthday", "gift"};
            List<Map<String, String>> result = new ArrayList<>();
            try (Cursor cursor = db.query(TABLE_PERSON, columns, null, null, null, null, null)) {
                while (cursor.moveToNext()) {
                    result.add(Person.fromCursor(cursor).toMap());
                }
                return result;
            }
        }
    }
}
