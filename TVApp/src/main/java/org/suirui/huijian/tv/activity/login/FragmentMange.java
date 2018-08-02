package org.suirui.huijian.tv.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;

import com.srpaas.version.util.InstallUtil;
import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.setting.BaseFragment;
import org.suirui.huijian.tv.activity.setting.FragmentSettingNet;
import org.suirui.huijian.tv.util.AppUtil;

/**
 * Created by hh on 2018/5/2.
 */

public class FragmentMange {
    private static final SRLog log = new SRLog(FragmentMange.class.getName(), TVAppConfigure.LOG_LEVE);
    private static FragmentMange instance = null;
    FragmentInputNick mFragmentInputNick;
    FragmentLogo mFragmentLogo;
    FragmentInput mFragmentInput;
    FragmentNetFail mFragmentNetFail;
    FragmentSettingNet mFragmentSetNet;
    FragmentFail mFragmentFail;

    private FragmentMange() {
    }

    public static synchronized FragmentMange getInstance() {
        if (instance == null) {
            instance = new FragmentMange();
        }

        return instance;
    }


    public void showFragmentNick(BaseFragment fragment, String http, String ip){
//        if(mFragmentInputNick == null){
//            mFragmentInputNick = new FragmentInputNick();
//        }else{
//            mFragmentInputNick = null;
//            mFragmentInputNick = new FragmentInputNick();
//        }
//        String address = http + "://"+ip;
//        Bundle args = new Bundle();
//            args.putInt("type",FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER);
//            args.putString("http",http);
//            args.putString("address",address);
//            args.putString("ip",ip);
//        if (!mFragmentInputNick.isAdded()){
//            log.E("wwwwwww--------11111");
//            mFragmentInputNick.setArguments(args);
//        }else {
//            log.E("wwwwwww--------22222222");
//        }
//        fragment.switchFragment(mFragmentInputNick);
        showFragmentNick(fragment,FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER, http, ip);
    }

    public void showFragmentNick(BaseFragment fragment,int type, String http, String ip){
        if(mFragmentInputNick == null){
            mFragmentInputNick = new FragmentInputNick();
        }else{
            mFragmentInputNick = null;
            mFragmentInputNick = new FragmentInputNick();
        }
        String address = http + "://"+ip;
        Bundle args = new Bundle();
        args.putInt("type",type);
        args.putString("http",http);
        args.putString("address",address);
        args.putString("ip",ip);
        if (!mFragmentInputNick.isAdded()){
            log.E("wwwwwww--------11111");
            mFragmentInputNick.setArguments(args);
        }else {
            log.E("wwwwwww--------22222222");
        }
        fragment.switchFragment(mFragmentInputNick);
    }
    public void showFragmentLogo(BaseFragment fragment,int type) {
        if (mFragmentLogo == null) {
            mFragmentLogo = new FragmentLogo();
        }else{
            mFragmentLogo = null;
            mFragmentLogo = new FragmentLogo();
        }
        Bundle args = new Bundle();
        args.putInt("type", type);
        if (!mFragmentLogo.isAdded()){
            log.E("showFragmentLogo--------11111");
            mFragmentLogo.setArguments(args);
        }else {
            log.E("showFragmentLogo--------22222222");
        }

        fragment.switchFragment(mFragmentLogo);
    }

    public void showFragmentLogo(BaseFragment fragment,int type, String http, String ip,String nick) {
        if (mFragmentLogo == null) {
            mFragmentLogo = new FragmentLogo();
        }else{
            mFragmentLogo = null;
            mFragmentLogo = new FragmentLogo();
        }
        String address = http + "://"+ip;
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("http", http);
        args.putString("address", address);
        args.putString("ip", ip);
        args.putString("nick", nick);
        if (!mFragmentLogo.isAdded()){
            log.E("showFragmentLogo--------11111");
            mFragmentLogo.setArguments(args);
        }else {
            log.E("showFragmentLogo--------22222222");
        }

        fragment.switchFragment(mFragmentLogo);

    }
    public void showFragmentLogo(BaseFragment fragment, String http, String ip,String nick) {
        showFragmentLogo(fragment,FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER, http, ip,nick);
//        if (mFragmentLogo == null) {
//            mFragmentLogo = new FragmentLogo();
//        }else{
//            mFragmentLogo = null;
//            mFragmentLogo = new FragmentLogo();
//        }
//        String address = http + "://"+ip;
//        Bundle args = new Bundle();
//        args.putInt("type", FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER);
//        args.putString("http", http);
//        args.putString("address", address);
//        args.putString("ip", ip);
//        args.putString("nick", nick);
//        if (!mFragmentLogo.isAdded()){
//            log.E("showFragmentLogo--------11111");
//            mFragmentLogo.setArguments(args);
//        }else {
//            log.E("showFragmentLogo--------22222222");
//        }
//
//        fragment.switchFragment(mFragmentLogo);

    }

    public void showFragmentInput(BaseFragment fragment){
//        if(mFragmentInput == null){
//            mFragmentInput = new FragmentInput();
//        }else{
//            mFragmentInput = null;
//            mFragmentInput = new FragmentInput();
//        }
//        Bundle args = new Bundle();
//        args.putInt("type",FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER);
//        mFragmentInput.setArguments(args);
//        fragment.switchFragment(mFragmentInput);
        showFragmentInput(fragment,FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER);
    }

    public void showFragmentInput(BaseFragment fragment,int type){
        if(mFragmentInput == null){
            mFragmentInput = new FragmentInput();
        }else{
            mFragmentInput = null;
            mFragmentInput = new FragmentInput();
        }
        Bundle args = new Bundle();
        args.putInt("type",type);
        mFragmentInput.setArguments(args);
        fragment.switchFragment(mFragmentInput);

    }

    public void showFragmentNetFail(BaseFragment fragment){
        Fragment ft = fragment.getCurFragment();
        log.E("showFragmentNetFail() curFragment:"+ft);
        if((ft instanceof FragmentNetFail)){
            return;
        }
        if(mFragmentNetFail == null){
            mFragmentNetFail = new FragmentNetFail();
        }else{
            mFragmentNetFail = null;
            mFragmentNetFail = new FragmentNetFail();
        }
        Bundle args = new Bundle();
        mFragmentNetFail.setArguments(args);
        fragment.switchFragment(mFragmentNetFail);
    }

    public void showFragmentSetNet(BaseFragment fragment){
        if(!AppUtil.getInstance().isShowRootUI()){
            fragment.getActivity().startActivity(new Intent(Settings.ACTION_SETTINGS));
            return;
        }
        if(mFragmentSetNet == null){
            mFragmentSetNet = new FragmentSettingNet();
        }else{
            mFragmentSetNet = null;
            mFragmentSetNet = new FragmentSettingNet();
        }
        Bundle args = new Bundle();
        args.putInt("type",FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER);
        mFragmentSetNet.setArguments(args);
        fragment.switchFragment(mFragmentSetNet);
    }

    public void showFragmentFail(BaseFragment fragment,boolean isServerFail) {
        if (mFragmentFail == null) {
            mFragmentFail = new FragmentFail();
        } else {
            mFragmentFail = null;
            mFragmentFail = new FragmentFail();
        }
        Bundle args = new Bundle();
        if (isServerFail) {
            args.putInt("type", FragmentLogo.ShowUIStatus.SERVER_CONNECT_FAIL);
        } else {
            args.putInt("type", FragmentLogo.ShowUIStatus.REGISTER_FAIL);
        }

        mFragmentFail.setArguments(args);
        fragment.switchFragment(mFragmentFail);
    }

}
