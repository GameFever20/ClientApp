package com.example.aisha.clientapp;

/**
 * Created by Aisha on 10/11/2016.
 */
public class Details {


    private long id;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    private String Name;
    private String RollNo;
    private String Sem;
    private String imei;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSem() {
        return Sem;
    }

    public void setSem(String sem) {
        Sem = sem;
    }




    public String getRollNo() {
        return RollNo;
    }

    public void setRollNo(String rollNo) {
        RollNo = rollNo;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }




    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return Name+", "+RollNo+","+Sem;

    }

    public String getFormattedDetail() {
        return "NAME   :"+getName()+"\n\n ROLL NUMBER  : " +getRollNo() +"\n\n SEMESTER   :"+getSem()+"\n\n DEVICE IMEI  :"+ getImei() +"\n";
    }
}
