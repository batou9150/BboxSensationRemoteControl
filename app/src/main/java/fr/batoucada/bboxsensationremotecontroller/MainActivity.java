package fr.batoucada.bboxsensationremotecontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String ipAddress = "192.168.1.150";

        ArrayList<Integer> buttonIdList = new ArrayList<>();
        buttonIdList.add(R.id.b_power);
        buttonIdList.add(R.id.b_volume_down);
        buttonIdList.add(R.id.b_volume_up);
        buttonIdList.add(R.id.b_program_down);
        buttonIdList.add(R.id.b_program_up);

        View.OnClickListener buttonOnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                new SNMPSet(ipAddress).execute((String) v.getTag());
            }
        };

        for (Integer buttonId : buttonIdList) {
            findViewById(buttonId).setOnClickListener(buttonOnClickListener);
        }
    }
}
