package org.suirui.huijian.tv.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.suirui.srpaas.base.NetBean;
import com.suirui.srpaas.base.ui.activity.BaseAppCompatActivity;
import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.login.FragmentFail;
import org.suirui.huijian.tv.activity.login.FragmentInput;
import org.suirui.huijian.tv.activity.login.FragmentLogo;
import org.suirui.huijian.tv.activity.login.FragmentNetFail;
import org.suirui.huijian.tv.activity.setting.BaseFragment;
import org.suirui.huijian.tv.activity.setting.FragmentSettingNet;
import org.suirui.huijian.tv.util.AppUtil;
import org.suirui.huijian.tv.util.MultiKeyUtil;
import org.suirui.huijian.tv.util.multKey.MultiKeyImpl;

import java.util.Arrays;
import java.util.HashSet;
//import org.suirui.huijian.tv.permission.MPermission;


/**
 * 账号初始化注册登录activity
 */
public class LogoActivity extends BaseAppCompatActivity implements BaseFragment.onMyFragmentListener{
    private static final SRLog log = new SRLog(LogoActivity.class.getName(), TVAppConfigure.LOG_LEVE);
    private FragmentTransaction mFtransaction;
    private FragmentLogo mFragmentLogo;
    private FragmentNetFail mFragmentNetFail;
    private FragmentFail mFragmentFail;
    private String mHttp = "";
    private String mHost = "";
    private String mAddress = "";
    private String mNick = "";
    private int modifyType = 0;
    private int mType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m_logo_layout);
        Intent intent = getIntent();
        if(intent != null){
            modifyType = intent.getIntExtra("modifyType",0);
            mType = intent.getIntExtra("type", 0);
            mHttp = intent.getStringExtra("http");
            mAddress = intent.getStringExtra("address");
            mHost = intent.getStringExtra("ip");
            mNick = intent.getStringExtra("nick");
        }
        if(mType == FragmentLogo.ShowUIStatus.SHOW_NET_FAIL){
            showNetFailView();
        }else if(mType == FragmentLogo.ShowUIStatus.SERVER_CONNECT_FAIL){
            showServerFailView();
        }else{
            showLogoView();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
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
//        log.E("onNetworkConnected()");
    }

    @Override
    protected void onNetworkDisConnected() {
//        log.E("onNetworkDisConnected()");

    }

    @Override
    protected boolean isSupportActionBar() {
        return false;
    }

    @Override
    public void onBackPressed() {

//        BaseFragment ft = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.main_content);
//        log.E("onBackPressed() curFragment:"+ft);
//        if (!this.getFragmentManager().popBackStackImmediate() ) {
//            if((ft instanceof FragmentSettingNet) || (ft instanceof FragmentInput)){
//                super.onBackPressed();
//            }
////            supportFinishAfterTransition();
//            return;
//        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        log.E("onKeyDown() keyCode:"+keyCode);
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            if(keyCode == KeyEvent.KEYCODE_BACK){
                if(!AppUtil.getInstance().isShowRootUI()){
                    onBackPressed();
                    return true;
                }
                BaseFragment ft = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.main_content);
                log.E("onBackPressed() curFragment:"+ft);
                if (!this.getFragmentManager().popBackStackImmediate() ) {
                    if((ft instanceof FragmentSettingNet) || (ft instanceof FragmentInput)){
                        onBackPressed();
                    }
//            supportFinishAfterTransition();
                    return true;
                }
            }
        }
        return MultiKeyUtil.getInstance().onMultiKey(LogoActivity.this,keyCode,event,
                new MultiKeyImpl(),MultiKeyUtil.mMultKey_sysSet1,
                new HashSet<>(Arrays.asList(MultiKeyUtil.mMultKey_sysSet1)));
    }

    private void showLogoView() {
        log.E("showLogoView():");
        mFtransaction = getSupportFragmentManager().beginTransaction();
        if(mFragmentLogo == null) {
            mFragmentLogo = new FragmentLogo();
            mFtransaction.add(R.id.main_content, mFragmentLogo);
        }else{
            mFtransaction.show(mFragmentLogo);
        }
        if(modifyType !=0){
            Bundle args = new Bundle();
            args.putInt("modifyType",modifyType);
            args.putInt("type",mType);
            args.putString("http",mHttp);
            args.putString("address",mAddress);
            args.putString("ip",mHost);
            args.putString("nick",mNick);
            mFragmentLogo.setArguments(args);
        }
        if(mType == FragmentLogo.ShowUIStatus.SHOW_RELOGIN){
            Bundle args = new Bundle();
            args.putInt("type",mType);
            mFragmentLogo.setArguments(args);
        }
        mFtransaction.commit();
    }

    private void showNetFailView() {
        log.E("showNetFailView():");
        mFtransaction = getSupportFragmentManager().beginTransaction();
        if(mFragmentNetFail == null) {
            mFragmentNetFail = new FragmentNetFail();
            mFtransaction.add(R.id.main_content, mFragmentNetFail);
        }else{
            mFtransaction.show(mFragmentNetFail);
        }
        mFtransaction.commit();
    }

    private void showServerFailView() {
        log.E("showNetFailView():");
        mFtransaction = getSupportFragmentManager().beginTransaction();
        if(mFragmentFail == null) {
            mFragmentFail = new FragmentFail();
            mFtransaction.add(R.id.main_content, mFragmentFail);
        }else{
            mFtransaction.show(mFragmentFail);
        }
        mFtransaction.commit();
    }

    @Override
    public void onChangeTitle(String title) {
    }
}
