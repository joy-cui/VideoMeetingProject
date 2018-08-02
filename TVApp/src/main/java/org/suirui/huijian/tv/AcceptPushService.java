package org.suirui.huijian.tv;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.util.StringUtil;

import org.suirui.huijian.tv.activity.InviteDialogActivity;
import org.suirui.huijian.tv.model.impl.MeetingModel;
import org.suirui.huijian.tv.prestener.impl.MeetingPrestener;
import org.suirui.srpaas.entry.ConferenceInfo;
import org.suirui.websocket.client.CommonResponse;
import org.suirui.websocket.client.manager.WsClientManager;
import org.suirui.websocket.client.manager.WsClientManager.OnAcceptWebSocketListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import static android.content.ContentValues.TAG;

/**
 * Created by cui on 2018/4/19.
 */

public class AcceptPushService extends Service implements OnAcceptWebSocketListener{
    SRLog log=new SRLog(AcceptPushService.class.getName(),TVAppConfigure.LOG_LEVE);
Context mContext=null;
    @Override
    public void onCreate() {
        super.onCreate();
        log.E("setupWebSocket....AcceptPushService..oncreate");
        mContext=this;
        WsClientManager.getInstance(this).setOnAcceptWebSocketListener(this);
//        test();
//        showInviteDialog("","3333邀请您加入会议",null);
    }
private void test(){
    UsbManager manager = (UsbManager) getSystemService(this.USB_SERVICE);
    HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
    Log.e(TAG, "UsbManager...get device list  = " + deviceList.size());
    Toast.makeText(this, "get device list  = " + deviceList.size(), Toast.LENGTH_LONG).show();
    Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

    while (deviceIterator.hasNext()) {
        UsbDevice device = deviceIterator.next();
        Log.e(TAG, "UsbManager...device name = " + device.getDeviceName()+" getProductName: "+device.getProductName()+"  toString:"+device.getDeviceClass()+" getClass :"+device.getManufacturerName());
    }
}
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log.E("setupWebSocket....AcceptPushService..onDestroy");
    }

    @Override
    public void accept(CommonResponse<String> response) {
    if(response.getConfid()!=null && !StringUtil.isEmpty(response.getConfid())) {
        log.E("setupWebSocket.AcceptPushService..accept......: " + response.getCode() + " : " + response.getType() + " getConfid: " + response.getConfid());
    }
//        meetingListPrestener.updateMeetingList();
        if(response!=null && response.getType()!=null){
            switch (response.getType()){
                case TVAppConfigure.PushType.ReservationMeetingPush:
                    //预约会议
                    MeetingModel.getInstance().updateMeetingList(response.getConfid(),getConferenceInfo(response.getData()),false);
                    break;
                case TVAppConfigure.PushType.InviteToMeetingPush://邀请
                    MeetingModel.getInstance().updateMeetingList(response.getConfid(),getConferenceInfo(response.getData()),false);
                    inviteToMeeting(response);
                    break;
                case TVAppConfigure.PushType.StartMeetingPush://开始会议
                    MeetingModel.getInstance().updateMeetingList(response.getConfid(),getConferenceInfo(response.getData()),true);
                    break;
                case TVAppConfigure.PushType.DeleteMeetingPush:
                    MeetingModel.getInstance().DeleteMeetingPush(response.getConfid());
                    break;
                case TVAppConfigure.PushType.UpdateMeetingPush:
                    MeetingModel.getInstance().updateMeetingList(response.getConfid(),getConferenceInfo(response.getData()),false);

                    break;

                default:
                    break;
            }


        }
    }
private void inviteToMeeting(CommonResponse<String> response){
    log.E("inviteToMeeting...."+TVSrHuiJianProperties.isAutoAnswer()+" : "+TVSrHuiJianProperties.isNotDisturb());
    ConferenceInfo conferenceInfo=getConferenceInfo(response.getData());
    if(TVSrHuiJianProperties.isNotDisturb()){
        return;
    }
    if(TVSrHuiJianProperties.isAutoAnswer()){
        new MeetingPrestener().joinMeeting(mContext, conferenceInfo.getSubject(), conferenceInfo.getConfPwd());
    }else if(!TVSrHuiJianProperties.isNotDisturb()){
        showInviteDialog(response.getConfid(), response.getContent(), getConferenceInfo(response.getData()));
    }
}

    private void showInviteDialog(String confId,String content,ConferenceInfo conferenceInfo){
  log.E("showInviteDialog.setupWebSocket..confId: "+confId+" getConfName: "+conferenceInfo.getConfName()+" : "+conferenceInfo.getNickname());
        if(conferenceInfo!=null) {
            content=conferenceInfo.getNickname()+getApplicationContext().getString(R.string.hj_invite_you_join)+conferenceInfo.getConfName();
            Intent intent = new Intent(getApplicationContext(), InviteDialogActivity.class);
            intent.putExtra(TVAppConfigure.Invite.conf_id, conferenceInfo.getConfId());
            intent.putExtra(TVAppConfigure.Invite.conf_subject, conferenceInfo.getSubject());
            intent.putExtra(TVAppConfigure.Invite.conf_name, conferenceInfo.getConfName());
            intent.putExtra(TVAppConfigure.Invite.invite_content, content);
            intent.putExtra(TVAppConfigure.Invite.conf_pwd, conferenceInfo.getConfPwd());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }


    }
    private void cancelDialog(){

    }
    private ConferenceInfo getConferenceInfo(String data){
//        ConferenceInfo conferenceInfo=new ConferenceInfo();
        ConferenceInfo conferenceInfo = JSON.parseObject(data, new TypeReference<ConferenceInfo>() {
        });
        log.E("setupWebSocket....confName:"+conferenceInfo.getConfName()+" getConfId"+conferenceInfo.getConfId());
        return conferenceInfo;


    }

//    private class AcceptSocketThread extends Thread {
//        @Override
//        public void run() {
//            log.E( "AcceptSocketThread->run()");
//
//        }
//    }
}
