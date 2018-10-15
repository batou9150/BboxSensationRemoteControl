package fr.batoucada.bboxsensationremotecontrol;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.Locale;


class BboxIp extends AsyncTask<Void, Integer, String> {

    private final Integer progressMax = 255;
    private WeakReference<ImageButton> searchButton;
    private WeakReference<EditText> ipAddressField;
    private Integer colorValid;
    private WeakReference<ProgressBar> progressBar;
    private String sysContactValue;

    BboxIp(ImageButton searchButton, EditText ipAddressField, ProgressBar progressBar, Integer colorValid) {
        this(searchButton, ipAddressField, progressBar, colorValid, null);
    }

    BboxIp(ImageButton searchButton, EditText ipAddressField, ProgressBar progressBar, Integer colorValid, String sysContactValue) {
        this.searchButton = new WeakReference<>(searchButton);
        this.ipAddressField = new WeakReference<>(ipAddressField);
        this.progressBar = new WeakReference<>(progressBar);
        this.colorValid = colorValid;
        this.sysContactValue = sysContactValue;
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
        if (progressBar.get() != null) {
            progressBar.get().setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        if (ipAddressField.get() != null) {
            String currentIpAddress = ipAddressField.get().getText().toString();
            if (new BboxSnmp(currentIpAddress).test()) {
                if (sysContactValue != null) new BboxSnmp(currentIpAddress).set(sysContactValue);
                return currentIpAddress;
            }
        }
        String myIp = BboxSnmp.getIp();
        if (myIp == null) {
            publishProgress(progressMax);
            return null;
        }
        String subnet = BboxSnmp.getSubnet(myIp);
        if (subnet == null) {
            publishProgress(progressMax);
            return null;
        }

        for (int i = 1; i < progressMax; i++) {
            String host = subnet + BboxSnmp.dot + i;
            try {
                publishProgress(i);
                if (InetAddress.getByName(host).isReachable(100)) {
                    if (new BboxSnmp(host).test()) {
                        publishProgress(progressMax);
                        if (sysContactValue != null) new BboxSnmp(host).set(sysContactValue);
                        return host;
                    }
                }
                if (isCancelled()) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        publishProgress(progressMax);
        return null;

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if (progressBar.get() != null) {
            progressBar.get().setProgress(progress[0]);
        }
        if (ipAddressField.get() != null) {
            ipAddressField.get().setText(String.format(Locale.FRANCE, "Scan : %d / %d", progress[0], progressMax));
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null && ipAddressField.get() != null) {
            if (!result.equals(ipAddressField.get().getText().toString())) ipAddressField.get().setText(result);
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
        if (progressBar.get() != null) {
            progressBar.get().setVisibility(View.GONE);
        }
    }
}
