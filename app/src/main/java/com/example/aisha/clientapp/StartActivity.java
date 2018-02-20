package com.example.aisha.clientapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_start_toolbar);
        setSupportActionBar(toolbar);

    }

    public void openAttendance(View view) {
        Intent intent = new Intent(StartActivity.this, AttendanceActivity.class);
        startActivity(intent);

    }


    public void openRegister(View view) {

        Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(intent);

    }

    public void openHelp(View view) {
        Intent intent = new Intent(StartActivity.this, HelpActivity.class);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            onSettingClick();
        }

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    private void onSettingClick() {

    }


    public void openAboutUs(View view) {
        Intent intent = new Intent(StartActivity.this, AboutUsActivity.class);
        startActivity(intent);
    }
}

