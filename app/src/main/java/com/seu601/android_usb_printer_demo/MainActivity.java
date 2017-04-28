package com.seu601.android_usb_printer_demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.seu601.android_usb_printer_demo.Tasks.InitTask;
import com.seu601.android_usb_printer_demo.util.PrinterUtil;

public class MainActivity extends AppCompatActivity {
    public static MainActivity instance;
    private SharedPreferences preferences;
    public static ImageView isInitImg;
    private ImageView printerAttachImg;
    private ImageView driverLoadImg;
    private TextView printerIdText;
    public Boolean isInit=false;//系统资源是否初始化,包括（GS,F002，驱动文件/sdcard/usb_printer_tools/drivers)
    public Boolean isRoot=false;//应用是否获得Root权限
    public Boolean driverLoad=false;//打印机驱动是否加载
    public static final int USB_PRINTER_ATTACH = 1;
    public static final int USB_PRINTER_DETACH = 2;
    public static final int USB_PRINTER_DRIVERLOAD = 3;
    public static final int USB_PRINTER_UNSUPPORTED= 4;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case USB_PRINTER_ATTACH:
                    printerAttachImg.setImageResource(R.mipmap.yes2);
                    printerIdText.setText((String) msg.obj);
                    break;
                case USB_PRINTER_DETACH:
                    printerAttachImg.setImageResource(R.mipmap.no);
                    driverLoadImg.setImageResource(R.mipmap.no);
                    printerIdText.setText("无打印机");
                    driverLoad = false;
                    break;
                case USB_PRINTER_DRIVERLOAD:
                    driverLoadImg.setImageResource(R.mipmap.yes2);
                    driverLoad = true;
                    break;
                case USB_PRINTER_UNSUPPORTED:
                    driverLoadImg.setImageResource(R.mipmap.no);
                    driverLoad = false;
                    break;

                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isRoot = PrinterUtil.getInstance().upgradeRootPermission(getPackageCodePath());
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isInitImg = (ImageView) findViewById(R.id.isInit_Img);
        printerAttachImg = (ImageView) findViewById(R.id.printer_Attach_Img);
        driverLoadImg = (ImageView) findViewById(R.id.diverLoadImg);
        printerIdText = (TextView) findViewById(R.id.printerId);
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
        }

    }


}

