package com.seu601.android_usb_printer_demo.Tasks;

import android.app.IntentService;
import android.content.Intent;
import android.os.Message;

import com.seu601.android_usb_printer_demo.MainActivity;
import com.seu601.android_usb_printer_demo.PrinterSupportted;
import com.seu601.android_usb_printer_demo.util.PrinterUtil;

import java.util.Arrays;

/**
 * @author ylx
 * created at 2017/5/3 14:53
 * Intro: 判断插入的USB设备是否是打印机，如果是则甄别出打印机的型号，并在主界面显示
 */
public class IdentifyUSBDeviceService extends IntentService {

    public IdentifyUSBDeviceService() {
        super("IdentifyUSBDeviceService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String usbprinterId = PrinterUtil.getInstance().IdentifyPrinterId();
//        Toast.makeText(MainActivity.instance, usbprinterId, Toast.LENGTH_SHORT).show();
        if (usbprinterId == "error") {
            Message message = new Message();
            message.what = MainActivity.USB_PRINTER_DETACH;
            MainActivity.instance.handler.sendMessage(message);
        } else {
            Message message = new Message();
            message.what = MainActivity.USB_PRINTER_ATTACH;
            message.obj = usbprinterId;
            MainActivity.instance.handler.sendMessage(message);

            boolean isSupport = Arrays.asList(PrinterSupportted.printersupport).contains(usbprinterId);
            String driverFile = "sihp1020.dl";
            if (isSupport == true) {
                switch (usbprinterId) {
                    case "HP LaserJet 1020":
                        driverFile = "sihp1020.dl";
                        break;
                    case "HP LaserJet 1018":
                        driverFile = "sihp1018.dl";
                        break;
                    case "HP LaserJet 1005":
                        driverFile = "sihp1005.dl";
                        break;
                    case "HP LaserJet 1000":
                        driverFile = "sihp1000.dl";
                        break;
                    case "HP LaserJet P1005":
                        driverFile = "sihpP1005.dl";
                        break;
                    case "HP LaserJet P1006":
                        driverFile = "sihpP1006.dl";
                        break;
                    case "HP LaserJet P1007":
                        driverFile = "sihpP1007.dl";
                        break;
                    case "HP LaserJet P1008":
                        driverFile = "sihpP1008.dl";
                        break;
                    case "HP LaserJet P1505/P1505n":
                        driverFile = "sihpP1505.dl";
                        break;
                    default:
                        isSupport = false;
                        driverFile = "error";
                        break;
                }
            }
            if (isSupport == true && MainActivity.instance.isInit == true) {
                if (PrinterUtil.getInstance().LoadPrinterDriver(driverFile)) {
                    Message message3 = new Message();
                    message3.what = MainActivity.USB_PRINTER_DRIVERLOAD;
                    MainActivity.instance.handler.sendMessage(message3);
                    return;
                }
            }
            Message message2 = new Message();
            message2.what = MainActivity.USB_PRINTER_UNSUPPORTED;
            MainActivity.instance.handler.sendMessage(message2);
            return;


        }
    }

}
