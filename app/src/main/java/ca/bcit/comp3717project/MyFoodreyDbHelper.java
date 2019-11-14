package ca.bcit.comp3717project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MyFoodreyDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "MyFoodrey.db";
    private static final int DB_VERSION = 4;
    private Context context;

    public MyFoodreyDbHelper(Context context) {
        // The 3'rd parameter (null) is an advanced feature relating to cursors
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        updateMyDatabase(sqLiteDatabase, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        updateMyDatabase(sqLiteDatabase, i, i1);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            if (oldVersion < 1) {
                db.execSQL(getCreateFavoritesTableSql());
            }
            if (oldVersion < 2) {
                db.execSQL("ALTER TABLE Favorite ADD COLUMN CREATED_AT NUMERIC;");
            }
            if (oldVersion < 3) {
                db.execSQL("ALTER TABLE Favorite ADD COLUMN HazardRating TEXT;");
                db.execSQL("ALTER TABLE Favorite ADD COLUMN InspectionDate TEXT;");
                db.execSQL("ALTER TABLE Favorite ADD COLUMN NumCritical NUMERIC;");
                db.execSQL("ALTER TABLE Favorite ADD COLUMN NumNonCritical NUMERIC;");
                db.execSQL("ALTER TABLE Favorite ADD COLUMN LATITUDE TEXT;");
                db.execSQL("ALTER TABLE Favorite ADD COLUMN LONGITUDE TEXT;");
            }
            if (oldVersion < 4) {
                db.execSQL(getCreateSettingTableSql());
            }

        } catch (SQLException sqle) {
            String msg = "[DB unavailable]";
            msg += "\n\n" + sqle.toString();
            Toast t = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            t.show();
        }
    }

    public void insertFavorite(SQLiteDatabase db, Restaurant r) {
        ContentValues values = new ContentValues();
        values.put("RESTAURANT", r.getNAME());
        values.put("CITY", r.getPHYSICALCITY());
        values.put("ADDRESS", r.getPHYSICALADDRESS());
        values.put("CREATED_AT", Calendar.getInstance().getTime().toString());
        values.put("HazardRating", r.getHazardRating());
        values.put("InspectionDate", r.getInspectionDate());
        values.put("NumCritical", r.getNumCritical());
        values.put("NumNonCritical", r.getNumNonCritical());
        values.put("LATITUDE", r.getLATITUDE());
        values.put("LONGITUDE", r.getLONGITUDE());

        db.insert("Favorite", null, values);
    }


    private String getCreateSettingTableSql() {
        String sql = "";
        sql += "CREATE TABLE IF NOT EXISTS Setting (";
        sql += "DB_VERSION NUMERIC);";

        return sql;
    }

    private String getCreateFavoritesTableSql() {
        String sql = "";
        sql += "CREATE TABLE IF NOT EXISTS Favorite (";
        sql += "_id INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += "RESTAURANT TEXT, ";
        sql += "CITY TEXT, ";
        sql += "ADDRESS TEXT);";

        return sql;
    }

}