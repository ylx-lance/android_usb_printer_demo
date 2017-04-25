package com.seu601.android_usb_printer_demo.Tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.seu601.android_usb_printer_demo.MyApplication;
import com.seu601.android_usb_printer_demo.util.PrinterUtil;

/**
 * Created by ylx on 2017/4/21.
 * 打印机所需系统资源加载
 * 包括gs,foo2,drivers文件
 * gs,foo2,usbprinerid均放于/system/bin下
 * drivers文件放在/sdcard/usb_printer_tools路径下
 * @return 初始化  成功——将DefaultSharedPreference里的isInit保存为true
 *                  失败——将isInit保存为false
 */

public class InitTask extends AsyncTask<Void, Void, Boolean> {
    private ProgressDialog progressDialog;
    private Context mcontext;

    public InitTask(Context context) {
        mcontext=context;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.e("TEST", "is doing");

        return PrinterUtil.getInstance().InitPrinter(MyApplication.getContext());
    }

    @Override
    protected void onPreExecute(){
        Log.e("TEST", "is start");
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mcontext);
            progressDialog.setMessage("正在初始化……");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        Log.e("TEST", "is over");
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (aBoolean) {
            editor.putBoolean("isInit", true);
            editor.apply();
            Toast.makeText(MyApplication.getContext(), "初始化成功！", Toast.LENGTH_SHORT).show();
            return;
        } else {
            editor.putBoolean("isInit", false);
            editor.apply();
            Toast.makeText(MyApplication.getContext(), "初始化失败...", Toast.LENGTH_SHORT).show();
        }
    }
}
