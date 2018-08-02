package org.suirui.huijian.tv.prestener.impl;

import android.content.Context;

import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.util.TokenUtil;

import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.JoinMeetingActivity;
import org.suirui.huijian.tv.model.impl.MeetingModel;
import org.suirui.huijian.tv.prestener.IMeetingListPrestener;
import org.suirui.huijian.tv.prestener.IMeetingPrestener;
import org.suirui.huijian.tv.view.IMeetingHavePwdView;
import org.suirui.huijian.tv.view.IMeetingListView;
import org.suirui.srpaas.entry.ConferenceInfo;
import org.suirui.srpaas.entry.MeetDetailInfo;
import org.suirui.srpaas.http.callback.OnMeetingDetailInfoCallBack;

import java.util.List;
import java.util.Observer;


public class MeetingPrestener implements IMeetingPrestener {
    SRLog log=new SRLog(this.getClass().getName(),TVAppConfigure.LOG_LEVE);

    public MeetingPrestener(){

    }

    @Override
    public void startMeeting(Context context) {
        MeetingModel.getInstance().startMeeting(context);
    }

    @Override
    public void joinMeeting(Context context, String meetId, String meetPwd) {
        MeetingModel.getInstance().joinMeeting(context,meetId, meetPwd);
    }

    @Override
    public void getMeetingPassword(final Context context, final String meetId, final IMeetingHavePwdView listener) {
        MeetingModel.getInstance().getMeetingPassword(context, meetId, new MeetingModel.OnMeetingPwdListener() {
            @Override
            public void onError(int code, String msg) {
                listener.onMeetingPwdError(code,msg);
                super.onError(code, msg);
            }

            @Override
            public void onSuccess(String msg, boolean isHavePwd) {
                if(isHavePwd){
                    listener.onShowPwdDialog(meetId);
                }else{
                    joinMeeting(context,meetId,"");
                }
                super.onSuccess(msg, isHavePwd);
            }
        });
    }
}
