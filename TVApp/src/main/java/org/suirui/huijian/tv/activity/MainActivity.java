/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.suirui.huijian.tv.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.suirui.pub.business.manage.IMiddleManage;
import com.suirui.srpaas.base.NetBean;
import com.suirui.srpaas.base.ui.activity.BaseAppCompatActivity;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.base.util.receiver.NetStateReceiver;
import com.suirui.srpaas.common.http.okhttp.utils.NetworkUtil;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.util.StringUtil;

import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.login.FragmentLogo;
import org.suirui.huijian.tv.activity.login.FragmentMange;
import org.suirui.huijian.tv.prestener.impl.MeetingPrestener;
import org.suirui.huijian.tv.util.AndroidUtils;
import org.suirui.huijian.tv.util.AppUtil;
import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.util.DateUtil;
import org.suirui.huijian.tv.util.LoginUtil;
import org.suirui.huijian.tv.util.TVNetworkUtil;
import org.suirui.huijian.tv.util.TVStringUtil;
import org.suirui.huijian.tv.widget.UIUtil;
import org.suirui.websocket.client.manager.WsClientManager;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends BaseAppCompatActivity implements Observer {
    SRLog log=new SRLog(MainActivity.class.getName());
    Handler mHandler = new Handler();
    Timer mTimeTimer = new Timer();
    MeetingPrestener mMeetingPrestener = new MeetingPrestener();
    TimerTask mTimerTask;
    TextView mNameTV;
    TextView mBottomINfoTV;
    TextView mTimeTV;
    TextView mTimeRangeTV;
    TextView mDateTV;
    TextView mWeekTV;
    TextView mLoginStatusTV;
    TextView mEthernetStatusTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findView();
        IMiddleManage.getInstance().addObserver(this);
//        NetStateReceiver.registerNetworkStateReceiver(this);//注册网络状态
        log.E("MainActivity.setupWebSocket.onCreate"+TVAppConfigure.SERVER_ADRESS+" : "+ThirdApi.getIntance(this).getServerAdress());
//        LoginUtil.getInstance().initWebSocket(this, TVAppConfigure.SERVER_ADRESS);

        WsClientManager.getInstance(MainActivity.this).initWebSocketClient(ThirdApi.getIntance(this).getServerAdress(),
                new WsClientManager.OnWebSocketClientListener(){
                    @Override
                    public void onConnectSuccess() {
                        //登录
                       log.E("setupWebSocket....开始登录client");
                        LoginUtil.getInstance().loginWebSocket(MainActivity.this);
                    }
                });
//        String ANDROID_ID  = AndroidUtils.getInstance().getAndroidId(this);
//        String SerialNumber = AndroidUtils.getInstance().getSerialNumber();
//        String deviceid = AndroidUtils.getInstance().getDeviceID(this);
//        String SerialNum = HardwareUtil.getCPUSerial();
//        log.E("AndroidUtils------"
//                +"\n ANDROID_ID"+ANDROID_ID
//                +"\n SerialNumber:"+SerialNumber
////                +"\n SerialNum:"+SerialNum
//                +"\n deviceid:"+deviceid

//        );

//        Point point = new Point();
//        WindowManager windowMgr = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
//        windowMgr.getDefaultDisplay().getSize(point);
//        log.E("AndroidUtils------the screen size is "+point.toString());
//        windowMgr.getDefaultDisplay().getRealSize(point);
//        log.E("AndroidUtils------the screen real size is "+point.toString());

//        UIUtil.getScreenSizeOfDevice2(MainActivity.this);
//        UIUtil.getDisplayInfomation(MainActivity.this);
//        log.E("oncreate--isShowRootUI :"+AppUtil.getInstance().isShowRootUI());
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!ThirdApi.getIntance(this).isConnectState() && !NetworkUtil.hasDataNetwork(this)){
            Intent intent = new Intent(getApplicationContext(), LogoActivity.class);
            intent.putExtra("type", FragmentLogo.ShowUIStatus.SHOW_NET_FAIL);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNameTV.setText(LoginUtil.getInstance().getNickName(this));
        showBottonInfo();
        showLoginStatus(LoginUtil.getInstance().isLogin(MainActivity.this));
        timeTimerTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        IMiddleManage.getInstance().deleteObserver(this);
        cancelTimer();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        NetStateReceiver.unRegisterNetworkStateReceiver(this);
    }

    @Override
    protected void onSensorEventChange(boolean b) {

    }

    @Override
    protected void onHeadsetStatus(boolean b) {

    }

    @Override
    protected void onBluetooth(int i, BluetoothAdapter bluetoothAdapter) {

    }

    @Override
    protected void onNetworkConnected(NetBean netBean) {
        TVStringUtil.writeToFile("TVApplication","MainActivity。。onNetworkDisConnected....网络连接");
        setEthernetStatusImage();
        showBottonInfo();
    }

    @Override
    protected void onNetworkDisConnected() {
        TVStringUtil.writeToFile("TVApplication","MainActivity。。onNetworkDisConnected....网络断开");
        setEthernetStatusImage();
    }

    @Override
    protected boolean isSupportActionBar() {
        return false;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof IMiddleManage) {
            if (data instanceof IMiddleManage.NotifyCmd) {
                final IMiddleManage.NotifyCmd cmd = (IMiddleManage.NotifyCmd) data;
                switch (cmd.type) {
                    case LOGIN_FAIL:
                        showLoginStatus(false);
                        break;
                    case LOGIN_STATE:
                        boolean status = (boolean) cmd.data;
                        showLoginStatus(status);
                        break;
                }
            }
        }
    }

    public void onClickBtnStartMeeting(View view) {
        mMeetingPrestener.startMeeting(MainActivity.this);
    }

    public void onClickBtnJoinMeeting(View view) {
        startActivity(new Intent(MainActivity.this, JoinMeetingActivity.class));
    }

    public void onClickBtnListMeeting(View view) {
        startActivity(new Intent(this,MeetingListActivity.class));

    }

    public void onClickBtnSetting(View view) {
        startActivity(new Intent(this,SettingActivity.class));
    }

    private void findView() {
        mNameTV = (TextView) getDelegate().findViewById(R.id.meetingNameTV);
        mBottomINfoTV = (TextView) getDelegate().findViewById(R.id.bottomInfoTV);
        mTimeTV = (TextView) getDelegate().findViewById(R.id.timeTV);
        mTimeRangeTV = (TextView) getDelegate().findViewById(R.id.timeRangeTV);
        mDateTV = (TextView) getDelegate().findViewById(R.id.dateTV);
        mWeekTV = (TextView) getDelegate().findViewById(R.id.weekTV);
        mLoginStatusTV = (TextView) getDelegate().findViewById(R.id.loginStatusTV);
        mEthernetStatusTV = (TextView) getDelegate().findViewById(R.id.ethernetStatusTV);
    }

    private void setEthernetStatusImage(){
        if(NetworkUtil.isEthernetConnected(this)){
            mEthernetStatusTV.setBackgroundResource(R.drawable.m_icon_ethernet_success);
        }else{
            mEthernetStatusTV.setBackgroundResource(R.drawable.m_icon_ethernet_fail);
        }
    }

    private void showBottonInfo() {
        String ip = NetworkUtil.getInstance(this).getIpAddress();
        mBottomINfoTV.setText(getString(
                R.string.m_main_layout_bottom_info,
                new Object[]{
                        NetworkUtil.getInstance(this).getSSID(),
                        AppUtil.getInstance().getAppVersionName(MainActivity.this),
                        StringUtil.isEmpty(ip) ? "0.0.0.0" : ip}));
    }

    public void showTime() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTimeTV.setText(DateUtil.getCurrentSysTime());
                mTimeRangeTV.setText(DateUtil.getCurrentTimeRange());
                mDateTV.setText(DateUtil.getCurrentData());
                mWeekTV.setText(DateUtil.getCurrentWeek());
            }
        });
    }

    private void showLoginStatus(boolean isLogin) {
        if (isLogin) {
            mLoginStatusTV.setBackgroundResource(R.drawable.m_icon_login_success);
        } else {
            mLoginStatusTV.setBackgroundResource(R.drawable.m_icon_login_fail);
        }
    }

    private void timeTimerTask() {
        cancelTimer();
        if (mTimeTimer == null) {
            mTimeTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    showTime();
                }
            };
        }

        mTimeTimer.schedule(mTimerTask, 0, 1000);//延时1s执行
    }

    private void cancelTimer() {
        if (mTimeTimer != null) {
            mTimeTimer.cancel();
            mTimeTimer = null;
            mTimerTask = null;
        }
        if (mTimerTask != null) {
            mTimerTask = null;
        }
    }

}
