package org.suirui.huijian.tv.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.LogoActivity;
import org.suirui.huijian.tv.activity.SettingActivity;
import org.suirui.huijian.tv.activity.login.FragmentInput;
import org.suirui.huijian.tv.activity.login.FragmentInputNick;
import org.suirui.huijian.tv.activity.login.FragmentLogo;
import org.suirui.huijian.tv.activity.login.FragmentMange;
import org.suirui.huijian.tv.activity.login.LoginCode;
import org.suirui.huijian.tv.activity.setting.BaseFragment;
import org.suirui.huijian.tv.bean.LocationBean;
import org.suirui.huijian.tv.util.AppUtil;
import org.suirui.huijian.tv.util.LoginUtil;
import org.suirui.huijian.tv.util.TVStringUtil;
import org.suirui.huijian.tv.widget.LocationDialog;
import org.suirui.srpaas.sdk.SRPaas;
import org.suirui.srpaas.util.StringUtil;

import com.suirui.pub.business.SRIMLoginClient;
import com.suirui.pub.business.contant.SRIMConfigure;
import com.suirui.pub.business.manage.IMiddleManage;
import com.suirui.srpaas.base.util.log.SRLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by hh on 2018/4/16.
 */
public class FragmentSettingAccount extends BaseFragment{
    private static final SRLog log = new SRLog(FragmentSettingAccount.class.getName(), TVAppConfigure.LOG_LEVE);

    private LocationDialog mLocationDialog;
    private TextView mAccount;
    private TextView mTVHttp;
    private TextView mServerET;
    private TextView mNickET;
    private Button mBtnChangeNick;
    private Button mBtnChangeServer;
    private String[] http = {"http", "https"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        View view = inflater.inflate(R.layout.m_setting_term_layout, null);
        view.requestFocus();
        changeTitle(getString(R.string.m_setting_account));

        mAccount = (TextView) view.findViewById(R.id.accountTV);
        mNickET = (TextView) view.findViewById(R.id.nickTV);
        mTVHttp = (TextView) view.findViewById(R.id.serverTypeTV);
        mServerET = (TextView) view.findViewById(R.id.serverAddressTV);
        mBtnChangeNick = (Button) view.findViewById(R.id.btnChangeNick);
        mBtnChangeServer = (Button) view.findViewById(R.id.btnChangeServer);

        mAccount.setText(LoginUtil.getInstance().getSuid(getContext()));
        mNickET.setText(LoginUtil.getInstance().getNickName(getContext()));
        mTVHttp.setText(LoginUtil.getInstance().getHttp(getContext()));
        mServerET.setText(LoginUtil.getInstance().getHost(getContext()));

        mBtnChangeNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String serverType = mTVHttp.getText().toString();
//                String serverHost = mServerET.getText().toString().trim();
//                String nick = mNickET.getText().toString().trim();
//                change();
//                LoginUtil.getInstance().updateServer(getContext(),serverHost, serverType);
                FragmentMange.getInstance().showFragmentNick(FragmentSettingAccount.this, FragmentLogo.ShowUIStatus.SHOW_MODIFY_NICK,LoginUtil.getInstance().getHttp(getContext()), LoginUtil.getInstance().getHost(getContext()));

            }
        });
        mBtnChangeServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMange.getInstance().showFragmentInput(FragmentSettingAccount.this,FragmentLogo.ShowUIStatus.SHOW_MODIFY_SERVER);

            }
        });
