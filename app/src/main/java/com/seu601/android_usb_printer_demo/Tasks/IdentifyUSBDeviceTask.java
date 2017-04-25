package com.seu601.android_usb_printer_demo.Tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import com.seu601.android_usb_printer_demo.R;
import com.seu601.android_usb_printer_demo.util.PrinterUtil;

/**
 * Created by ylx on 2017/4/25.
 * 系统检测到USB设备插入后，由此任务检测插入的设备是不是USB打印机
 * 如果是，则在主界面显示检测到打印机 并输出相应的打印机型号
 * 如果不是,则显示没有检测到打印机
 */

public class IdentifyUSBDeviceTask extends AsyncTask<Void, Void, String> {

    private ProgressDialog progressDialog;
    private Activity mactivity;
    private Boolean mattach;

    public IdentifyUSBDeviceTask(Activity activity, Boolean attach) {
        mactivity= activity;
        mattach = attach;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return PrinterUtil.getInstance().Identify();
    }

    @Override
    protected void onPreExecute() {
        //如果是插入USB设备，则显示正在识别
        //如果是拔出则什么都不显示
        if (mattach) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(mactivity);
                progressDialog.setMessage("正在识别USB设备……");
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        ImageView printerAttachImg;
        TextView printerIdText;
        printerAttachImg = (ImageView) mactivity.findViewById(R.id.printer_Attach_Img);
        printerIdText = (TextView) mactivity.findViewById(R.id.printerId);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (s.equals("error")) {
            printerAttachImg.setImageResource(R.mipmap.no);
            printerIdText.setText("无打印机");
        } else {
            printerAttachImg.setImageResource(R.mipmap.yes2);
            printerIdText.setText(s);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
