package org.suirui.huijian.tv.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.suirui.srpaas.common.http.okhttp.utils.NetworkUtil;
import com.suirui.srpaas.common.permission.AndPermission;
import com.suirui.srpaas.common.permission.Rationale;
import com.suirui.srpaas.common.permission.RationaleListener;

import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.srpaas.util.MD5;
import java.io.PrintWriter;


/**
 * Created by hh on 2018/4/25.
 */

public class AndroidUtils {

    private static AndroidUtils instance = null;

    private AndroidUtils() {
    }

    public static synchronized AndroidUtils getInstance() {
        if (instance == null) {
            instance = new AndroidUtils();
        }

        return instance;
    }

    public String getBrand() {
        return Build.BRAND;
    }

    public String getDeviceModel() {
        return Build.MODEL;
    }

    public String getSerialNumber() {
        String serialNumber = android.os.Build.SERIAL;
        return serialNumber;
    }

    public String getDeviceID(final Context context) {
        String id = "";
        try {
            final TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                AndPermission.with((Activity) context)
                        .requestCode(TVAppConfigure.permission.READ_PHONE_STATE)
                        .permission(Manifest.permission.READ_PHONE_STATE)
                        .rationale(
                                new RationaleListener() {
                                    @Override
                                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                        // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                                        AndPermission.rationaleDialog((Activity) context, rationale).show();
                                    }
                                }
                        ).send();
            }
            if (manager.getDeviceId() == null || manager.getDeviceId().equals("")) {
                if (Build.VERSION.SDK_INT >= 23) {
                    id = manager.getDeviceId(0);
                }
            } else {
                id = manager.getDeviceId();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return id;
    }

    public String getAndroidId(Context context){
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        return ANDROID_ID;
    }

    public String getAndroidFlag(Context context){
        String mac = NetworkUtil.getInstance(context).getMac();
        String flag = mac+System.currentTimeMillis();
        return MD5.getMD5Str(flag);
    }

    /**
     * 判断手机是否有root权限
     */
    public boolean hasRootPerssion(){
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(process!=null){
                process.destroy();
            }
        }
        return false;
    }

    private boolean returnResult(int value){
        // 代表成功
        if (value == 0) {
            return true;
        } else if (value == 1) { // 失败
            return false;
        } else { // 未知情况
            return false;
        }
    }


}
