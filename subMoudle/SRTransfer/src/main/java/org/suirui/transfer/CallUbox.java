package org.suirui.transfer;

import android.app.Activity;
import android.content.Context;

import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.suirui.srpaas.base.util.log.SRLog;

/**
 * Created by hh on 2018/3/26.
 */

public class CallUbox {
    private static SRLog log = new SRLog(CallUbox.class.getName(), Config.LOG_LEVE);
    private static CallUbox mCallUbox;
    public static synchronized CallUbox getInstance() {
        if (mCallUbox == null) {
            mCallUbox = new CallUbox();
        }
        return mCallUbox;
    }

    public void registerHandler(final Context context, String op, String data, CallBackFunction function) {
        if (context == null) return;
        log.E("CallUbox handler+  op:" + op+"  data:= " + data );
        switch (op) {
            case MethodName.startMeeting:
                break;

            case MethodName.startAudioMeeting:
                break;

            default:
                log.E("default....op:" + op);
                break;
        }
    }
}
