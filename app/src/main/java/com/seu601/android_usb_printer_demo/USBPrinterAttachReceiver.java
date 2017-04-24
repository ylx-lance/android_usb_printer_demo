package com.seu601.android_usb_printer_demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class USBPrinterAttachReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(MyApplication.getContext(), "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
        // an Intent broadcast.

    }
}
