package com.seu601.android_usb_printer_demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.seu601.android_usb_printer_demo.Tasks.IdentifyUSBDeviceService;


public class USBPrinterAttachReceiver extends BroadcastReceiver {


    @Override

    public void onReceive(Context context, Intent intent) {

        Boolean attached = true;
        Log.e("USB", intent.getAction());
        if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            Toast.makeText(context, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
            attached = true;
        } else if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
            Toast.makeText(context, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
            attached = false;
        }
        Intent intentService = new Intent(context,IdentifyUSBDeviceService.class);
        context.startService(intentService);


        // an Intent broadcast.

//        TextView printerId;
//        printerId = (TextView) MainActivity.instance.findViewById(R.id.printerId);
//        printerId.setText(PrinterUtil.getInstance().IdentifyPrinterId());
    }
}
