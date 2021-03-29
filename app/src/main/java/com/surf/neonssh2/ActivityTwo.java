package com.surf.neonssh2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class ActivityTwo extends Activity {

    public static final String TAG = "ActivityTwo";

    // Shared pref
    SharedPreferences sharedPreferences_activityTwo;
    public static final String SHARED_PREFS_TWO = "sharedPrefsTwo";
    public static final String SP2_ADDRESS = "address";
    public static final String SP2_PORT = "port";

    public static final String CHECKBOX_2 = "checkboxTwo";
    private boolean is_checkBox2_selected;

    // Layout
    private TextView show_user;
    private TextView showErr;
    private Button btn_test;
    private String txt_username, txt_password;
    private LinearLayout linearLayout;
    private EditText editTextServerAddress, editTextServerPort;
    private CheckBox checkBox2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        // inizializzazione delle "shared preferences"
        sharedPreferences_activityTwo = getSharedPreferences(SHARED_PREFS_TWO, 0);

        final Handler updateHandler = new Handler();

        linearLayout = (LinearLayout) findViewById(R.id.userprofile);

        editTextServerAddress = (EditText) findViewById(R.id.server_addr);
        editTextServerPort = (EditText) findViewById(R.id.server_port);

        show_user = (TextView) findViewById(R.id.view_show_user);
        showErr = (TextView) findViewById(R.id.show_err_msg);
        btn_test = (Button) findViewById(R.id.button_test);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);

        txt_username = getIntent().getStringExtra("EXTRA_USERNAME_ID");
        txt_password = getIntent().getStringExtra("EXTRA_PASSWORD_ID");

        show_user.setText(txt_username);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(toMainActivity);
            }
        });

        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox2.isChecked()) {
                    SharedPreferences.Editor editor = sharedPreferences_activityTwo.edit();
                    editor.putString(SP2_ADDRESS, editTextServerAddress.getText().toString());
                    editor.putString(SP2_PORT, editTextServerPort.getText().toString());
                    editor.putBoolean(CHECKBOX_2, checkBox2.isChecked());
                    editor.apply();
                } else {
                    editTextServerAddress.setText("");
                    editTextServerPort.setText("");

                    // Cancelloi valori memorizzati e li salvo come vuoti
                    SharedPreferences.Editor editor = sharedPreferences_activityTwo.edit();
                    editor.putString(SP2_ADDRESS, "");
                    editor.putString(SP2_PORT, "");
                    editor.putBoolean(CHECKBOX_2, checkBox2.isChecked());
                    editor.apply();
                }
            }
        });


        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread connection_test = new Thread() {
                    @Override
                    public void run() {
                        try {
                            showErr.setText(R.string.try_conn);

                            String server_address = editTextServerAddress.getText().toString();
                            int port_number = Integer.parseInt(editTextServerPort.getText().toString());

                            executeConnectionTest(txt_username, txt_password, server_address, port_number, updateHandler);

                        } catch (Exception e) {
                            Log.d(TAG, "errore generico");

                            String message = ("Connection error");
                            updateThreadUI(updateHandler, message);


                            e.printStackTrace();
                        }
                    }
                };
                connection_test.start();

            }
        });

        loadData_2();
    }

    // Carico le "shared preferences" nell'activityTwo
    public void loadData_2() {
        String set_addr = sharedPreferences_activityTwo.getString(SP2_ADDRESS, "");
        String set_port = sharedPreferences_activityTwo.getString(SP2_PORT, "");
        is_checkBox2_selected = sharedPreferences_activityTwo.getBoolean(CHECKBOX_2, false);

        editTextServerAddress.setText(set_addr);
        editTextServerPort.setText(set_port);
        checkBox2.setChecked(is_checkBox2_selected);
    }


    private Class executeConnectionTest(String txt_username, String txt_password, String server_address, int port_number, Handler updateHandler) {
        try {
            JSch jsch=new JSch();
            Session session=jsch.getSession(txt_username, server_address, port_number);
            session.setPassword(txt_password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(10000);     // Timeout di connessione

            String message = ("Connection established");
            updateThreadUI(updateHandler, message);

            session.disconnect();

            // Go to command activity
            Intent goToActivityThree = new Intent(getApplicationContext(), ActivityThree.class);
            startActivity(goToActivityThree);


        } catch (com.jcraft.jsch.JSchException e) {
            String message = ("Host is unreachable");
            updateThreadUI(updateHandler, message);
            e.printStackTrace();
        } catch (Exception ex) {
            Log.d(TAG, "generico");
            ex.printStackTrace();
        }
        return null;
    }

    private void updateThreadUI(Handler updateHandler, final String message) {
        updateHandler.post(new Runnable() {
            @Override
            public void run() {
                showErr.setText(message);
            }
        });
    }
}