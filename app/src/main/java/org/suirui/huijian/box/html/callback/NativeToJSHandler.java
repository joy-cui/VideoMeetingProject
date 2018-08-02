package org.suirui.huijian.box.html.callback;

import android.text.TextUtils;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.huijian.box.AppConfigure;


/**
 * @authordingna
 * @date2017-08-05
 **/
public class NativeToJSHandler {
    private static final SRLog log = new SRLog(NativeToJSHandler.class.getName(), AppConfigure.LOG_LEVE);

    public static void callHandler(BridgeWebView mWebView, String json) {
        if (mWebView == null || TextUtils.isEmpty(json)) return;
        mWebView.callHandler(AppConfigure.CallJSMethod.CALL_JS_HANDLER, json, new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                //native调用js方法,js给native的回调
                log.E("onCallBack....data:" + data);
            }
        });
    }
}
