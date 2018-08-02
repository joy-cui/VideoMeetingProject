package org.suirui.huijian.box.html;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.srpaas.version.entry.VersionInfo;
import com.suirui.pub.business.manage.IMiddleManage;
import com.suirui.srpaas.base.NetBean;
import com.suirui.srpaas.base.ui.activity.BaseAppCompatActivity;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.base.util.receiver.NetStateReceiver;
import com.suirui.srpaas.common.permission.AndPermission;
import com.suirui.srpaas.video.passsdk.manages.HuiJianSDKEvent;
import com.suirui.srpaas.video.passsdk.manages.MeetingListener;
import com.suirui.srpaas.video.passsdk.manages.TermInfo;
import com.suirui.srpaas.video.util.RequestPermissionsUtil;

import org.suirui.huijian.box.AppApplication;
import org.suirui.huijian.box.AppConfigure;
import org.suirui.huijian.box.R;
import org.suirui.huijian.box.bean.HistoryBean;
import org.suirui.huijian.box.html.callback.CallJsUtil;
import org.suirui.huijian.box.manage.SRMiddleManage;
import org.suirui.huijian.box.util.HistoryUtil;
import org.suirui.huijian.box.util.VersionUpdateUtil;
import org.suirui.huijian.box.html.callback.JSCallbackNativeHandler;
import org.suirui.huijian.box.html.callback.NativeToJSHandler;
import org.suirui.huijian.box.html.interaction.ToJson;
import org.suirui.huijian.box.html.util.ToolsUtil;
import org.suirui.login.huijian.ui.LoginActivity;
import org.suirui.srpaas.entry.SRError;
import org.suirui.transfer.listener.AudioListener;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 首页
 *
 * @authordingna
 * @date2017-08-08
 **/
public class HomeActivity extends BaseAppCompatActivity implements VersionUpdateUtil.CheckVersionListener, Observer,
        MeetingListener, AudioListener.ITestMicListener {
    private static final SRLog log = new SRLog(HomeActivity.class.getName(), AppConfigure.LOG_LEVE);
    private BridgeWebView mWebView;
    private String tel = "";
    private String sendSMS = "";


    private RequestPermissionsUtil.BMPermissionsListener bmPermissionsListener = new RequestPermissionsUtil.BMPermissionsListener() {
        @Override
        public void onSendSMS() {
            startActivity(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", sendSMS, null)));
        }

        @Override
        public void onPhone() {
            if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel)));
        }

//        @Override
        public void onWirte(boolean isAllow) {
            if (isAllow) {
                VersionUpdateUtil.getInstance().doDownloadVersion(HomeActivity.this);
            } else {
                Toast.makeText(HomeActivity.this, "获取权限失败，不能进行版本更新下载", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hj_home_layout);
        VersionUpdateUtil.getInstance().addCheckPermissionListener(this);
        IMiddleManage.getInstance().addObserver(this);
//        ThirdApi.getIntance(this).initPassSDK();
        HuiJianSDKEvent.getInstance().addMeetingListener(this);
        mWebView = (BridgeWebView) findViewById(R.id.webview);

        mWebView.setWebViewClient(new BridgeWebViewClient(mWebView));

        mWebView.loadUrl("file:///android_asset/HuiJianHtml/html/HuiJianBox/home.html");
        mWebView.setBackgroundColor(0x00000000);
        mWebView.requestFocus();
        registerHandler(mWebView);
//        AudioListener.getInstance().addITestMicListener(this);

    }


    public void registerHandler(final BridgeWebView mWebView){
        mWebView.registerHandler(AppConfigure.RegisterMethod.CALL_NATIVE_HANDLER, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                //收到js的消息
                String op = ToolsUtil.getJsonValue(data, AppConfigure.MethodParam.OP);
                log.E("handler = data:= " + data + " op:" + op);
                if (op == null) return;
                switch (op) {
                    case AppConfigure.MethodParam.outLogin:
                        log.E("退出登录");
                        SRMiddleManage.getInstance().onLoginOut();
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        Bundle b = new Bundle();
                        b.putBoolean(AppConfigure.VPN_MODULE, AppConfigure.isVpnModule);
                        intent.putExtras(b);
                        startActivity(intent);
                        HomeActivity.this.finish();
                        break;

                    case AppConfigure.MethodParam.versionUpdate:
                        log.E("版本更新");
                        //先检查相关权限,获取到相关权限后，才开始下载最新版
                        onCheckPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, AppConfigure.permission.WRITE_EXTERNAL_STORAGE);
                        break;

                    case AppConfigure.MethodParam.telephone:
                        tel = ToolsUtil.getJsonValue(data, AppConfigure.RegisterMethod.phone);
                        log.E("打电话..tel:" + tel);
                        onCheckPermission(Manifest.permission.CALL_PHONE, AppConfigure.permission.CALL_PHONE);
                        break;

                    case AppConfigure.MethodParam.sendSMS:
                        sendSMS = ToolsUtil.getJsonValue(data, AppConfigure.RegisterMethod.phone);
                        log.E("发短信..telephone:" + sendSMS);
                        onCheckPermission(Manifest.permission.SEND_SMS, AppConfigure.permission.SEND_SMS);
                        break;

                    case AppConfigure.MethodParam.sendEmail:
                        String _email = ToolsUtil.getJsonValue(data, AppConfigure.RegisterMethod.email);
                        log.E("发邮件..email:" + _email);
                        if (!TextUtils.isEmpty(_email)) {
                            startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + _email)));
                        }
                        break;

                    case AppConfigure.MethodParam.ON_BACK:
                        boolean isBack = ToolsUtil.getJsonBooleanValue(data, AppConfigure.RegisterMethod.isBack);
                        if (isBack) {
                            log.E("退出app...");
                            HomeActivity.this.finish();
                            exitApp();
                        } else {
                            if (mWebView != null && mWebView.canGoBack()) {
                                log.E("浏览器回退...");
                                mWebView.goBack();
                            }
                        }
                        break;
                    case AppConfigure.MethodParam.huijianControl://会见设置
