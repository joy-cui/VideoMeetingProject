package org.suirui.huijian.box;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.srpaas.version.entry.VersionInfo;
import com.suirui.srpaas.base.NetBean;
import com.suirui.srpaas.base.ui.activity.BaseAppCompatActivity;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.common.permission.AndPermission;
import com.suirui.srpaas.video.contant.Configure;
import com.suirui.srpaas.video.passsdk.manages.HuiJianSDKEvent;
import com.suirui.srpaas.video.passsdk.manages.MeetingListener;
import com.suirui.srpaas.video.passsdk.manages.TermInfo;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.third.ThirdApiListener;
import com.suirui.srpaas.video.third.VideoProperties;
import com.suirui.srpaas.video.util.StringUtil;
import com.suirui.srpaas.video.util.ToolsUtil;
import com.suirui.srpaas.video.util.TvToolsUtil;

import org.suirui.huijian.box.dialog.SettingServerDialog;
import org.suirui.huijian.box.manage.SRMiddleManage;
import org.suirui.huijian.box.util.VersionUpdateUtil;
import org.suirui.login.huijian.ui.LoginActivity;
import org.suirui.srpaas.entry.SRError;

import static org.suirui.huijian.box.AppConfigure.CONNECT_JOIN_URL;
import static org.suirui.huijian.box.AppConfigure.pass_url_root;

/**
 * Created by cui.li on 2016/10/26.
 */

