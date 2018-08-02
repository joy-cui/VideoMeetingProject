package org.suirui.transfer;

import android.content.Context;
import android.text.TextUtils;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.suirui.srpaas.base.util.log.SRLog;

/**
 * Created by hh on 2018/3/26.
 */

public class SRTransfer {
    private static SRLog log = new SRLog(SRTransfer.class.getName(), Config.LOG_LEVE);
    private static SRTransfer mSRTransfer;

    public static synchronized SRTransfer getInstance() {
        if (mSRTransfer == null) {
            mSRTransfer = new SRTransfer();
        }
        return mSRTransfer;
    }

    /**
     *
     * @param type SRSdkType.SRSdkType_SDK;SRSdkType.SRSdkType_UBOX
     */
    public void setSDKType(int type){
        Config.SDKType = type;
    }

    public void isVersionUpdate(boolean isVersionUpdate){
        Config.VERSION_UPDATE = isVersionUpdate;
    }

    /**
     * 使用此接口 时需设置接口类型：setSDKType(int type)；如不设置默认为接口类型为 SRSdkType_SDK
     * @param context
     * @param data json数据
     * @param function 接口类型为 SRSdkType_SDK，此参数 可传 null；
     */
    public void callNative(final Context context,String data, CallBackFunction function){
        SRSdk.getInstance().registerHandler(context, Config.SDKType,data,  function);
    }

    /**
     *
     * @param context
     * @param interfaceType 接口类型：SRSdkType_SDK/SRSdkType_UBOX
     * @param data json数据
     * @param function 接口类型为 SRSdkType_SDK，此参数 可传 null；
     */
    public void callNative(final Context context,int interfaceType,String data, CallBackFunction function){
        SRSdk.getInstance().registerHandler(context, interfaceType,data,  function);
    }

    public void callJs(BridgeWebView mWebView, String json) {
        if (mWebView == null || TextUtils.isEmpty(json)) {
            log.E("callJs....is  null !!!:");
            return;
        }

        mWebView.callHandler(MethodName.callJsHandler, json, new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                //native调用js方法,js给native的回调
                log.E("callJs....data:" + data);
            }
        });
    }

}
