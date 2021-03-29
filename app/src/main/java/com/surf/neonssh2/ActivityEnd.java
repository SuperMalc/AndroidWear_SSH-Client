package com.surf.neonssh2;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;

import static com.surf.neonssh2.ActivityTwo.SHARED_PREFS_TWO;
import static com.surf.neonssh2.ActivityTwo.SP2_ADDRESS;
import static com.surf.neonssh2.ActivityTwo.SP2_PORT;
import static com.surf.neonssh2.MainActivity.SHARED_PREFS;
import static com.surf.neonssh2.MainActivity.SP_PASSWORD;
import static com.surf.neonssh2.MainActivity.SP_USERNAME;

public class ActivityEnd extends Activity {

    public static final String TAG = "ActivityEnd";


    // Shared preferences
    SharedPreferences sharedPreferences_from_activity_main;
    SharedPreferences sharedPreferences_from_activity_two;

    Handler handlerUI;

    private TextView tv, tv2;
    public String retrieveCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        handlerUI = new Handler();

        // Ottengo le shared prefs dalla MainActivity
        sharedPreferences_from_activity_main = getSharedPreferences(SHARED_PREFS, 0);
        sharedPreferences_from_activity_two = getSharedPreferences(SHARED_PREFS_TWO, 0);

        retrieveCommand = getIntent().getStringExtra("EXECUTE_COMMAND");
        Log.d(TAG, "now executing: " + retrieveCommand);

        tv = (TextView) findViewById(R.id.tv_results);
        tv2 = (TextView) findViewById(R.id.tv_results2);

        tv.setText(retrieveCommand);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {

                    // From MainActivity preferences
                    String username = sharedPreferences_from_activity_main.getString(SP_USERNAME, "");
                    String password = sharedPreferences_from_activity_main.getString(SP_PASSWORD, "");

                    String address = sharedPreferences_from_activity_two.getString(SP2_ADDRESS, "");
                    String port = sharedPreferences_from_activity_two.getString(SP2_PORT, "");

                    int intPort = Integer.parseInt(port);

                    Log.d(TAG, "USER=" + username + " PSW=" + password + " ADDRESS=" + address + " PORT=" + intPort + " COMMAND=" + retrieveCommand);

                    executeRemoteCommand(username, password, address, intPort, retrieveCommand, handlerUI);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    public String executeRemoteCommand(String username, String password, String hostname, int port, String command, Handler handlerUI) throws Exception {

        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);

        session.connect();



        // SSH Channel
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setInputStream(null);
        channel.setErrStream(System.err);
        InputStream in = channel.getInputStream();
        channel.setCommand(command);
        channel.connect();

        byte[] tmp=new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                //Log.d(TAG, "READ!");
                if (i < 0) {
                    break;
                }


                String message = new String(tmp, 0, i);

                updateThreadUI(handlerUI, message);

                //Log.d(TAG, "> " + new String(tmp, 0, i));
                //System.out.print(new String(tmp, 0, i));
            }

            if (channel.isClosed()) {
                if (in.available() > 0) {
                    continue;
                }
                System.out.println("exit-status: "+channel.getExitStatus());
                break;
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        channel.setOutputStream(baos);

        // Execute command


        channel.disconnect();
        session.disconnect();

        return baos.toString();
    }

    private void updateThreadUI(Handler handlerUI, final String message) {
        handlerUI.post(new Runnable() {
            @Override
            public void run() {
                tv2.append(message);
            }
        });
    }
}