public class MainActivity extends BaseAppCompatActivity implements ThirdApiListener, VersionUpdateUtil.CheckVersionListener,MeetingListener {
    private SRLog log = new SRLog(MainActivity.class.getName(), AppConfigure.LOG_LEVE);
    private int TH_TYPE = AppConfigure.URLMeet.NO_TYPE; //1 start开始会议 2 加入会议  3 结束会议  4 连接入会
    private String appid = "";
    private String MeetingNumber = "";
    private String NickName = "";
    private String pwd = "";
    private String uid = "";
    private String Domain = "";
    private String secretKeyTh = "";

//    private void setDoMain() {
//        if (StringUtil.isEmpty(Domain)) {
//            Domain = AppConfigure.DO_MAIN;
//        }
//    }

//    private void setAppId() {
//        if (StringUtil.isEmpty(appid)) {
//            appid = AppConfigure.appId;
//        }
//    }

//    private void getMainIntent() {
//        Intent mainIntent = getIntent();
//        if (mainIntent != null) {
//            String action = mainIntent.getAction();
//            Uri uri = mainIntent.getData();
//            if (uri == null) return;
//            String path = uri.getPath();
//            if (path != null) {
//                if (path.contains(AppConfigure.URLMeet.START)) {
//                    TH_TYPE = Configure.JoinType.TH_START_TYPE;
//                    getUriParams(uri, TH_TYPE);
//                } else if (path.contains(AppConfigure.URLMeet.JOIN)) {
//                    TH_TYPE = Configure.JoinType.TH_JOIN_TYPE;
//                    getUriParams(uri, TH_TYPE);
//                } else if (path.contains(AppConfigure.URLMeet.END)) {
//                    TH_TYPE = Configure.JoinType.TH_END_TYPE;
//                    getUriParams(uri, TH_TYPE);
//                } else if (path.contains(AppConfigure.URLMeet.JM)) {
//                    TH_TYPE = Configure.JoinType.TH_JM_TYPE;
//                    getUriParams(uri, TH_TYPE);
//                } else {
//                    TH_TYPE = AppConfigure.URLMeet.NO_TYPE;
//                }
//            }
//
//
//        }
//
//    }

//    private void getUriParams(Uri uri, int type) {
//        if (uri != null && type != AppConfigure.URLMeet.NO_TYPE) {
//            if (type == Configure.JoinType.TH_JM_TYPE) {
//                MeetingNumber = uri.getQueryParameter(AppConfigure.URLMeet.ID);
//                pwd = uri.getQueryParameter(AppConfigure.URLMeet.pwd);
//            } else if (type == Configure.JoinType.TH_START_TYPE || type == Configure.JoinType.TH_JOIN_TYPE) {
//                //加入会议或者开始会议
//                appid = uri.getQueryParameter(AppConfigure.URLMeet.appid);
//                MeetingNumber = uri.getQueryParameter(AppConfigure.URLMeet.MeetingNumber);
//                NickName = uri.getQueryParameter(AppConfigure.URLMeet.NickName);
//                pwd = uri.getQueryParameter(AppConfigure.URLMeet.pwd);
//                uid = uri.getQueryParameter(AppConfigure.URLMeet.uid);
//                Domain = uri.getQueryParameter(AppConfigure.URLMeet.Domain);
//                secretKeyTh = uri.getQueryParameter(AppConfigure.URLMeet.secretKey);
////                log.E("加入会议或者开始会议...appid:" + appid + "  MeetingNumber: " + MeetingNumber + "  NickName: " + NickName + " pwd:" + pwd + " uid:" + uid + " Domain:" + Domain);
//
//            } else if (type == Configure.JoinType.TH_END_TYPE) {
//                //结束会议
//                MeetingNumber = uri.getQueryParameter(AppConfigure.URLMeet.MeetingNumber);
//            }
//
//        }
//
//    }

//    private boolean isjoinMeet() {
//        if (TH_TYPE != AppConfigure.URLMeet.NO_TYPE) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    //    private void joinMeet() {
//        if (isjoinMeet()) {
//            //连接入会
//            new Handler().postDelayed(new Runnable() {
//                public void run() {
//                    if (TH_TYPE == Configure.JoinType.TH_END_TYPE) {//结束会议
////                        if (MeetingNumber == null || MeetingNumber.equals("")) {
////                            Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.conf_no_null), Toast.LENGTH_LONG).show();
////                        } else {
//                        ThirdApi.getIntance(MainActivity.this).endUrlConf(MainActivity.this, MeetingNumber);
////                        }
//                    } else {
//                        setDoMain();
//                        setNickNameUid();
//                        setAppId();
//                        if (StringUtil.isEmpty(secretKeyTh)) {
//                            secretKeyTh = AppConfigure.secretKey;
//                        }
//                        ThirdApi.getIntance(MainActivity.this).joinUrlConf(MainActivity.this, AppConfigure.pass_url_root, appid, secretKeyTh, Domain, CONNECT_JOIN_URL, uid, NickName, MeetingNumber, pwd, TH_TYPE);
//                    }
//                }
//            }, 10);
//
//        }
//    }
    //    @Override
//    public void onBackPressed() {
////        super.onBackPressed();
//        //点击返回,退出程序
////        log.E("MainActivity........onBackPressed");
//        android.os.Process.killProcess(android.os.Process.myPid());
////        isExsitMianActivity(MainActivity.class);
//        System.exit(0);
//
//    }
    private long exitTime = 0;
    //    /**
//     * 判断某一个类是否存在任务栈里面
//     *
//     * @return
//     */
//    private boolean isExsitMianActivity(Class<MainActivity> cls) {
//        Intent intent = new Intent(this, cls);
//        ComponentName cmpName = intent.resolveActivity(getPackageManager());
//        boolean flag = false;
////        log.E("MainActivity....cmpName:" + cmpName);
//        if (cmpName != null) {
//            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//            List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
//            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
////                log.E("MainActivity....taskInfo.baseActivity:" + taskInfo.baseActivity);
//                flag = true;
//            }
//        }
//        return flag;
//    }
    private boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.E("MainActivity....onCreate()");
//        getMainIntent();
//        setNickNameUid();
//        ThirdApi.getIntance(this).cleanSharedPreference(this, BuildConfig.VERSION_CODE);
//        ThirdApi.getIntance(this).addThirdApiListener(this);
//        if (!this.isTaskRoot()) {//是否是最后一个activity
//            if (!isjoinMeet()) {
//                finish();
//                return;
//            } else {
//                //连接入会
//                joinMeet();
//                finish();
//                return;
//
//            }
//        } else {
//            joinMeet();
//        }
        setContentView(R.layout.main);
        HuiJianSDKEvent.getInstance().addMeetingListener(this);
        TextView version = (TextView) this.findViewById(R.id.version);
        String versionNum = ToolsUtil.getCurrentVersionNum();
        version.setText(versionNum);
        TextView sr_btn_out = (TextView) findViewById(R.id.sr_btn_out);
        ImageButton setting_btn = (ImageButton) this.findViewById(R.id.setting_btn);
        if (AppConfigure.isLoginUI) {
            sr_btn_out.setVisibility(View.VISIBLE);
            setting_btn.setVisibility(View.GONE);
            sr_btn_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    log.E("MainActivity....退出登录..");
                    SRMiddleManage.getInstance().onLoginOut();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    Bundle b = new Bundle();
                    b.putBoolean(AppConfigure.VPN_MODULE, AppConfigure.isVpnModule);
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }
            });

        } else {
            sr_btn_out.setVisibility(View.GONE);
            setting_btn.setVisibility(View.VISIBLE);
            setting_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settingClick();
                }
            });
        }
        VersionUpdateUtil.getInstance().addCheckPermissionListener(this);
        if (AppConfigure.VERSION_UPDATE)//配置文件配置，是否带更新功能
            VersionUpdateUtil.getInstance().doAppUpdate(MainActivity.this, false);

        if (AppConfigure.isAudioMeet) {
            Button start_audio_meeting_btn = (Button) findViewById(R.id.start_audio_meeting_btn);
            start_audio_meeting_btn.setVisibility(View.VISIBLE);
            Button start_meeting_btn = (Button) findViewById(R.id.start_meeting_btn);
            start_meeting_btn.setVisibility(View.GONE);
        } else {
            Button start_audio_meeting_btn = (Button) findViewById(R.id.start_audio_meeting_btn);
            start_audio_meeting_btn.setVisibility(View.GONE);
            Button start_meeting_btn = (Button) findViewById(R.id.start_meeting_btn);
            start_meeting_btn.setVisibility(View.VISIBLE);
        }
    }

    private void setNickNameUid() {
        if (StringUtil.isEmpty(NickName)) {
            NickName = SRMiddleManage.getInstance().getNickName();
        }
        if (StringUtil.isEmpty(uid)) {
            uid = SRMiddleManage.getInstance().getSuid();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConfigure.permission.WRITE_EXTERNAL_STORAGE:
                int length = grantResults.length;
                if (length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {// Permission Granted
                        log.E("SRVideoActivity...允许写文件权限");
                        VersionUpdateUtil.getInstance().doDownloadVersion(MainActivity.this);
                    } else {// Permission Denied
                        log.E("SRVideoActivity...拒绝写文件权限");
                        Toast.makeText(MainActivity.this, "获取权限失败，不能进行版本更新下载", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onSensorEventChange(boolean isonSensorChanged) {

    }

    @Override
    public void onStart() {
        super.onStart();
//        NetStateReceiver.registerNetworkStateReceiver(this);//注册网络状态
        log.E("MainActivity........onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        log.E("MainActivity........onRestart");
    }

//    @Override
//    protected int getContentViewLayoutID() {
//        return  R.layout.main;
//    }

    @Override
    protected void onHeadsetStatus(boolean isHead) {

    }

    @Override
    protected void onBluetooth(int status, BluetoothAdapter mBluetoothAdapter) {

    }

    @Override
    protected void onNetworkConnected(NetBean netBean) {
        log.E("MainActivity....NetStateReceiver...onNetworkConnected...");
    }

    @Override
    protected void onNetworkDisConnected() {

    }

    @Override
    protected boolean isSupportActionBar() {
        return false;
    }

    public void joinMeeting(View view) {
        setNickNameUid();
        Intent intent = new Intent(this, joinMeetingActivity.class);
        intent.putExtra("nickName", NickName);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }

    public void startMeeting(View view) {
        log.E("开始会议....AppConfigure.DO_MAIN:" + AppConfigure.DO_MAIN);
        setNickNameUid();
        ThirdApi.getIntance(this).startMeeting(MainActivity.this, pass_url_root, AppConfigure.appId, AppConfigure.secretKey, ThirdApi.getIntance(this).getSharePreferDoMain(AppConfigure.DO_MAIN), CONNECT_JOIN_URL, uid, NickName, "", "");
    }

    public void startAudioMeeting(View view) {
        log.E("开始音频会议....AppConfigure.DO_MAIN:" + AppConfigure.DO_MAIN);
        setNickNameUid();
        ThirdApi.getIntance(this).startAudioMeeting(MainActivity.this, pass_url_root, AppConfigure.appId, AppConfigure.secretKey, ThirdApi.getIntance(this).getSharePreferDoMain(AppConfigure.DO_MAIN), CONNECT_JOIN_URL, uid, NickName, "", "");
    }

    //退出
    public void settingClick() {
//        final SettingDialog settingDialog = new SettingDialog(this, R.style.sr_custom_dialog);
//        settingDialog.show();
//        settingDialog.setClicklistener(new SettingDialog.ClickListenerInterface() {
//            @Override
//            public void onCancel() {
//                settingDialog.dismiss();
//            }
//
//            @Override
//            public void onComplete(String addressName,String requestHttp) {
//                ThirdApi.getIntance(MainActivity.this).setServerAdress(addressName,requestHttp);
//                settingDialog.dismiss();
//            }
//        });

        final SettingServerDialog settingServerDialog = new SettingServerDialog(this, R.style.sr_custom_dialog);
        settingServerDialog.show();
        settingServerDialog.setClicklistener(new SettingServerDialog.ClickListenerInterface() {
            @Override
            public void onComplete(String addressName, String httpEquest, boolean isDefalut) {
//                String doMain = ThirdApi.getIntance(MainActivity.this).getDoMain();
                String doMain = ThirdApi.getIntance(MainActivity.this).getSharePreferDoMain(AppConfigure.DO_MAIN);
                String newDoMain = httpEquest + Configure.SPRITR + addressName;
                log.E("MainActivity...onComplete...doMain:" + doMain + "  newDoMain:" + newDoMain);
                if (!StringUtil.isEmpty(doMain) && !StringUtil.isEmpty(newDoMain) && !doMain.equals(newDoMain)) {
                    //获取新服务器的配置文件
                    SrHuiJianProperties.loadProperties(newDoMain, MainActivity.this);//加载会见的配置文件
                    VideoProperties.loadProperties(newDoMain);//加载video配置文件
                    //获取新服务器地址是否有版本更新
                    if (AppConfigure.VERSION_UPDATE)
                        VersionUpdateUtil.getInstance().doAppUpdate(MainActivity.this, false);
                    //保存新设置的服务器地址
                    ThirdApi.getIntance(mContext).updateAdressUrl(addressName, httpEquest, isDefalut);
                }
                settingServerDialog.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
//        log.E("MainActivity........onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
//        log.E("MainActivity........onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
//        log.E("MainActivity........onStop");
        super.onStop();
//        NetStateReceiver.unRegisterNetworkStateReceiver(this);//注册网络状态
    }

    /**
     * 连续点击两次，退出App
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
//            if (AppConfigure.isLoginUI) {
//                if (KeyEvent.ACTION_DOWN == event.getAction()) {
//                    this.moveTaskToBack(true);
//                }
//                return true;
//            } else {
            AppApplication.getInstance().release(false);
//            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清除会议号
//        log.E("MainActivity........onDestroy");
    }

    @Override
    public void onJoinError(int code, String errMsg) {
        Toast.makeText(MainActivity.this, errMsg, Toast.LENGTH_LONG).show();
    }

//    @Override
//    public void onCheckPermission(String permission, int requestCode) {
//        if (!AndPermission.hasPermission(MainActivity.this, permission)) {
//            AndPermission.with(MainActivity.this)
//                    .requestCode(requestCode)
//                    .permission(permission)
//                    .rationale(
//                            new RationaleListener() {
//                                @Override
//                                public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
//                                    // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
//                                    AndPermission.rationaleDialog(MainActivity.this, rationale).show();
//                                }
//                            }
//                    ).send();
//        } else {
//            VersionUpdateUtil.getInstance().doDownloadVersion(MainActivity.this);
//        }
//    }

    @Override
    public void exitApp() {
        AppApplication.getInstance().release(false);
    }

    @Override
    public void openDialog(VersionInfo info) {

    }

    @Override
    public void onTermLeaveCallBack(String confId, TermInfo termInfo, SRError srError) {
        log.E("onTermLeaveCallBack.....");
    }

    @Override
    public void onNewTermJoinCallBack(String confId, TermInfo termInfo) {
        log.E("onTermLeaveCallBack.....");
    }

    @Override
    public void onMeetingState(String confId, int code, String msg) {
        log.E("onMeetingState.....");
    }
}
