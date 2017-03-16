package com.example.aisha.clientapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gamef on 05-02-2017.
 */

public class PrefManager {


    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "studentDetails";

    private static final String NAME = "name";

    private static final String ROLLNUMBER = "rollnumber";

    private static final String SEMESTER = "semester";

    private static final String IMEI = "imei";


    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setPrefName(String prefName) {
        editor.putString(NAME, prefName);
        editor.commit();
    }

    public String getPrefName() {
        return pref.getString(NAME, "");
    }

    public void setPrefRollNumber(String prefRollNumber) {
        editor.putString(ROLLNUMBER, prefRollNumber);
        editor.commit();
    }

    public String getPrefRollNumber() {
        return pref.getString(ROLLNUMBER, "");
    }

    public void setPrefSemester(String prefSemester) {
        editor.putString(SEMESTER, prefSemester);
        editor.commit();
    }

    public String getPrefSemester() {
        return pref.getString(SEMESTER, "");
    }

    public void setPrefStudentDetail(String prefRollNumber, String prefName, String prefSemester, String iMEI, boolean isFirstTime) {
        editor.putString(ROLLNUMBER, prefRollNumber);
        editor.putString(NAME , prefName);
        editor.putString(SEMESTER ,prefSemester);
        editor.putString(IMEI , iMEI);
        editor.putBoolean(IS_FIRST_TIME_LAUNCH , isFirstTime);
        editor.commit();
    }

    public Details getPrefStudentDetail() {
        Details studentDetails =new Details();

        studentDetails.setName(pref.getString(NAME, ""));

        studentDetails.setRollNo(pref.getString(ROLLNUMBER ,""));
        studentDetails.setSem(pref.getString(SEMESTER ,""));
        studentDetails.setImei(pref.getString(IMEI ,""));

        return studentDetails;
    }






}
