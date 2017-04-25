package com.seu601.android_usb_printer_demo.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.seu601.android_usb_printer_demo.MyApplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by xingkong on 2017/3/13.
 * Modified by ylx on 2017/4/21
 */

public class PrinterUtil {
    private static PrinterUtil singleton;
    private static String resPath = "/sdcard";

    public static PrinterUtil getInstance() {
        if (singleton == null) {
            singleton = new PrinterUtil();
        }
        return singleton;
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Toast.makeText(MyApplication.getContext(), "无法修改应用程序读写权限", Toast.LENGTH_SHORT).show();
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    public boolean InitPrinter(Context context) {
        Process process = null;
        DataOutputStream os = null;
        try {
            copyBigDataToSD(resPath + "/Profile.tar", context); //把需要的资源文件压缩包从assets拷贝到SD卡里

            process = Runtime.getRuntime().exec("su"); //切换到root帐号

            os = new DataOutputStream(process.getOutputStream());

            os.writeBytes("mount -o remount,rw /system\n");
            os.writeBytes("mount -o remount,rw /\n");
            os.writeBytes("cd " + resPath + "\n");
            os.writeBytes("busybox mkdir " + resPath + "/usb_printer_tools\n");
            os.writeBytes("busybox tar -xvf ./Profile.tar -C ./usb_printer_tools\n");
            os.writeBytes("busybox rm ./Profile.tar\n");
            os.writeBytes("cd " + resPath +"/usb_printer_tools\n");
            os.writeBytes("busybox tar -xvzf ./gs.tar.gz -C /\n");
            os.writeBytes("busybox rm ./gs.tar.gz\n");
            os.writeBytes("busybox cp ./gs ./usb_printerid ./foo2/* /system/bin\n");
            os.writeBytes("busybox chomd a+x /system/bin/gs /system/bin/foo2*\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }


        Log.e("TEST", "success");
        return true;
    }

    private void copyBigDataToSD(String strOutFileName, Context context) throws IOException, InterruptedException {
        InputStream myInput;
        Log.e("TEST", "new file");
        OutputStream myOutput = new FileOutputStream(strOutFileName);

        Log.e("TEST", "new file success");
        myInput = context.getAssets().open("Profile.tar");
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            Log.e("TEST", "copying");
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();

        Log.e("TEST", "copy success");
    }


    public boolean Print() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("cd /storage/sdcard0/printer/\n");
//            os.writeBytes("gs -q -dBATCH -dSAFER -dNOPAUSE -sPAPERSIZE=a4 -r1200x600 -sDEVICE=pbmraw -sOutputFile=pdf1.pbm /storage/sdcard0/printer/pdf1.pdf\n");
            os.writeBytes("gs -q -dBATCH -dSAFER -dNOPAUSE -sPAPERSIZE=a4 -r1200x600 -sDEVICE=pbmraw -sOutputFile=pdf1.pbm /storage/sdcard0/printer/testpic.pdf\n");
            os.flush();
            Log.d("Print", "pbm complete");
            os.writeBytes("foo2zjs -z1 -p9 -r1200x600 pdf2.pbm > /dev/usb/lp0\n");
//            os.writeBytes("rm pdf1.pbm\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    public String Identify() {
        String printerId = "";
        Process process = null;
        DataInputStream in = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            in = new DataInputStream(process.getInputStream());
            Log.e("Identify", "write");
            os.writeBytes("usb_printerid /dev/usb/lp0\n");
            os.writeBytes("exit\n");
            os.flush();
            os.flush();
            Log.e("Identify", "flush");
            os.close();
            Log.e("Identify", "close");
//            process.waitFor();

            printerId = in.readUTF();

            // TODO: 2017/4/25 如果用readline有阻塞性 
            Log.e("Identify", printerId);
//            printerId = in.readLine();
//            printerId = printerId + in.readLine();

            Log.e("Identify", printerId);


            process.waitFor();
        } catch (Exception e) {
            return "error";
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                process.destroy();
            } catch (Exception e) {
                return "error";
            }
        }
        int begin = printerId.indexOf("DES:");
        if (begin > -1) {
            int end = printerId.indexOf(";", begin);
            printerId = printerId.substring(begin + 4, end);
            return printerId;
        }
        return "error";
    }
}
