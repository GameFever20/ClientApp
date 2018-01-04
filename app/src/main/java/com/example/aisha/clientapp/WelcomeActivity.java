package com.example.aisha.clientapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;

    private Button btnSkip, btnNext;
    private PrefManager prefManager;
    final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
             launchHomeScreen();
             finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        // add few more layouts if you want
      /*  layouts = new int[]{
                R.layout.welcome_slide_1,

                R.layout.welcome_slide_2,
                };
*/
        // adding bottom dots
        //addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        setupViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                addBottomDots(position);

                //Toast.makeText(WelcomeActivity.this, "Page changed", Toast.LENGTH_SHORT).show();

                // changing the next button text 'NEXT' / 'GOT IT'
                if (position == myViewPagerAdapter.getCount() - 1) {
                    // last page. make button text to GOT IT
                    btnNext.setText(getString(R.string.start));
                    btnNext.setVisibility(View.GONE);
                    btnSkip.setVisibility(View.GONE);
                    if (!checkForPermission()) {
                        requestForPermission();
                    }
                } else {
                    // still pages are left
                    btnNext.setText(getString(R.string.next));
                    btnSkip.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launchHomeScreen();
                Toast.makeText(WelcomeActivity.this, "You cannot skip!!", Toast.LENGTH_SHORT).show();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < myViewPagerAdapter.getCount()) {
                    // move to next screen
                    viewPager.setCurrentItem(current);

                    Toast.makeText(WelcomeActivity.this, "next clicked", Toast.LENGTH_SHORT).show();
                } else if (current == myViewPagerAdapter.getCount()) {
                    btnNext.setVisibility(View.GONE);
                    btnSkip.setVisibility(View.GONE);
                } else {
                    launchHomeScreen();
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[myViewPagerAdapter.getCount()];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        Intent intent = new Intent(WelcomeActivity.this ,StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //  viewpager change listener
/*
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            //Toast.makeText(WelcomeActivity.this, "Page changed", Toast.LENGTH_SHORT).show();

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == myViewPagerAdapter.getCount() - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
*/

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */

    private void setupViewPager(ViewPager viewPager) {
        myViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        myViewPagerAdapter.addFragment(new WelcomeSlideFragment1(), "Hello");
        myViewPagerAdapter.addFragment(new WelcomeSlideFragment3(), "Tutorial");
        myViewPagerAdapter.addFragment(new WelcomeSlideFragment2(), "Details");
        viewPager.setAdapter(myViewPagerAdapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public boolean checkForPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(WelcomeActivity.this,
                Manifest.permission.READ_PHONE_STATE);
        boolean permission;
        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
            permission = true;
        else {
            permission = false;
        }
        return permission;

    }

    void requestForPermission() {


        ActivityCompat.requestPermissions(WelcomeActivity.this,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(WelcomeActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "permission Denied\nWithout Permission App will not Work", Toast.LENGTH_SHORT).show();
                    requestForPermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
