package org.suirui.huijian.box.prestener;

import android.content.Intent;

/**
 * 配置文件业务
 *
 * @authordingna
 * @date2017-06-16
 **/
public interface IConfigurePrestener {

    /**
     * 是否有链接入会
     *
     * @param intent
     */
    void getMainIntent(Intent intent);

    /**
     * 是否链接入会
     *
     * @return
     */
    boolean isjoinMeet();

    /**
     * 是否登录（成功还是失败）
     *
     * @return
     */
    boolean isLogin();

    /**
     * 开始连接入会
     */
    void joinMeet();

    /**
     * 加入会议的类型
     *
     * @return
     */
    int joinType();

    /**
     * 清楚数据
     */
    void clear();
}
