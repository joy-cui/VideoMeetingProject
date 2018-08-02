package org.suirui.huijian.box;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.suirui.pub.business.contant.SRErrorCode;
import com.suirui.pub.business.manage.IMiddleManage;
import com.suirui.pub.business.util.LoginSharePreferencesUtil;
import com.suirui.srpaas.base.NetBean;
import com.suirui.srpaas.base.ui.activity.BaseAppCompatActivity;
import com.suirui.srpaas.base.util.CommonUtils;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.base.util.receiver.NetStateReceiver;
import com.suirui.srpaas.video.model.impl.ConfigureModelImpl;
import com.suirui.srpaas.video.third.ThirdApiListener;
import com.suirui.srpaas.video.ui.activity.SRVideoActivity;
import com.suirui.srpaas.video.util.ToolsUtil;
import com.suirui.srpaas.video.util.TvToolsUtil;

import org.suirui.huijian.box.manage.SRMiddleManage;
import org.suirui.huijian.box.prestener.IConfigurePrestener;
import org.suirui.huijian.box.prestener.Impl.ConfigurePrestenerImpl;
import org.suirui.huijian.box.html.callback.CallJsUtil;
import org.suirui.huijian.box.view.IMainView;
import org.suirui.huijian.box.html.HomeActivity;
import org.suirui.transfer.SRSdkType;
import org.suirui.transfer.SRTransfer;

import java.util.Observable;
import java.util.Observer;

/**
 * 引导加载界面
 *
 * @authordingna
 * @date2017-06-07
 **/
public class WelcomeActivity extends BaseAppCompatActivity implements ThirdApiListener, IMainView,Observer{
    private static final SRLog log = new SRLog(WelcomeActivity.class.getName(), AppConfigure.LOG_LEVE);
    private IConfigurePrestener configurePrestener = null;
    private BridgeWebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.E("WelcomeActivity....onCreate()...");
        setContentView(R.layout.logo);
        webView = (BridgeWebView) this.findViewById(R.id.webview);
        webView.setDefaultHandler(new DefaultHandler());
        webView.loadUrl("file:///android_asset/HuiJianHtml/html/HuiJianBox/logo.html");
        registerHandler(webView);
        IMiddleManage.getInstance().addObserver(this);

