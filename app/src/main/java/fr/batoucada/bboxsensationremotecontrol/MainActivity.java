package fr.batoucada.bboxsensationremotecontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        String mIpAddress = mPrefs.getString("ip_address", getResources().getString(R.string.edit_text_ip_address_default));
        EditText editTextIpAddress = findViewById(R.id.edit_text_ip_address);
        editTextIpAddress.setText(mIpAddress);

        if (!isWifiOK()) new EnableWifiDialog().show(getSupportFragmentManager(), "fragment_edit_name");

        View.OnClickListener buttonOnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (isWifiOK()) {
                    EditText editTextIpAddress = findViewById(R.id.edit_text_ip_address);
                    if (getResources().getString(R.string.edit_text_ip_address_default).equals(editTextIpAddress.getText().toString())) {
                        ImageButton b_search_target = findViewById(R.id.b_search_target);
                        Integer colorValid = ResourcesCompat.getColor(getResources(), R.color.colorValid, null);
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        new BboxIp(b_search_target, editTextIpAddress, progressBar, colorValid, (String) v.getTag()).execute();
                    } else
                        new BboxSend(editTextIpAddress.getText().toString()).execute((String) v.getTag());
                } else
                    new EnableWifiDialog().show(getSupportFragmentManager(), "enable_wifi");
            }
        };

        for (Integer buttonId : getButtons()) {
            findViewById(buttonId).setOnClickListener(buttonOnClickListener);
        }

        ImageButton b_search_target = findViewById(R.id.b_search_target);
        b_search_target.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isWifiOK()) {
                    EditText editTextIpAddress = findViewById(R.id.edit_text_ip_address);
                    ImageButton b_search_target = findViewById(R.id.b_search_target);
                    Integer colorValid = ResourcesCompat.getColor(getResources(), R.color.colorValid, null);
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    new BboxIp(b_search_target, editTextIpAddress, progressBar, colorValid).execute();
                } else
                    new EnableWifiDialog().show(getSupportFragmentManager(), "enable_wifi");
            }
        });
    }

    private boolean isWifiOK() {
        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectionManager != null) {
            NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
            return networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        } else
            return false;
    }

    private ArrayList<Integer> getButtons() {
        ArrayList<Integer> buttonIdList = new ArrayList<>();
        buttonIdList.add(R.id.b_power);

        buttonIdList.add(R.id.b_volume_up);
        buttonIdList.add(R.id.b_volume_down);
        buttonIdList.add(R.id.b_home);
        buttonIdList.add(R.id.b_volume_mute);
        buttonIdList.add(R.id.b_program_up);
        buttonIdList.add(R.id.b_program_down);

        buttonIdList.add(R.id.b_quit);
        buttonIdList.add(R.id.b_back);
        buttonIdList.add(R.id.b_0);
        buttonIdList.add(R.id.b_1);
        buttonIdList.add(R.id.b_2);
        buttonIdList.add(R.id.b_3);
        buttonIdList.add(R.id.b_4);
        buttonIdList.add(R.id.b_5);
        buttonIdList.add(R.id.b_6);
        buttonIdList.add(R.id.b_7);
        buttonIdList.add(R.id.b_8);
        buttonIdList.add(R.id.b_9);

        buttonIdList.add(R.id.b_ok);
        buttonIdList.add(R.id.b_left_arrow);
        buttonIdList.add(R.id.b_up_arrow);
        buttonIdList.add(R.id.b_right_arrow);
        buttonIdList.add(R.id.b_down_arrow);

        buttonIdList.add(R.id.b_fast_rewind);
        buttonIdList.add(R.id.b_record);
        buttonIdList.add(R.id.b_stop);
        buttonIdList.add(R.id.b_play_pause);
        buttonIdList.add(R.id.b_fast_forward);
        return buttonIdList;
    }

    @Override
    protected void onStop() {
        // call the superclass method first
        super.onStop();
        EditText editTextIpAddress = findViewById(R.id.edit_text_ip_address);
        getSharedPreferences("label", 0).edit().putString("ip_address", editTextIpAddress.getText().toString()).apply();
    }
}
