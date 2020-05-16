package com.bedrankarakoc.mobilesentinel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import org.json.JSONObject;

public class PacketViewerActivity extends AppCompatActivity {

    private static String content;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packet_viewer);
        Bundle extras = getIntent().getExtras();
        textView = (TextView) findViewById(R.id.textView2);
        textView.setMovementMethod(new ScrollingMovementMethod());

        if (extras != null) {
            content = extras.getString("content");


        }

        try {
            JSONObject obj = new JSONObject(content);
            textView.setText(obj.toString(4));

        } catch (Throwable tx) {
            tx.printStackTrace();
            textView.setText("Malformed packet");
        }


    }
}
