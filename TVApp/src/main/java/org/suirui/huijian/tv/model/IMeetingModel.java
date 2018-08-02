package org.suirui.huijian.tv.model;

import android.content.Context;

import org.suirui.huijian.tv.model.impl.MeetingModel;
import org.suirui.srpaas.entry.ConferenceInfo;
import org.suirui.srpaas.http.callback.OnMeetingDetailInfoCallBack;
import org.suirui.srpaas.http.callback.OnMeetingListCallBack;

import java.util.List;

/**
 * Created by cui on 2018/4/11.
 */

public interface IMeetingModel {
    /**
     * 获取会议列表
     * @param appId
     * @param secretKey
     * @param resfulUrl
     * @param token
     * @param timestamp
     * @param start
     * @param count
     * @param callBack
     */
    void getMeetingList(String appId,String secretKey,String resfulUrl, String token,String timestamp,int start,int count,MeetingModel.OnMeetingListListener callBack);

    /**
     * 获取会议详情
     * @param appId
     * @param secretKey
     * @param token
     * @param confId
     * @param onMeetingDetailInfoCallBack
     */
    void  getMeetDetailInfo(String appId, String secretKey, String token,String resfulUrl, String confId, OnMeetingDetailInfoCallBack onMeetingDetailInfoCallBack);

    /**
     * 搜索
     * @param str
     * @param onSearchMeetingListListener
     */
    void search(String str,MeetingModel.OnSearchMeetingListListener onSearchMeetingListListener);

    void startMeeting(Context context);

    void joinMeeting(Context context,String meetId, String meetPwd);

    void getMeetingPassword(final Context context,final String meetId,MeetingModel.OnMeetingPwdListener listener);

    /**
     * 更新会议列表
     * @param confId
     * @param conferenceInfo
     * @param  isStart true会议开始，更新开始状态
     */
    void updateMeetingList(String confId, ConferenceInfo conferenceInfo,boolean isStart);

    /**
     * 删除会议推送
     * @param confId
     */
    void DeleteMeetingPush(String confId);

    /**
     * 获取当前的数据列表（本地缓存）
     * @param isStart
     * @param isSearch
     * @return
     */
    List<ConferenceInfo> getMeetingList(boolean isStart, boolean isSearch);



    void clearData();

}
