package org.suirui.transfer;

/**
 * Created by hh on 2018/3/26.
 */

public class MethodName {
    public static final String callJsHandler = "callJsHandler";
    public static final String OP = "op";//js与native直接的协议类型，表示某个操作
    public static final String startMeeting = "startMeeting";//开始会议
    public static final String joinMeeting = "joinMeeting";//加入会议
    public static final String getHistory = "getHistory";//最近十条历史记录
    public static final String versionUpdate = "m_versionUpdate";//版本更新
    public static final String getServerInfo = "getServerInfo";//获取服务器地址
    public static final String outLogin = "m_outLogin";//退出登录
    public static final String data = "data";//列表
    public static final String Code = "code";
    public static final String Login = "login";//登录
    public static final String checkVersion = "m_checkVersion";//检查版本更新
    public static final String telephone = "m_telephone";//打电话
    public static final String sendSMS = "m_sendSMS";//发短信
    public static final String sendEmail = "m_sendEmail";//发邮件
    public static final String setParticipantOrderMeet = "m_setParticipantOrderMeet";//点对点预约会议
    public static final String getParticipantOrderMeet = "m_getParticipantOrderMeet";//获取点对点预约会议用户
    public static final String ON_BACK = "m_onBack";//点击手机返回键
    public static final String clearHistoryList = "m_clearHistoryList";//清除历史记录
    public static final String startAudioMeeting = "m_startAudioMeeting";//开始音频会议
    public static final String getCurrentVersion = "m_getCurrentVersion";//获取当前的版本号
    public static final String updateMeetInfo = "m_updateMeetInfo";//更新当前的会议信息
    public static final String imChatOP="m_imchat" ;//IM相关的
    public static final String huijianControl = "m_huijianControl";//会见控制


    public static final String onLoginStatus = "onLoginStatus";//会见登录是否成功
    public static final String onLoginFail = "onLoginFail";//会见登录errorcode
    public static final String startTestingMic = "startTestingMic";
    public static final String stopTestingMic = "stopTestingMic"; //音频设置：停止测试麦克风
    public static final String setMicVolume = "setMicVolume"; //音频设置：停止测试麦克风
    public static class Param {
        public static final String account = "account";//密码
        public static final String password = "password";//密码
        public static final String serverType = "serverType";//密码
        public static final String serverhost = "serverHost";//密码

    }
}
