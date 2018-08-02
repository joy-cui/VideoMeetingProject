package org.suirui.huijian.tv.util;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build;
import android.text.TextUtils;
import com.suirui.srpaas.base.util.log.SRLog;
import org.suirui.huijian.tv.TVAppConfigure;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WifiAdmin {
    private static final SRLog log = new SRLog(WifiAdmin.class.getName(), TVAppConfigure.LOG_LEVE);
    // 定义一个WifiLock
    WifiLock mWifiLock;
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    private List<ScanResult> newWifiList = new ArrayList<ScanResult>();// 过滤后的我ifi列表
    private DhcpInfo mDhcpInfo;
    private Context mContext;

    // 构造器
    public WifiAdmin(Context context) {
        mContext = context;
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
        //获取dhcp
        mDhcpInfo = mWifiManager.getDhcpInfo();
    }

    public String getSSID() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo.getSSID().replace("\"", "");
    }

    // 得到wifi信号强度
    public int getLevel() {
        return mWifiInfo.getRssi();
    }

    public int getGateway() {
        mDhcpInfo = mWifiManager.getDhcpInfo();
        return mDhcpInfo.gateway;
    }

    // 打开WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    public void updateNetWork(WifiConfiguration config) {
        mWifiManager.updateNetwork(config);
    }

    /**
     * 检查当前WIFI状态
     *
     * @return WIFI_STATE_ENABLED(3) 已启动 WIFI_STATE_ENABLING(2) 正在启动
     * WIFI_STATE_DISABLING(0) 正在关闭 WIFI_STATE_DISABLED(1) 已关闭
     * WIFI_STATE_UNKNOWN 未知
     */
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建一个WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
//        if (mWifiConfiguration != null) {
//            for (WifiConfiguration wifi : mWifiConfiguration) {
//                log.E("WifiSetActivity.....wifi.networkId:" + wifi.networkId
//                        + "  wifiName: " + wifi.SSID + "  preSharedKey: "
//                        + wifi.preSharedKey);
//            }
//        }
        return mWifiConfiguration;
    }

    // 指定配置好的网络进行连接
    public boolean connectConfiguration(int index) {
        // 连接配置好的指定ID的网络
        boolean isConnect = mWifiManager.enableNetwork(index, true);
        log.E("WifiSetActivity....enableNetwork...isConnect:" + isConnect);
        return isConnect;
    }

    public void startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
