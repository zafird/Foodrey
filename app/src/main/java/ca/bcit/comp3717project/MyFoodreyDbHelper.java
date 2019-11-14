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
    private static final int DB_VERSION = 1;
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
            if (oldVersion < 2)
                db.execSQL("ALTER TABLE Favorite ADD COLUMN CREATED_AT NUMERIC;");
        } catch (SQLException sqle) {
            String msg = "[DB unavailable]";
            msg += "\n\n" + sqle.toString();
            Toast t = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            t.show();
        }
    }

    private void insertFavorite(SQLiteDatabase db, Restaurant r) {
        ContentValues values = new ContentValues();
        values.put("RESTAURANT", r.getNAME());
        values.put("CITY", r.getPHYSICALCITY());
        values.put("ADDRESS", r.getPHYSICALADDRESS());
//        values.put("CREATED_AT", Calendar.getInstance().getTime().toString());

        db.insert("Favorite", null, values);
    }


    private String getCreateFavoritesTableSql() {
        String sql = "";
        sql += "CREATE TABLE Favorite (";
        sql += "_id INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += "RESTAURANT TEXT, ";
        sql += "CITY TEXT, ";
        sql += "ADDRESS TEXT);";

        return sql;
    }

}