package fr.batoucada.bboxsensationremotecontrol;


import android.os.AsyncTask;

public class BboxSend extends AsyncTask<String, Void, Void> {

    private String ipAddress;

    BboxSend(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    protected Void doInBackground(String... sysContactValues) {
        if (sysContactValues.length < 1) return null;
        new BboxSnmp(ipAddress).set(sysContactValues[0]);
        return null;
    }
}