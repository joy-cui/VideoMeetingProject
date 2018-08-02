package org.suirui.huijian.tv.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import com.suirui.pub.business.manage.IMiddleManage;
import com.suirui.srpaas.base.NetBean;
import com.suirui.srpaas.base.ui.activity.BaseAppCompatActivity;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.base.util.receiver.NetStateReceiver;
import com.suirui.srpaas.common.http.okhttp.utils.NetworkUtil;
import com.suirui.srpaas.video.event.DataEvent;
import com.suirui.srpaas.video.model.impl.ConfigureModelImpl;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.util.StringUtil;
import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.login.FragmentLogo;
import org.suirui.huijian.tv.adapter.MeetingHistoryAdapter;
import org.suirui.huijian.tv.bean.HistoryBean;
import org.suirui.huijian.tv.model.impl.MeetingModel;
import org.suirui.huijian.tv.prestener.impl.MeetingPrestener;
import org.suirui.huijian.tv.util.AppUtil;
import org.suirui.huijian.tv.util.ErrorCodeUtil;
import org.suirui.huijian.tv.util.HistoryUtil;
import org.suirui.huijian.tv.util.LoginUtil;
import org.suirui.huijian.tv.view.IMeetingHavePwdView;
import org.suirui.huijian.tv.widget.ConfirmInputPasswordDialog;
import org.suirui.huijian.tv.widget.MyListView;
import org.suirui.huijian.tv.widget.UIUtil;
import org.suirui.srpaas.entry.MeetingInfo;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 加入会议界面
 */
public class JoinMeetingActivity extends BaseAppCompatActivity implements Observer,IMeetingHavePwdView{
    private static final SRLog log = new SRLog(JoinMeetingActivity.class.getName(), TVAppConfigure.LOG_LEVE);

    private Handler mHandler = new Handler();
    private TextView mCallNumTV;
    private Button mBtnJoin;
    private MyListView mListView;
    private MeetingHistoryAdapter mAdapter;
    private View mPanelNoItem;
    private ConfirmInputPasswordDialog mPwdDialog;
    private MeetingInfo mJoinMeetingInfo;
    private MeetingPrestener mMeetingPrestener = new MeetingPrestener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.E("XXXXJoinMeetingActivity....onCreate()...");
        setContentView(R.layout.m_join_layout);
        findView();
        DataEvent.getInstance().addObserver(this);
        UIUtil.closeSoftKeyboard(JoinMeetingActivity.this,JoinMeetingActivity.this.getWindow().getDecorView());
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!ThirdApi.getIntance(this).isConnectState() && !NetworkUtil.hasDataNetwork(this)){
            Intent intent = new Intent(getApplicationContext(), LogoActivity.class);
            intent.putExtra("type", FragmentLogo.ShowUIStatus.SHOW_NET_FAIL);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        log.E("XXXXJoinMeetingActivity....onResume()...");
//        mCallNumTV.setText("");
        setJoinBtnEnable();
        showMeetingList();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
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
        log.E("JoinMeetingActivity...NetStateReceiver.. wifiState:" + NetStateReceiver.isNetworkAvailable());
    }

    @Override
    protected void onNetworkDisConnected() {

    }

    @Override
    protected boolean isSupportActionBar() {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissPwdDialog();
        mMeetingPrestener = null;
        DataEvent.getInstance().deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof DataEvent) {
            if (data instanceof DataEvent.NotifyCmd) {
                final DataEvent.NotifyCmd cmd = (DataEvent.NotifyCmd) data;
                switch (cmd.type) {
                    case ADD_HISTORY_MEET:
//                        HistoryUtil.getInstance(mContext).saveHistoryMeet((MeetingInfo) cmd.data);
                        mJoinMeetingInfo = (MeetingInfo) cmd.data;
                        HistoryUtil.getInstance(mContext).saveJoinMeetHistory(mJoinMeetingInfo, LoginUtil.getInstance().getSuid(JoinMeetingActivity.this),0);
                        break;
                    case MEETING_STATE:
                        int meetingState = (int) cmd.data;
                        log.E("XXX meetingstatus"+ + meetingState);
                        if(meetingState == ConfigureModelImpl.MeetState.MEETING_LEAVE_ING
                                || meetingState == ConfigureModelImpl.MeetState.MEETING_LEAVE_OR_END_SUCCESS){
                            HistoryUtil.getInstance(mContext).saveJoinMeetHistory(mJoinMeetingInfo, LoginUtil.getInstance().getSuid(JoinMeetingActivity.this),System.currentTimeMillis());
                        }
                        break;
                    case TEMP_MEET_CONFID:
//                        HistoryUtil.getInstance(mContext).saveTempJoinConfid((String) cmd.data);
                        HistoryUtil.getInstance(mContext).saveTempJoinConfid((String) cmd.data, LoginUtil.getInstance().getSuid(JoinMeetingActivity.this));
                        break;
                    case UPDATE_HISTORY_LIST:
                        IMiddleManage.getInstance().updateHistoryList();
                        break;
                    case CLEAR_TERM_JOIN_CONFID:
                        HistoryUtil.getInstance(mContext).saveTempJoinConfid("", "");
                        break;
                    case UPDATE_CURRENT_MEETINFO:
                        IMiddleManage.getInstance().updateMeetInfo((String) cmd.data);
                        break;
                }
            }
        }
    }

    public void clickBtnNum0(View v){
        setNumTVText(v);
    }
    public void clickBtnNum1(View v){
        setNumTVText(v);
    }
    public void clickBtnNum2(View v){
        setNumTVText(v);
    }
    public void clickBtnNum3(View v){
        setNumTVText(v);
    }
    public void clickBtnNum4(View v){
        setNumTVText(v);
    }
    public void clickBtnNum5(View v){
        setNumTVText(v);
    }
    public void clickBtnNum6(View v){
        setNumTVText(v);
    }
    public void clickBtnNum7(View v){
        setNumTVText(v);
    }
    public void clickBtnNum8(View v){
        setNumTVText(v);
    }
    public void clickBtnNum9(View v){
        setNumTVText(v);
    }
    public void clickBtnNumDel(View v){
        setDelNumTV();
    }
    public void clickBtnJoin(View v){

        String meetId = mCallNumTV.getText().toString();
        if(!StringUtil.isEmpty(meetId)){
            mBtnJoin.setEnabled(false);
            getMeetingPassword(meetId);
        }
    }

    private void findView(){
        mBtnJoin = (Button) getDelegate().findViewById(R.id.num_j);
        mPanelNoItem = getDelegate().findViewById(R.id.panelNoItem);
        mCallNumTV = (TextView) getDelegate().findViewById(R.id.input_number_tv);
        mListView = (MyListView) getDelegate().findViewById(R.id.callHostoryListView);
        mListView.setPullDownRefreshEnabled(false);
        mListView.removeHeaderView();
        mCallNumTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setJoinBtnEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mHandler.postAtFrontOfQueue(new Runnable() {
                        public void run() {
                            mListView.setSelection(0);
                        }
                    });
                }
            }
        });
    }

    private void showMeetingList() {
        log.E("XXX Listener:showMeetingList():");
        String uid = LoginUtil.getInstance().getSuid(JoinMeetingActivity.this);
        final List<HistoryBean> historyList = HistoryUtil.getInstance(JoinMeetingActivity.this).getHistoryListBySuid(uid);

        if (historyList == null || historyList.size() <= 0) {
            log.W("Listener:showMeetingList(): historyList is null!!!");
            setPanelNoItemVisible(true);
        } else {
            setPanelNoItemVisible(false);
        }
        if (mAdapter == null) {
            mAdapter = new MeetingHistoryAdapter(JoinMeetingActivity.this, historyList);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.changeList(historyList);
            mAdapter.notifyDataSetChanged();
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HistoryBean member = historyList.get(position);
                String meetingNumber = member.getConfId();
                getMeetingPassword(meetingNumber);
            }
        });

