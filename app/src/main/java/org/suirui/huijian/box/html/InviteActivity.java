package org.suirui.huijian.box.html;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.suirui.srpaas.base.NetBean;
import com.suirui.srpaas.base.ui.activity.BaseAppCompatActivity;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.base.util.receiver.NetStateReceiver;
import com.suirui.srpaas.video.contant.Configure;

import org.suirui.huijian.box.AppConfigure;
import org.suirui.huijian.box.R;
import org.suirui.huijian.box.html.callback.JSCallbackNativeHandler;
import org.suirui.huijian.box.html.util.ToolsUtil;

/**
 * @authordingna
 * @date2017-10-27
 **/
public class InviteActivity extends BaseAppCompatActivity {
    private static final SRLog log = new SRLog(InviteActivity.class.getName(), AppConfigure.LOG_LEVE);
    private int inviteType = 0;
    private String confid = "";
    private String conPwd = "";
    private BridgeWebView mWebView;
    private int termid = 0;
    private String termName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hj_invite_layout);
        getInviteDate();
        mWebView = (BridgeWebView) findViewById(R.id.webview);
        mWebView.loadUrl("file:///android_asset/HuiJianHtml/html/HuiJianMobile/inviteContacts.html" + setInviteParams());
        mWebView.registerHandler(AppConfigure.RegisterMethod.CALL_NATIVE_HANDLER, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                //收到js的消息
                String op = ToolsUtil.getJsonValue(data, AppConfigure.MethodParam.OP);
                log.E("handler = data:= " + data + " op:" + op);
                if (op == null) return;
                switch (op) {
                    case AppConfigure.MethodParam.ON_BACK:
                        boolean isBack = ToolsUtil.getJsonBooleanValue(data, AppConfigure.RegisterMethod.isBack);
                        if (!isBack) {
                            InviteActivity.this.finish();
                        }
                        break;
//                    case AppConfigure.MethodParam.imChatOP:
//                        int imType = ToolsUtil.getJsonIntValue(data, AppConfigure.RegisterMethod.imChatType);
//                        log.E("imChatOP....." + imType);
//                        switch (imType) {
//                            case AppConfigure.ImOpType.IM_INVITE_PARTICIPANTS://通讯录中选中的创建群组的人
//                                try {
//                                    String participants = ToolsUtil.getJsonValue(data, AppConfigure.RegisterMethod.IM_INVITE_PARTICIPANTS);
//                                    String rooms = ToolsUtil.getJsonValue(data, AppConfigure.RegisterMethod.IM_INVITE_ROOMS);
//                                    String participantStr = "";
//                                    if (!TextUtils.isEmpty(participants)) {
//                                        if (!TextUtils.isEmpty(rooms)) {
//                                            participantStr = participants + "," + rooms;
//                                        } else {
//                                            participantStr = participants;
//                                        }
//                                    } else {
//                                        participantStr = rooms;
//                                    }
//                                    if (!TextUtils.isEmpty(participantStr)) {
//                                        log.E("createGroup....participantStr:" + participantStr + "   participants:" + participants + " rooms:" + rooms);
//                                        List<String> participantList = java.util.Arrays.asList(participantStr.trim().split(","));
//                                        SRIMSdk.getIntance().createGroup(InviteActivity.this, participantList);
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                break;
//                        }
//                        break;
                    default:
                        JSCallbackNativeHandler.registerHandler(InviteActivity.this, op, data, function);
                        break;

                }
            }
        });
    }

    public void getInviteDate() {
        Intent intent = this.getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                inviteType = intent.getIntExtra(Configure.Invite.INVITE_TYPE, 0);
                confid = intent.getStringExtra(Configure.Invite.INVITE_CONFID);
                conPwd = intent.getStringExtra(Configure.Invite.INVITE_CONPWD);
                termid = intent.getIntExtra(Configure.Invite.INVITE_TERMID, 0);
                termName = intent.getStringExtra(Configure.Invite.INVITE_TERMNAME);
            }
            log.E("InviteActivity...邀请的类型...inviteType:" + inviteType + " :" + confid + " conPwd:" + conPwd + " termid:" + termid + " termName:" + termName);
        }
    }

    //设置传递的参数
    private String setInviteParams() {
        return "?confid=" + confid + "&conPwd=" + conPwd + "&inviteType=" + inviteType + "&termId=" + termid + "&termName=" + termName;
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
        log.E("InviteActivity...NetStateReceiver.. wifiState:" + NetStateReceiver.isNetworkAvailable());
    }

    @Override
    protected void onNetworkDisConnected() {

    }

    @Override
    protected boolean isSupportActionBar() {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        log.E("InviteActivity...onKeyDown..." + event.getAction() + " keyCode:" + keyCode);
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null) {
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                if (parent != null) {
                    parent.removeView(mWebView);
                }
                mWebView.removeAllViews();
                mWebView.destroy();
                mWebView = null;
            }
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log.E("InviteActivity...onDestroy()");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //解决不能全屏显示的问题
        final View view = getWindow().getDecorView();
        final WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindowManager().updateViewLayout(view, lp);
    }
}
