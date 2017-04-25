package com.seu601.android_usb_printer_demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.seu601.android_usb_printer_demo.Tasks.InitTask;

public class MainActivity extends AppCompatActivity {
    public static MainActivity instance;
    private SharedPreferences preferences;
    private ImageView isInitImg;
    private ImageView printerAttachImg;
    public Boolean isInit;//系统资源是否初始化,包括（GS,F002，驱动文件/sdcard/usb_printer_tools/drivers)
    public Boolean driverLoad;//打印机驱动是否加载


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isInitImg = (ImageView) findViewById(R.id.isInit_Img);
        printerAttachImg = (ImageView) findViewById(R.id.printer_Attach_Img);
        ListView listView = (ListView) findViewById(R.id.printer_info_list);

        isInitImg.setImageResource(R.mipmap.yes2);
        printerAttachImg.setImageResource(R.mipmap.no);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, PrinterSupportted.printersupport);
        listView.setAdapter(adapter);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //初始化打印机需要的系统资源
        isInit = preferences.getBoolean("isInit", false);
        if (isInit == false) {
            isInitImg.setImageResource(R.mipmap.no);
            new InitTask(this).execute();
            isInit = preferences.getBoolean("isInit", false);
            if (isInit) isInitImg.setImageResource(R.mipmap.yes2);
        }

        instance = this;
    }


}