//                mAdapter.setOnSelectClickListener(new onSelectClickListener() {
//
//                    @Override
//                    public void setOnShowMeetingItemInfo(
//                            SZRCMeetingListItem item) {
//                        mAdapter.notifyDataSetInvalidated();
//                        if(item != null){
//                            mMeetingItem = item;
//                        }
//                        showMeetingInfo(item);
//                    }
//
//                    @Override
//                    public void setOnShowFirstMeetingItemInfo(
//                            SZRCMeetingListItem item) {
//                        if(item != null){
//                            mMeetingItem = item;
//                        }
//                        showMeetingInfo(item);
//
//                    }
//                });

    }

    private void setPanelNoItemVisible(boolean isVisible){
        if(isVisible){
            mPanelNoItem.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }else{
            mPanelNoItem.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    private void setJoinBtnEnable(){
        String meetId = mCallNumTV.getText().toString();
        if(StringUtil.isEmpty(meetId)){
            mBtnJoin.setEnabled(false);
        }else{
            mBtnJoin.setEnabled(true);
        }
    }
    private void setNumTVText(View v){
        Button joinBtn = (Button) v;
        mCallNumTV.setText(mCallNumTV.getText().toString()+joinBtn.getText().toString().trim());
    }
    private void setDelNumTV(){
        String num = mCallNumTV.getText().toString();
        if(!StringUtil.isEmpty(num) && num.length() >=1){
            num = num.substring(0,num.length() -1);
        }
        mCallNumTV.setText(num);
    }

    private void getMeetingPassword(String meetId){
        if(mMeetingPrestener == null){
            mMeetingPrestener = new MeetingPrestener();
        }
        mMeetingPrestener.getMeetingPassword(JoinMeetingActivity.this,meetId,this);
    }

    private void showPwdDialog(final String meetId){
        if(mPwdDialog == null){
            mPwdDialog = new ConfirmInputPasswordDialog(JoinMeetingActivity.this);
        }
        mPwdDialog.show();
        mPwdDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            setJoinBtnEnable();
                        break;
                    }
                }
                return false;
            }
        });
        mPwdDialog.setOnJoinClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = mPwdDialog.getInputText();
                if(!StringUtil.isEmpty(meetId) && !StringUtil.isEmpty(pwd) ){
//                    v.setEnabled(false);
                    if(mMeetingPrestener == null){
                        mMeetingPrestener = new MeetingPrestener();
                    }
                    mMeetingPrestener.joinMeeting(JoinMeetingActivity.this,meetId,pwd);
                    mPwdDialog.dismiss();

                }
            }
        });
    }

    private void dismissPwdDialog(){
        if(mPwdDialog != null){
            mPwdDialog.dismiss();
        }
    }


    @Override
    public void onMeetingPwdError(int code, String msg) {
//        if(StringUtil.isEmpty(msg)){
//            AppUtil.getInstance().showWaringDialog(JoinMeetingActivity.this,getString(
//                    R.string.m_error_info,new Object[]{code}));
//        }else {
//            AppUtil.getInstance().showWaringDialog(JoinMeetingActivity.this, msg);
//        }

        AppUtil.getInstance().showWaringDialog(JoinMeetingActivity.this, ErrorCodeUtil.getCodeMsg(code));
        setJoinBtnEnable();
    }

    @Override
    public void onShowPwdDialog(String meetId) {
        showPwdDialog(meetId);

    }
}
