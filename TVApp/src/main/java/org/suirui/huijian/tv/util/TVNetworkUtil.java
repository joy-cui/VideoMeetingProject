package org.suirui.huijian.tv.util;

import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.suirui.srpaas.base.NetBean;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.base.util.receiver.NetUtils;
import com.suirui.srpaas.common.http.okhttp.utils.NetworkUtil;

import org.suirui.huijian.tv.TVAppConfigure;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

/**
 * Created by hh on 2018/4/21.
 */

public class TVNetworkUtil {
    private static final SRLog log = new SRLog(TVNetworkUtil.class.getName(), TVAppConfigure.LOG_LEVE);
    private static TVNetworkUtil instance = null;
    private static WifiAdmin mWifiAdmin;
    private Context mContext;

    public TVNetworkUtil(Context context) {
        mContext = context;
        mWifiAdmin = new WifiAdmin(context);
    }

    public static synchronized TVNetworkUtil getInstance(Context context) {
        if (instance == null) {
            instance = new TVNetworkUtil(context);
        }
        return instance;
    }

//    public String getIP(){
//        String ethernetIP = NetworkUtil.getInstance(mContext).getIpAddress(ConnectivityManager.TYPE_ETHERNET);
//        String wifiIP = NetworkUtil.getInstance(mContext).getIpAddress(ConnectivityManager.TYPE_WIFI);
//        String mobileIP = NetworkUtil.getInstance(mContext).getIpAddress(ConnectivityManager.TYPE_MOBILE);
//
//        boolean isEthernet = NetworkUtil.isEthernetConnected(mContext);
//        boolean iswifi = NetworkUtil.getInstance(mContext).isWifiConnection(mContext);
//        boolean ismobile = NetworkUtil.getInstance(mContext).isGPRSConnection(mContext);
//        String isnetlog = " isEthernet:"+isEthernet + " iswifi:"+iswifi + " ismobile:"+ismobile;
//        String netlog = " ETHERNET:"+ethernetIP + " WIFI:"+wifiIP + " MOBILE:"+mobileIP;
//        log.E("getIP()"+isnetlog + " "+netlog);
//        if(isEthernet && iswifi && ismobile){
//            return ethernetIP;
//        }else if(isEthernet && iswifi){
//            return ethernetIP;
//        }else if(isEthernet && ismobile){
//            return ethernetIP;
//        }else if(isEthernet){
//            return ethernetIP;
//        }else if(iswifi){
//            return wifiIP;
//        }else if(ismobile){
//            return mobileIP;
//        }
//        return "";
//    }

    public boolean ping(String host, int pingCount) {
        String line = null;
        Process process = null;
        BufferedReader successReader = null;
//        String command = "ping -c " + pingCount + " -w 5 " + host;
        String command = "ping -c " + pingCount + " " + host;
        boolean isSuccess = false;
        try {
            process = Runtime.getRuntime().exec(command);
            if (process == null) {
//                log.E("ping fail:process is null.");
                return false;
            }
            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = successReader.readLine()) != null) {
//                log.E(line);
            }
            int status = process.waitFor();
            if (status == 0) {
//                log.E("exec cmd success:" + command);
                isSuccess = true;
            } else {
//                log.E("exec cmd fail.");
                isSuccess = false;
            }
//            log.E("exec finished.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
//            log.E("ping exit.");
            if (process != null) {
                process.destroy();
            }
            if (successReader != null) {
                try {
                    successReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSuccess;
    }


    public boolean isWifiApOpen() {
        try {
            WifiManager manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            //通过放射获取 getWifiApState()方法
            Method method = manager.getClass().getDeclaredMethod("getWifiApState");
            //调用getWifiApState() ，获取返回值
            int state = (int) method.invoke(manager);
            //通过放射获取 WIFI_AP的开启状态属性
            Field field = manager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED");
            //获取属性值
            int value = (int) field.get(manager);
            //判断是否开启
            if (state == value) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

//    public void setWifiApOPen(Context context,boolean isOPen){
//        try {
//            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            manager.setWifiEnabled(false);
//            manager.setWifiApEnabled(null, isOPen);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private static final String WIFI_SAVED_STATE = "wifi_saved_state";
    public void setWifiApOpen(boolean enable) {
        WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        final ContentResolver cr = mContext.getContentResolver();
        /**
         * Disable Wifi if enabling tethering
         */
        int wifiState = mWifiManager.getWifiState();
        if (enable && ((wifiState == WifiManager.WIFI_STATE_ENABLING) ||
                (wifiState == WifiManager.WIFI_STATE_ENABLED))) {
            mWifiManager.setWifiEnabled(false);
            Settings.Global.putInt(cr, WIFI_SAVED_STATE, 1);
        }
        //编译不过，临时注释 bycui

//        if (mWifiManager.setWifiApEnabled(null, enable)) {
////            if (mSwitch != null) {
////                /* Disable here, enabled on receiving success broadcast */
////                mSwitch.setEnabled(false);
////            }
//        } else {
////            if (mSwitch != null) {
////                mSwitch.setSummary(R.string.wifi_error);
////            }
//        }

        /**
         *  If needed, restore Wifi on tether disable
         */
        if (!enable) {
            int wifiSavedState = 0;
            try {
                wifiSavedState = Settings.Global.getInt(cr, WIFI_SAVED_STATE);
            } catch (Settings.SettingNotFoundException e) {
                ;
            }
            if (wifiSavedState == 1) {
                mWifiManager.setWifiEnabled(true);
                Settings.Global.putInt(cr, WIFI_SAVED_STATE, 0);
            }
        }
    }

    public void createWifiAP(String ssid,String pwd){
        if(mWifiAdmin != null){
            mWifiAdmin.createWifiApEnabled(mContext,ssid,pwd,true);
//            mWifiAdmin.connectNetwork(ssid,pwd,2);
        }
    }


    public static NetBean getNetType(Context context) {
        NetBean netBean = new NetBean();
        netBean.setType(NetUtils.NetType.NONE);
        netBean.setNetType(NetUtils.Net_Type.SR_NETWORK_TYPE_NONE.getValue());

        try {
            ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService("connectivity");
            if(connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if(info != null && info.isConnected() && info.getState() == NetworkInfo.State.CONNECTED) {
                    Log.e("", "NetUtils.....网络:isConnect....SubtypeName: " + info.getSubtypeName() + " Subtype: " + info.getSubtype() + " Type: " + info.getType() + " TypeName: " + info.getTypeName());
                    String netTypeString = "wifi";
                    int nType = info.getType();
//                    NetUtils.....网络:isConnect....SubtypeName:  Subtype: 0 Type: 9 TypeName: Ethernet
                    if(nType == 9){//Ethernet
                        netBean.setType(NetUtils.NetType.WIFI);
                    }

                    if(nType == 1 || nType == 9) {
                        netBean.setType(NetUtils.NetType.WIFI);
                        netBean.setNetType(NetUtils.Net_Type.SR_NETWORK_TYPE_WIFI.getValue());
                    }
                }
            }

            return netBean;
        } catch (Exception var8) {
            Log.e("", var8.toString());
            var8.printStackTrace();
            return netBean;
        }
    }

    private void shella(){
        String com="ifconfig eth0 192.168.2.210 netmask 255.255.255.0";

        try{
            Process suProcess = Runtime.getRuntime().exec("su");//root权限
            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            // Execute commands that require root access
            os.writeBytes(com+ "\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
