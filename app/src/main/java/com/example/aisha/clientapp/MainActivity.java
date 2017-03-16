package com.example.aisha.clientapp;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    WifiManager wifiManager;
    ListView lv;

    int size = 0;
    List<ScanResult> results;
    String ITEM_KEY = "key";
    ArrayList<String> arraylist = new ArrayList<String>();


    ArrayAdapter adapter;


    String networkSSID = "test";
    String networkPass = "pass";

    //SERVER SOCKET
    String serverIP = "10.0.2.15";
    final int SERVERPORT = 5432;
    private ServerSocket serverSocket;

    TextView serverStatus;
    String sender_name, sender_roll = "";

    //client side
    EditText name, roll, sem;

    private Button connectPhones;
    private String serverIpAddress = "";
    private boolean connected = false;
    //FloatingActionButton fabbuttonScan;

    Button buttonscan;
    Button register;

    InsertQuerySource insertQuery;

    android.os.Handler handler = new android.os.Handler();

    TextView detailsTv;
    ArrayList<Details> arraylistData;

    //REGISTEREING WITH DIALOG
    Dialog dialog;
    EditText e1, e2, e3;
    Button enter;
    String n, r, s;


    final int  MY_PERMISSIONS_REQUEST_READ_PHONE_STATE =2;

    String iMEINumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //client declaration
        sem = (EditText) findViewById(R.id.sem);
        name = (EditText) findViewById(R.id.name);
        roll = (EditText) findViewById(R.id.roll);
        lv = (ListView) findViewById(R.id.availabehotspotlist);
        buttonscan = (Button) findViewById(R.id.buttonscan);
        serverStatus = (TextView) findViewById(R.id.textView);
        register = (Button) findViewById(R.id.register);
        detailsTv = (TextView) findViewById(R.id.detailsTv);

        serverIP = "192.168.43.1";

        String s = retrieveDataInTv();
        detailsTv.setText(s);
        //send msg
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();
                if(!checkForPermission()){
                    requestForPermission();
                }

               if(Connectivity.isConnectedWifi(MainActivity.this)) {
                   Toast.makeText(MainActivity.this, "imei Number is " + getDeviceIMEI(), Toast.LENGTH_SHORT).show();
                   if (!connected) {
                       serverIpAddress = "192.168.43.1";
                       // serverIpAddress = servertext.getText().toString();
                       Thread cThread = new Thread(new ClientThread());
                       cThread.start();
                   }
               }else{
                   Toast.makeText(MainActivity.this, "Not connected to any wifi", Toast.LENGTH_SHORT).show();

               }


            }
        });
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);


        //wifi scan
        // fabbuttonScan=(FloatingActionButton)findViewById(R.id.fabscan);
        buttonscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //scanning of available wifi

                Intent intent = new Intent (MainActivity.this ,WelcomeActivity.class);
                startActivity(intent);

                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                }
                arraylist.clear();
                wifiManager.startScan();

                Toast.makeText(MainActivity.this, "Scanning...." + size, Toast.LENGTH_SHORT).show();
                try {

                    for (size = 0; size < results.size(); size++) {
                        HashMap<String, String> item = new HashMap<String, String>();
                        item.put(ITEM_KEY, results.get(size).SSID + "  " + results.get(size).capabilities);
                        Toast.makeText(MainActivity.this, "in try" + size, Toast.LENGTH_SHORT).show();

                        arraylist.add(results.get(size).SSID);
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "in catch", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //adding in listview
        adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, arraylist);

        lv.setAdapter(this.adapter);
        lv.setVisibility(View.VISIBLE);
        //broadcast
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                results = wifiManager.getScanResults();


                size = results.size();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


        //connect
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int item_id, long l) {


                WifiConfiguration conf = new WifiConfiguration();

                Log.i("before get", "exception");
                networkSSID = results.get(item_id).SSID;
                Toast.makeText(MainActivity.this, "network SSSid " + networkSSID, Toast.LENGTH_SHORT).show();
                conf.SSID = "\"" + networkSSID + "\"";
                Log.i("after get", "exception");
                conf.preSharedKey = "\"" + networkPass + "\"";
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.preSharedKey = "\"" + networkPass + "\"";

                wifiManager.addNetwork(conf);

                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

                for (WifiConfiguration p : list) {
                    //  Toast.makeText(MainActivity.this, "ssids " + p.SSID, Toast.LENGTH_SHORT).show();
                    if (p.SSID.equals("\"" + networkSSID + "\"")) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(p.networkId, true);
                        wifiManager.reconnect();
                        break;
                    }
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class ClientThread implements Runnable {

        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                Socket socket = null;
                try {
                    socket = new Socket(serverAddr, SERVERPORT);
                    connected = true;
                } catch (final ConnectException connectiponException) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Attendance Session is Inactive", Toast.LENGTH_SHORT).show();

                            connectiponException.printStackTrace();

                        }
                    });
                }


                while (connected) {

                    try {
                        Log.d("ClientActivity", "C: Sending command.");
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                                .getOutputStream())), true);
                        // WHERE YOU ISSUE THE COMMANDS
                        out.println(name.getText().toString());
                        out.println(roll.getText().toString());
                        out.println(sem.getText().toString());
                        if(iMEINumber != null ) {
                            out.println(iMEINumber);
                        }
                        Log.d("ClientActivity", "C: Sent.");

                        connected = false;
                    } catch (Exception e) {
                        Log.e("ClientActivity", "S: Error", e);
                    }
                }
                try {
                    socket.close();
                }catch(NullPointerException nullPointerExceptio){
                    nullPointerExceptio.printStackTrace();
                }catch(Exception e){
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        storeInData();
                    }
                });
                //storeInData();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }


    protected void onStop() {
        super.onStop();
        try {
            // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void registerMe(View view) {

      /* dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_list);
        dialog.setCancelable(true);
        dialog.setTitle("Enter your Detail in CAPS");

        e1=(EditText) dialog.findViewById(R.id.editText1);
        e2=(EditText) dialog.findViewById(R.id.editText2);
        e3=(EditText) dialog.findViewById(R.id.editText3);
        enter=(Button)dialog.findViewById(R.id.enter);

        dialog.show();
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                n=e1.getText().toString();
                r=e2.getText().toString();
                s=e3.getText().toString();
                dialog.cancel();
            }
        });
        */
        if (!connected) {
            serverIpAddress = "192.168.43.1";
            // serverIpAddress = servertext.getText().toString();
            Thread cThread = new Thread(new ClientThread());
            cThread.start();


        }

    }

    public void storeInData() {
        String na = name.getText().toString();
        String ra = roll.getText().toString();
        String sa = sem.getText().toString();

        insertQuery = new InsertQuerySource(this);
        insertQuery.open();
        Details detail = null;
        detail = insertQuery.createMessage(na, ra, sa);
        Toast.makeText(this, detail.toString(), Toast.LENGTH_SHORT).show();
        detailsTv.setText(detail.toString());


    }

    public String retrieveDataInTv() {
        insertQuery = new InsertQuerySource(MainActivity.this);
        String d = "";
        insertQuery.open();
        // List<Message> values = datasource.getAllMessages();
        arraylistData = (ArrayList<Details>) insertQuery.getAllMessages();
        int i = arraylistData.size();
        if (i != 0) {
            for (int j = 0; j < i; j++) {
                d = d + "" + arraylistData.get(j);

            }
        } else {
            Toast.makeText(MainActivity.this, "No Item In List", Toast.LENGTH_SHORT).show();
        }

        return d;
    }

    public String getDeviceIMEI(){
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String iMEINumber= telephonyManager.getDeviceId();

        return iMEINumber ;
    }

    public boolean checkForPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE);
        boolean permission;
        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
            permission =true ;
        else{
            permission =false ;
        }
        return permission;

    }

    void requestForPermission(){

        ActivityCompat.requestPermissions(MainActivity.this,
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

                } else {

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
