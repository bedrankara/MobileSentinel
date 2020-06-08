package com.bedrankarakoc.mobilesentinel;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class LoggingFragment extends Fragment {

    private Button testButton;
    private ListView listView;

    // Setup
    ArrayList<LogPacket> packetList;
    private static LogAdapter adapter;
    Context mContext;
    private File sdcard;

    View view;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.logging_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        stopLoggingButtonListener();
        mContext = getActivity();
        sdcard = Environment.getExternalStorageDirectory();
        System.out.println(sdcard);
        if (!Python.isStarted())
            Python.start(new AndroidPlatform(mContext));
        packetList = new ArrayList<>();
        adapter = new LogAdapter(packetList, mContext);
        listView.setAdapter(adapter);
        createConfig();
        return view;
    }


    public void stopLoggingButtonListener() {
        testButton = (Button) view.findViewById(R.id.testButton);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
                final String filename = sdf.format(new Date());

                Python py = Python.getInstance();
                PyObject pyf = py.getModule("setup_parser");
                pyf.callAttr("initiate_logging", filename);

                File baseDir = new File(Environment.getExternalStorageDirectory() + "/logs/" + filename);
                System.out.println(baseDir);

                // TODO: Dirty
                String qmdlFilename = "";

                for (File f : baseDir.listFiles()) {
                    if (f.getName().endsWith(".qmdl")) {
                        qmdlFilename = f.getName();
                    }
                }

                pyf.callAttr("initiate_parsing", packetList, filename, qmdlFilename);
                listView.setAdapter(adapter);


            }
        });


    }

    // Create logging config files (from raw resources) to external storage
    public void createConfig() {
        String configDir = "/logs";
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.full_diag);
        String filename = mContext.getResources().getResourceEntryName(R.raw.full_diag);

        File f = new File(filename + ".cfg");
        try {
            OutputStream out = new FileOutputStream(new File(sdcard + configDir, filename));
            byte[] buffer = new byte[4096 * 2];
            int len;
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
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
