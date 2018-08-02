package org.suirui.huijian.tv.prestener;


import org.suirui.huijian.tv.model.impl.MeetingModel;
import org.suirui.srpaas.entry.ConferenceInfo;

import java.util.List;

/**
 * Created by cui on 2018/4/11.
 */

public interface IMeetingListPrestener {
    /**
     * 获取会议列表
     */
    void getMeetingList();

    /**
     * 获取会议详情
     * @param confId
     */
    void getMeetDetail(String confId);

    /**
     * 控制焦点
     * @param isMeeting
     * @param position
     */
    void onFoucus(boolean isMeeting,int position);

    /**
     * 搜索
     * @param str
     */
    void search(String str);

    /**
     * 获取会议列表
     * @param isStart
     * @param isSearch
     * @return
     */
    List<ConferenceInfo> getMeetingList(boolean isStart, boolean isSearch);

    /**
     * clear数据
     */
    void clearData();

}