        NetStateReceiver.registerNetworkStateReceiver(this);//注册网络状态
        configurePrestener = new ConfigurePrestenerImpl(this, this, this.isTaskRoot());
        configurePrestener.getMainIntent(getIntent());
        int meetState = SRMiddleManage.getInstance().getMeetingState();
        log.E("WelcomeActivity....onCreate()...:" + this.isTaskRoot() + " meetState:" + meetState);
        String versionNum = ToolsUtil.getCurrentVersionNum();
        LoginSharePreferencesUtil.setVersionNum(WelcomeActivity.this, versionNum);
        if (meetState != ConfigureModelImpl.MeetState.MEETING_ING) {//by test 20170627,在会议中后台，再到前台时，没有进入到会议中
//            setContentView(R.layout.hj_welcome_layout);
//            TextView hj_version = (TextView) findViewById(R.id.hj_version);
//            log.E("WelcomeActivity....onCreate()...versionNum:" + versionNum);
//            hj_version.setText(versionNum);

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

    public void registerHandler(final BridgeWebView mWebView){
        mWebView.registerHandler(AppConfigure.RegisterMethod.CALL_NATIVE_HANDLER, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                //收到js的消息
                String op = org.suirui.huijian.box.html.util.ToolsUtil.getJsonValue(data, AppConfigure.MethodParam.OP);
                log.E("handler = data:= " + data + " op:" + op);
                if (op == null) return;
                switch (op) {
                    default:
                        SRTransfer.getInstance().callNative(WelcomeActivity.this, SRSdkType.SRSdkType_SDK, data, function);
                        break;
                }
            }
        });
    }

    /**
     * 跳转相应的界面Activity
     */
    public void doActivity(int uiType, Bundle b) {
        log.E("SRMiddleManage...222222222222登录成功后跳转到首页ype:");
        Intent intent = null;
        switch (uiType) {
            case AppConfigure.UI.LOGIN_AT:
//                intent = new Intent(WelcomeActivity.this, LoginActivity.class);
//                if (b != null) {
//                    intent.putExtras(b);
//                }
//                startActivity(intent);
//                finish();
                webView.loadUrl("file:///android_asset/HuiJianHtml/html/HuiJianBox/login.html");
                break;
            case AppConfigure.UI.MAIN_AT:

                if (!AppConfigure.isHtml) {
                    intent = new Intent(WelcomeActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                }
                startActivity(intent);
                finish();
                break;
            case AppConfigure.UI.MEET_AT:
                intent = new Intent(WelcomeActivity.this, SRVideoActivity.class);
                startActivity(intent);
                finish();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppApplication.getInstance().release(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (configurePrestener != null) {
            configurePrestener.clear();
            log.E("ConfigurePrestenerImpl........onDestroy.......clear...");
            configurePrestener = null;
        }
        IMiddleManage.getInstance().deleteObserver(this);
//        NetStateReceiver.unRegisterNetworkStateReceiver(this);//注册网络状态
    }

    @Override
    public void onJoinError(int code, String errMsg) {

    }

    @Override
    public void finishView() {
        log.E("WelcomeActivity......finish()");
        WelcomeActivity.this.finish();
    }


    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof IMiddleManage) {
            if (data instanceof IMiddleManage.NotifyCmd) {
                final IMiddleManage.NotifyCmd cmd = (IMiddleManage.NotifyCmd) data;
                switch (cmd.type) {
                    case LOGIN_FAIL:
                        log.E("WelcomeActivity...LOGIN_FAIL..."+cmd.data);
                        loginFail((int)cmd.data);
                        break;
                    case LOGIN_STATE:
                        boolean status = (boolean)cmd.data;
                        if(status){
                            enterHomeActiviey(status);
                        }
                        break;
                }
            }
        }
    }

    private void enterHomeActiviey(boolean isSuccess){
        CallJsUtil.getInstance().onLoginStatus(webView,isSuccess);
        Intent intent = null;
        if (!AppConfigure.isHtml) {
            intent = new Intent(WelcomeActivity.this, MainActivity.class);
        } else {
            intent = new Intent(WelcomeActivity.this, HomeActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void loginFail(int eType){
        String msg = "";
        if (eType == SRErrorCode.LoginCode.LOGIN_TIME_OUT.getCode()) {
            msg = getResources().getString(org.suirui.login.huijian.R.string.sr_login_time_out);

        } else if (eType == SRErrorCode.LoginCode.SERVER_ERROR.getCode()) {
            msg = getResources().getString(org.suirui.login.huijian.R.string.sr_login_server_error);

        } else if (eType == SRErrorCode.LoginCode.invalid_TOKEN.getCode()) {
            msg = getResources().getString(org.suirui.login.huijian.R.string.sr_login_invalid_token);

        } else if (eType == SRErrorCode.LoginCode.PWD_ERROR.getCode()) {
            msg = getResources().getString(org.suirui.login.huijian.R.string.sr_login_pwd_error);

        } else if (eType == SRErrorCode.LoginCode.Account_freeze.getCode()) {
            msg = getResources().getString(org.suirui.login.huijian.R.string.sr_login_account_freeze);

        } else if (eType == SRErrorCode.LoginCode.USER_Identification_is_null.getCode()) {
            msg = getResources().getString(org.suirui.login.huijian.R.string.sr_login_identification_is_null);

        } else if (eType == SRErrorCode.LoginCode.USER_does_not_exist.getCode()) {
            msg = getResources().getString(org.suirui.login.huijian.R.string.sr_login_user_not_exist);

        } else if (eType == SRErrorCode.LoginCode.SERVER_FIELD_ERROR.getCode()) {
            msg = getResources().getString(org.suirui.login.huijian.R.string.sr_server_field_error);

        }
        if (CommonUtils.isEmpty(msg)) {
            msg = getResources().getString(org.suirui.login.huijian.R.string.sr_login_fail);
        }
        log.E("WelcomeActivity...LOGIN_FAIL..."+eType+"  msg:" + msg);

        CallJsUtil.getInstance().onLoginFail(webView,eType,msg);
    }
}
