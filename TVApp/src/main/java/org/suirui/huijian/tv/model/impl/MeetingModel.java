package org.suirui.huijian.tv.model.impl;

import android.content.Context;

import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.util.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.event.UpdateEvent;
import org.suirui.huijian.tv.model.IMeetingModel;
import org.suirui.huijian.tv.util.CallNativeUtil;
import org.suirui.huijian.tv.util.LoginUtil;
import org.suirui.huijian.tv.util.TVStringUtil;
import org.suirui.srpaas.entry.ConferenceInfo;
import org.suirui.srpaas.entry.MeeingListParams;
import org.suirui.srpaas.http.HttpService;
import org.suirui.srpaas.http.HttpServiceImpl;
import org.suirui.srpaas.http.callback.OnHasPwdCallBack;
import org.suirui.srpaas.http.callback.OnMeetingDetailInfoCallBack;
import org.suirui.srpaas.http.callback.OnMeetingListCallBack;
import org.suirui.srpaas.sdk.SRPaasSDK;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cui on 2018/4/11.
 */

public class MeetingModel implements IMeetingModel {

    class HasStart{
        private static final String hasStarted="1";
        private static final String hasNoStarted="0";
    }
    SRLog log=new SRLog(this.getClass().getName(),TVAppConfigure.LOG_LEVE);
    List<ConferenceInfo> meetingList=null;
    List<ConferenceInfo> noMeetingList=null;
    List<ConferenceInfo> searchMeetingList=null;
    List<ConferenceInfo> searchNoMeetingList=null;
    List<ConferenceInfo> allMeetList=null;
    private String searchText="";
    boolean isGetMeetList=false;

     static MeetingModel model=null;
    public  static MeetingModel getInstance(){
        synchronized (MeetingModel.class) {
            if (model == null) {
                model = new MeetingModel();

            }
        }
        return model;
    }
    public MeetingModel(){

    }

    public boolean isGetMeetList() {
        return isGetMeetList;
    }

    public List<ConferenceInfo> getMeetingList() {
        return meetingList;
    }


