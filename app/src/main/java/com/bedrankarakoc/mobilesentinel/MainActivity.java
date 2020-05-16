package com.bedrankarakoc.mobilesentinel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    ArrayList<LogPacket> packetList;
    private static LogAdapter adapter;

    // UI Elements
    private Button testButton;
    private ListView listView;

    // Permission elements
    private String[] permissions = {"android.permissions.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permissions.READ_PHONE_STATE"};
    private int requestCode = 1337;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(permissions, requestCode);
        stopLoggingButtonListener();

        listView = (ListView) findViewById(R.id.listView);
        packetList = new ArrayList<>();
        adapter = new LogAdapter(packetList, MainActivity.this);
        listView.setAdapter(adapter);

        if (!Python.isStarted())
            Python.start(new AndroidPlatform(this));


    }

    // Button Listeners
    public void stopLoggingButtonListener() {
        testButton = (Button) findViewById(R.id.testButton);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Python py = Python.getInstance();
                PyObject pyf = py.getModule("setup_parser");
                PyObject obj = pyf.callAttr("initiate_parsing", packetList);
                listView.setAdapter(adapter);


            }
        });


    }


}
