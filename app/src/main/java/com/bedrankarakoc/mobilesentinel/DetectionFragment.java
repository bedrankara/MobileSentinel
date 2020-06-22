package com.bedrankarakoc.mobilesentinel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetectionFragment extends Fragment {


    private View view;
    private String filename;
    private Button startDetectionButton;
    private Button stopDetectionButton;
    private TelephonyManager telephonyManager;
    private TelecomManager telecomManager;
    private String phoneNumber = "";
    ArrayList<LogPacket> packetList;
    private volatile boolean stopDetection = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.detection_fragment, container, false);
        startDetectionButton = (Button) view.findViewById(R.id.start_detection_button);
        stopDetectionButton = (Button) view.findViewById(R.id.stop_detection_button);
        startDetectionButtonListener();
        stopDetectionButtonListener();
        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        telecomManager = (TelecomManager) getActivity().getSystemService(Context.TELECOM_SERVICE);
        packetList = new ArrayList<>();

        return view;
    }


    public void startDetectionButtonListener() {
        startDetectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
                filename = sdf.format(new Date());

                Python py = Python.getInstance();
                PyObject pyf = py.getModule("setup_parser");
                pyf.callAttr("start_logging", filename);

                stopDetection = false;

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < 32; i++) {

                            if (stopDetection == true) {
                                System.out.println("Reached stop detection");

                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Python py = Python.getInstance();
                                PyObject pyf = py.getModule("setup_parser");
                                pyf.callAttr("stop_logging");

                                try {
                                    Thread.sleep(4000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

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
                                System.out.println("Parsing finished");

                                break;
                            }

                            try {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + phoneNumber));
                                startActivity(intent);
                            } catch (SecurityException s) {
                                s.printStackTrace();
                            }

                            try {

                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                            telecomManager.endCall();

                            try {

                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }).start();


            }
        });
    }

    public void stopDetectionButtonListener() {
        stopDetectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDetection = true;

                /*new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Python py = Python.getInstance();
                        PyObject pyf = py.getModule("setup_parser");
                        pyf.callAttr("stop_logging");

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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("Parsing finished");
                            }
                        });

                    }
                }

                ).start();*/
            }
        });
    }
}
