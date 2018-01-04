package com.example.aisha.clientapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.FlatButton;
import com.dd.processbutton.ProcessButton;
import com.dd.processbutton.iml.ActionProcessButton;
import com.dd.processbutton.iml.SubmitProcessButton;

import com.pro100svitlo.fingerprintAuthHelper.FahErrorType;
import com.pro100svitlo.fingerprintAuthHelper.FahListener;
import com.pro100svitlo.fingerprintAuthHelper.FingerprintAuthHelper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class AttendanceActivity extends AppCompatActivity {

    private ServerSocket serverSocket;
    String serverIP = "192.168.43.1";
    final int SERVERPORT = 5432;
    private String serverIpAddress = "";
    private boolean connected = false;
    android.os.Handler handler = new android.os.Handler();
    Details studentDetail;
    TextView statusTextView;


    ProgressDialog progress = null;

    FingerprintAuthHelper mFAH;

    ActionProcessButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        statusTextView = (TextView) findViewById(R.id.attendance_activity_status_textview);

        serverIP = "192.168.43.1";
        retriveStudentDetail();
        showDetailInTextView();

        progress = new ProgressDialog(this);


        mFAH = new FingerprintAuthHelper
                .Builder(this, new FahListener() {
            @Override
            public void onFingerprintStatus(boolean b, int i, CharSequence charSequence) {

                if (b) {
                    // do some stuff here in case auth was successful
                    Toast.makeText(AttendanceActivity.this, " Authenticated", Toast.LENGTH_SHORT).show();

                    closeProgress();


                    sendAttendance();


                } else if (mFAH != null) {
                    // do some stuff here in case auth failed
                    switch (i) {
                        case FahErrorType.General.LOCK_SCREEN_DISABLED:
                            Toast.makeText(AttendanceActivity.this, "Lock screen disabled", Toast.LENGTH_SHORT).show();
                            closeProgress();
                            break;
                        case FahErrorType.General.NO_FINGERPRINTS:
                            mFAH.showSecuritySettingsDialog();
                            Toast.makeText(AttendanceActivity.this, " No fringer print", Toast.LENGTH_SHORT).show();
                            closeProgress();
                            break;
                        case FahErrorType.Auth.AUTH_NOT_RECOGNIZED:
                            //do some stuff here
                            Toast.makeText(AttendanceActivity.this, "Not recognized", Toast.LENGTH_SHORT).show();

                            break;
                        case FahErrorType.Auth.AUTH_TO_MANY_TRIES:
                            //do some stuff here
                            Toast.makeText(AttendanceActivity.this, "too many tries", Toast.LENGTH_SHORT).show();

                            closeProgress();
                            break;
                    }
                }

            }

            @Override
            public void onFingerprintListening(boolean listening, long millis) {

                if (listening) {
                    //add some code here
                    //Toast.makeText(AttendanceActivity.this, "Listening", Toast.LENGTH_SHORT).show();

                } else {
                    //add some code here

                }
                if (millis > 0) {
                    //if u need, u can show timeout for user

                }

            }
        }) //(Context inscance of Activity, FahListener)
                .build();

        btnSend = (ActionProcessButton) findViewById(R.id.attendance_send_processbutton);
        btnSend.setMode(ActionProcessButton.Mode.ENDLESS);

    }

    private void sendAttendance() {
        if (Connectivity.isConnectedWifi(AttendanceActivity.this)) {
            if (!connected) {


                serverIpAddress = "192.168.43.1";
                // serverIpAddress = servertext.getText().toString();
                Thread cThread = new Thread(new ClientThread());
                cThread.start();


            }
        } else {
            Toast.makeText(AttendanceActivity.this, "Not connected to any wifi", Toast.LENGTH_SHORT).show();

            updateStatusTextView("Not connected to any wifi \n MAKE sure you connect to teacher\'s wifi");
        }

    }

    private void updateStatusTextView(String status) {

        statusTextView.setText(status);

    }

    private void showDetailInTextView() {

        TextView detailTextview = (TextView) findViewById(R.id.attendance_activity_detail_textview);
        detailTextview.setText(studentDetail.getFormattedDetail());

    }

    private void retriveStudentDetail() {
        PrefManager prefManager = new PrefManager(AttendanceActivity.this);
        studentDetail = prefManager.getPrefStudentDetail();

    }

    public void showDialog(String message) {


        progress.setMessage(message);
        //progress.setCancelable(false);

        progress.show();


    }

    public void closeProgress() {
        try {
            progress.cancel();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ClientThread implements Runnable {

        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                Socket socket = null;
                try {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showDialog("Sending attendance");
                            updateStatusTextView("Sending...");
                            btnSend.setProgress(1);
                        }
                    });
                    socket = new Socket(serverAddr, SERVERPORT);
                    connected = true;
                } catch (final ConnectException connectiponException) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AttendanceActivity.this, "Attendance Session is Inactive \n MAke sure you are connected to right Device ", Toast.LENGTH_SHORT).show();

                            btnSend.setProgress(-1);
                            closeProgress();
                            updateStatusTextView("Attendance Session is Inactive \n MAke sure you are connected to right Device ");
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
                        out.println(studentDetail.getName());
                        out.println(studentDetail.getRollNo());
                        out.println(studentDetail.getSem());
                        out.println(studentDetail.getImei());

                        Log.d("ClientActivity", "C: Sent.");
                        connected = false;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                notifySuccessfullSend();
                            }
                        });
                    } catch (Exception e) {
                        Log.e("ClientActivity", "S: Error", e);
                    }
                }
                try {
                    socket.close();
                } catch (NullPointerException nullPointerExceptio) {
                    nullPointerExceptio.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //storeInData();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }

    private void notifySuccessfullSend() {

        btnSend.setProgress(100);

        //Toast.makeText(AttendanceActivity.this, "Send Succesfully", Toast.LENGTH_SHORT).show();

        closeProgress();
        updateStatusTextView("Sent Succesfully \n Attendence Registered");

        turnWifiOff();


    }

    private void turnWifiOff() {
        try {
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            wifiManager.setWifiEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendButtonClick(View view) {

    /*    if (mFAH.isHardwareEnable()) {
            //do some stuff here

            mFAH.startListening();
            showDialog("Authenticate by fingerprint. (Tap on fingerprint sensor)");

        } else {
            //otherwise do
            sendAttendance();

        }*/

        statusTextView.setVisibility(View.VISIBLE);
        sendAttendance();


    }


    protected void onStop() {
        super.onStop();
        try {
            // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