//        if (mWifiConfiguration != null) {
//            for (WifiConfiguration wifi : mWifiConfiguration) {
//                log.E("WifiSetActivity.....wifi.networkId:" + wifi.networkId
//                        + "  wifiName: " + wifi.SSID + "  preSharedKey: "
//                        + wifi.preSharedKey);
//            }
//        }
    }

    // 得到网络列表
    public List<ScanResult> getWifiList() {
        // 过滤重复名称的wifi
        List<ScanResult> newSr = new ArrayList<ScanResult>();
        for (ScanResult result : mWifiList) {
            if (!TextUtils.isEmpty(result.SSID)
                    && !containName(newSr, result.SSID))
                newSr.add(result);
        }
        newWifiList = newSr;
        return newSr;
    }

    private boolean containName(List<ScanResult> sr, String name) {
        for (ScanResult result : sr) {
            if (!TextUtils.isEmpty(result.SSID) && result.SSID.equals(name))
                return true;
        }
        return false;
    }

    // 查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < newWifiList.size(); i++) {
            stringBuilder
                    .append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((newWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    // 得到MAC地址
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 得到接入点的BSSID
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // 得到IP地址
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 得到连接的ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    public WifiInfo getWifiInfoObj() {
        return mWifiInfo = mWifiManager.getConnectionInfo();
    }

    // 添加一个网络并连接
    public int addNetwork(WifiConfiguration wcg) {
        log.E(" wwwww addNetwork()");
        int netID = -1;
        boolean isScan = false;
        openWifi();
        if (!isScan && wcg != null) {
            WifiConfiguration tempConfig = this.IsExsits(wcg.SSID);
            if (tempConfig != null) {
                mWifiManager.removeNetwork(tempConfig.networkId);
                log.E(" wwwww addNetwork..移出之前的配置的信息");
            }
            netID = mWifiManager.addNetwork(wcg);
//            for (WifiConfiguration c:getConfiguration()) {
//                mWifiManager.disableNetwork(c.networkId);
//            }
            Method connectMethod = connectWifiByReflectMethod(netID);
            if (connectMethod == null) {
                log.E("wwwww addNetwork wifi by enableNetwork method");
                // 通用API
                boolean enable = mWifiManager.enableNetwork(netID, true);
//                log.E("wwwww addNetwork wifi by enableNetwork method  enable:"+enable);
            }
        }
        return netID;
    }

    // wifi热点开关
    public boolean createWifiApEnabled(Context context,String ssid,String pwd,boolean enabled) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (enabled) { // disable WiFi in any case
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
//            manager.setWifiEnabled(false);
        }
        try {
            Method method = manager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            boolean shut = (Boolean) method.invoke(manager, null, false);
            //热点的配置类
            WifiConfiguration tempConfig = IsExsits(ssid);
            if (tempConfig != null) {
                mWifiManager.removeNetwork(tempConfig.networkId);
            }
            WifiConfiguration apConfig = createWifiAPInfo(ssid,pwd,2);
            //返回热点打开状态
            boolean isOPen  = (Boolean) method.invoke(manager, apConfig, enabled);
            return isOPen;
        } catch (Exception e) {
            return false;
        }

    }

    public int connectNetwork(String wifiName, String password, int lockedType) {
        log.E("connectNetwork():..当前连接的wifi .:");
        int netId = -1;
//        log.E("connectNetwork():..当前连接的wifi .getNetworkId():"+getNetworkId());
//        if (getNetworkId() != -1){
//            log.D("connectNetwork():................disconnectWifi()");
//            disconnectWifi(getNetworkId());
//        }
        WifiConfiguration config = CreateWifiInfo(wifiName, password, lockedType);
        log.E("connectNetwork():................config:" + config + "...wifiName:"
                + wifiName + "  password:" + password + "  lockedType:"+lockedType);
        if (config == null) {
            return -1;
        }
        netId = addNetwork(config);
        return netId;
    }

    public boolean enableNetwork(int wcgID) {
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        return b;
    }

    // 断开指定ID的网络
    public void disconnectWifi(int netid) {
        mWifiManager.disableNetwork(netid);
        mWifiManager.disconnect();
    }


    public WifiConfiguration createWifiAPInfo(String SSID, String password, int type) {
        WifiConfiguration config = null;
        if (config == null) {
            config = new WifiConfiguration();
        }
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = SSID;
        // 分为三种情况：0没有密码1用wep加密2用wpa加密
        if (type == 0) {// WIFICIPHER_NOPASSwifiCong.hiddenSSID = false;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (type == 1) {  //  WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = password;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == 2) {   // WIFICIPHER_WPA
            config.preSharedKey = password;
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String password, int type) {
        WifiConfiguration config = null;
        WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null) {
            List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
            if(existingConfigs != null){
                for (WifiConfiguration existingConfig : existingConfigs) {
                    if (existingConfig == null) continue;
                    if (existingConfig.SSID.equals("\"" + SSID + "\"")  /*&&  existingConfig.preSharedKey.equals("\""  +  password  +  "\"")*/) {
                        config = existingConfig;
                        break;
                    }
                }
            }

        }
        if (config == null) {
            config = new WifiConfiguration();
        }
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // 分为三种情况：0没有密码1用wep加密2用wpa加密
        if (type == 0) {// WIFICIPHER_NOPASSwifiCong.hiddenSSID = false;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (type == 1) {  //  WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == 2) {   // WIFICIPHER_WPA
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }


    public WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                log.E("existingConfig.SSID:"+existingConfig.SSID);
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        }else{

        }
        return null;
    }

    /**
     * 通过反射出不同版本的connect方法来连接Wifi
     */
    private Method connectWifiByReflectMethod(int netId) {
        log.E("wwwww  connectWifiByReflectMethod()  sdk:" +Build.VERSION.SDK_INT);
        Method connectMethod = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            log.E(" wwwww connectWifiByReflectMethod road 1");
            // 反射方法： connect(int, listener) , 4.2 <= phone's android version
            for (Method methodSub : mWifiManager.getClass()
                    .getDeclaredMethods()) {
                if ("connect".equalsIgnoreCase(methodSub.getName())) {
                    Class<?>[] types = methodSub.getParameterTypes();
                    if (types != null && types.length > 0) {
                        if ("int".equalsIgnoreCase(types[0].getName())) {
                            connectMethod = methodSub;
                        }
                    }
                }
            }
            if (connectMethod != null) {
                try {
                    connectMethod.invoke(mWifiManager, netId, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.E( "wwwww connectWifiByReflectMethod Android "
                            + Build.VERSION.SDK_INT + " error!");
                    return null;
                }
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
            // 反射方法: connect(Channel c, int networkId, ActionListener listener)
            // 暂时不处理4.1的情况 , 4.1 == phone's android version
            log.E("wwwww connectWifiByReflectMethod road 2");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            log.E("wwwww connectWifiByReflectMethod road 3");
            // 反射方法：connectNetwork(int networkId) ,
            // 4.0 <= phone's android version < 4.1
            for (Method methodSub : mWifiManager.getClass()
                    .getDeclaredMethods()) {
                if ("wwwww connectNetwork".equalsIgnoreCase(methodSub.getName())) {
                    Class<?>[] types = methodSub.getParameterTypes();
                    if (types != null && types.length > 0) {
                        if ("int".equalsIgnoreCase(types[0].getName())) {
                            connectMethod = methodSub;
                        }
                    }
                }
            }
            if (connectMethod != null) {
                try {
                    connectMethod.invoke(mWifiManager, netId);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.E("wwwww connectWifiByReflectMethod Android "
                            + Build.VERSION.SDK_INT + " error!");
                    return null;
                }
            }
        } else {
            // < android 4.0
            return null;
        }
        return connectMethod;
    }

}
