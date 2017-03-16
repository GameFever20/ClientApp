package com.example.aisha.clientapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aisha on 10/11/2016.
 */
public class InsertQuerySource {

    private SQLiteDatabase database;
    private NewDbHelper newDbHelper;
    private String[] allColumns = { NewDbHelper.COLUMN_ID,NewDbHelper.COLUMN_MESSAGE, NewDbHelper.COLUMN_ROLLNO,NewDbHelper.COLUMN_SEM};
    Context main;

    public InsertQuerySource(Context context) {
        newDbHelper = new NewDbHelper(context);
        main=context;
    }

    public void open() throws SQLException {
        database = newDbHelper.getWritableDatabase();
    }

    public void close() {
        newDbHelper.close();
    }

    public Details createMessage( String message,String rollno,String se) {
        ContentValues values = new ContentValues();
        values.put(NewDbHelper.COLUMN_MESSAGE, message);
        values.put(NewDbHelper.COLUMN_ROLLNO,rollno);
        values.put(NewDbHelper.COLUMN_SEM,se);


        long insertId = database.insert(NewDbHelper.TABLE_MESSAGES, null,values);
        Cursor cursor = database.query(NewDbHelper.TABLE_MESSAGES,allColumns, NewDbHelper.COLUMN_ID + " = " + insertId, null,null, null, null);
        // Cursor cursor = database.query(MySQLiteHelper.TABLE_MESSAGES,allColumns,null, null,null, null, null);
        cursor.moveToFirst();
        Details newDetail = cursorToMessage(cursor);
        cursor.close();
        return newDetail;
    }

    public List<Details> getAllMessages() {
        List<Details> detailsList = new ArrayList<Details>();

        Cursor cursor = database.query(NewDbHelper.TABLE_MESSAGES,allColumns, null, null, null, null, null);
        // Cursor cursor = database.query(MySQLiteHelper.TABLE_MESSAGES,allColumns,MainActivity.SEARCH_BY, null,null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Details details = cursorToMessage(cursor);
            detailsList.add(details);
            Toast.makeText(main, "message retrieve from dabase"+details.toString(), Toast.LENGTH_SHORT).show();
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return detailsList;
    }

    private Details cursorToMessage(Cursor cursor) {
        Details details = new Details();
        details.setId(cursor.getLong(0));
       // details.setMessage(cursor.getString(1));
        details.setRollNo(cursor.getString(2));
        details.setSem(cursor.getString(3));

        return details;
    }

}
