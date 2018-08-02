package org.suirui.huijian.tv.util.multKey;

import android.view.KeyEvent;
import com.suirui.srpaas.base.util.log.SRLog;
import org.suirui.huijian.tv.TVAppConfigure;

/**
 * Created by hh on 2018/5/24.
 */

public class MultiKeyImpl implements IMultKey {
    private static final SRLog log = new SRLog(MultiKeyImpl.class.getName(), TVAppConfigure.LOG_LEVE);
    //组合键序列
    private static Integer[] mMultKey = new Integer[]{KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_DPAD_UP};

    //是否限定要在时间间隔里再次输入按键
    private static boolean ALLAW_SETTING_DELAYED_FLAG = true;

    //允许用户在多少时间间隔里输入按键
    private static int CHECK_NUM_ALLAW_MAX_DELAYED = 5000;

    //记录用户连续输入了多少个有效的键
    private static int mAvailableNum = 0;

    private static long mLastEventTime = 0;//最后一次用户输入按键的时间

    private boolean isAllow = true;

    public MultiKeyImpl() {
    }

    @Override
    public boolean isAllow() {
        return isAllow;
    }

    @Override
    public boolean checkKey(int keycode, long eventTime) {
        boolean check;
        int delayed;
        int num = keycode;
//       log.E("multikey checkKey lastEventTime="+lastEventTime);
//        log.E("multikey checkKey num= "+num+" , eventTime = "+eventTime);

        if (mLastEventTime == 0) { //首次按键
            delayed = 0;
        } else {//非首次按键
            delayed = (int) (eventTime - mLastEventTime);
        }
        check = checkKeyValid(num, delayed);
        mLastEventTime = check ? eventTime : 0L;
//        log.E("multikey checkKey check key valid = "+check);
        return check;
    }

    @Override
    public boolean checkMultKey() {
        if(mMultKey == null || mMultKey.length <= 0){
            return false;
        }
        return mAvailableNum == mMultKey.length;
    }

    @Override
    public void clearKeys() {
        mLastEventTime = 0;
        mAvailableNum = 0;
    }

    @Override
    public void onMultKey() {
        if (checkMultKey()) {
            log.E("multikey  conMultKey  触发了组合键 ");
            if (mListener != null) {
                mListener.onMultKeyResult(true);
            }
        } else {
            if (mListener != null) {
                mListener.onMultKeyResult(false);
            }
        }
        //触发完成后清除掉原先的输入
        clearKeys();
    }

    /**
     * 传入用户输入的按键
     *
     * @param num
     * @param delayed 两次按键之间的时间间隔
     * @return
     */
    private boolean checkKeyValid(int num, int delayed) {
        if(mMultKey == null || mMultKey.length <= 0){
            return false;
        }
//        log.E("multikey  checkKeyValid num= " + num + " , delayed = " + delayed);
        if (ALLAW_SETTING_DELAYED_FLAG && delayed > CHECK_NUM_ALLAW_MAX_DELAYED) {
            mAvailableNum = 0;
            return false;
        }
        if (mAvailableNum < mMultKey.length && mMultKey[mAvailableNum] == num) {
            mAvailableNum++;
            return true;
        } else {
            mAvailableNum = 0;//如果输入错误的话，则重置掉原先输入的
        }
        return false;
    }


    private onMultkeyListener mListener = null;

    public void setOnMultiKeyListener(boolean isAllow, Integer[] multKeys, int keyCode, KeyEvent event, onMultkeyListener listener) {
        if(multKeys == null || multKeys.length <= 0){
            throw new RuntimeException("you must be sure multKeys[] isn't null !!!");
        }
        this.isAllow = isAllow;
        this.mMultKey = multKeys;
        mListener = listener;
        if (isAllow) {
            boolean vaildKey = checkKey(keyCode, event.getEventTime());
            if (vaildKey && checkMultKey()) {
                onMultKey();
            }
        }
    }

    public interface onMultkeyListener {
        void onMultKeyResult(boolean result);
    }
}
