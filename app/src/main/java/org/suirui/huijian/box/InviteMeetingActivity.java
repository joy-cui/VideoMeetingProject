package org.suirui.huijian.box;

import android.bluetooth.BluetoothAdapter;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.suirui.srpaas.base.NetBean;
import com.suirui.srpaas.base.ui.activity.BaseAppCompatActivity;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.contant.Configure;
import com.suirui.srpaas.video.model.impl.ConfigureModelImpl;
import com.suirui.srpaas.video.passsdk.manages.AndroidAppUtil;
import com.suirui.srpaas.video.util.ParamUtil;
import com.suirui.srpaas.video.widget.dialog.ToastCommom;

import org.suirui.huijian.box.html.InviteActivity;

/**
 * 邀请弹框
 *
 * @authordingna
 * @date2017-12-21
 **/
public class InviteMeetingActivity extends BaseAppCompatActivity implements View.OnClickListener {
    SRLog log = new SRLog(InviteMeetingActivity.class.getName(), AppConfigure.LOG_LEVE);
    private String subject = "";
    private String confId = "";
    private String meetingId = "";
    private String confPwd = "";
    private int termId = 0;
    private String userName = "";
    private String inviteUrl = "";
    private boolean isAudioMeet = false;
    private LinearLayout btnEmailInvite, btnSMSInvite, btnCopyAddr, btnMeetSys, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFinishOnTouchOutside(true);
        setContentView(R.layout.sr_invite_dialog_layout);
        int screenW = ParamUtil.getScreenWidth(mContext);
        int screenH = ParamUtil.getScreenHeight(mContext);
        WindowManager.LayoutParams params = win.getAttributes();
        if (screenW > screenH) {//横屏
            params.width = screenH - 100;
            params.height = screenW / 3;
        } else {//竖屏
            params.width = screenW - 100;
            params.height = screenH / 3;
        }
        params.gravity = Gravity.BOTTOM;
        win.setAttributes(params);
        Intent intent = this.getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                subject = bundle.getString(AndroidAppUtil.EXTRA_SUBJECT);
                meetingId = bundle.getString(AndroidAppUtil.EXTRA_MEETING_ID);
                confId = bundle.getString(AndroidAppUtil.MyPrivate.EXTRA_CONFID);
                confPwd = bundle.getString(AndroidAppUtil.EXTRA_MEETING_PSW);
                termId = bundle.getInt(AndroidAppUtil.MyPrivate.EXTRA_TERMID, 0);
                userName = bundle.getString(AndroidAppUtil.MyPrivate.EXTRA_USERNAME);
                isAudioMeet = bundle.getBoolean(AndroidAppUtil.MyPrivate.EXTRA_MEET_TYPE, false);
                inviteUrl = bundle.getString(AndroidAppUtil.MyPrivate.EXTRA_INVITE_URL);
                log.E("邀请弹框....subject:" + subject + " meetingId:" + meetingId + " confId:" + confId + " confPwd:" + confPwd + " termId:" + termId + " userName:" + userName + " inviteUrl:" + inviteUrl);
            }
        }
        findviewList();
    }

    private void findviewList() {
        btnEmailInvite = (LinearLayout) findViewById(R.id.btnEmailInvite);
        btnSMSInvite = (LinearLayout) findViewById(R.id.btnSMSInvite);
        btnCopyAddr = (LinearLayout) findViewById(R.id.btnCopyAddr);
        btnCancel = (LinearLayout) findViewById(R.id.btnCancel);
        btnMeetSys = (LinearLayout) findViewById(R.id.btnMeetSys);
        btnEmailInvite.setOnClickListener(this);
        btnSMSInvite.setOnClickListener(this);
        btnCopyAddr.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnMeetSys.setOnClickListener(this);
    }

    @Override
    protected void onSensorEventChange(boolean b) {

    }

    @Override
    protected void onHeadsetStatus(boolean b) {

    }

    @Override
    protected void onBluetooth(int i, BluetoothAdapter bluetoothAdapter) {

    }

    @Override
    protected void onNetworkConnected(NetBean netBean) {

    }

    @Override
    protected void onNetworkDisConnected() {

    }

    @Override
    protected boolean isSupportActionBar() {
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEmailInvite://邮件邀请/通讯录
                doSkipInvite(ConfigureModelImpl.Invite.EMAIL_INVITE);
                break;
            case R.id.btnSMSInvite://短信邀请/拨号邀请
                doSkipInvite(ConfigureModelImpl.Invite.SMS_INVITE);
                break;
            case R.id.btnCopyAddr://复制地址
                setCopyMeetAddress();
                finish();
                break;
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnMeetSys://会议室系统
                doSkipInvite(ConfigureModelImpl.Invite.MEETSYS_INVITE);
                break;
        }
    }

    private void doSkipInvite(int inviteType) {
        Intent intent = new Intent(this, InviteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Configure.Invite.INVITE_TYPE, inviteType);
        bundle.putString(Configure.Invite.INVITE_CONFID, confId);
        bundle.putString(Configure.Invite.INVITE_CONPWD, confPwd);
        bundle.putInt(Configure.Invite.INVITE_TERMID, termId);
        bundle.putString(Configure.Invite.INVITE_TERMNAME, userName);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    //设置链接入会的地址
    private void setCopyMeetAddress() {
        String invite_Url = "";
        if (isAudioMeet) {
            invite_Url = inviteUrl + meetingId + "&pwd=" + confPwd + "&t=" + 0;
        } else {
            invite_Url = inviteUrl + meetingId + "&pwd=" + confPwd + "&t=" + 1;
        }
        log.E("链接地址.....invite_Url:" + invite_Url);
        ToastCommom.getInstance().ToastShowCenter(this, (ViewGroup) findViewById(R.id.success_tips_layout), getResources().getString(R.string.sr_invite_address), 5);
        ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clip.setText(invite_Url);
    }
}
