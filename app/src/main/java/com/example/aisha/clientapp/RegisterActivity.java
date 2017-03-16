package com.example.aisha.clientapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class RegisterActivity extends AppCompatActivity {
    private ServerSocket serverSocket;
    String serverIP = "192.168.43.1";
    final int SERVERPORT = 6666;
    private String serverIpAddress = "";
    private boolean connected = false;
    android.os.Handler handler = new android.os.Handler();
    Details studentDetail;

    TextView statusTextView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        statusTextView =(TextView)findViewById(R.id.register_activity_status_textview);

        updateStatusTextView("STATUS");


        serverIP = "192.168.43.1";
        retriveStudentDetail();

        showDetailInTextView();

    }

    private void updateStatusTextView(String status) {

        statusTextView.setText(status);

    }

    private void showDetailInTextView() {

        TextView detailTextview = (TextView)findViewById(R.id.register_activity_detail_textview);
        detailTextview.setText(studentDetail.getFormattedDetail());

    }


    private void retriveStudentDetail() {
        PrefManager prefManager = new PrefManager(RegisterActivity.this);
        studentDetail = prefManager.getPrefStudentDetail();

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
                            Toast.makeText(RegisterActivity.this, "Attendance Session is Inactive \n MAke sure you are connected to right Device ", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(RegisterActivity.this, "Send Succesfully", Toast.LENGTH_SHORT).show();

        updateStatusTextView("Send Succesfully \n Attendence Registered");

    }

    public void sendButtonRegisterClick(View view) {


        if (Connectivity.isConnectedWifi(RegisterActivity.this)) {
            if (!connected) {
                serverIpAddress = "192.168.43.1";
                // serverIpAddress = servertext.getText().toString();
                Thread cThread = new Thread(new ClientThread());
                cThread.start();
            }
        } else {
            Toast.makeText(RegisterActivity.this, "Not connected to any wifi", Toast.LENGTH_SHORT).show();
            updateStatusTextView("Not connected to any wifi \n MAKE sure you connect to teacher\'s wifi");

        }


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
