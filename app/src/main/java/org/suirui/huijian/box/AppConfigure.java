package org.suirui.huijian.box;

import com.suirui.pub.business.contant.SRIMConfigure;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.contant.Configure;

/**
 * Created by cui.li on 2017/2/8.
 */

public class AppConfigure {
    public static final int login_type = SRIMConfigure.Login.HUIJIAN;//默认是会见的登录
    public static final boolean isTempLogin = false;//默认不支持临时号的登录
    public static final String version = "4.0.0.20170622A_beta";
    //public
    public static final String CONNECT_JOIN_URL = "/SRhuijian.html?id=";//连接入会
    public static final String pass_url_root_NoLogin = "/api/v1";//不带登录入会
    public static final String login_pass_url_root = "/api";//(登录注册使用)
    public static String pass_url_root = "/api/v2";//(入会使用)
    public static String VERSION_URL = "/huijian4/v1/version/checkversion";
    public static boolean VERSION_UPDATE = true;
    public static boolean isLoginUI = true;//是否有登陆的UI
    public static int LOG_LEVE = SRLog.DebugType.V;// log级别
    /******************************************
     * 外网环境
     **************************************************/
    public static String appId = "beb7da4ced7c42a085c3c99697f9aa42";
    public static String secretKey = "beb7da4ced7c42a085c3c99697f9aa42";
    public static String HTTPREQUEST = "https";
    public static String SERVER_ADRESS = "lab.suirui.com";//（会议的服务器地址）
    public static String DO_MAIN = HTTPREQUEST + Configure.SPRITR + SERVER_ADRESS;//http://testgbz.ihuijian.com
    public static boolean isAudioMeet = false;//是否是音频会议(默认是视频会议)
    public static boolean isVpnModule = false;//是否有vpn设置
    public static boolean isHtml = true;//是否是H5界面

    /****************************
     * 版本更新
     *********************************/

    public static String CLIETN_TYPE = "android";
    public static final String VPN_MODULE="vpn_module";//vpn

    public static class URLMeet {
        public static final int NO_TYPE = 0;
        public static final String START = "start";//开始会议
        public static final String JOIN = "join";//加入会议
        public static final String END = "end";//结束会议
        public static final String JM = "jm";//连接入会

        //params
        public static final String appid = "appid";
        public static final String MeetingNumber = "meetingnumber";
        public static final String NickName = "nickname";
        public static final String pwd = "pwd";
        public static final String uid = "uid";
        public static final String Domain = "domain";
        public static final String secretKey = "secretKey";
        public static final String token = "token";

        public static final String ID = "id";//加入会议传递的参数
        public static final String T = "t";//会议类型（0时表示纯音频，1表示纯视频，2表示音频视频都启用）

    }

    public static class UI {
        public static final int LOGIN_AT = 0;//登录界面
        public static final int MAIN_AT = 1;//主界面
        public static final int MEET_AT = 2;//会议界面
    }

    public static class RegisterMethod {//提供给js调用方法
        public static final String CALL_NATIVE_HANDLER = "callNativeHanler";
        public static final String confId = "account";
        public static final String nickName = "nickName";
        public static final String confPwd = "passWord";
        public static final String data = "data";//JSON中的data对象
        public static final String videoOn = "videoOn";//是否启用摄像头
        public static final String audioOn = "audioOn";//是否静音
        public static final String password = "password";//密码
        public static final String suid = "suid";
        public static final String email = "email";
        public static final String phone = "phone";
        public static final String nickname = "nickname";
        public static final String relayserver = "relayserver";
        public static final String token = "token";
        public static final String isUseOneselfPhone = "isUseOneselfPhone";//是否使用个人会议号
        public static final String memberId = "id";//点对点用户id
        public static final String memberName = "name";//点对点用户id
        public static final String memberChecked = "checked";//点对点用户id
        public static final String memberType = "type";//点对点用户type
        public static final String isBack = "isBack";//手机返回键的处理
        public static final String currentVersion = "currentVersion";//当前的版本号
        public static final String subject = "subject";//当前的会议号
        public static final String imChatType = "isIMChat";//im操作
        public static final String IM_ACCOUNT = "IMAccount";//im账号
        public static final String IM_INVITE_PARTICIPANTS = "participants";//参会人
        public static final String IM_INVITE_ROOMS = "rooms";//参会人会议室终端
    }
    public static  class ImOpType{
        public static final int MY_GROUPS=1;//我的群组
        public static final int MY_CHATTING=2;//我的沟通
        public static final int IM_SEND_MESSAGE=3;//发送消息
        public static final int IM_INVITE_MEETING=4;//邀请视频
        public static final int IM_CREATE_GROUP = 5;//创建群组
        public static final int IM_INVITE_PARTICIPANTS = 6;//通讯录中选中的创建群组的人

    }
    public static class CallJSMethod {//调用js方法
        public static final String CALL_JS_HANDLER = "callJsHandler";
    }

    public static class MethodParam {
        public static final String OP = "op";//js与native直接的协议类型，表示某个操作
        public static final String startMeeting = "startMeeting";//开始会议
        public static final String joinMeeting = "joinMeeting";//加入会议
        public static final String meetingList="meetingList";//会议列表
        public static final String getHistory = "getHistory";//最近十条历史记录
        public static final String versionUpdate = "m_versionUpdate";//版本更新
        public static final String getServerInfo = "getServerInfo";//获取服务器地址
        public static final String outLogin = "m_outLogin";//退出登录
        public static final String data = "data";//列表
        public static final String Code = "code";
        public static final String Login = "m_login";//登录
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
        //系统管理：重启
        public static final String restart = "restart";
        public static final String shutDown = "shutDown";
//        public static final String startTestingMic = "startTestingMic";
//        public static final String stopTestingMic = "stopTestingMic"; //音频设置：停止测试麦克风
        public static final String testingMicNotify = "TestingMicNotify";//接收测试麦克风音量通知
    }

    public static class permission {
        public static final int WRITE_EXTERNAL_STORAGE = 100;
        public static final int CALL_PHONE = 101;
        public static final int SEND_SMS = 102;
    }

    public static class SPData {
        public static final String SPDATA = "share_preferences_data";
    }
    public static enum VideoType {
        SR_CFG_VIDEO_CLOSE(0),
        SR_CFG_VIDEO_SIZE_90P(1),
        SR_CFG_VIDEO_SIZE_144P(2),
        SR_CFG_VIDEO_SIZE_180P(3),
        SR_CFG_VIDEO_SIZE_216P(4),
        SR_CFG_VIDEO_SIZE_240P(5),
        SR_CFG_VIDEO_SIZE_270P(6),
        SR_CFG_VIDEO_SIZE_360P(7),
        SR_CFG_VIDEO_SIZE_480P(8),
        SR_CFG_VIDEO_SIZE_540P(9),
        SR_CFG_VIDEO_SIZE_720P(10),
        SR_CFG_VIDEO_SIZE_1080P(11),
        SR_CFG_VIDEO_SIZE_1440P(12),
        SR_CFG_VIDEO_SIZE_2160P(13),
        SR_CFG_VIDEO_SIZE_4320P(14),
        SR_CFG_DESKTOP_OPEN(15),
        SR_CFG_DESKTOP_CLOSE(16);

        private int type;

        private VideoType(int type) {
            this.type = type;
        }

        public int getValue() {
            return this.type;
        }
    }
}
