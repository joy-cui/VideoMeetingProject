package org.suirui.huijian.tv.activity.setting;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.srpaas.version.contant.VersionConfigure;
import com.srpaas.version.service.VSRClient;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.common.http.okhttp.utils.NetworkUtil;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.util.AppUtil;
import org.suirui.huijian.tv.util.MultiKeyUtil;
import org.suirui.huijian.tv.util.TVNetworkUtil;
import org.suirui.huijian.tv.util.VersionUpdateUtil;
import org.suirui.huijian.tv.util.WirteUnique;
import org.suirui.huijian.tv.util.multKey.IMultKey;
import org.suirui.huijian.tv.util.multKey.MultiKeyImpl;
import org.suirui.huijian.tv.widget.ConfirmCommonDialog;
import org.suirui.huijian.tv.widget.ConfrimDialog;
import org.suirui.huijian.tv.widget.WaitingDialog;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hh on 2018/4/19.
 */
public class FragmentSettingSysInfo extends BaseFragment implements View.OnClickListener ,View.OnKeyListener{
    private static final SRLog log = new SRLog(FragmentSettingSysInfo.class.getName(), TVAppConfigure.LOG_LEVE);

    private Handler mHandler = new Handler();
    private TextView mCurVersionTV;
    private TextView mNewVersionTV;
    private TextView mIpAddressTV;
    private TextView mWifiAddressTV;
    private TextView mMacAddressTV;
    private Button mUpdateBtn;
    private WaitingDialog mWaitiDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        View view = inflater.inflate(R.layout.m_setting_sysinfo_layout, null);
        mUpdateBtn = (Button) view.findViewById(R.id.btnUpdate);
        mCurVersionTV = (TextView) view.findViewById(R.id.curVersionTV);
        mNewVersionTV = (TextView) view.findViewById(R.id.newVersionTV);
        mIpAddressTV = (TextView) view.findViewById(R.id.ipAddressTV);
        mWifiAddressTV = (TextView) view.findViewById(R.id.wifiAddressTV);
        mMacAddressTV = (TextView) view.findViewById(R.id.macAddressTV);

        String newVersion = VSRClient.getVersionService().getServerVersion();
        int updateStatus = VSRClient.getVersionService().getVersionStatus();
        mCurVersionTV.setText(getString(R.string.m_cur_version_info,new Object[]{AppUtil.getInstance().getAppVersionName(getContext())}));
        mNewVersionTV.setText(getString(R.string.m_new_version_info,new Object[]{newVersion}));
        mIpAddressTV.setText(NetworkUtil.getInstance(getContext()).getIpAddress());
        mWifiAddressTV.setText(NetworkUtil.getInstance(getContext()).getSSID());
        mMacAddressTV.setText(WirteUnique.getInstance().readUniqueMac());

        log.E("onCreateView" + "serverVersion"+VSRClient.getVersionService().getServerVersion());
        switch (updateStatus) {
            case VersionConfigure.Update.v_no_update://没有更新
                setButtonVisible(false);
                break;
            case VersionConfigure.Update.v_no_force_update:
                setButtonVisible(true);
                break;
        }
        mUpdateBtn.setOnClickListener(this);
        mUpdateBtn.setOnKeyListener(this);
        view.requestFocus();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        changeTitle(getString(R.string.m_sys_info));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        return MultiKeyUtil.getInstance().onMultiKey(getActivity(),keyCode,event,
                new MultiKeyImpl(),MultiKeyUtil.mMultKey_sysSet,
                new HashSet<>(Arrays.asList(MultiKeyUtil.mMultKey_sysSet)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnUpdate:
                onClickBtnUpdate();
                break;
        }
    }

    private void onClickBtnUpdate(){

        final ConfrimDialog dialog = new ConfrimDialog(getActivity());

        dialog.setWarningMsg(R.string.m_update_tips);
        dialog.show();
        dialog.setButton1(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setButton2(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VersionUpdateUtil.getInstance(getContext()).doDownloadVersion(getString(R.string.authoritise),false);
                dialog.dismiss();
            }
        });

//        VersionUpdateUtil.getInstance(getContext()).doDownloadVersion(getString(R.string.authoritise),false);
    }

    private void setButtonVisible(boolean isVisible){
        mUpdateBtn.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }



}
