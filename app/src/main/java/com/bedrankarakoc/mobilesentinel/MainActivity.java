package com.bedrankarakoc.mobilesentinel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;



public class MainActivity extends AppCompatActivity {

    // UI Elements
    private Button testButton;


    private String[] permissions = {"android.permissions.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permissions.READ_PHONE_STATE"};
    private int requestCode = 1337;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(permissions, requestCode);
        stopLoggingButtonListener();


        if (!Python.isStarted())
            Python.start(new AndroidPlatform(this));


    }


    // Button Listeners
    public void stopLoggingButtonListener() {
        testButton = (Button) findViewById(R.id.testButton);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





            }
        });


    }





}
