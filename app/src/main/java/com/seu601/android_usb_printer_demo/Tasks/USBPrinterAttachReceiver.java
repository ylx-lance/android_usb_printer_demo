package com.seu601.android_usb_printer_demo.Tasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.seu601.android_usb_printer_demo.MainActivity;
import com.seu601.android_usb_printer_demo.Tasks.IdentifyUSBDeviceService;

/**
 * @author ylx
 *         created at 2017/5/2 14:46
 *         Intro:检测USB打印机插拔情况的广播接收器
 *         并启动USB设备识别服务IdentifyUSBDeviceService
 */

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
            Message message = new Message();
            message.what = MainActivity.USB_PRINTER_DETACH;
            MainActivity.instance.handler.sendMessage(message);
            attached = false;
        }
        Intent intentService = new Intent(context, IdentifyUSBDeviceService.class);
        context.startService(intentService);
    }
}
