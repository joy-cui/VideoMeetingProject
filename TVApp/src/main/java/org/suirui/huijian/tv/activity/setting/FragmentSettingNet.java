package org.suirui.huijian.tv.activity.setting;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.StaticIpConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings.System;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.suirui.pub.business.contant.SRIMConfigure;
import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.LogoActivity;
import org.suirui.huijian.tv.activity.login.FragmentLogo;
import org.suirui.huijian.tv.activity.login.FragmentMange;
import org.suirui.huijian.tv.activity.login.FragmentNetFail;
import org.suirui.huijian.tv.bean.LocationBean;
import org.suirui.huijian.tv.util.LoginUtil;
import org.suirui.huijian.tv.util.TVStringUtil;
import org.suirui.huijian.tv.widget.LocationDialog;
import org.suirui.srpaas.util.NetworkUtil;
import org.suirui.srpaas.util.StringUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by hh on 2018/4/19.
 */
public class FragmentSettingNet extends BaseFragment{
    private static final SRLog log = new SRLog(FragmentSettingNet.class.getName(), TVAppConfigure.LOG_LEVE);

    private String[] IPType = {"手动指定", "动态获取"};
    private LocationDialog mLocationDialog;
    private TextView mIPTypeTV;
    private EditText mIPAddrET;
    private EditText mNetMaskET;
    private EditText mGateWayET;
    private EditText mDnsET;
    private Button mChangeBtn;
    private TextView mTvIPError;
    private TextView mTvNetMaskError;
    private TextView mTvGateWayError;
    private TextView mTvDnsError;
    private TextView mTipsTV;

    private int mCurIPType;
    EthernetManager mEthManager;
    IpConfiguration mIpConfiguration;
    ;
    private static final String KEY_ETH_IP_ADDRESS = "ethernet_ip_addr";
    private static final String KEY_ETH_HW_ADDRESS = "ethernet_hw_addr";
    private static final String KEY_ETH_NET_MASK = "ethernet_netmask";
    private static final String KEY_ETH_GATEWAY = "ethernet_gateway";
    private static final String KEY_ETH_DNS1 = "ethernet_dns1";
    private static final String KEY_ETH_DNS2 = "ethernet_dns2";
    private static final String KEY_ETH_MODE = "ethernet_mode_select";

    public static final String ETHERNET_USE_STATIC_IP = "ethernet_use_static_ip";
    public static final String ETHERNET_STATIC_IP = "ethernet_static_ip";
    public static final String ETHERNET_STATIC_GATEWAY = "ethernet_static_gateway";
    public static final String ETHERNET_STATIC_NETMASK = "ethernet_static_netmask";
    public static final String ETHERNET_STATIC_DNS1 = "ethernet_static_dns1";
    public static final String ETHERNET_STATIC_DNS2 = "ethernet_static_dns2";


    private static String mEthHwAddress = null;
    private static String mEthIpAddress = null;
    private static String mEthNetmask = null;
    private static String mEthGateway = null;
    private static String mEthdns1 = null;
    private static String mEthdns2 = null;
    private final static String nullIpInfo = "0.0.0.0";
    private static String mTempEthHwAddress = null;
    private static String mTempEthIpAddress = null;
    private static String mTempEthNetmask = null;
    private static String mTempEthGateway = null;
    private static String mTempEthdns1 = null;
    private static String mTempEthdns2 = null;
    private int mTempIPType;
    String tips = "";

