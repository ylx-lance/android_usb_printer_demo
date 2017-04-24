package com.seu601.android_usb_printer_demo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.seu601.android_usb_printer_demo.util.PrinterUtil;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private ImageView isInitImg;
    private ImageView printerAttachImg;
    private Boolean isInit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isInitImg = (ImageView) findViewById(R.id.isInit_Img);
        printerAttachImg = (ImageView) findViewById(R.id.printer_Attach_Img);

        isInitImg.setImageResource(R.mipmap.yes2);
        printerAttachImg.setImageResource(R.mipmap.no);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //初始化打印机需要的系统资源
        isInit = preferences.getBoolean("isInit", false);
        if (isInit == false) {
            isInitImg.setImageResource(R.mipmap.no);
            new InitTask(this).execute();
            isInit = preferences.getBoolean("isInit", false);
            if (isInit) isInitImg.setImageResource(R.mipmap.yes2);
        }

    }


}

