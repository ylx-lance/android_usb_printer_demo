package com.seu601.android_usb_printer_demo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.print.*;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by wubin on 2017-05-04.
 */

public class downloadActivity extends Activity {
    WebView show;
    EditText txt;
    String web;
    PopupMenu popup;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download);
        show = (WebView) findViewById(R.id.webshow);
        txt=(EditText)findViewById(R.id.url);
        Button Bnt=(Button)findViewById(R.id.search);
        Bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                web=txt.getText().toString();
                txt.setText(web);
                show.loadUrl(web);
            }
        });

        show.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                onPopupButtonClick(show);
                return true;

            }
        });
    }
    public void onPopupButtonClick(View button)
    {
        // 创建PopupMenu对象
        popup = new PopupMenu(this, button);
        // 将R.menu.popup_menu菜单资源加载到popup菜单中
        getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        // 为popup菜单的菜单项单击事件绑定事件监听器
        popup.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        Toast.makeText(downloadActivity.this,
                                "您单击了【" + item.getTitle() + "】菜单项"
                                , Toast.LENGTH_SHORT).show();
                        createPdfFromCurrentScreen(popup);
                        return true;
                    }
                });
        popup.show();
    }
    public void createPdfFromCurrentScreen(PopupMenu v)
    {
        // Get the directory for the app's private pictures directory.
        final File file = new File(
                Environment.getExternalStorageDirectory(), "demo.pdf");

        if (file.exists ()) {
            file.delete ();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);

            PdfDocument document = new PdfDocument();
            Point windowSize = new Point();
            getWindowManager().getDefaultDisplay().getSize(windowSize);
            PdfDocument.PageInfo pageInfo =
                    new PdfDocument.PageInfo.Builder(
                            //         			windowSize.x, windowSize.y, 1).create();
                            595,842,1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            //View content = getWindow().getDecorView();
            Picture content=show.capturePicture();
            Bitmap bmp=Bitmap.createBitmap(content.getWidth(),content.getHeight(),Bitmap.Config.ARGB_8888);
            Canvas canvas=new Canvas(bmp);
            content.draw(canvas);
            Bitmap A4bmp=Bitmap.createScaledBitmap(bmp,595,842,true);
           // content.draw(page.getCanvas());
            page.getCanvas().drawBitmap(A4bmp,0,0,null);
            page.getCanvas().save();
            document.finishPage(page);
            document.writeTo(out);
            document.close();
            out.flush();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(downloadActivity.this, "File created: "+file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.d("TAG_PDF", "File was not created: "+e.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
