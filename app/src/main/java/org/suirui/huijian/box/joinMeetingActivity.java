package org.suirui.huijian.box;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.suirui.srpaas.base.NetBean;
import com.suirui.srpaas.base.ui.activity.BaseAppCompatActivity;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.third.ThirdApiListener;
import com.suirui.srpaas.video.util.StringUtil;

import org.suirui.huijian.box.bean.HistoryBean;
import org.suirui.huijian.box.dialog.HistoryDialog;
import org.suirui.huijian.box.manage.SRMiddleManage;
import org.suirui.huijian.box.util.HistoryUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * Created by cui.li on 2016/10/26.
 */

public class joinMeetingActivity extends BaseAppCompatActivity implements ThirdApiListener {
    private final String TAG = joinMeetingActivity.class.getName();
    EditText confid_edit, nickname_edit;
    String uid = "";
    private SRLog log = new SRLog(joinMeetingActivity.class.getName(), AppConfigure.LOG_LEVE);
    private String nickName = "";
    private String joinUrlConfId = "";
    private String joinUrlPwd = "";
    private Button join_meeting_btn;
    private ImageView hj_btn_history;
    private TextWatcher dietWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            setChangeBtnBg();

            String editable = nickname_edit.getText().toString();
            String str = stringFilter(editable.toString());
            if (!editable.equals(str)) {
                nickname_edit.setText(str);
                //设置新的光标所在位置
                nickname_edit.setSelection(str.length());
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            setChangeBtnBg();
        }
    };

    public static String stringFilter(String str) throws PatternSyntaxException {
        // 只允许字母、数字和汉字
        String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";//正则表达式
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThirdApi.getIntance(this).addThirdApiListener(this);
        log.E("joinMeetingActivity.........onCreate");
        setContentView(R.layout.join_meet_layout);
        confid_edit = (EditText) this.findViewById(R.id.confid_edit);
        nickname_edit = (EditText) this.findViewById(R.id.nickname_edit);
        join_meeting_btn = (Button) this.findViewById(R.id.join_meeting_btn);
        hj_btn_history = (ImageView) this.findViewById(R.id.hj_btn_history);
        Intent intent = this.getIntent();
        if (intent != null) {
            nickName = intent.getStringExtra("nickName");
            uid = intent.getStringExtra("uid");
        }
        setConfid();
        setNickName();
        setNickNameUid();
//        nickname_edit.setText(nickName);
        nickname_edit.addTextChangedListener(dietWatcher);
        confid_edit.addTextChangedListener(dietWatcher);
        setChangeBtnBg();
        getPix();
    }

    private void setConfid() {
        String suid = SRMiddleManage.getInstance().getSuid();
        List<HistoryBean> confid_list_ = HistoryUtil.getInstance(mContext).getHistoryListBySuid(suid);
        if (confid_list_ == null || confid_list_.size() <= 0) {
            confid_edit.setText("");
        } else {
            HistoryBean history = confid_list_.get(0);
            if (history != null) {
                confid_edit.setText(history.getConfId());
            }
        }
    }

    private void setNickName() {
        if (StringUtil.isEmpty(nickName)) {
            nickName = SRMiddleManage.getInstance().getNickName();
        }
        nickname_edit.setText(nickName);
    }

    private void setNickNameUid() {
        if (StringUtil.isEmpty(uid)) {
            uid = SRMiddleManage.getInstance().getSuid();
        }
    }

    private void getPix() {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        log.E("NoImageYuv...getPix...density:" + dm.density + " densityDpi:" + dm.densityDpi + "  w*h: " + dm.widthPixels + "*" + dm.heightPixels);
    }

    @Override
    protected void onSensorEventChange(boolean isonSensorChanged) {

    }

    @Override
    public void onStart() {
        super.onStart();
//        NetStateReceiver.registerNetworkStateReceiver(this);
        log.E("joinMeetingActivity........onStart");


    }

    @Override
    protected void onHeadsetStatus(boolean isHead) {

    }

    @Override
    protected void onBluetooth(int status, BluetoothAdapter mBluetoothAdapter) {

    }

    @Override
    protected void onNetworkConnected(NetBean netBean) {
        log.E("joinMeetingActivity...NetStateReceiver...onNetworkConnected...");
    }

    @Override
    protected void onNetworkDisConnected() {

    }

    @Override
    protected boolean isSupportActionBar() {
        return false;
    }

    private boolean isEable() {
        if (nickname_edit == null || nickname_edit.getText() == null || StringUtil.isEmpty(nickname_edit.getText().toString())) {
            return false;
        }
        if (confid_edit == null || confid_edit.getText() == null || StringUtil.isEmpty(confid_edit.getText().toString())) {
            return false;
        }
        return true;
    }

    private void setChangeBtnBg() {
        new Handler().post(new Runnable() {
            public void run() {
                if (isEable()) {
                    join_meeting_btn.setBackground(getResources().getDrawable(R.drawable.sr_login_bt_selector));
                } else {
                    join_meeting_btn.setBackground(getResources().getDrawable(R.drawable.login_bt_disabled));
                }
            }
        });
    }

    public void joinMeeting(View view) {
        if (!isEable()) {
            return;
        }
        nickName = nickname_edit.getText().toString();
        String confid = confid_edit.getText().toString();
        if (StringUtil.isEmpty(nickName)) {
            Toast.makeText(this, getResources().getString(R.string.please_input_nikename), Toast.LENGTH_LONG).show();
            return;
        }
        if (StringUtil.isEmpty(confid)) {
            Toast.makeText(this, getResources().getString(R.string.please_input_confid), Toast.LENGTH_LONG).show();
            return;
        }
        ThirdApi.getIntance(this).joinMeeting(joinMeetingActivity.this, AppConfigure.pass_url_root, AppConfigure.appId, AppConfigure.secretKey, ThirdApi.getIntance(this).getSharePreferDoMain(AppConfigure.DO_MAIN), AppConfigure.CONNECT_JOIN_URL, uid, nickName, confid, "");
    }

    public void backBtn(View view) {
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
//        NetStateReceiver.unRegisterNetworkStateReceiver(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onJoinError(int code, String errMsg) {
        Toast.makeText(joinMeetingActivity.this, errMsg, Toast.LENGTH_LONG).show();
    }

    public void historyData(View view) {
        if (isHistoryEable()) {
            openHistoryDialog();
        } else {
            Toast.makeText(joinMeetingActivity.this, getResources().getString(R.string.no_history_list), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isHistoryEable() {
//        List<HistoryBean> confid_list_ = SRMiddleManage.getInstance().getHistoryList();
        String suid = SRMiddleManage.getInstance().getSuid();
        List<HistoryBean> confid_list_ = HistoryUtil.getInstance(mContext).getHistoryListBySuid(suid);
        if (confid_list_ == null || confid_list_.size() <= 0)
            return false;
        return true;
    }

    private void openHistoryDialog() {
        try {
            final HistoryDialog historyDialog = new HistoryDialog(joinMeetingActivity.this);
            historyDialog.showPopupWindow(hj_btn_history);
            historyDialog.setClicklistener(new HistoryDialog.ClickListenerInterface() {
                @Override
                public void onCancel() {
                    historyDialog.dismiss();
                }

                @Override
                public void onComplete(HistoryBean history) {
                    if (history == null)
                        return;
                    if (!StringUtil.isEmpty(history.getConfId())) {
                        confid_edit.setText(history.getConfId());
                    }
//                    if (!StringUtil.isEmpty(history.getUserName())) {
//                        nickname_edit.setText(history.getUserName());
//                    }
                }
            });
        } catch (Exception e) {
            StringUtil.Exceptionex(TAG, "***openHistoryDialog***Exception***", e);
        }
    }
}
