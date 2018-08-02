package org.suirui.huijian.tv.util;


import android.content.Context;
import android.view.KeyEvent;
import android.view.View;

import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.util.multKey.MultiKeyImpl;
import java.util.HashSet;

public class MultiKeyUtil{
    private static final SRLog log = new SRLog(MultiKeyUtil.class.getName(), TVAppConfigure.LOG_LEVE);
    public static final Integer[] mMultKey_sysSet = new Integer[]{KeyEvent.KEYCODE_DPAD_UP,KeyEvent.KEYCODE_DPAD_DOWN,KeyEvent.KEYCODE_DPAD_LEFT,KeyEvent.KEYCODE_DPAD_RIGHT,KeyEvent.KEYCODE_VOLUME_UP};
    public static final Integer[] mMultKey_sysSet1 = new Integer[]{KeyEvent.KEYCODE_VOLUME_DOWN,KeyEvent.KEYCODE_VOLUME_UP};
    private static MultiKeyUtil instance = null;

    private MultiKeyUtil() {
    }

    public static synchronized MultiKeyUtil getInstance() {
        if (instance == null) {
            instance = new MultiKeyUtil();
        }

        return instance;
    }

    public boolean onMultiKey(final Context context, int keyCode, KeyEvent event,MultiKeyImpl multiKey, Integer[] mMultKey,HashSet<Integer> mSet) {
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
            log.E("keyCode:" + keyCode);
            if(mSet.contains(keyCode)){
//                multiKeyImpl = multiKey;
                if(multiKey == null){
                    multiKey = new MultiKeyImpl();
                }
                multiKey.setOnMultiKeyListener(true, mMultKey, keyCode, event, new MultiKeyImpl.onMultkeyListener() {
                    @Override
                    public void onMultKeyResult(boolean result) {
                        log.E("keyCode:result" + result);
                        if(result){
//                            multiKey = null;
                        }
                        AppUtil.getInstance().skipSet(context);
                    }
                });
                return true;
            }
        }
        return false;
    }

}
