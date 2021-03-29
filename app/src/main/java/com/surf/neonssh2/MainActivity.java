package com.surf.neonssh2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";

    private TextView titleTextView, textViewWarn;
    private EditText editTextUsername, editTextPassword;
    private Button connectButton;
    private CheckBox checkBox;

    // Shared preferences
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SP_USERNAME = "username";
    public static final String SP_PASSWORD = "password";

    public static final String CHECKBOX = "checkbox";
    private boolean is_checkBox_selected;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inizializzazione delle "shared preferences"
        sharedPreferences = getSharedPreferences(SHARED_PREFS, 0);

        // Layout
        titleTextView = (TextView) findViewById(R.id.text);
        textViewWarn = (TextView) findViewById(R.id.text_view_warn);
        editTextUsername = (EditText) findViewById(R.id.edit_text_Username);
        editTextPassword = (EditText) findViewById(R.id.edit_text_Password);
        connectButton = (Button) findViewById(R.id.bt_Connect);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SP_USERNAME, editTextUsername.getText().toString());
                    editor.putString(SP_PASSWORD, editTextPassword.getText().toString());
                    editor.putBoolean(CHECKBOX, checkBox.isChecked());
                    editor.apply();
                } else {
                    editTextUsername.setText("");
                    editTextPassword.setText("");

                    // Cancelloi valori memorizzati e li salvo come vuoti
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SP_USERNAME, "");
                    editor.putString(SP_PASSWORD, "");
                    editor.putBoolean(CHECKBOX, checkBox.isChecked());
                    editor.apply();
                }
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread button_click_thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            
                            if (mBluetoothAdapter.isEnabled()) {
                                Intent intentConnect = new Intent(getApplicationContext(), Connectivity.class);
                                intentConnect.putExtra("EXTRA_USERNAME_ID", editTextUsername.getText().toString());
                                intentConnect.putExtra("EXTRA_PASSWORD_ID", editTextPassword.getText().toString());
                                startActivity(intentConnect);

                            } else {
                                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                                if (mWifi.isConnected()) {
                                    Intent intent = new Intent(getApplicationContext(), ActivityTwo.class);
                                    intent.putExtra("EXTRA_USERNAME_ID", editTextUsername.getText().toString());
                                    intent.putExtra("EXTRA_PASSWORD_ID", editTextPassword.getText().toString());
                                    startActivity(intent);
                                } else {
                                    textViewWarn.setText(R.string.Wifi_is_off);
                                }
                            }

                        } catch (Exception e) {
                            Log.d(TAG, "run: EXCAPTION!!!");
                            e.printStackTrace();
                        }
                    }
                };
                button_click_thread.start();
            }
        });

    loadData();
    }

    // Carico le "shared preferences" nell'activity
    public void loadData() {
        String set_username = sharedPreferences.getString(SP_USERNAME, "");
        String set_password = sharedPreferences.getString(SP_PASSWORD, "");
        is_checkBox_selected = sharedPreferences.getBoolean(CHECKBOX, false);

        editTextUsername.setText(set_username);
        editTextPassword.setText(set_password);
        checkBox.setChecked(is_checkBox_selected);
    }
}