package org.suirui.huijian.tv.prestener;

import android.content.Context;

import org.suirui.huijian.tv.view.IMeetingHavePwdView;


public interface IMeetingPrestener {
    void startMeeting(Context context);

    void joinMeeting(Context context,String meetId, String meetPwd);

    void getMeetingPassword(final Context context,final String meetId,IMeetingHavePwdView obj);



}