//        mServerET.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                setBtnIPEnable();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        mTVHttp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showHttpSelect();
//            }
//        });
        return view;
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            return;
        }
        if(getCurFragment() instanceof FragmentInputNick){
            mBtnChangeNick.requestFocus();
        }else if(getCurFragment() instanceof FragmentInput){
            mBtnChangeServer.requestFocus();
        }
        changeTitle(getString(R.string.m_setting_account));
        mNickET.setText(LoginUtil.getInstance().getNickName(getContext()));
        mTVHttp.setText(LoginUtil.getInstance().getHttp(getContext()));
        mServerET.setText(LoginUtil.getInstance().getHost(getContext()));

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

    private void setBtnIPEnable(){
//        if(!StringUtil.isEmptyOrNull(mTVHttp.getText().toString().trim())
//                || !StringUtil.isEmptyOrNull(mNickET.getText().toString().trim())
//                || !StringUtil.isEmptyOrNull(mAccount.getText().toString().trim())
//                || !StringUtil.isEmptyOrNull(mServerET.getText().toString().trim())
//                ){
//            mBtnOK.setEnabled(true);
//        }else{
//            mBtnOK.setEnabled(false);
//        }
    }

    private void setIPErrorTipVisible(boolean isVisible){
//        if(isVisible){
//            mTvIPError.setVisibility(View.VISIBLE);
//        }else{
//            mTvIPError.setVisibility(View.GONE);
//        }
    }

    private List<LocationBean> getdata() {
        List<LocationBean> list = new ArrayList<LocationBean>();
        for (int i = 0; i < http.length; i++) {
            LocationBean l = new LocationBean();
            l.setName(http[i]);
            list.add(l);
        }
        return list;
    }

    private void showHttpSelect() {
        if (mLocationDialog == null) {
            mLocationDialog = new LocationDialog(getContext(), mTVHttp);
        }
        mLocationDialog.show(mTVHttp, getdata(), mTVHttp);
    }

    private int getModifyType(){
        int type = 0;
        String http = mTVHttp.getText().toString();
        String nick = mNickET.getText().toString();
        String host = mServerET.getText().toString().trim();
        boolean httpEqule = StringUtil.isSameString(http,LoginUtil.getInstance().getHttp(getContext()));
        boolean hostEqule = StringUtil.isSameString(host,LoginUtil.getInstance().getHost(getContext()));
        boolean nickEqule = StringUtil.isSameString(nick,LoginUtil.getInstance().getNickName(getContext()));

        log.E("change-------------"+"http:"+http + " "+LoginUtil.getInstance().getHttp(getContext())
                + " host:"+host+ " localhost:"+LoginUtil.getInstance().getHost(getContext())
                + " nick:"+nick + " localnick:"+LoginUtil.getInstance().getNickName(getContext())
                + " httpEqule:"+httpEqule
                + " hostEqule:"+hostEqule
                + " nickEqule:"+nickEqule

        );


        if(((!StringUtil.isEmptyOrNull(http) && !httpEqule)
                ||( !StringUtil.isEmptyOrNull(host) && !hostEqule))
                &&( !StringUtil.isEmptyOrNull(nick) && !nickEqule)
                ){
            type = 1;//服务，昵称
        }else if((!StringUtil.isEmptyOrNull(http) && !httpEqule)
                ||( !StringUtil.isEmptyOrNull(host) && !hostEqule)
                ){
            type = 2;//服务
        }else if(!StringUtil.isEmptyOrNull(nick) && !nickEqule){
            type = 3;//昵称
        }
        return type;
    }

    private void change(){
        String http = mTVHttp.getText().toString().trim();
        String ip = mServerET.getText().toString().trim();
        String nick = mNickET.getText().toString().trim();
        String address = http + "://" + ip;
        Intent intent = new Intent(getActivity(),LogoActivity.class);
        intent.putExtra("type", FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER);
        intent.putExtra("http", http);
        intent.putExtra("address", address);
        intent.putExtra("ip", ip);
        intent.putExtra("nick", nick);
        log.E("change-------------getModifyType:"+getModifyType());
        switch (getModifyType()){
            case 1:
                LoginUtil.getInstance().setLoginStatus(getContext(), SRIMConfigure.State.hj_Login_fail);
                intent.putExtra("modifyType", 1);
                startActivity(intent);
                break;
            case 2:
                LoginUtil.getInstance().setLoginStatus(getContext(), SRIMConfigure.State.hj_Login_fail);
                intent.putExtra("modifyType", 2);
                startActivity(intent);
                break;
            case 3:
                LoginUtil.getInstance().updateUser(getContext(),nick);
                break;
        }
    }


}
