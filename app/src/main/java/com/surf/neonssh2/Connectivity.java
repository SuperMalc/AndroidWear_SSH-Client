package com.surf.neonssh2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Connectivity extends Activity {

    TextView tv1, tv2;
    Button btn1, btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectivity);

        final String txt_username = getIntent().getStringExtra("EXTRA_USERNAME_ID");
        final String txt_password = getIntent().getStringExtra("EXTRA_PASSWORD_ID");

        tv1 = (TextView) findViewById(R.id.connect_main);
        tv2 = (TextView) findViewById(R.id.connect_main2);
        btn1 = (Button) findViewById(R.id.selection_1);
        btn2 = (Button) findViewById(R.id.selection_2);

        // Tieni le cose come stanno (salva lo stato)
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goOnWithBT = new Intent(getApplicationContext(), ActivityTwo.class);
                goOnWithBT.putExtra("EXTRA_USERNAME_ID", txt_username);
                goOnWithBT.putExtra("EXTRA_PASSWORD_ID", txt_password);
                startActivity(goOnWithBT);
            }
        });

        // Spegni bluetooth
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                mBluetoothAdapter.disable();

                Intent goOnNoBT = new Intent(getApplicationContext(), ActivityTwo.class);
                goOnNoBT.putExtra("EXTRA_USERNAME_ID", txt_username);
                goOnNoBT.putExtra("EXTRA_PASSWORD_ID", txt_password);
                startActivity(goOnNoBT);
            }
        });

    }
}