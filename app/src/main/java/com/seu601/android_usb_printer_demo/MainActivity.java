package com.seu601.android_usb_printer_demo;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.seu601.android_usb_printer_demo.Dialog.CallbackBundle;
import com.seu601.android_usb_printer_demo.Dialog.OpenFileDialog;
import com.seu601.android_usb_printer_demo.Tasks.IdentifyUSBDeviceService;
import com.seu601.android_usb_printer_demo.Tasks.InitTask;
import com.seu601.android_usb_printer_demo.Tasks.PrintIntentService;
import com.seu601.android_usb_printer_demo.util.PrinterUtil;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static MainActivity instance;

    public static ImageView isInitImg;
    private ImageView printerAttachImg;
    private ImageView driverLoadImg;
    private TextView printerIdText;

    public Boolean isInit = false;//系统资源是否初始化,包括（GS,F002，驱动文件/sdcard/usb_printer_tools/drivers)
    public Boolean isRoot = false;//应用是否获得Root权限
    public Boolean driverLoad = false;//打印机驱动是否加载

    private Button chooseFileButton;
    private Button printFileButton;
    private Button previewButton;
    private TextView fileNameText;


    private static final int CHOOSEFILE_DIALOG_ID = 511;


    public static final int USB_PRINTER_ATTACH = 1;
    public static final int USB_PRINTER_DETACH = 2;
    public static final int USB_PRINTER_DRIVERLOAD = 3;
    public static final int USB_PRINTER_UNSUPPORTED = 4;
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
                    printFileButton.setEnabled(false);
                    break;
                case USB_PRINTER_DRIVERLOAD:
                    driverLoadImg.setImageResource(R.mipmap.yes2);
                    driverLoad = true;
                    break;
                case USB_PRINTER_UNSUPPORTED:
                    driverLoadImg.setImageResource(R.mipmap.no);
                    driverLoad = false;
                    printFileButton.setEnabled(false);
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

        chooseFileButton = (Button) findViewById(R.id.choosfile_button);
        printFileButton = (Button) findViewById(R.id.printfile_button);
        previewButton = (Button) findViewById(R.id.preview_button);
        fileNameText = (TextView) findViewById(R.id.filename_text);
        chooseFileButton.setOnClickListener(this);
        printFileButton.setOnClickListener(this);

        ListView listView = (ListView) findViewById(R.id.printer_info_list);
        isInitImg.setImageResource(R.mipmap.yes2);
        printerAttachImg.setImageResource(R.mipmap.no);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, PrinterSupportted.printersupport);
        listView.setAdapter(adapter);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //初始化打印机需要的系统资源
        isInit = preferences.getBoolean("isInit", false);
        if (isInit == false) {
            isInitImg.setImageResource(R.mipmap.no);
            new InitTask(this).execute();
        }

//        Intent intentService = new Intent(this, IdentifyUSBDeviceService.class);
//        startService(intentService);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choosfile_button:
                Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
                showDialog(CHOOSEFILE_DIALOG_ID);
                break;
            case R.id.printfile_button:
                Intent intentService = new Intent(this, PrintIntentService.class);
                intentService.putExtra("filename", fileNameText.getText().toString());
                intentService.putExtra("printerid", printerIdText.getText().toString());
                startService(intentService);
                break;
            case R.id.preview_button:
                break;
            default:
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case CHOOSEFILE_DIALOG_ID:
                Map<String, Integer> images = new HashMap<String, Integer>();
                // 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
                images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root);    // 根目录图标
                images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up);    //返回上一层的图标
                images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder);    //文件夹图标
                images.put("wav", R.drawable.filedialog_wavfile);    //wav文件图标
                images.put("pdf", R.drawable.filedialog_pdf);//pdf文件图标
                images.put(OpenFileDialog.sEmpty, R.drawable.filedialog_root);
                Dialog dialog = OpenFileDialog.createDialog(id, this, "打开PDF文件", new CallbackBundle() {
                            @Override
                            public void callback(Bundle bundle) {
                                String filepath = bundle.getString("path");
//                                setTitle(filepath); // 把文件路径显示在标题上
                                fileNameText.setText(filepath);
                                if (driverLoad) {
                                    printFileButton.setEnabled(true);
                                }
                            }
                        },
                        ".pdf;",
                        images);
                return dialog;
            default:
                return null;
        }

    }

}

