package fr.batoucada.bboxsensationremotecontrol;

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

        View.OnClickListener buttonOnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                EditText editTextIpAddress = findViewById(R.id.edit_text_ip_address);
                new BboxSend(editTextIpAddress.getText().toString()).execute((String) v.getTag());
            }
        };

        for (Integer buttonId : buttonIdList) {
            findViewById(buttonId).setOnClickListener(buttonOnClickListener);
        }

        ImageButton b_search_target = findViewById(R.id.b_search_target);
        b_search_target.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editTextIpAddress = findViewById(R.id.edit_text_ip_address);
                ImageButton b_search_target = findViewById(R.id.b_search_target);
                Integer colorValid = ResourcesCompat.getColor(getResources(), R.color.colorValid, null);
                ProgressBar progressBar = findViewById(R.id.progressBar);
                new BboxIp(b_search_target, editTextIpAddress, progressBar, colorValid).execute(editTextIpAddress.getText().toString());
            }
        });
    }
}
