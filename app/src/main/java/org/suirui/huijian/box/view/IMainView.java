package org.suirui.huijian.box.view;

import android.os.Bundle;

/**
 * @authordingna
 * @date2017-06-16
 **/
public interface IMainView {
    /**
     * 错误提示
     *
     * @param code
     * @param errMsg
     */
    void onJoinError(int code, String errMsg);

    /**
     * finish
     */
    void finishView();

    /**
     * 跳转activity
     *
     * @param b
     */
    void doActivity(int uiType, Bundle b);
}