    private static final int SHOW_CONNECTED_FAIL = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        View view = inflater.inflate(R.layout.m_setting_net_layout, null);
        changeTitle(getString(R.string.m_setting_net));
        mIPTypeTV = (TextView) view.findViewById(R.id.tvIPType);
        mIPAddrET = (EditText) view.findViewById(R.id.edtIpAddress);
        mNetMaskET = (EditText) view.findViewById(R.id.edtNetmask);
        mGateWayET = (EditText) view.findViewById(R.id.edtGateway);
        mDnsET = (EditText) view.findViewById(R.id.edtDNS);
        mChangeBtn = (Button) view.findViewById(R.id.btnChange);
        mTipsTV = (TextView) view.findViewById(R.id.tips);
        mTvIPError = (TextView) view.findViewById(R.id.tvIPError);
        mTvNetMaskError = (TextView) view.findViewById(R.id.tvNetmaskError);
        mTvGateWayError = (TextView) view.findViewById(R.id.tvGatewayError);
        mTvDnsError = (TextView) view.findViewById(R.id.tvDNSError);

        mIPTypeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIPTypeSelect();
            }
        });

        mChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.E("mChangeBtn onClick()::mCurIPType:" + mCurIPType);
                setChangeBtnEnable(false);
                if (mCurIPType == 0) {//静态IP
                    saveStaticIpSettingInfo();
                    boolean isSet = setStaticIpConfiguration();
//                    setTips("mChangeBtn onClick():mIpConfiguration.toString():" + mIpConfiguration.toString());
                    if (isSet) {
                        mEthManager.setConfiguration(mIpConfiguration);
//                        showConnectedFail();
                    } else {
//                        log.E("mChangeBtn onClick():mIpConfiguration.toString():" + mIpConfiguration.toString());
                    }
                } else if (mCurIPType == 1) {//动态获取

                }


            }
        });

        mEthManager = (EthernetManager) getContext().getSystemService("ethernet");

        if (mEthManager == null) {
            log.E("get ethernet manager failed");
        }
        view.requestFocus();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        handleEtherStateChange(mEthManager.getEthernetConnectState());
        IpConfiguration.IpAssignment mode = mEthManager.getConfiguration().getIpAssignment();
        mCurIPType = mode.ordinal();
        refreshUI();
        setChangeBtnEnable(false);
        registerReceiver();
        mIPAddrET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mCurIPType == 1){//动态获取
                    return;
                }
                mEthIpAddress = mIPAddrET.getText().toString();
                if (TVStringUtil.isIp(mEthIpAddress)){
                    mTvIPError.setVisibility(View.INVISIBLE);
                }else{
                    mTvIPError.setVisibility(View.VISIBLE);
                }
                boolean isCorrect = checkIPValue();
                setChangeBtnEnable(isCorrect && isChange());
                log.E("afterTextChanged   s:" + s);
            }
        });
        mNetMaskET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mCurIPType == 1){//动态获取
                    return;
                }
                mEthNetmask = mNetMaskET.getText().toString();
                if (TVStringUtil.isNetMask(mEthNetmask)){
                    mTvNetMaskError.setVisibility(View.INVISIBLE);
                }else{
                    mTvNetMaskError.setVisibility(View.VISIBLE);
                }
                boolean isCorrect = checkIPValue();
                setChangeBtnEnable(isCorrect && isChange());
                log.E("afterTextChanged   s:" + s);
            }
        });
        mGateWayET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mCurIPType == 1){//动态获取
                    return;
                }
                mEthGateway = mGateWayET.getText().toString();
                if (TVStringUtil.isIp(mEthGateway)){
                    mTvGateWayError.setVisibility(View.INVISIBLE);
                }else{
                    mTvGateWayError.setVisibility(View.VISIBLE);
                }
                boolean isCorrect = checkIPValue();
                setChangeBtnEnable(isCorrect && isChange());
                log.E("afterTextChanged   s:" + s);
            }
        });
        mDnsET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mCurIPType == 1){//动态获取
                    return;
                }
                mEthdns1 = mDnsET.getText().toString();
                if (TVStringUtil.isIp(mEthdns1)){
                    mTvDnsError.setVisibility(View.INVISIBLE);
                }else{
                    mTvDnsError.setVisibility(View.VISIBLE);
                }
                boolean isCorrect = checkIPValue();
                setChangeBtnEnable(isCorrect && isChange());
                log.E("afterTextChanged   s:" + s);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        resetTempData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mReceiver);
    }

    private void setEditEnable(boolean isEnable) {

//        mIPTypeTV.setFocusable(isEnable);
        mIPAddrET.setFocusable(isEnable);
        mNetMaskET.setFocusable(isEnable);
        mGateWayET.setFocusable(isEnable);
        mDnsET.setFocusable(isEnable);
    }

    private void setTempData(){
        mTempEthIpAddress = mEthIpAddress;
        mTempEthGateway = mEthGateway;
        mTempEthNetmask = mEthNetmask;
        mTempIPType = mCurIPType;
        mTempEthdns1 = mEthdns1;
    }
    private void resetTempData(){
        mTempEthIpAddress = nullIpInfo;
        mTempEthGateway = nullIpInfo;
        mTempEthNetmask = nullIpInfo;
        mTempIPType = 0;
        mTempEthdns1 = nullIpInfo;

        mCurIPType = 0;
        mEthHwAddress = nullIpInfo;
        mEthIpAddress = nullIpInfo;
        mEthNetmask = nullIpInfo;
        mEthGateway = nullIpInfo;
        mEthdns1 = nullIpInfo;
        mEthdns2 = nullIpInfo;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_CONNECTED_FAIL:
                    relogin();
                    break;

            }
            super.handleMessage(msg);
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            log.E("mReceiver Action " + action);
            TVStringUtil.writeToFile("TVApplication", "handleEtherStateChange。-11--接受网络广播："+action);
            if (EthernetManager.ETHERNET_STATE_CHANGED_ACTION.equals(action)) {
                    /*接收到以太网状态改变的广播*/
                int EtherState = intent.getIntExtra(EthernetManager.EXTRA_ETHERNET_STATE, -1);
                handleEtherStateChange(EtherState);
            }
        }
    };

    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        getContext().registerReceiver(mReceiver, intentFilter);
    }

    private void handleEtherStateChange(int EtherState) {
        log.E("handleEtherStateChange" + EtherState);
        String mStatusString = "";
        log.E("handleEtherStateChange" + EtherState + " mCurIPType:"+mCurIPType);
        TVStringUtil.writeToFile("TVApplication", "handleEtherStateChange。-11---mEthIpAddress---"+mEthIpAddress + "  EtherState:"+EtherState);

        switch (EtherState) {
            case EthernetManager.ETHER_STATE_DISCONNECTED:
                Toast.makeText(getActivity(),"网络断开",Toast.LENGTH_SHORT).show();
                mEthHwAddress = nullIpInfo;
                mEthIpAddress = nullIpInfo;
                mEthNetmask = nullIpInfo;
                mEthGateway = nullIpInfo;
                mEthdns1 = nullIpInfo;
                mEthdns2 = nullIpInfo;
                tips += "\n 网络断开。。。。。。";
                break;
            case EthernetManager.ETHER_STATE_CONNECTING:
                mStatusString = this.getResources().getString(R.string.ethernet_info_getting);
//                if(getContext() != null){
//                    Toast.makeText(getContext(),"正在设置...",Toast.LENGTH_SHORT).show();
//                }
                mEthHwAddress = mStatusString;
                mEthIpAddress = mStatusString;
                mEthNetmask = mStatusString;
                mEthGateway = mStatusString;
                mEthdns1 = mStatusString;
                mEthdns2 = mStatusString;
                tips += "\n 正在设置。。。";
                break;
            case EthernetManager.ETHER_STATE_CONNECTED:
                mHandler.removeMessages(SHOW_CONNECTED_FAIL);
                Toast.makeText(getActivity(),"网络连接成功",Toast.LENGTH_SHORT).show();

//                tips = "网络连接成功::mEthIpAddress:" + mEthIpAddress
//                        + " mEthNetmask:" + mEthNetmask
//                        + " mEthGateway:" + mEthGateway
//                        + " mEthdns1:" + mEthdns1
//                        + " mEthdns2:" + mEthdns2
//                        + " 连接成功。11。。TempIPType:" + mTempIPType +  " CurIPType:"+mCurIPType
//                        +" ip:"+mTempEthIpAddress
//                        + " mEthNetmask:" + mTempEthNetmask
//                        + " mEthGateway:" + mTempEthGateway
//                        + " mEthdns1:" + mTempEthdns1
//                        + " mEthdns2:" + mTempEthdns2
//                        + " isChange:"+isChange();;
//                log.E(tips);
//                setTips(logs);
                if(getContext() != null && isChange()){
//                    Toast.makeText(getContext(),R.string.m_modify_success,Toast.LENGTH_SHORT).show();
//                    log.E("网络连接成功：：--重新登陸----");
                    relogin();
                }
                getEthInfo();
//                tips = "网络连接成功22::mEthIpAddress:" + mEthIpAddress
//                        + " mEthNetmask:" + mEthNetmask
//                        + " mEthGateway:" + mEthGateway
//                        + " mEthdns1:" + mEthdns1
//                        + " mEthdns2:" + mEthdns2
//                        + " 连接成功。11。。TempIPType:" + mTempIPType +  " CurIPType:"+mCurIPType
//                        +" ip:"+mTempEthIpAddress
//                        + " mEthNetmask:" + mTempEthNetmask
//                        + " mEthGateway:" + mTempEthGateway
//                        + " mEthdns1:" + mTempEthdns1
//                        + " mEthdns2:" + mTempEthdns2
//                        + " isChange:"+isChange();;
//                log.E(tips);
                break;
        }
        refreshUI();
    }

    private void setTips(String tips){
//        mTipsTV.setText(tips);
    }

    private void relogin(){
        Intent intent = new Intent(getActivity(),LogoActivity.class);
        intent.putExtra("type", FragmentLogo.ShowUIStatus.SHOW_RELOGIN);
        LoginUtil.getInstance().setLoginStatus(getContext(), SRIMConfigure.State.hj_Login_fail);
        startActivity(intent);
        getActivity().finish();
    }
    private void showConnectedFail(){
        mHandler.removeMessages(SHOW_CONNECTED_FAIL);
        mHandler.sendEmptyMessageDelayed(SHOW_CONNECTED_FAIL,1 * 1000);
    }


    private void refreshUI() {
        log.E("refreshUI（）---------------");
        String logs = "refreshUI():::mEthIpAddress:" + mEthIpAddress
                + " mEthNetmask:" + mEthNetmask
                + " mEthGateway:" + mEthGateway
                + " mEthdns1:" + mEthdns1
                + " mEthdns2:" + mEthdns2;
        log.E(logs);
//        setTips(logs);
        mIPAddrET.setText(mEthIpAddress);
        mGateWayET.setText(mEthGateway);
        mNetMaskET.setText(mEthNetmask);
        mIPTypeTV.setText(IPType[mCurIPType]);
        mDnsET.setText(mEthdns1);
//        IpConfiguration.IpAssignment mode = mEthManager.getConfiguration().getIpAssignment();
//        mCurIPType = mode.ordinal();
        log.E("refreshUI（）mCurIPType："+mCurIPType);
        if (mCurIPType == IpConfiguration.IpAssignment.DHCP.ordinal()) {
            setEditEnable(false);
        } else {
            setEditEnable(true);
        }
//        setStringSummary(KEY_ETH_HW_ADDRESS,mEthHwAddress);//zhg++
//        setStringSummary(KEY_ETH_IP_ADDRESS, mEthIpAddress);
//        setStringSummary(KEY_ETH_NET_MASK, mEthNetmask);
//        setStringSummary(KEY_ETH_GATEWAY, mEthGateway);
//        setStringSummary(KEY_ETH_DNS1, mEthdns1);
//        setStringSummary(KEY_ETH_DNS2, mEthdns2);
//        updateCheckbox();
    }

    public void getEthInfo() {
        log.E("getEthInfo(): " + mEthManager.getEthernetIfaceName());
        mEthHwAddress = mEthManager.getEthernetHwaddr(mEthManager.getEthernetIfaceName());//zhg++
        if (mEthHwAddress == null) mEthHwAddress = nullIpInfo;//
        log.E("getEthInfo(): mEthHwAddress：" + mEthHwAddress);
        IpConfiguration.IpAssignment mode = mEthManager.getConfiguration().getIpAssignment();

        mCurIPType = mode.ordinal();
        if (mode == IpConfiguration.IpAssignment.DHCP) {
            //getEth from dhcp
            getEthInfoFromDhcp();
        } else if (mode == IpConfiguration.IpAssignment.STATIC) {
            //get static IP
            getEthInfoFromStaticIp();
        }

        setTempData();
    }

    private boolean setStaticIpConfiguration() {
        log.E("setStaticIpConfiguration():");
        StaticIpConfiguration mStaticIpConfiguration = new StaticIpConfiguration();
        //get ip address, netmask,dns ,gw etc.
        Inet4Address inetAddr = getIPv4Address(this.mEthIpAddress);
        int prefixLength = maskStr2InetMask(this.mEthNetmask);
        InetAddress gatewayAddr = getIPv4Address(this.mEthGateway);
        InetAddress dnsAddr = getIPv4Address(this.mEthdns1);
        String logs = "setStaticIpConfiguration(): mEthIpAddress:" +this.mEthIpAddress+ " mEthNetmask:"+this.mEthNetmask
                +" mEthGateway:"+this.mEthGateway +" mEthdns1:"+this.mEthdns1;
        setTips(logs);
        if (inetAddr.getAddress().toString().isEmpty() || prefixLength == 0 || gatewayAddr.toString().isEmpty()
                || dnsAddr.toString().isEmpty()) {
            log.E("ip,mask or dnsAddr is wrong");
            return false;
        }

        String dnsStr2 = this.mEthdns2;
        //编译不过，临时注释 bycui
//        mStaticIpConfiguration.ipAddress = new LinkAddress(inetAddr, prefixLength);
        mStaticIpConfiguration.gateway = gatewayAddr;
        mStaticIpConfiguration.dnsServers.add(dnsAddr);

        if (!dnsStr2.isEmpty()) {
            mStaticIpConfiguration.dnsServers.add(getIPv4Address(dnsStr2));
        }
        mIpConfiguration = new IpConfiguration(IpConfiguration.IpAssignment.STATIC, IpConfiguration.ProxySettings.NONE, mStaticIpConfiguration, null);
        return true;
    }

    public void getEthInfoFromDhcp() {
        String tempIpInfo;
        String iface = "eth0";

        tempIpInfo = SystemProperties.get("dhcp." + iface + ".ipaddress");

        if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
            mEthIpAddress = tempIpInfo;
        } else {
            mEthIpAddress = nullIpInfo;
        }

        tempIpInfo = SystemProperties.get("dhcp." + iface + ".mask");
        if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
            mEthNetmask = tempIpInfo;
        } else {
            mEthNetmask = nullIpInfo;
        }

        tempIpInfo = SystemProperties.get("dhcp." + iface + ".gateway");
        if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
            mEthGateway = tempIpInfo;
        } else {
            mEthGateway = nullIpInfo;
        }

        tempIpInfo = SystemProperties.get("dhcp." + iface + ".dns1");
        if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
            mEthdns1 = tempIpInfo;
        } else {
            mEthdns1 = nullIpInfo;
        }

        tempIpInfo = SystemProperties.get("dhcp." + iface + ".dns2");
        if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
            mEthdns2 = tempIpInfo;
        } else {
            mEthdns2 = nullIpInfo;
        }
        String logs = "getEthInfoFromDhcp():::mEthIpAddress:" + mEthIpAddress
                + " mEthNetmask:" + mEthNetmask
                + " mEthGateway:" + mEthGateway
                + " mEthdns1:" + mEthdns1
                + " mEthdns2:" + mEthdns2;
        log.E(logs);
        setTips(logs);
    }


    public void getEthInfoFromStaticIp() {
        StaticIpConfiguration staticIpConfiguration = mEthManager.getConfiguration().getStaticIpConfiguration();

        if (staticIpConfiguration == null) {
            return;
        }
        LinkAddress ipAddress = staticIpConfiguration.ipAddress;
        InetAddress gateway = staticIpConfiguration.gateway;
        ArrayList<InetAddress> dnsServers = staticIpConfiguration.dnsServers;

        if (ipAddress != null) {
            mEthIpAddress = ipAddress.getAddress().getHostAddress();
            mEthNetmask = interMask2String(ipAddress.getPrefixLength());
        }
        if (gateway != null) {
            mEthGateway = gateway.getHostAddress();
        }
        mEthdns1 = dnsServers.get(0).getHostAddress();

        if (dnsServers.size() > 1) { /* 只保留两个*/
            mEthdns2 = dnsServers.get(1).getHostAddress();
        }

        String logs = "getEthInfoFromStaticIp():::mEthIpAddress:" + mEthIpAddress
                + " mEthNetmask:" + mEthNetmask
                + " mEthGateway:" + mEthGateway
                + " mEthdns1:" + mEthdns1
                + " mEthdns2:" + mEthdns2;
        log.E(logs);
//        setTips(logs);
    }

    //将子网掩码转换成ip子网掩码形式，比如输入32输出为255.255.255.255
    public String interMask2String(int prefixLength) {
        String netMask = null;
        int inetMask = prefixLength;

        int part = inetMask / 8;
        int remainder = inetMask % 8;
        int sum = 0;

        for (int i = 8; i > 8 - remainder; i--) {
            sum = sum + (int) Math.pow(2, i - 1);
        }

        if (part == 0) {
            netMask = sum + ".0.0.0";
        } else if (part == 1) {
            netMask = "255." + sum + ".0.0";
        } else if (part == 2) {
            netMask = "255.255." + sum + ".0";
        } else if (part == 3) {
            netMask = "255.255.255." + sum;
        } else if (part == 4) {
            netMask = "255.255.255.255";
        }

        return netMask;
    }

    /*
     * convert subMask string to prefix length
     */
    private int maskStr2InetMask(String maskStr) {
        StringBuffer sb;
        String str;
        int inetmask = 0;
        int count = 0;
    	/*
    	 * check the subMask format
    	 */
        Pattern pattern = Pattern.compile("(^((\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$)|^(\\d|[1-2]\\d|3[0-2])$");
        if (pattern.matcher(maskStr).matches() == false) {
            log.E("maskStr2InetMask（）：subMask is error");
            return 0;
        }

        String[] ipSegment = maskStr.split("\\.");
        for (int n = 0; n < ipSegment.length; n++) {
            sb = new StringBuffer(Integer.toBinaryString(Integer.parseInt(ipSegment[n])));
            str = sb.reverse().toString();
            count = 0;
            for (int i = 0; i < str.length(); i++) {
                i = str.indexOf("1", i);
                if (i == -1)
                    break;
                count++;
            }
            inetmask += count;
        }
        return inetmask;
    }

    private Inet4Address getIPv4Address(String text) {
        try {
            return (Inet4Address) NetworkUtils.numericToInetAddress(text);
        } catch (IllegalArgumentException | ClassCastException e) {
            return null;
        }
    }

