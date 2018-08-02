package org.suirui.huijian.tv.activity.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.setting.BaseFragment;

/**
 * Created by hh on 2018/4/25.
 *
 * 网络设置
 */

public class FragmentSetNet extends BaseFragment implements View.OnClickListener{
    private static final SRLog log = new SRLog(FragmentSetNet.class.getName(), TVAppConfigure.LOG_LEVE);

    private FragmentInput mFragmentInput;
    private FragmentLogo mFragmentLogo;
    private Button mBtnSetNet;
    private Button mBtnSetServer;
    private TextView mTVFailTips;

    private int mShowStatus = FragmentLogo.ShowUIStatus.SERVER_CONNECT_FAIL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        View view = inflater.inflate(R.layout.m_set_net_view, null);
        view.requestFocus();
        log.E("onCreateView-----mShowStatus:"+mShowStatus);
//        mTVFailTips = (TextView) view.findViewById(R.id.tv_FailTips);
        mBtnSetNet = (Button) view.findViewById(R.id.btnSetNet);
//        mBtnSetServer = (Button) view.findViewById(R.id.btnSetServer);
//
//        Bundle bundle = getArguments();
//        if(bundle != null){
//            mShowStatus = bundle.getInt("type");
//        }
//        if(mShowStatus == FragmentLogo.ShowUIStatus.REGISTER_FAIL){
//            mTVFailTips.setText(R.string.m_register_fail);
//        }else if(mShowStatus == FragmentLogo.ShowUIStatus.SERVER_CONNECT_FAIL){
//            mTVFailTips.setText(R.string.m_set_server_connect_fail);
//        }
//
//
        mBtnSetNet.setOnClickListener(this);
//        mBtnSetServer.setOnClickListener(this);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
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
//                FragmentMange.getInstance().showFragmentInput(FragmentSetNet.this);
                break;
        }
    }
}

