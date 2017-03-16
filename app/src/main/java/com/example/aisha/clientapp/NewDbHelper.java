package com.example.aisha.clientapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Aisha on 10/11/2016.
 */
public class NewDbHelper extends SQLiteOpenHelper {


    public static final String TABLE_MESSAGES = "REGISTERED_STUDENTS";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MESSAGE = "MESSAGE";
    public static final String COLUMN_ROLLNO = "Roll_No";
    public static final String COLUMN_SEM = "Semester";

    private static final String DATABASE_NAME = "Register.db";
    private static final int DATABASE_VERSION = 3;
    Context main;

    private static final String DATABASE_CREATE=        "create table " + TABLE_MESSAGES + "(" + COLUMN_ID +" integer primary key autoincrement, "
            + COLUMN_MESSAGE + " text not null,"
            + COLUMN_ROLLNO + " text not null,"
            + COLUMN_SEM + " text not null"
            + ");";


    public NewDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        main=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
        Log.d("in o create ofdatabes","databse new create");
        Toast.makeText(main, "on create of  register databse", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w(NewDbHelper.class.getName(), "Upgrading database from version " + i + " to " + i1 + ", which will destroy all old data");
        Log.d("in upgrade","databse new create");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(sqLiteDatabase);

    }
}
