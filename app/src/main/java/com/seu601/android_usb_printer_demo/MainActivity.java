package com.seu601.android_usb_printer_demo;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.seu601.android_usb_printer_demo.Dialog.CallbackBundle;
import com.seu601.android_usb_printer_demo.Dialog.OpenFileDialog;
import com.seu601.android_usb_printer_demo.Tasks.IdentifyUSBDeviceService;
import com.seu601.android_usb_printer_demo.Tasks.InitTask;
import com.seu601.android_usb_printer_demo.Tasks.PrintIntentService;
import com.seu601.android_usb_printer_demo.View.DrawView;
import com.seu601.android_usb_printer_demo.util.PrinterUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static MainActivity instance;
//asdasd
//    public int useless = 0;
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
    private Button saveAsPDFButton;
    private EditText pdfFileNameText;


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

    //两种画笔效果
    private EmbossMaskFilter emboss; //模糊效果
    private BlurMaskFilter blur;     //浮雕效果

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
        saveAsPDFButton = (Button) findViewById(R.id.saveaspdf_button);
        pdfFileNameText = (EditText) findViewById(R.id.pdffilename_text);

        chooseFileButton.setOnClickListener(this);
        printFileButton.setOnClickListener(this);
        saveAsPDFButton.setOnClickListener(this);

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

        //初始化画笔效果
        emboss = new EmbossMaskFilter(new float[]
                {1.5f, 1.5f, 1.5f}, 0.6f, 6, 4.2f);
        blur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: 2017/5/4  
        getMenuInflater().inflate(R.menu.menu_draw, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawView dv = (DrawView) findViewById(R.id.draw);// ylxNOTICE (2017/5/3) Fragment中获取控件ID的方法
        switch (item.getItemId()) {
            case R.id.red:
                dv.paint.setColor(Color.RED);
                item.setChecked(true);
                break;
            case R.id.green:
                dv.paint.setColor(Color.GREEN);
                item.setChecked(true);
                break;
            case R.id.blue:
                dv.paint.setColor(Color.BLUE);
                item.setChecked(true);
                break;
            case R.id.yellow:
                dv.paint.setColor(Color.YELLOW);
                item.setChecked(true);
                break;
            case R.id.purple:
                dv.paint.setColor((getResources().getColor(R.color.purple)));
                item.setChecked(true);
                break;
            case R.id.orange:
                dv.paint.setColor((getResources().getColor(R.color.orange)));
                item.setChecked(true);
                break;
            case R.id.black:
                dv.paint.setColor(Color.BLACK);
                item.setChecked(true);
                break;
            case R.id.width_1:
                dv.paint.setStrokeWidth(1);
                item.setChecked(true);
                break;
            case R.id.width_3:
                dv.paint.setStrokeWidth(3);
                item.setChecked(true);
                break;
            case R.id.width_5:
                dv.paint.setStrokeWidth(5);
                item.setChecked(true);
                break;
            case R.id.width_7:
                dv.paint.setStrokeWidth(7);
                item.setChecked(true);
                break;
            case R.id.width_9:
                dv.paint.setStrokeWidth(9);
                item.setChecked(true);
                break;
            case R.id.blur:
                dv.paint.setMaskFilter(blur);
                break;
            case R.id.emboss:
                dv.paint.setMaskFilter(emboss);
                break;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
            case R.id.saveaspdf_button:
                // create a new document
                PdfDocument document = new PdfDocument();

                // crate a page description
                PdfDocument.PageInfo pageInfo = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    pageInfo = new PdfDocument.PageInfo.Builder(595,842, 1).create();
                }

                // start a page
                PdfDocument.Page page = document.startPage(pageInfo);

                // draw something on the page
                DrawView content =(DrawView) findViewById(R.id.draw);

                Canvas canvas = page.getCanvas();
//                canvas.rotate(90);
                canvas.drawBitmap(Bitmap.createScaledBitmap( content.cacheBitmap,595, 842,true), 0, 0, content.paint);
                canvas.save();
                // finish the page
                document.finishPage(page);
                // add more pages
                // write the document content
                FileOutputStream pdfFile = null;
                try {
                    pdfFile = new FileOutputStream("/storage/sdcard0/usb_printer_tools/pdf_tmp.pdf");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (pdfFileNameText.getText().toString() == null) {
                    try {
                        pdfFile = new FileOutputStream("/storage/sdcard0/usb_printer_tools/pdf_tmp.pdf");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        pdfFile = new FileOutputStream("/storage/sdcard0/usb_printer_tools/"+pdfFileNameText.getText().toString()+".pdf");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                try {
                    document.writeTo(pdfFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                document.close();
                //close the document
                Toast.makeText(this, "pdf saved", Toast.LENGTH_SHORT).show();
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

