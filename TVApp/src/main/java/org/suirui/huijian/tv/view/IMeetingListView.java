package org.suirui.huijian.tv.view;

import org.suirui.srpaas.entry.ConferenceInfo;
import org.suirui.srpaas.entry.MeetDetailInfo;

import java.util.List;

/**
 * Created by cui on 2018/4/11.
 */

public interface IMeetingListView {
    void loadMeeting();
    void  onSuccess(List<ConferenceInfo> meetingList, List<ConferenceInfo> noMeetingList);
    void onFailer(String msg);
    void showMeetListDialog(MeetDetailInfo conferenceInfo);
    void showSeachMeetList(List<ConferenceInfo> searchList,List<ConferenceInfo> searchNoMeetList);
    void moveFocus(boolean isMoveLeft,boolean isMoveRight);

    /**
     * 向上按键 1是会议中 2未开始会议
     * @param isTop
     */
    void moveTopFocus(boolean isTop,boolean isMeeting);
}
