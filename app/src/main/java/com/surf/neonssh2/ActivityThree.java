package com.surf.neonssh2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;

public class ActivityThree extends Activity {

    public static final String TAG = "ActivityThree";

    private EditText et;
    private Button bt;
    private ListView lv;

    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);

        et = (EditText) findViewById(R.id.insert_cmd);
        bt = (Button) findViewById(R.id.btn_insert_cmd);
        lv = (ListView) findViewById(R.id.list_item);

        arrayList = new ArrayList<String>();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selected = adapter.getItem(position);
                //Log.d(TAG, "SELECTED=" + selected);

                Intent intentExecute = new Intent(getApplicationContext(), ActivityExecute.class);
                intentExecute.putExtra("EXECUTE_COMMAND", selected);
                startActivity(intentExecute);

            }
        });

        onBtnClick();
        retrieveSavedArrayList();
    }

    // Retrieve prefs array saved data and RELOAD ListView in Activity
    public void retrieveSavedArrayList() {
        arrayList = ActivityThreePreferences.getArrayPrefs("OrderList", ActivityThree.this);
        Log.d(TAG, "Saved ARRAYList retrieved: " + arrayList);

        adapter = new ArrayAdapter<String>(ActivityThree.this,
                android.R.layout.simple_list_item_1,
                arrayList);

        lv.setAdapter(adapter);
    }

    // Item selected in ListView
    public void onBtnClick() {
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command_name = et.getText().toString();

                if (!command_name.equals("")) {
                    arrayList.add(command_name);
                    adapter.notifyDataSetChanged();

                    ActivityThreePreferences.setArrayPrefs("OrderList", arrayList, getApplicationContext());
                    Log.d(TAG, "ADDED: " + command_name);
                    Log.d(TAG, "ARRAY UPDATED: " + arrayList);

                    et.setText("");

                }
            }
        });
    }

    public void onBackPressed() {
        Intent back = new Intent(getApplicationContext(), ActivityTwo.class);
        startActivity(back);
    }
}