//                        SRHuiJianSdk.getInstance().startSRControl(HomeActivity.this);
                        break;
                    default:
                        JSCallbackNativeHandler.registerHandler(HomeActivity.this, op, data, function);
                        break;
                }
            }
        });
    }
//    @Override
    public void onCheckPermission(String permission, int requestCode) {
        RequestPermissionsUtil.getInstance().initMeetingBeforePermission(HomeActivity.this, permission, requestCode, bmPermissionsListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        RequestPermissionsUtil.getInstance().onBMRequestPermissionsResult(permissions, grantResults, bmPermissionsListener);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public void openDialog(VersionInfo info) {
        log.E("有版本更新");
        NativeToJSHandler.callHandler(mWebView, ToJson.getInstance().ToJsonObjectAddOP(AppConfigure.MethodParam.versionUpdate, new Gson().toJson(info), false));
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
        log.E("HomeActivity...NetStateReceiver.. wifiState:" + NetStateReceiver.isNetworkAvailable());
    }

    @Override
    protected void onNetworkDisConnected() {

    }

    @Override
    protected boolean isSupportActionBar() {
        return false;
    }

    @Override
    public void exitApp() {
        AppApplication.getInstance().release(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        log.E("HomeActivity...onKeyDown..." + event.getAction() + " keyCode:" + keyCode);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {  //表示按返回键
                log.E("HomeActivity...canGoBack");
                NativeToJSHandler.callHandler(mWebView, ToJson.getInstance().ToJsonOP(AppConfigure.MethodParam.ON_BACK));
                mWebView.goBack();   //后退
                return true;    //已处理
            } else {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    log.E("HomeActivity...finish");
                    moveTaskToBack(true);
//                    this.finish();
                }
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        log.E("HomeActivity...onDestroy");
        super.onDestroy();
        IMiddleManage.getInstance().deleteObserver(this);
        if (mWebView != null) {
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        VersionUpdateUtil.getInstance().removeCheckPermissionListener();
//        AudioListener.getInstance().removeTokenListener(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof IMiddleManage) {
            if (data instanceof IMiddleManage.NotifyCmd) {
                final IMiddleManage.NotifyCmd cmd = (IMiddleManage.NotifyCmd) data;
                switch (cmd.type) {
                    case LOGIN_STATE:
                        int status = ToolsUtil.BooleanToInt((boolean) cmd.data);
                        log.E("当前登录的状态....status:" + status);
                        NativeToJSHandler.callHandler(mWebView, ToJson.getInstance().NativeJsBaseJson(status, AppConfigure.MethodParam.Login));
                        if (status == 0) {//登录失败
                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                            Bundle b = new Bundle();
                            b.putBoolean(AppConfigure.VPN_MODULE, AppConfigure.isVpnModule);
                            intent.putExtras(b);
                            startActivity(intent);
                            finish();
                        }
                        break;

                    case UPDATE_HISTORY_LIST:
                        String suid = SRMiddleManage.getInstance().getSuid();
                        log.E("退出会议时...更新历史记录....suid:" + suid);
                        List<HistoryBean> historyList = HistoryUtil.getInstance(HomeActivity.this).getHistoryListBySuid(suid);
//                        List<HistoryBean> historyList = HistoryUtil.getInstance(HomeActivity.this).getHistoryList();
                        NativeToJSHandler.callHandler(mWebView, ToJson.getInstance().ToJsonListAddOP(AppConfigure.MethodParam.getHistory, new Gson().toJson(historyList), true));
                        break;
                    case UPDATE_CURRENT_MEETINFO:
                        String confId = (String) cmd.data;
                        log.E("退出会议时...更新会议详情界面....confId:" + confId);
                        NativeToJSHandler.callHandler(mWebView, ToJson.getInstance().ToJsonKeyValue(AppConfigure.MethodParam.updateMeetInfo, AppConfigure.RegisterMethod.subject, confId));
                        break;
                }
            }
        }
    }

    @Override
    public void onTermLeaveCallBack(String confId, TermInfo termInfo, SRError srError) {
        log.E("onTermLeaveCallBack.....");
    }

    @Override
    public void onNewTermJoinCallBack(String confId, TermInfo termInfo) {
        log.E("onNewTermJoinCallBack.....");
    }

    @Override
    public void onMeetingState(String confId, int code, String msg) {
        log.E("onMeetingState....."+confId+" code: "+code);
    }

    @Override
    public void onTestMicNotify(final int volume) {
        log.E("onTestMicNotify....."+" volume: "+volume);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CallJsUtil.getInstance().onTestMicNotify(mWebView,volume);
            }
        });

    }
}
