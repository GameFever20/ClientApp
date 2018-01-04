package com.example.aisha.clientapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_start_toolbar);
        setSupportActionBar(toolbar);

    }

    public void openAttendance(View view){
        Intent intent = new Intent(StartActivity.this ,AttendanceActivity.class);
        startActivity(intent);

    }

    public void openRegister(View view){

        Intent intent = new Intent(StartActivity.this ,RegisterActivity.class);
        startActivity(intent);

    }

    public void openHelp(View view){
         Intent intent = new Intent(StartActivity.this ,HelpActivity.class);
        startActivity(intent);

    }

}

