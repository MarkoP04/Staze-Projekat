package com.example.cycling;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "bike_trails.db";
    private static final int DB_VERSION = 1;
    private static DBHelper instance;

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "profile_image_path TEXT," +
                "created_at TEXT" +
                ")";

        String CREATE_TRAILS = "CREATE TABLE trails (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "image_path TEXT NOT NULL," +
                "user_id INTEGER NOT NULL," +
                "created_at TEXT," +
                "FOREIGN KEY(user_id) REFERENCES users(id)" +
                ")";

        String CREATE_LOCATIONS = "CREATE TABLE trail_locations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "trail_id INTEGER NOT NULL," +
                "location_name TEXT NOT NULL," +
                "FOREIGN KEY(trail_id) REFERENCES trails(id)" +
                ")";

        db.execSQL(CREATE_USERS);
        db.execSQL(CREATE_TRAILS);
        db.execSQL(CREATE_LOCATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS trail_locations");
        db.execSQL("DROP TABLE IF EXISTS trails");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public boolean registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        values.put("created_at", System.currentTimeMillis());

        long result = db.insert("users", null, values);
        return result != -1;
    }

    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT username, email FROM users WHERE id = ?",
                new String[]{String.valueOf(userId)}
        );
    }


    public int loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM users WHERE email=? AND password=?",
                new String[]{email, password}
        );

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        }

        cursor.close();
        return -1;
    }

    public boolean updateUser(int userId, String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);

        int rowsUpdated = db.update(
                "users",
                values,
                "id = ?",
                new String[]{String.valueOf(userId)}
        );

        return rowsUpdated > 0;
    }


    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("trails", "user_id = ?", new String[]{String.valueOf(userId)});
        int rowsDeleted = db.delete("users", "id = ?", new String[]{String.valueOf(userId)});

        return rowsDeleted > 0;
    }


    public boolean insertTrail(String title, String imagePath, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("image_path", imagePath);
        values.put("user_id", userId);
        values.put("created_at", System.currentTimeMillis());

        long result = db.insert("trails", null, values);
        return result != -1;
    }

    public boolean deleteTrail(int trailId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("trail_locations", "trail_id = ?", new String[]{String.valueOf(trailId)});
        int rowsDeleted = db.delete("trails", "id = ?", new String[]{String.valueOf(trailId)});

        return rowsDeleted > 0;
    }

    public void insertTrailLocation(int trailId, String locationName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("trail_id", trailId);
        values.put("location_name", locationName);

        db.insert("trail_locations", null, values);
    }

    public boolean deleteTrailLocation(int locationId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsDeleted = db.delete("trail_locations", "id = ?", new String[]{String.valueOf(locationId)});

        return rowsDeleted > 0;
    }

    public Cursor searchTrailsByLocation(String location) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT DISTINCT t.* FROM trails t " +
                        "JOIN trail_locations l ON t.id = l.trail_id " +
                        "WHERE l.location_name LIKE ?",
                new String[]{"%" + location + "%"}
        );
    }
    public List<String> getTrailLocations(int trailId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT location_name FROM trail_locations WHERE trail_id = ?",
                new String[]{String.valueOf(trailId)}
        );

        List<String> locations = new ArrayList<>();
        if(c.moveToFirst()) {
            do {
                locations.add(c.getString(c.getColumnIndexOrThrow("location_name")));
            } while(c.moveToNext());
        }
        c.close();
        return locations;
    }


    public Cursor getAllTrails() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM trails", null);
    }

}