    public List<ConferenceInfo> getNoMeetingList() {
        return noMeetingList;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public void getMeetingList(String appId, String secretKey, String resfulUrl, String token, String timestamp, int start, int count, final OnMeetingListListener callBack) {
        SRPaasSDK srpaasSDK = SRPaasSDK.getInstance();
        srpaasSDK.setPassUrl(resfulUrl);
        MeeingListParams meetListingParams=new MeeingListParams();
        meetListingParams.setAppId(appId);
        meetListingParams.setSecretKey(secretKey);
        meetListingParams.setToken(token);
        meetListingParams.setTimestamp(timestamp);
        meetListingParams.setNumber(count);
        meetListingParams.setStart(start);
        log.E("getMeetingList..."+resfulUrl+"conference/list"+"?appId="+appId+"&token="+token+"&secretKey="+secretKey);

        HttpService h = HttpServiceImpl.getInstance();
        h.getMeetingList(meetListingParams, new OnMeetingListCallBack() {
            @Override
            public void onError(int code, String msg) {
                callBack.onError(msg);
            }

            @Override
            public void onSuccess(List<ConferenceInfo> conferenceInfoList) {
                try {
                    isGetMeetList = true;
                    if(allMeetList!=null){
                        allMeetList.clear();
                    }
                    allMeetList=conferenceInfoList;
                    setmeetListOrNoMeetList();

//
//                    meetingList = conferenceInfoList;
//                    noMeetingList = conferenceInfoList;
//                    if(meetingList!=null){
//                        log.E("getMeetingList....size:"+meetingList.size());
//                    }


                }catch (Exception e){
                    e.printStackTrace();
                }

                callBack.onSuccess(meetingList,noMeetingList);
            }
        });

    }

    /**
     * 分类更新
     */
    private void updateAllMeetList(){
        if(allMeetList!=null){
            setmeetListOrNoMeetList();
            if(StringUtil.isEmpty(searchText)){
                if(searchMeetingList!=null){
                    searchMeetingList.clear();
                }
                if(searchNoMeetingList!=null) {
                    searchNoMeetingList.clear();
                }
            }else {
                searchMeetingList = setSearchMeetList(meetingList, searchText);
                searchNoMeetingList = setSearchMeetList(noMeetingList, searchText);
            }
        }
    }

    /**
     * 分类显示会议列表
     */
    private void setmeetListOrNoMeetList(){
        if(allMeetList!=null){
            if(meetingList!=null){
                meetingList.clear();
            }
            if(noMeetingList!=null){
                noMeetingList.clear();
            }
            for (ConferenceInfo conferenceInfo : allMeetList) {
                if (conferenceInfo != null) {
                    log.E("getMeetingList....size:" + allMeetList.size() + " : " + conferenceInfo.getHasStarted());
                    if (conferenceInfo.getHasStarted() != null && conferenceInfo.getHasStarted().equals("1")) {
                        if (meetingList == null) {
                            meetingList = new ArrayList<ConferenceInfo>();
                        }
                        meetingList.add(conferenceInfo);

                    } else {
                        if (noMeetingList == null) {
                            noMeetingList = new ArrayList<ConferenceInfo>();
                        }
                        noMeetingList.add(conferenceInfo);

                    }
                }
            }

    }
}
    @Override
    public void getMeetDetailInfo(String appId, String secretKey, String token, String resfulUrl, String confId, OnMeetingDetailInfoCallBack onMeetingDetailInfoCallBack) {
        HttpService h = HttpServiceImpl.getInstance();
        h.getMeetingInfo(appId,secretKey,token,confId,onMeetingDetailInfoCallBack);
    }

    @Override
    public void search(String str, OnSearchMeetingListListener onSearchMeetingListListener) {
        if(!StringUtil.isEmpty(str)){
            log.E("search..."+str+" : "+this.searchText);
            searchMeetingList=setSearchMeetList(meetingList,str);
            searchNoMeetingList=setSearchMeetList(noMeetingList,str);

            if(searchMeetingList!=null && meetingList!=null)log.E("search...searchMeetingList:"+searchMeetingList.size() +" : "+meetingList.size());
            if(searchNoMeetingList!=null && noMeetingList!=null)log.E("search...searchNoMeetingList:"+searchNoMeetingList.size()+" : "+noMeetingList.size());

            onSearchMeetingListListener.onSearchSuccess(searchMeetingList,searchNoMeetingList);
//            EventBus.getDefault().post(new UpdateEvent(UpdateEvent.TypeEvent.UPDATE_MEETINGLIST,null));
        }
    }

    @Override
    public void startMeeting(Context context){
        CallNativeUtil.getInstance().startMeeting(context);
    }

    @Override
    public void joinMeeting(Context context,String meetId, String meetPwd){
        CallNativeUtil.getInstance().joinMeeting( context, meetId, meetPwd);
    }

    @Override
    public void getMeetingPassword(final Context context,final String meetId,final OnMeetingPwdListener listener){
        String token = LoginUtil.getInstance().getToken(context);
        String domain = ThirdApi.getIntance(context).getSharePreferDoMain(TVAppConfigure.DO_MAIN)  + TVAppConfigure.pass_url_root;
        ThirdApi.getIntance(context).hasPwd(domain,token,meetId,
                new OnHasPwdCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        listener.onError(i,s);
                    }

                    @Override
                    public void onSuccess(String s, boolean b) {
                        listener.onSuccess(s,b);
                    }
                });
    }

    /**
     * 预约或修改，邀请会议推送，更新会议列表
     * @param confId
     * @param conferenceInfo
     * @param  isStart true会议开始，更新开始状态
     */
    @Override
    public void updateMeetingList(String confId, ConferenceInfo conferenceInfo,boolean isStart) {
        try {
            if(meetingList!=null) {
                log.E("AcceptPushService.setupWebSocket...confId:" + confId + " subject: " + conferenceInfo.getSubject() + " sonfId: " + conferenceInfo.getConfId() + " : " + meetingList.size());
            }
                allMeetList=updateOrAdd(confId, conferenceInfo, allMeetList,isStart);
                updateAllMeetList();

            EventBus.getDefault().post(new UpdateEvent(UpdateEvent.TypeEvent.UPDATE_MEETINGLIST,null));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 删除会议推送
     * @param confId
     */
    @Override
    public void DeleteMeetingPush(String confId) {
        try {
            meetingList=delMeetList(confId,meetingList);
            noMeetingList=delMeetList(confId,noMeetingList);
            searchMeetingList=delMeetList(confId,searchMeetingList);
            searchNoMeetingList=delMeetList(confId,searchNoMeetingList);
            EventBus.getDefault().post(new UpdateEvent(UpdateEvent.TypeEvent.UPDATE_MEETINGLIST, null));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<ConferenceInfo> getMeetingList(boolean isStart, boolean isSearch) {

            log.E("Model...getMeetingList:" + isStart + " : " + isSearch + " : " + TVStringUtil.getListSize(meetingList));

        if(isStart  ){
            if(isSearch) {
                return searchMeetingList;
            }else{
                return meetingList;
            }
        }else{
            if(isSearch){
                return  searchNoMeetingList;
            }else{
                return noMeetingList;
            }
        }

    }



    @Override
    public void clearData() {
        searchText="";
        if(allMeetList!=null){
            allMeetList.clear();
            allMeetList=null;
        }
        if(meetingList!=null){
            meetingList.clear();
            meetingList=null;
        }
        if(noMeetingList!=null){
            noMeetingList.clear();
            noMeetingList=null;
        }
        if(searchMeetingList!=null){
            searchMeetingList.clear();
            searchMeetingList=null;
        }
        if(searchNoMeetingList!=null){
            searchNoMeetingList.clear();
            searchNoMeetingList=null;
        }
        isGetMeetList=false;

    }

//更新或新增
    private synchronized List<ConferenceInfo> updateOrAdd(String confId,ConferenceInfo conferenceInfo,List<ConferenceInfo> mList,boolean isStart){

        try {

            if (mList != null) {
                boolean isAdd = true;
                ConferenceInfo meet=null;
                for (int i=0;i<mList.size();i++) {
                     meet=mList.get(i);
//                    log.E("updateOrAdd: "+meet.getSubject()+" : "+meet.getConfId()+":"+meet.getConfName()+" pushId: "+confId+" : "+conferenceInfo.getConfName());
                    if (meet!=null && confId.equals(meet.getConfId())) {//修改
                        log.E("updateOrAdd.AcceptPushService..update."+isAdd+" isStart: "+isStart+": "+confId+" getConfId:"+meet.getSubject()+" : "+conferenceInfo.getSubject());

                             String hasStart=meet.getHasStarted();
                             meet=conferenceInfo;
                             if(isStart){
                                 meet.setHasStarted(HasStart.hasStarted);
                             }else {
                                //不是开始会议
                                  meet.setHasStarted(hasStart);
                             }
                             mList.set(i,meet);
//                            log.E("updateOrAdd.....setupWebSocket..conferenceInfoName:"+conferenceInfo.getConfName()+" confId : "+confId+" : "+meet.getConfId()+" subject: "+meet.getSubject()+" meetName: "+meet.getConfName());
                            isAdd = false;
                            break;
                    }
                }
                log.E("updateOrAdd.AcceptPushService..."+isAdd+" isAdd: "+isAdd);
                if (isAdd && !isStart) {
                    mList.add(conferenceInfo);
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return mList;

    }

    private List<ConferenceInfo> delMeetList(String confId,List<ConferenceInfo> mList){
        try{
            if(mList!=null) {
                for (ConferenceInfo info : mList) {
                    if (info != null && info.getConfId().equals(confId)) {
                        mList.remove(info);
                        break;
                    }

                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return mList;


    }
    private List<ConferenceInfo> setSearchMeetList(List<ConferenceInfo> list,String str){
        List<ConferenceInfo> searchList=null;
    if(list!=null && str!=null){
        for(ConferenceInfo conferenceInfo:list){
            if(conferenceInfo!=null){
                if(conferenceInfo.getConfName().indexOf(str)>-1){
                    if(searchList==null){
                        searchList=new ArrayList<ConferenceInfo>();
                    }
                     searchList.add(conferenceInfo);
                }
            }
        }

    }
    return searchList;
}
    public abstract static class OnMeetingListListener {
        public OnMeetingListListener() {
        }

        public void onError(String msg) {
        }

        public void onSuccess(List<ConferenceInfo> meetingList,List<ConferenceInfo> noMeetingList) {
        }
    }

    public abstract static class OnSearchMeetingListListener {
        public OnSearchMeetingListListener() {
        }

        public void onSearchSuccess(List<ConferenceInfo> searchMeetingList,List<ConferenceInfo> searchNoMeetingList) {
        }
    }

    public abstract static class OnMeetingPwdListener {
        public void onError(int code, String msg){}
        public void onSuccess(String msg, boolean isHavePwd) {}
    }
}
