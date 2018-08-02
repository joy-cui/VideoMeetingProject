package org.suirui.huijian.tv.activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.suirui.pub.business.contant.SRErrorCode;
import com.suirui.pub.business.contant.SRIMConfigure;
import com.suirui.pub.business.util.LoginSharePreferencesUtil;
import com.suirui.srpaas.base.NetBean;
import com.suirui.srpaas.base.ui.activity.BaseAppCompatActivity;
import com.suirui.srpaas.base.util.CommonUtils;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.base.util.receiver.NetStateReceiver;
import com.suirui.srpaas.video.model.impl.ConfigureModelImpl;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.util.LoginUtil;
import org.suirui.huijian.tv.widget.UIUtil;


/**
 * 引导加载界面
 **/
public class WelcomeActivity extends BaseAppCompatActivity{
    private static final SRLog log = new SRLog(WelcomeActivity.class.getName(), TVAppConfigure.LOG_LEVE);
//    private IConfigurePrestener configurePrestener = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        log.E("WelcomeActivity....onCreate()...screen Density is "+displayMetrics.density+" densityDpi is "+displayMetrics.densityDpi+" height: "+displayMetrics.heightPixels+
                " width: "+displayMetrics.widthPixels);
        String screenSize  = UIUtil.getScreenCategoryName(WelcomeActivity.this);
        UIUtil.getScreenSizeOfDevice2(WelcomeActivity.this);
        UIUtil.getDisplayInfomation(WelcomeActivity.this);
        setContentView(R.layout.m_welcome);
        //test
        int loginState = LoginSharePreferencesUtil.getHJLoginState(mContext);
        log.E("suid   loadServer_URL:"+LoginSharePreferencesUtil.getLoginSuid(mContext));
        if (loginState == SRIMConfigure.State.hj_Login_success || loginState == SRIMConfigure.State.hj_tempLogin_success) {
            startActivity(new Intent(this,MainActivity.class));
            this.finish();
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
        log.E("WelcomeActivity...NetStateReceiver.. wifiState:" + NetStateReceiver.isNetworkAvailable());
    }

    @Override
    protected void onNetworkDisConnected() {

    }

    @Override
    protected boolean isSupportActionBar() {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        log.E("onKeyDown（）：" + event.getAction() + " keyCode:" + event.getKeyCode());
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {  //表示按返回键
                case KeyEvent.KEYCODE_ENTER:
                    skipLogin();
                return true;    //已处理
                case KeyEvent.KEYCODE_BACK:
                    moveTaskToBack(true);
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        skipLogin();
        return super.dispatchTouchEvent(ev);
    }

    private void skipLogin(){
        if(LoginUtil.getInstance().isLogin(WelcomeActivity.this)){
            startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
            return;
        }
        startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
    }

}
