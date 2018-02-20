package com.example.aisha.clientapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WelcomeSlideFragment2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WelcomeSlideFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeSlideFragment2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnFragmentInteractionListener mListener;

    EditText mrollNumberEditText, mNameEditText;
    Spinner mSemSpinner;
    Button mOkayButton;

    final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;

    public WelcomeSlideFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WelcomeSlideFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static WelcomeSlideFragment2 newInstance(String param1, String param2) {
        WelcomeSlideFragment2 fragment = new WelcomeSlideFragment2();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_welcome_slide_fragment2, container, false);

        mrollNumberEditText = (EditText) view.findViewById(R.id.fragment2_roll_edittext);
        mNameEditText = (EditText) view.findViewById(R.id.fragment2_name_edittext);
        mSemSpinner = (Spinner) view.findViewById(R.id.fragment2_sem_spinner);
        mOkayButton = (Button) view.findViewById(R.id.fragment2_okay_button);

        mOkayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rollNumber = mrollNumberEditText.getText().toString().trim();
                String name = mNameEditText.getText().toString().trim();
                String semester = mSemSpinner.getSelectedItem().toString();

                String iMEI = getDeviceIMEI();


                if (rollNumber == null || rollNumber.isEmpty()) {
                    Toast.makeText(getContext(), "Enter A RollNumber", Toast.LENGTH_SHORT).show();

                } else {


                    try {
                        //Integer.parseInt(rollNumber);

                        rollNumber = "0101" + rollNumber.toUpperCase();
                        if (name == null || name.isEmpty()) {
                            Toast.makeText(getContext(), "Enter Your Name", Toast.LENGTH_SHORT).show();
                        } else {

                            name = name.toUpperCase();
                            if (semester.equals("0")) {
                                Toast.makeText(getContext(), "Select Semester", Toast.LENGTH_SHORT).show();
                            } else {
                                saveDetails(rollNumber, name, semester, iMEI);
                            }

                        }

                    } catch (NumberFormatException parse) {
                        parse.printStackTrace();
                        Toast.makeText(getContext(), "Enter a Valid RollNumber (Integer ONLY)", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        });


        return view;
    }

    private void saveDetails(String rollNumber, String name, String semester, String iMEI) {
        Toast.makeText(getContext(), "Details Saved as " + rollNumber + "," + name + "," + semester + "," + iMEI, Toast.LENGTH_SHORT).show();

        PrefManager prefManager = new PrefManager(getContext());
        prefManager.setPrefStudentDetail(rollNumber, name, semester, iMEI, false);
        openNextActivity();
    }

    private void openNextActivity() {
        Intent i = new Intent(getContext(), StartActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(i, 1);
        getActivity().finish();
    }

    public boolean checkForPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
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


        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_PHONE_STATE},
                MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

    }

    public String getDeviceIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String iMEINumber = telephonyManager.getDeviceId();

        return iMEINumber;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}

