package org.suirui.huijian.tv.util;

import android.util.Log;

import com.suirui.srpaas.base.util.BaseUtil;
import com.suirui.srpaas.base.util.log.LogToFile;
import com.suirui.srpaas.video.contant.Configure;

import org.suirui.huijian.tv.TVAppConfigure;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hh on 2018/4/26.
 */

public class TVStringUtil {
    /**
     * 过滤判断ip是否合法
     *
     * @param ipAddress
     * @return
     */
    public static boolean isIp(String ipAddress) {
        if (ipAddress != null) {
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(ipAddress);
            return matcher.matches();
        } else {
            return false;
        }
    }

    public static boolean isNetMask(String netmask){
        Pattern pattern = Pattern.compile("(^((\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$)|^(\\d|[1-2]\\d|3[0-2])$"); /*check subnet mask*/
        if(pattern.matcher(netmask).matches()){
            return true;
        }
        return false;
    }

    //初始化log写文件
    public static void initWriteToFile() {
        Log.e("","获取读写权限成功....初始化写log文件");
        try {
            String path = BaseUtil.getSDPath() + "/atv/logs";
            LogToFile.init(path);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //写log文件
    public static void writeToFile(String TAG, String message) {
        try {
            if (TVAppConfigure.isTestLogToFile) {
//                log.E("获取到读写权限了,开始写log了...注意啦");
                LogToFile.d(TAG, message);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getListSize(List list){
        if(list!=null){
            return list.size();
        }else{
            return 0;
        }

    }
}
