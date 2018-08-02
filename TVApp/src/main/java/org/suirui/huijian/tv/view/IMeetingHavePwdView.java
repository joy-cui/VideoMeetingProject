package org.suirui.huijian.tv.view;

import org.suirui.srpaas.entry.ConferenceInfo;
import org.suirui.srpaas.entry.MeetDetailInfo;

import java.util.List;

/**
 * Created by cui on 2018/4/11.
 */

public interface IMeetingHavePwdView {
    void onMeetingPwdError(int code, String msg);
    void onShowPwdDialog(String meetId);
}
