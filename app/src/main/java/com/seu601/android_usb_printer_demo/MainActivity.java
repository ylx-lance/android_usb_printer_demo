package com.seu601.android_usb_printer_demo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.seu601.android_usb_printer_demo.util.PrinterUtil;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //初始化打印机需要的系统资源
        Boolean isInit = preferences.getBoolean("isInit", false);
        if (isInit == false) {new InitTask(this).execute();}

    }

}

