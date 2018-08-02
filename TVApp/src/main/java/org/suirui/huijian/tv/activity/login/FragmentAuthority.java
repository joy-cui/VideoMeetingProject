package org.suirui.huijian.tv.activity.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.common.http.okhttp.utils.NetworkUtil;
import com.suirui.srpaas.video.util.StringUtil;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.setting.BaseFragment;
import org.suirui.huijian.tv.util.LoginUtil;
import org.suirui.huijian.tv.util.WirteUnique;

/**
 * Created by hh on 2018/4/25.
 *
 * 終端未授权
 *
 */

public class FragmentAuthority extends BaseFragment implements View.OnClickListener{
    private static final SRLog log = new SRLog(FragmentAuthority.class.getName(), TVAppConfigure.LOG_LEVE);

    private FragmentInput mFragmentInput;
    private FragmentLogo mFragmentLogo;
    private Button mBtnAuthority;
    private Button mBtnChangeServer;
    private TextView mTVTermInfo;

    private int mShowStatus = FragmentLogo.ShowUIStatus.SHOW_AUTHORIZING;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        View view = inflater.inflate(R.layout.m_set_authority_view, null);
        view.requestFocus();
        log.E("onCreateView-----mShowStatus:"+mShowStatus);
        mTVTermInfo = (TextView) view.findViewById(R.id.tvTermInfo);
        mBtnAuthority = (Button) view.findViewById(R.id.btnAuthority);
        mBtnChangeServer = (Button) view.findViewById(R.id.btnChangeServer);
        String unique = WirteUnique.getInstance().readUniqueMac();
        mTVTermInfo.setText(
                getString(
                        R.string.m_term_info,
                        new Object[]{
                                LoginUtil.getInstance().getNickName(getContext()),
                                NetworkUtil.getInstance(getContext()).getIpAddress(),
//                                NetworkUtil.getInstance(getContext()).getMac()
                                StringUtil.isEmpty(unique) ? "" : unique.toString().trim()
                        }));
        mBtnAuthority.setOnClickListener(this);
        mBtnChangeServer.setOnClickListener(this);
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
            case R.id.btnAuthority:
                showFragmentLogo();
                break;
            case R.id.btnChangeServer:
                FragmentMange.getInstance().showFragmentInput(FragmentAuthority.this);
//                showFragmentInput();
                break;
        }
    }

    private void showFragmentLogo(){
        if(mFragmentLogo == null){
            mFragmentLogo = new FragmentLogo();
        }
        Bundle args = new Bundle();
        args.putInt("type",FragmentLogo.ShowUIStatus.SHOW_AUTHORIZING);
        mFragmentLogo.setArguments(args);
        switchFragment(mFragmentLogo);

    }
}

