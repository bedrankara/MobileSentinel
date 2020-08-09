package com.bedrankarakoc.mobilesentinel;

import com.bedrankarakoc.mobilesentinel.BaseStationLTE;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

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
    private TextView cellIDtextview;
    private TextView TACtextview;
    private TextView PLMNtextview;
    private TelephonyManager telephonyManager;
    private ProgressBar progressBar;
    private TelecomManager telecomManager;
    private String phoneNumber;
    public int isVulnerable = 0;
    private volatile boolean nextIntervall = false;
    ArrayList<LogPacket> packetList;
    private volatile boolean stopDetection = false;
    private BaseStationLTE baseStationLTE = new BaseStationLTE();


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
        updateCellParameters();


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize UI elements and properties
        view = inflater.inflate(R.layout.detection_fragment, container, false);
        startDetectionButton = (Button) view.findViewById(R.id.start_detection_button);
        stopDetectionButton = (Button) view.findViewById(R.id.stop_detection_button);
        stopDetectionButton.setClickable(false);
        detectionProgressText = (TextView) view.findViewById(R.id.detectionProgressText);
        cellStatusText = (TextView) view.findViewById(R.id.cellStatusTextView);
        isVolteEnabledText = (TextView) view.findViewById(R.id.volteStatusTextView);
        cellIDtextview = (TextView) view.findViewById(R.id.cellIDtextView);
        TACtextview = (TextView) view.findViewById(R.id.TACtextview);
        PLMNtextview = (TextView) view.findViewById(R.id.PLMNtextview);
        progressBar = view.findViewById(R.id.detectionProgress);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.animate();
        detectionProgressText.setVisibility(View.INVISIBLE);
        progressBar.setMax(32);
        progressBar.setScaleY(3f);
        startDetectionButtonListener();
        stopDetectionButtonListener();
        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        baseStationLTE.setTelephonyManager(telephonyManager);
        updateCellParameters();
        telecomManager = (TelecomManager) getActivity().getSystemService(Context.TELECOM_SERVICE);
        packetList = new ArrayList<>();

        return view;
    }

    public void updateCellParameters() {
        baseStationLTE.bindServingCellParameter();
        cellIDtextview.setText(("Cell ID: " + String.valueOf(baseStationLTE.getCid())));
        TACtextview.setText("TAC: " + String.valueOf(baseStationLTE.getTac()));
        PLMNtextview.setText("PLMN: " + String.valueOf(baseStationLTE.getMcc()) + String.valueOf(baseStationLTE.getMnc()) );
    }

    public void startDetectionRun(final int intervall) {

        // UI elements can only be changed on UI thread

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startDetectionButton.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        // DIAG log filename as current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
        filename = sdf.format(new Date());


        Python py = Python.getInstance();
        PyObject pyf = py.getModule("setup_parser");
        pyf.callAttr("start_logging", filename);
        stopDetectionButton.setClickable(true);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                detectionProgressText.setVisibility(View.VISIBLE);

            }
        });


        stopDetection = false;

        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < intervall+1; i++) {

                    //progressBar.setProgress(i + 1, true);
                    progressBar.incrementProgressBy(1);


                    if (stopDetection == true || i == intervall) {




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


                        pyf.callAttr("initiate_parsing", packetList, filename, qmdlFilename, cellStatusText, isVulnerable);
                        System.out.println("Parsing finished");


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //detectionProgressText.setText("Detection Run finished");
                                detectionProgressText.setTextColor(Color.GREEN);
                                nextIntervall = true;
                            }
                        });
                        startDetectionButton.setClickable(true);

                        break;
                    }

                    try {
                        // Start the test call
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + phoneNumber));
                        startActivity(intent);
                    } catch (SecurityException s) {
                        s.printStackTrace();
                    }

                    try {
                        // Call duration 4 seconds, should be enough to catch the bearer
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // End the test call
                    telecomManager.endCall();

                    try {
                        // Wait 3 seconds untill next call so users can click on the abort button if needed
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }

    public void startDetectionButtonListener() {
        startDetectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SHARED_PREFERENCES", Context.MODE_PRIVATE);
                phoneNumber = sharedPreferences.getString("CALL_NUMBER", "+4915792389038");
                updateCellParameters();
                progressBar.setProgress(0);
                cellStatusText.setText("Cell: Test Running");
                detectionProgressText.setTextColor(Color.GREEN);
                detectionProgressText.setText("Detection Running");
                System.out.println(isVulnerable);
                if (isVolteEnabled(telephonyManager) == true) {
                    isVolteEnabledText.setText("isVolteEnabled : True");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startDetectionRun(4);
                            while (true) {
                                if (nextIntervall) {
                                    if (cellStatusText.getText().toString().matches("Cell: VULNERABLE")) {
                                        System.out.println("VULN YEEEESSS");
                                        nextIntervall = false;
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                //detectionProgressText.setText("Detection Run finished");
                                                detectionProgressText.setTextColor(Color.GREEN);
                                                detectionProgressText.setText("Detection Run finished");
                                                progressBar.setProgress(progressBar.getMax());
                                                Toast.makeText(getActivity(), "Vulnerable eNodeB", Toast.LENGTH_LONG).show();

                                            }
                                        });
                                        break;
                                    } else {
                                        startDetectionRun(29);
                                        nextIntervall = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }).start();
                } else {
                    isVolteEnabledText.setText("isVolteEnabled : False");
                    Toast.makeText(getActivity(), "VoLTE appears to be not enabled, check your carrier configs", Toast.LENGTH_LONG).show();

                }








            }
        });
    }

    public void stopDetectionButtonListener() {
        stopDetectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                detectionProgressText.setTextColor(Color.RED);
                detectionProgressText.setText("Stopping Detection");
                cellStatusText.setText("Cell Status : Not Tested");
                progressBar.setProgress(progressBar.getMax());
                stopDetectionButton.setClickable(false);
                stopDetection = true;
                detectionProgressText.setText("Detection Stopped");



            }
        });
    }
    // Use reflection to access method with @UnsupportedAppUsage tag
    // Returns wether VoLTE is enabled or not, may not be 100% reliable
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
