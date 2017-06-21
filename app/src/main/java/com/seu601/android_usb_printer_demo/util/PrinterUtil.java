package com.seu601.android_usb_printer_demo.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.seu601.android_usb_printer_demo.MyApplication;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by xingkong on 2017/3/13.
 * Modified by ylx on 2017/4/21
 */

public class PrinterUtil {
    private static PrinterUtil singleton;
        private static String resPath = "/sdcard";
//    private static String resPath = Environment.getExternalStorageDirectory().getPath();
        private String command = "su";//如果系统没有默认root，执行这行命令
//    private String command = "ls";//如果系统已经默认root，执行这行命令
    // ylxNOTICE (2017/6/17) 


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
            process = Runtime.getRuntime().exec(command); //切换到root帐号
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
        Log.e("Init", "start");
        try {
            process = Runtime.getRuntime().exec(command);
            os = new DataOutputStream(process.getOutputStream());

            Log.e("Init", "copy start");

            copyBigDataToSD(resPath + "/Profile.tar", "Profile.tar", context); //把需要的资源文件压缩包从assets拷贝到SD卡里
            copyBigDataToSD(resPath + "/busybox", "busybox", context); //把需要的资源文件压缩包从assets拷贝到SD卡里

            Log.e("Init", "cmd start");
            os.writeBytes("mount -o remount,rw /system\n");
            os.writeBytes("mount -o remount,rw /\n");
            os.writeBytes("cd " + resPath + "\n");
            os.writeBytes("cp busybox /system/bin/\n");
            os.writeBytes("chmod 777 /system/bin/busybox\n");
            os.writeBytes("busybox mkdir " + resPath + "/usb_printer_tools\n");
            os.writeBytes("busybox tar -xvf ./Profile.tar -C ./usb_printer_tools\n");
            os.writeBytes("busybox rm ./Profile.tar\n");
            os.writeBytes("cd " + resPath + "/usb_printer_tools\n");
            os.writeBytes("busybox tar -xvzf ./gs.tar.gz -C /\n");
            os.writeBytes("busybox rm ./gs.tar.gz\n");
            os.writeBytes("busybox cp ./gs ./usb_printerid ./foo2/* /system/bin/\n");
            os.writeBytes("busybox chmod a+x /system/bin/gs /system/bin/foo2* /system/bin/usb_printerid\n");
            os.writeBytes("exit\n");
            os.flush();

            Log.e("Init", "cmd over");
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

    private void copyBigDataToSD(String strOutFileName, String strInFileName, Context context) throws IOException, InterruptedException {
        InputStream myInput;
        Log.e("TEST", "new file");
        OutputStream myOutput = new FileOutputStream(strOutFileName);

        Log.e("TEST", "new file success");
        myInput = context.getAssets().open(strInFileName);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        Log.e("TEST", "copying");
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();

        Log.e("TEST", "copy success");
    }


    public boolean Print(String fileNamePath, String foo2) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(command); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("cd " + resPath + "/usb_printer_tools\n");
//            os.writeBytes("gs -q -dBATCH -dSAFER -dNOPAUSE -sPAPERSIZE=a4 -r1200x600 -sDEVICE=pbmraw -sOutputFile=pdf1.pbm /storage/sdcard0/printer/pdf1.pdf\n");
            os.writeBytes("gs -q -dBATCH -dSAFER -dNOPAUSE -sPAPERSIZE=a4 -r1200x600 -sDEVICE=pbmraw -sOutputFile=pdf1.pbm " + fileNamePath + "\n");
            os.flush();
            Log.e("Print", "pbm complete");
            os.writeBytes(foo2 + " -z1 -p9 -r1200x600 pdf1.pbm > /dev/usb/lp0\n");
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

    public String IdentifyPrinterId() {
        String printerId = "";
        Process process = null;
        DataInputStream in = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(command); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            in = new DataInputStream(process.getInputStream());
//            os.writeBytes("usb_printerid /dev/usb/lp0\n");
//            os.writeBytes("adb version\n");
//            os.flush();

            Log.e("Identify", "write");
            process = Runtime.getRuntime().exec("ls /dev/usb/");
            BufferedReader mReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer mRespBuff = new StringBuffer();
            char[] buff = new char[1024];
            int ch = 0;
            while ((ch = mReader.read(buff)) != -1) {
                mRespBuff.append(buff, 0, ch);
            }
            if (mRespBuff.toString().contains("lp0")) {
                Log.e("Identify", mRespBuff.toString());
                mReader.close();
//                process = Runtime.getRuntime().exec("cd /dev/usb");
//                process = Runtime.getRuntime().exec("usb_printerid lp0");
//                while((ch = mReader.read(buff)) != -1){
//                    mRespBuff.append(buff, 0, ch);
//                }
//                printerId = mRespBuff.toString();
                os.writeBytes("usb_printerid /dev/usb/lp0\n");
                os.flush();
                printerId = in.readLine();
                printerId = printerId + in.readLine();
                Log.e("Identify", printerId);
            } else {
                Log.e("Identify", "error");
                mReader.close();
                return "error";
            }


            os.writeBytes("exit\n");
            os.flush();

            process.waitFor();
        } catch (Exception e) {

            Log.e("Identify", "error");
            return "error";
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (in != null) {
                    in.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        int begin = printerId.indexOf("DES:");
        if (begin > -1) {
            int end = printerId.indexOf(";", begin);
            printerId = printerId.substring(begin + 4, end);
            Log.e("Identify", printerId);
            return printerId;
        }
        return "error";
    }


    public boolean LoadPrinterDriver(String driverFileName) {

        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(command); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());

            os.writeBytes("cd " + resPath + "/usb_printer_tools/drivers\n");
            os.writeBytes("cat " + driverFileName + " > /dev/usb/lp0\n");
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


        Log.e("Driver", "success");
        return true;

    }
}