//    private int getMode(){
//        IpConfiguration.IpAssignment mode =mEthManager.getConfiguration().getIpAssignment();
//
//
//        if ( mode== IpConfiguration.IpAssignment.DHCP) {
//	   /*
//	    * getEth from dhcp
//	   */
//            return 1;
//        } else if(mode == IpConfiguration.IpAssignment.STATIC) {
//	   /*
//	    * TODO: get static IP
//	   */
//            return 0;
//        }
//        return 0;
//    }

    private void updateIpSettingsInfo() {
        log.E("Static IP status updateIpSettingsInfo");
        ContentResolver contentResolver = getActivity().getContentResolver();
        String staticip = System.getString(contentResolver,
                ETHERNET_STATIC_IP);
        if (!TextUtils.isEmpty(staticip))
            mIPAddrET.setText(staticip);

        String ipmask = System.getString(contentResolver,
                ETHERNET_STATIC_NETMASK);
        if (!TextUtils.isEmpty(ipmask))
            mNetMaskET.setText(ipmask);

        String gateway = System.getString(contentResolver,
                ETHERNET_STATIC_GATEWAY);
        if (!TextUtils.isEmpty(gateway))
            mGateWayET.setText(gateway);

        String dns1 = System.getString(contentResolver,
                ETHERNET_STATIC_DNS1);
        if (!TextUtils.isEmpty(dns1))
            mDnsET.setText(dns1);

//        String dns2 = System.getString(contentResolver,
//                System.ETHERNET_STATIC_DNS2);
//        if (!TextUtils.isEmpty(dns2))
//            mdns2.setText(dns2);
    }
    public void saveStaticIpSettingInfo() {
        log.E("saveStaticIpSettingInfo():");
        ContentResolver contentResolver = getContext().getContentResolver();
        mEthIpAddress = mIPAddrET.getText().toString();
        mEthGateway = mGateWayET.getText().toString();
        mEthNetmask = mNetMaskET.getText().toString();
        mEthdns1 = mDnsET.getText().toString();
//        String dns2 = mdns2.getText().toString();
        int network_prefix_length = 24;// Integer.parseInt(ipnetmask.getText().toString());
        // 保存 "是否使用静态 IP" 的配置
        System.putInt(contentResolver, ETHERNET_USE_STATIC_IP, 0);
        if (!TextUtils.isEmpty(mEthIpAddress)) { // not empty
            System.putString(contentResolver, ETHERNET_STATIC_IP, mEthIpAddress);
        } else {
            System.putString(contentResolver, ETHERNET_STATIC_IP, null);
        }
        if (!TextUtils.isEmpty(mEthGateway)) { // not empty
            System.putString(contentResolver, ETHERNET_STATIC_GATEWAY,
                    mEthGateway);
        } else {
            System.putString(contentResolver, ETHERNET_STATIC_GATEWAY,
                    null);
        }
        if (!TextUtils.isEmpty(mEthNetmask)) { // not empty
            System.putString(contentResolver, ETHERNET_STATIC_NETMASK,
                    mEthNetmask);
        } else {
            System.putString(contentResolver, ETHERNET_STATIC_NETMASK,
                    null);
        }
        if (!TextUtils.isEmpty(mEthdns1)) { // not empty
            System.putString(contentResolver, ETHERNET_STATIC_DNS1, mEthdns1);
        } else {
            System.putString(contentResolver, ETHERNET_STATIC_DNS1, null);
        }
//        if (!TextUtils.isEmpty(dns2)) { // not empty
//            System.putString(contentResolver, ETHERNET_STATIC_DNS2, dns2);
//        } else {
//            System.putString(contentResolver, ETHERNET_STATIC_DNS2, null);
//        }
		/*
		 * 回调传给EthernetSetting
		 */
//        mGetStaticInfo.getStaticIp(ipAddr);
//        mGetStaticInfo.getStaticNetMask(netMask);
//        mGetStaticInfo.getStaticGateway(gateway);
//        mGetStaticInfo.getStaticDns1(dns1);
//        mGetStaticInfo.getStaticDns2(dns2);
    }

    /*
     * 返回 指定的 String 是否是 有效的 IP 地址.
     */
    private boolean isValidIpAddress(String value) {
        int start = 0;
        int end = value.indexOf('.');
        int numBlocks = 0;

        while (start < value.length()) {

            if (-1 == end) {
                end = value.length();
            }

            try {
                int block = Integer.parseInt(value.substring(start, end));
                if ((block > 255) || (block < 0)) {
                    log.W("EthernetIP isValidIpAddress() : invalid 'block', block = "
                            + block);
                    return false;
                }
            } catch (NumberFormatException e) {
                log.W("EthernetIP isValidIpAddress() : e = " + e);
                return false;
            }

            numBlocks++;

            start = end + 1;
            end = value.indexOf('.', start);
        }
        return numBlocks == 4;
    }

    public boolean checkIPValue() {
        boolean enable = false;
        Pattern pattern = Pattern.compile("(^((\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$)|^(\\d|[1-2]\\d|3[0-2])$"); /*check subnet mask*/
//        if (isValidIpAddress(mEthIpAddress) && isValidIpAddress(mEthGateway)
//                && isValidIpAddress(mEthdns1) && (pattern.matcher(mEthNetmask).matches())) {
          if (TVStringUtil.isIp(mEthIpAddress) && TVStringUtil.isIp(mEthGateway)
                  && TVStringUtil.isIp(mEthdns1) && (TVStringUtil.isNetMask(mEthNetmask))) {
//            if (TextUtils.isEmpty(dns2)) { // 为空可以不考虑
//                enable = true;
//            } else {
//                if (isValidIpAddress(dns2)) {
//                    enable = true;
//                } else {
//                    enable = false;
//                }
//            }
            enable = true;
        } else {
            enable = false;
        }
        setChangeBtnEnable(enable);
        return enable;
    }

    private boolean isChange() {
        if (!StringUtil.isSameString(mTempEthIpAddress, mEthIpAddress)
                || !StringUtil.isSameString(mTempEthGateway, mEthGateway)
                || !StringUtil.isSameString(mTempEthNetmask, mEthNetmask)
                || !StringUtil.isSameString(mTempEthdns1, mEthdns1)
                || mTempIPType != mCurIPType) {
            return true;
        }
        return false;
    }

    private void setChangeBtnEnable(boolean isEnable) {
        mChangeBtn.setEnabled(isEnable);
        mChangeBtn.setFocusable(isEnable);
    }


    private List<LocationBean> getdata() {
        List<LocationBean> list = new ArrayList<LocationBean>();
        for (int i = 0; i < IPType.length; i++) {
            LocationBean l = new LocationBean();
            l.setName(IPType[i]);
            list.add(l);
        }
        return list;
    }

    private void showIPTypeSelect() {
        if (mLocationDialog == null) {
            mLocationDialog = new LocationDialog(getContext(), mIPTypeTV);
        }
        mLocationDialog.show(mIPTypeTV, getdata(), mIPTypeTV);
        mLocationDialog.setOnClickTypeListener(new LocationDialog.onClickTypeListener() {
            @Override
            public void setOnClickType(int type) {
                log.E("setOnClickTypeListener  ---type:" + type + "  mode:" + mCurIPType);
                if (mCurIPType == type) {
                    return;
                }
                mCurIPType = type;
                if (type == 0) {//静态IP
                    TVStringUtil.writeToFile("TVApplication", "handleEtherStateChange。-11---选择了静态IP-");
                    setChangeBtnEnable(true);
                    getEthInfoFromStaticIp();
                    refreshUI();
                    saveStaticIpSettingInfo();
                    updateIpSettingsInfo();
                } else if (type == 1) {//动态获取
                    setChangeBtnEnable(false);
                    mEthManager.setConfiguration(new IpConfiguration(IpConfiguration.IpAssignment.DHCP, IpConfiguration.ProxySettings.NONE, null, null));
                    int state = mEthManager.getEthernetIfaceState();
                    TVStringUtil.writeToFile("TVApplication", "handleEtherStateChange。-11---选择了动态IP-state:"+state);
                    log.E("switch to dhcp:state:"+state);
//                    getEthInfoFromDhcp();
//                    refreshUI();
                    showConnectedFail();
                }


            }
        });
    }


}
