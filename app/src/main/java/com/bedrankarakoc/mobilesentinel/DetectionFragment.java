package com.bedrankarakoc.mobilesentinel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetectionFragment extends Fragment {


    private View view;
    private String filename;
    private Button startDetectionButton;
    private Button stopDetectionButton;
    private TextView detectionProgressText;
    private TextView cellStatusText;
    private TextView isVolteEnabledText;
    private TelephonyManager telephonyManager;
    private ProgressBar progressBar;
    private TelecomManager telecomManager;
    private String phoneNumber = "";
    ArrayList<LogPacket> packetList;
    private volatile boolean stopDetection = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // TODO: Pay attention to Fragment Lifecycle https://developer.android.com/guide/components/fragments.html#Creating
    @Override
    public void onStart() {
        super.onStart();
        if (isVolteEnabled(telephonyManager) == true) {
            isVolteEnabledText.setText("isVolteEnabled : True");
            isVolteEnabledText.setTextColor(Color.GREEN);
        } else {
            isVolteEnabledText.setText("isVolteEnabled : False");
            isVolteEnabledText.setTextColor(Color.RED);
        }


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.detection_fragment, container, false);
        startDetectionButton = (Button) view.findViewById(R.id.start_detection_button);
        stopDetectionButton = (Button) view.findViewById(R.id.stop_detection_button);
        stopDetectionButton.setClickable(false);
        detectionProgressText = (TextView) view.findViewById(R.id.detectionProgressText);
        cellStatusText = (TextView) view.findViewById(R.id.cellStatusTextView);
        isVolteEnabledText = (TextView) view.findViewById(R.id.volteStatusTextView);
        progressBar = view.findViewById(R.id.detectionProgress);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.animate();
        detectionProgressText.setVisibility(View.INVISIBLE);
        progressBar.setMax(32);
        progressBar.setScaleY(3f);
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
                startDetectionButton.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
                filename = sdf.format(new Date());

                Python py = Python.getInstance();
                PyObject pyf = py.getModule("setup_parser");
                pyf.callAttr("start_logging", filename);
                stopDetectionButton.setClickable(true);

                progressBar.setVisibility(View.VISIBLE);
                detectionProgressText.setVisibility(View.VISIBLE);
                detectionProgressText.setText("Starting Detection ...");

                stopDetection = false;

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < 32; i++) {

                            progressBar.setProgress(i + 1, true);


                            if (stopDetection == true) {


                                progressBar.setProgress(0, true);

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
                                progressBar.setProgress(16);

                                // TODO: Dirty
                                String qmdlFilename = "";

                                for (File f : baseDir.listFiles()) {
                                    if (f.getName().endsWith(".qmdl")) {
                                        qmdlFilename = f.getName();
                                    }
                                }


                                pyf.callAttr("initiate_parsing", packetList, filename, qmdlFilename, cellStatusText);
                                System.out.println("Parsing finished");
                                progressBar.setProgress(32);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        detectionProgressText.setText("Detection Run finished");
                                        detectionProgressText.setTextColor(Color.GREEN);

                                    }
                                });
                                startDetectionButton.setClickable(true);

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

                stopDetectionButton.setClickable(false);
                stopDetection = true;
                detectionProgressText.setTextColor(Color.RED);
                detectionProgressText.setText("Stopping Detection ...");


            }
        });
    }
    // Use reflection to access method with @UnsupportedAppUsage tag
    public boolean isVolteEnabled(TelephonyManager telephonyManager) {
        boolean isVolteEnabled = false;
        try {

            Method method = telephonyManager.getClass().getMethod("isVolteAvailable");
            isVolteEnabled = (boolean) method.invoke(telephonyManager);


        } catch (Exception e) {
            e.printStackTrace();

        }
        return isVolteEnabled;

    }
}
