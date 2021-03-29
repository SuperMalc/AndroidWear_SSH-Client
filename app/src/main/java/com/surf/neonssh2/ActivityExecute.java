package com.surf.neonssh2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

public class ActivityExecute extends Activity {

    public static final String TAG = "ActivityExecute";

    private TextView mTextView;
    private String retrieveCommand;
    private Button btnExecute, btnBack, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execute);

        mTextView = (TextView) findViewById(R.id.text);
        btnExecute = (Button) findViewById(R.id.execute_btn);
        btnBack = (Button) findViewById(R.id.back_btn);
        btnDelete = (Button) findViewById(R.id.delete_btn);

        retrieveCommand = getIntent().getStringExtra("EXECUTE_COMMAND");
        mTextView.setText(retrieveCommand);

        btnExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentExecuteEnd = new Intent(getApplicationContext(), ActivityEnd.class);
                intentExecuteEnd.putExtra("EXECUTE_COMMAND", retrieveCommand);
                startActivity(intentExecuteEnd);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: CLICKED");

                ArrayList<String> arrayList;
                arrayList = ActivityThreePreferences.getArrayPrefs("OrderList", getApplicationContext());

                boolean checkString = arrayList.contains(retrieveCommand);

                if (checkString) {

                    int indexNumber = getCategoryPos(retrieveCommand, arrayList);
                    Log.d(TAG, "index of " + retrieveCommand + " is: " + indexNumber);

                    arrayList.remove(indexNumber);
                    Log.d(TAG, "NEW ARRAY LIST: " + arrayList);

                    ActivityThreePreferences.setArrayPrefs("OrderList", arrayList, getApplicationContext());

                    Intent back = new Intent(getApplicationContext(), ActivityThree.class);
                    startActivity(back);


                } else {
                    Log.d(TAG, "NO");
                }
                
                /*

                ArrayList<String> arrayList;
                arrayList = ActivityThreePreferences.getArrayPrefs("OrderList", getApplicationContext());
                Log.d(TAG, "retrieved: " + arrayList);

                //Check se lo contiene
                boolean checkString = arrayList.contains(retrieveCommand);

                if (checkString) {
                    Log.d(TAG, "YES");
                } else {
                    Log.d(TAG, "NO");
                }

                //ActivityThreePreferences.remArrayPrefs("OrderList",  ,getApplicationContext());




                //ActivityThreePreferences.remArrayPrefs("OrderList", , this);
                
                 */

            }
        });



    }

    public int getCategoryPos(String category, ArrayList<String> arrayList) {
        return arrayList.indexOf(category);
    }
}

