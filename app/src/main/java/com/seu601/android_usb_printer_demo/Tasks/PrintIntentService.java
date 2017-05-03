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
        String printerId = intent.getStringExtra("printerid");
        String foo2 = "foo2zjs";
        switch (printerId) {
            case "HP LaserJet 1022":
            case "HP LaserJet 1022n":
            case "HP LaserJet 1022nw":
            case "HP LaserJet 1020":
            case "HP LaserJet 1018":
            case "HP LaserJet 1005":
            case "HP LaserJet 1000":
            case "HP LaserJet M1319 MFP":
                foo2 = "foo2zjs";
                break;
            case "HP LaserJet P1005":
            case "HP LaserJet P1006":
            case "HP LaserJet P1007":
            case "HP LaserJet P1008":
            case "HP LaserJet P1505/P1505n":
                foo2 = "foo2xqx";
                break;
            default:
                foo2 = "foo2zjs";
                break;
        }

        PrinterUtil.getInstance().Print(fileName,foo2);

    }

}
