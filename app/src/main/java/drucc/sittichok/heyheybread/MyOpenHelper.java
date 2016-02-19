package drucc.sittichok.heyheybread;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mosza_000 on 2/1/2559.
 */
public class MyOpenHelper extends SQLiteOpenHelper{

    // Explicit
    public static final String DATABASE_NAME = "Heyhey.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE_USER = "create table userTABLE (" +
            "_id integer primary key, " +
            "User text," +
            "Password text," +
            "Name text," +
            "Surname text," +
            "Address text," +
            "Phone text," +
            "Complacency text);";

    private static final String CREATE_TABLE_BREAD = "create table breadTABLE (" +
            "_id integer primary key," +
            "Bread text," +
            "Price text," +
            "Amount text," +
            "Image text);";

    private static final String CREATE_TABLE_ORDER = "create table orderTABLE (" +
            "_id integer primary key," +
            "Date text," +
            "Name text," +
            "Surname text," +
            "Address text," +
            "Phone text," +
            "Bread text," +
            "Price text," +
            "Item text)";

    private static final String CREATE_TABLE_ORDER_FINISH = "create table orderTABLE_FINISH (" +
            "_id integer primary key," +
            "idReceive text," +
            "Date text," +
            "Name text," +
            "Surname text," +
            "Address text," +
            "Phone text," +
            "Bread text," +
            "Price text," +
            "Item text)";

    public MyOpenHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }   //Constructor

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_BREAD);
        db.execSQL(CREATE_TABLE_ORDER);
        db.execSQL(CREATE_TABLE_ORDER_FINISH);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
} //Main Class
