package org.suirui.huijian.tv.prestener.impl;

import android.content.Context;


import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.passsdk.manages.SRErrCode;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.util.StringUtil;
import com.suirui.srpaas.video.util.TokenUtil;

import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.model.impl.MeetingModel;
import org.suirui.huijian.tv.prestener.IMeetingListPrestener;
import org.suirui.huijian.tv.view.IMeetingListView;
import org.suirui.srpaas.entry.ConferenceInfo;
import org.suirui.srpaas.entry.MeetDetailInfo;
import org.suirui.srpaas.http.callback.OnMeetingDetailInfoCallBack;
import org.suirui.srpaas.http.callback.OnMeetingListCallBack;

import java.util.List;

/**
 * Created by cui on 2018/4/11.
 */

public class MeetingListPrestener implements IMeetingListPrestener {
    SRLog log=new SRLog(this.getClass().getName(),TVAppConfigure.LOG_LEVE);
    Context context;
    IMeetingListView view;
    public  MeetingListPrestener(Context context,IMeetingListView view){
        this.context=context;
        this.view=view;

    }
    public  MeetingListPrestener(){

    }

    @Override
    public void getMeetingList() {
        try {
            MeetingModel meetingModel = MeetingModel.getInstance();
            meetingModel.setSearchText("");
            log.E("MeetingListPrestener....getMeetingList:"+meetingModel.isGetMeetList());
            if (meetingModel.isGetMeetList()) {
                if (view != null) {
                    view.onSuccess(meetingModel.getMeetingList(), meetingModel.getNoMeetingList());
                }
            } else {
                if (context == null) {
                    if (view != null) {
                        view.onFailer("context not null");
                    }
                    return;
                }
                if (view != null) {
                    view.loadMeeting();
                }
                String token = TokenUtil.getIntance(context).getToken();
                //ThirdApi.getIntance(context).getSharePreferDoMain(TVAppConfigure.DO_MAIN) + TVAppConfigure.pass_url_root
                meetingModel.getMeetingList(TVAppConfigure.appId, TVAppConfigure.secretKey, TVAppConfigure.RESFUL_URL, token, "", 0, 100, new MeetingModel.OnMeetingListListener() {
                    @Override
                    public void onError(String msg) {
                        view.onFailer(msg);
                    }

                    @Override
                    public void onSuccess(List<ConferenceInfo> meetingList, List<ConferenceInfo> noMeetingList) {
                        if (view != null) {
                            view.onSuccess(meetingList, noMeetingList);
                        }
                    }
                });
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    @Override
    public void getMeetDetail(String confId) {
        if(context==null){
            view.onFailer("context not null");
            return ;
        }
        log.E("conferenceInfo...setupWebSocket..getMeetDetail:"+confId);
        String token=TokenUtil.getIntance(context).getToken();
        MeetingModel.getInstance().getMeetDetailInfo(TVAppConfigure.appId, TVAppConfigure.secretKey, token, ThirdApi.getIntance(context).getSharePreferDoMain(TVAppConfigure.DO_MAIN) + TVAppConfigure.pass_url_root, confId, new OnMeetingDetailInfoCallBack() {
            @Override
            public void onError(int i, String msg) {
                view.onFailer(msg);
            }

            @Override
            public void onSuccess(MeetDetailInfo meetDetailInfo) {
                if(view!=null) {
                    view.showMeetListDialog(meetDetailInfo);
                }
            }
        });
    }

    /**
     *
     * @param isMeeting 是否开始会议
     * @param position 当前位置
     */
    @Override
    public void onFoucus(boolean isMeeting, int position) {
        log.E("onFocusChange....position:" + position+" : " + position+" %4:"+position%4 +" isMeeting: "+isMeeting);
        if(view!=null) {
            if (position % 4 == 3) {//最右边

                if (isMeeting) {
                    //不允许点击右
//                log.E("onFocusChange....position:" + position + " 不允许点击右" );
                    view.moveFocus(true, false);
                } else {
                    view.moveFocus(true, true);
                }
            } else if (position % 4 == 0) {

                if (!isMeeting) {
                    view.moveFocus(false, true);
//                log.E("onFocusChange....position:" + position + " 不允许点击左 " );

                } else {
                    view.moveFocus(true, true);
                }
            } else {
                view.moveFocus(true, true);
            }

            //向上按键
            if(position<4){
                log.E("dispatchKeyEvent...向上按键:");
                view.moveTopFocus(true,isMeeting);
            }else{
                view.moveTopFocus(false,isMeeting);
            }
        }
    }

    @Override
    public void search(String str) {
        log.E("search...."+str);
       if(StringUtil.isEmpty(str)){
//           view.onSuccess(meetingList,noMeetingList);
           MeetingModel.getInstance().setSearchText("");
       }else {
           MeetingModel.getInstance().setSearchText(str);
           MeetingModel.getInstance().search(str, new MeetingModel.OnSearchMeetingListListener() {
               @Override
               public void onSearchSuccess(List<ConferenceInfo> searchMeetingList, List<ConferenceInfo> searchNoMeetingList) {
                   if(view!=null) {
                       view.showSeachMeetList(searchMeetingList, searchNoMeetingList);
                   }
               }
           });
       }
    }

    @Override
    public List<ConferenceInfo> getMeetingList(boolean isStart, boolean isSearch) {
        return MeetingModel.getInstance().getMeetingList(isStart,isSearch);
    }

    @Override
    public void clearData() {
        MeetingModel.getInstance().clearData();
    }


}
