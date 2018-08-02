package org.suirui.huijian.tv.activity.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.srpaas.version.util.InstallUtil;
import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.setting.BaseFragment;
import org.suirui.huijian.tv.util.AppUtil;
import org.suirui.srpaas.contant.SRPaaSdkConfigure;

/**
 * Created by hh on 2018/4/25.
 *
 * 終端注冊失敗
 * 鏈接服務失敗
 */

public class FragmentFail extends BaseFragment implements View.OnClickListener{
    private static final SRLog log = new SRLog(FragmentFail.class.getName(), TVAppConfigure.LOG_LEVE);

    private FragmentInput mFragmentInput;
    private FragmentLogo mFragmentLogo;
    private Button mBtnSetNet;
    private Button mBtnSetServer;
    private TextView mTVFailTips;

    private int mShowStatus = FragmentLogo.ShowUIStatus.SERVER_CONNECT_FAIL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        View view = inflater.inflate(R.layout.m_set_server_fail_view, null);
        view.requestFocus();
        log.E("onCreateView-----mShowStatus:"+mShowStatus);
        mTVFailTips = (TextView) view.findViewById(R.id.tv_FailTips);
        mBtnSetNet = (Button) view.findViewById(R.id.btnSetNet);
        mBtnSetServer = (Button) view.findViewById(R.id.btnSetServer);

        Bundle bundle = getArguments();
        if(bundle != null){
            mShowStatus = bundle.getInt("type");
        }
        if(mShowStatus == FragmentLogo.ShowUIStatus.REGISTER_FAIL){
            mTVFailTips.setText(R.string.m_register_fail);
        }else if(mShowStatus == FragmentLogo.ShowUIStatus.SERVER_CONNECT_FAIL){
            mTVFailTips.setText(R.string.m_set_server_connect_fail);
        }
        if(!AppUtil.getInstance().isShowRootUI()){
//            mBtnSetNet.setVisibility(View.GONE);
            mBtnSetNet.setText(R.string.m_setting_sys);
        }

        mBtnSetNet.setOnClickListener(this);
        mBtnSetServer.setOnClickListener(this);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        log.E("onHiddenChanged-----hidden:"+hidden);
        if(hidden){
            return;
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
                FragmentMange.getInstance().showFragmentSetNet(FragmentFail.this);
                break;
            case R.id.btnSetServer:
                FragmentMange.getInstance().showFragmentInput(FragmentFail.this);
                break;
        }
    }
}

