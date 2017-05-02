package com.seu601.android_usb_printer_demo.Tasks;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.seu601.android_usb_printer_demo.util.PrinterUtil;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class PrintIntentService extends IntentService {

    public PrintIntentService() {
        super("PrintIntentService");
    }

//todo 1.完善Print(cmd1,cmd2)
    @Override
    protected void onHandleIntent(Intent intent) {
        String fileName = intent.getStringExtra("filename");
        PrinterUtil.getInstance().Print(fileName);

    }

}
