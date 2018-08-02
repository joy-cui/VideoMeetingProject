package org.suirui.huijian.tv.activity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;
import com.suirui.srpaas.base.NetBean;
import com.suirui.srpaas.base.ui.activity.BaseAppCompatActivity;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.base.util.receiver.NetStateReceiver;
import com.suirui.srpaas.video.util.StringUtil;
import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.setting.BaseFragment;
import org.suirui.huijian.tv.activity.setting.FragmentSettingMenu;
import org.suirui.huijian.tv.util.VersionUpdateUtil;


/**
 * 设置界面
 */
public class SettingActivity extends BaseAppCompatActivity implements BaseFragment.onMyFragmentListener {
    private static final SRLog log = new SRLog(SettingActivity.class.getName(), TVAppConfigure.LOG_LEVE);
    private FragmentTransaction mFtransaction;
    private FragmentSettingMenu mFragmentSettingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m_setting_layout);
        showMenu();
        VersionUpdateUtil.getInstance(this).checkVersion();
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
        log.E("SettingActivity...NetStateReceiver.. wifiState:" + NetStateReceiver.isNetworkAvailable());
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
        VersionUpdateUtil.getInstance(this).onDestory();
    }

    public void setSettingTitle(String title){
        TextView arrowTV = (TextView)getDelegate().findViewById(R.id.settingArrow);
        TextView titleTV = (TextView)getDelegate().findViewById(R.id.settingTitle);
        if(StringUtil.isEmpty(title)){
            arrowTV.setVisibility(View.GONE);
            titleTV.setText(title);
            titleTV.setVisibility(View.GONE);
        }else{
            arrowTV.setVisibility(View.VISIBLE);
            titleTV.setText(title);
            titleTV.setVisibility(View.VISIBLE);
        }
    }

    private void showMenu() {
        mFtransaction = getSupportFragmentManager().beginTransaction();
        if(mFragmentSettingMenu == null) {
            mFragmentSettingMenu = new FragmentSettingMenu();
            mFtransaction.add(R.id.main_content, mFragmentSettingMenu);
        }else{
            mFtransaction.show(mFragmentSettingMenu);
        }
        mFtransaction.commit();
    }


    @Override
    public void onChangeTitle(String title) {
        setSettingTitle(title);
    }

}
