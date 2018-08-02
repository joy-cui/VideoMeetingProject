package org.suirui.huijian.tv.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.srpaas.version.util.InstallUtil;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.common.http.okhttp.utils.NetworkUtil;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.LogoActivity;
import org.suirui.huijian.tv.activity.SettingActivity;
import org.suirui.huijian.tv.activity.login.FragmentLogo;
import org.suirui.huijian.tv.util.AppUtil;

/**
 * Created by xh.wang on 2016/6/12.
 */
public class FragmentSettingMenu extends BaseFragment implements View.OnClickListener{
    private static final SRLog log = new SRLog(SettingActivity.class.getName(), TVAppConfigure.LOG_LEVE);

    private FragmentSettingAccount mFragmentSettingAccount;
    private FragmentSettingAudio mFragmentSettingAudio;
    private FragmentSettingCamera mFragmentSettingCamera;
    private FragmentSettingNet mFragmentSettingNet;
    private FragmentSettingSys mFragmentSettingSys;
    private FragmentSettingSysInfo mFragmentSettingSysInfo;
    private FragmentSettingAudioMic mFragmentSettingAudioMic;
    Button mBtnSetAccount;
    Button mBtnSetAudio;
    Button mBtnSetCamera;
    Button mBtnSetNet;
    Button mBtnSetSys;
    Button mBtnSetSysInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        log.I("FragmentSettingMenu--onCreateView()");
        View view = inflater.inflate(R.layout.m_setting_menu_layout, null);
        mBtnSetAccount = (Button) view.findViewById(R.id.btnSettingMenuAccount);
        mBtnSetAudio = (Button) view.findViewById(R.id.btnSettingMenuAudio);
        mBtnSetCamera = (Button) view.findViewById(R.id.btnSettingMenuCamera);
        mBtnSetNet = (Button) view.findViewById(R.id.btnSettingMenuNet);
        mBtnSetSys = (Button) view.findViewById(R.id.btnSettingMenuSys);
        mBtnSetSysInfo = (Button) view.findViewById(R.id.btnSettingMenuSysInfo);
        mBtnSetAccount.setOnClickListener(this);
        mBtnSetAudio.setOnClickListener(this);
        mBtnSetCamera.setOnClickListener(this);
        mBtnSetNet.setOnClickListener(this);
        mBtnSetSys.setOnClickListener(this);
        mBtnSetSysInfo.setOnClickListener(this);
        if(!AppUtil.getInstance().isShowRootUI()){
            mBtnSetNet.setVisibility(View.GONE);
        }
        view.requestFocus();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            return;
        }
        if(getCurFragment() == mFragmentSettingAccount){
            mBtnSetAccount.requestFocus();
        }else if(getCurFragment() == mFragmentSettingAudio){
            mBtnSetAudio.requestFocus();
        }else if(getCurFragment() == mFragmentSettingCamera){
            mBtnSetCamera.requestFocus();
        }else if(getCurFragment() == mFragmentSettingNet){
            mBtnSetNet.requestFocus();
            if(!NetworkUtil.hasDataNetwork(getActivity())){
                Intent intent = new Intent(getActivity(), LogoActivity.class);
                intent.putExtra("type", FragmentLogo.ShowUIStatus.SHOW_NET_FAIL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
            }
        }else if(getCurFragment() == mFragmentSettingSys){
            mBtnSetSys.requestFocus();
        }else if(getCurFragment() == mFragmentSettingSysInfo){
            mBtnSetSysInfo.requestFocus();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        changeTitle("");
        log.I("FragmentSettingMenu--onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        log.I("FragmentSettingMenu--onPause()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log.I("FragmentSettingMenu--onDestroy()");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSettingMenuAccount:
                onClickBtnMenuAccount();
                break;
            case R.id.btnSettingMenuAudio:
                onClickBtnMenuAudio();
                break;
            case R.id.btnSettingMenuCamera:
                onClickBtnMenuCamera();
                break;
            case R.id.btnSettingMenuNet:
                if(AppUtil.getInstance().isShowRootUI()){
                    onClickBtnMenuNet();
                }else{
                    Toast.makeText(getContext(),"此版本不支持网络设置",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnSettingMenuSys:
                onClickBtnMenuSys();
                break;
            case R.id.btnSettingMenuSysInfo:
                onClickBtnMenuSysInfo();
                break;
        }
    }

    private void onClickBtnMenuAccount(){
        if(mFragmentSettingAccount == null){
            mFragmentSettingAccount = new FragmentSettingAccount();
        }
        switchFragment(mFragmentSettingAccount);
    }

    private void onClickBtnMenuAudio(){
        if(mFragmentSettingAudio == null){
            mFragmentSettingAudio = new FragmentSettingAudio();
        }
        switchFragment(mFragmentSettingAudio);
    }

    private void onClickBtnMenuCamera(){
        if(mFragmentSettingCamera == null){
            mFragmentSettingCamera = new FragmentSettingCamera();
        }
        switchFragment(mFragmentSettingCamera);
    }

    private void onClickBtnMenuNet(){
        if(mFragmentSettingNet == null){
            mFragmentSettingNet = new FragmentSettingNet();
        }
        switchFragment(mFragmentSettingNet);
    }

    private void onClickBtnMenuSys(){
        if(mFragmentSettingSys == null){
            mFragmentSettingSys = new FragmentSettingSys();
        }
        switchFragment(mFragmentSettingSys);
    }

    private void onClickBtnMenuSysInfo(){
        if(mFragmentSettingSysInfo == null){
            mFragmentSettingSysInfo = new FragmentSettingSysInfo();
        }
        switchFragment(mFragmentSettingSysInfo);
    }

}
