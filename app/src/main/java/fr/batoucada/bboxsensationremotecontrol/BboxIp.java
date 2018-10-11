package fr.batoucada.bboxsensationremotecontrol;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;


class BboxIp extends AsyncTask<String, Integer, String> {

    private WeakReference<ImageButton> searchButton;
    private WeakReference<EditText> ipAddressField;
    private Integer colorValid;

    BboxIp(ImageButton searchButton, EditText ipAddressField, Integer colorValid) {
        this.searchButton = new WeakReference<>(searchButton);
        this.ipAddressField = new WeakReference<>(ipAddressField);
        this.colorValid = colorValid;
    }

    @Override
    protected void onPreExecute() {
        if (searchButton.get() != null) {
            searchButton.get().setEnabled(false);
            searchButton.get().setColorFilter(Color.GRAY);
        }
        if (ipAddressField.get() != null) {
            ipAddressField.get().getBackground().clearColorFilter();
        }
    }

    @Override
    protected String doInBackground(String... ipAddresses) {
        if (ipAddresses.length > 0 && new BboxSnmp(ipAddresses[0]).test()) return ipAddresses[0];
        else {
            String myIp = BboxSnmp.getIp();
            if (myIp == null) {
                publishProgress(100);
                return null;
            }
            String subnet = BboxSnmp.getSubnet(myIp);
            if (subnet == null) {
                publishProgress(100);
                return null;
            }

            for (int i = 1; i < 255; i++) {
                String host = subnet + BboxSnmp.dot + i;
                try {
                    if (InetAddress.getByName(host).isReachable(100)) {
                        if (new BboxSnmp(host).test()) {
                            publishProgress(100);
                            return host;
                        }
                        publishProgress((int) ((i / (float) 255) * 100));
                        if (isCancelled()) break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            publishProgress(100);
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null && ipAddressField.get() != null) {
            if (result.equals(ipAddressField.get().getText().toString())) ipAddressField.get().setText(result);
            ipAddressField.get().setError(null);
            ipAddressField.get().getBackground().setColorFilter(colorValid, PorterDuff.Mode.SRC_ATOP);
        } else if (ipAddressField.get() != null) {
            ipAddressField.get().setText("");
            ipAddressField.get().setError("Introuvable");
        }
        if (searchButton.get() != null) {
            searchButton.get().setEnabled(true);
            searchButton.get().clearColorFilter();
        }
    }
}
