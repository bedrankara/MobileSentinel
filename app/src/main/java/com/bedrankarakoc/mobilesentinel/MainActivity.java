package com.bedrankarakoc.mobilesentinel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    // Setup
    ArrayList<LogPacket> packetList;
    private static LogAdapter adapter;
    Context mContext;
    private File sdcard;
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
        mContext = getApplicationContext();
        sdcard = Environment.getExternalStorageDirectory();
        System.out.println(sdcard);
        listView = (ListView) findViewById(R.id.listView);
        packetList = new ArrayList<>();
        adapter = new LogAdapter(packetList, MainActivity.this);
        listView.setAdapter(adapter);
        createConfig();

        if (!Python.isStarted())
            Python.start(new AndroidPlatform(this));




    }

    // Button Listeners
    public void stopLoggingButtonListener() {
        testButton = (Button) findViewById(R.id.testButton);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
                final String filename = sdf.format(new Date());

                Python py = Python.getInstance();
                PyObject pyf = py.getModule("setup_parser");
                pyf.callAttr("initiate_logging",filename);

                File baseDir = new File(Environment.getExternalStorageDirectory() + "/logs/" + filename);
                System.out.println(baseDir);

                // TODO: Dirty
                String qmdlFilename = "";

                for (File f : baseDir.listFiles()) {
                    if (f.getName().endsWith(".qmdl")) {
                        qmdlFilename = f.getName();
                    }
                }

                pyf.callAttr("initiate_parsing", packetList,filename,qmdlFilename);
                listView.setAdapter(adapter);



            }
        });


    }

    // Create logging config files (from raw resources) to external storage
    public void createConfig() {
        String configDir = "/logs";
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.full_diag);
        String filename = mContext.getResources().getResourceEntryName(R.raw.full_diag);

        File f = new File(filename);
        try {
            OutputStream out = new FileOutputStream(new File(sdcard+configDir, filename));
            byte[] buffer = new byte[4096*2];
            int len;
            while((len = inputStream.read(buffer, 0, buffer.length)) != -1){
                out.write(buffer, 0, len);
            }
            inputStream.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
