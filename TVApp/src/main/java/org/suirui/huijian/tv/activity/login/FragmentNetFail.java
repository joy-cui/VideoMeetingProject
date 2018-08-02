package org.suirui.huijian.tv.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.srpaas.version.util.InstallUtil;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.common.http.okhttp.utils.NetworkUtil;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.setting.BaseFragment;
import org.suirui.huijian.tv.util.AppUtil;

/**
 * Created by hh on 2018/4/25.
 *
 *
 * 网络连接失敗
 */

public class FragmentNetFail extends BaseFragment implements View.OnClickListener{
    private static final SRLog log = new SRLog(FragmentNetFail.class.getName(), TVAppConfigure.LOG_LEVE);

    private Button mBtnSetNet;
    private TextView mTVFailTips;

    private int mShowStatus = FragmentLogo.ShowUIStatus.SERVER_CONNECT_FAIL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        View view = inflater.inflate(R.layout.m_net_fail_view, null);
        view.requestFocus();
        log.E("onCreateView-----mShowStatus:"+mShowStatus);
        mTVFailTips = (TextView) view.findViewById(R.id.tv_FailTips);
        mBtnSetNet = (Button) view.findViewById(R.id.btnSetNet);

        if(!AppUtil.getInstance().isShowRootUI()){
            mBtnSetNet.setText(R.string.m_setting_sys);
        }
        mBtnSetNet.setOnClickListener(this);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            return;
        }
        if(NetworkUtil.hasDataNetwork(getActivity())){
            FragmentMange.getInstance().showFragmentLogo(this,FragmentLogo.ShowUIStatus.SHOW_RELOGIN);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSetNet:
                FragmentMange.getInstance().showFragmentSetNet(FragmentNetFail.this);
                break;
        }
    }
}

