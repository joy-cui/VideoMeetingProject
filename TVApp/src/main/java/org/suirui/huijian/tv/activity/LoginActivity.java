package org.suirui.huijian.tv.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.suirui.pub.business.manage.IMiddleManage;
import com.suirui.srpaas.base.NetBean;
import com.suirui.srpaas.base.ui.activity.BaseAppCompatActivity;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.base.util.receiver.NetStateReceiver;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.util.LoginUtil;
import org.suirui.websocket.client.manager.WsClientManager;

import java.util.Observable;
import java.util.Observer;


/**
 * 引导加载界面
 **/
public class LoginActivity extends BaseAppCompatActivity implements Observer {
    private static final SRLog log = new SRLog(LoginActivity.class.getName(), TVAppConfigure.LOG_LEVE);
//    private IConfigurePrestener configurePrestener = null;

    private Button mNextBtn;
    private Button mPreviousBtn;

    private EditText mAccountET;
    private EditText mPwdET;
    private EditText mServerTypeET;
    private EditText mServerHostET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.E("WelcomeActivity....onCreate()...");
        setContentView(R.layout.m_login);
        findView();
        IMiddleManage.getInstance().addObserver(this);
    }

    @Override
    public void onStart() {
        super.onStart();
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
        log.E("WelcomeActivity...NetStateReceiver.. wifiState:" + NetStateReceiver.isNetworkAvailable());
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
        IMiddleManage.getInstance().deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof IMiddleManage) {
            if (data instanceof IMiddleManage.NotifyCmd) {
                final IMiddleManage.NotifyCmd cmd = (IMiddleManage.NotifyCmd) data;
                switch (cmd.type) {
                    case LOGIN_FAIL:
                        log.E("LoginActivity...LOGIN_FAIL..."+cmd.data);
                        LoginUtil.getInstance().ShowLoginFail(LoginActivity.this,(int)cmd.data);
                        break;
                    case LOGIN_STATE:
                       boolean status = (boolean)cmd.data;
                        if(status){
                            enterMainActiviey(status);
                        }
                        break;
                }
            }
        }
    }

    public void onClickBtnPrevious(View v){

    }

    public void onClickBtnNext(View v){
        String account = mAccountET.getText().toString();
        String pwd = mPwdET.getText().toString();
        String serverType = mServerTypeET.getText().toString();
        String serverHost = mServerHostET.getText().toString();
        LoginUtil.getInstance().login(LoginActivity.this,account,pwd,serverHost,serverType);
    }

    private void findView(){
        mNextBtn = (Button) getDelegate().findViewById(R.id.btnNext);
        mPreviousBtn = (Button) getDelegate().findViewById(R.id.btnNext);
        mAccountET = (EditText) getDelegate().findViewById(R.id.edtAccount);
        mPwdET = (EditText) getDelegate().findViewById(R.id.edtPwd);
        mServerTypeET = (EditText) getDelegate().findViewById(R.id.edtServerType);
        mServerHostET = (EditText) getDelegate().findViewById(R.id.edtServerAddress);
    }

    private void enterMainActiviey(boolean isSuccess){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
