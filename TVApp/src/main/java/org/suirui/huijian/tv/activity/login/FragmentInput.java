package org.suirui.huijian.tv.activity.login;

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

import com.suirui.pub.business.contant.SRIMConfigure;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.util.StringUtil;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.LogoActivity;
import org.suirui.huijian.tv.activity.setting.BaseFragment;
import org.suirui.huijian.tv.bean.LocationBean;
import org.suirui.huijian.tv.util.HistoryUtil;
import org.suirui.huijian.tv.util.LoginUtil;
import org.suirui.huijian.tv.util.TVStringUtil;
import org.suirui.huijian.tv.widget.LocationDialog;
import org.suirui.huijian.tv.widget.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hh on 2018/4/25.
 * 输入服务器地址
 */

public class FragmentInput extends BaseFragment{
    private static final SRLog log = new SRLog(FragmentInput.class.getName(), TVAppConfigure.LOG_LEVE);

//    private FragmentInputNick mFragmentInputNick;
//    private FragmentLogo mFragmentLogo;
    private EditText mServerET;
    private Button mBtnOK;
    private TextView mTvIPError;
    private TextView mTVHttp;
    private  TextView mTVInputTips;
    private LocationDialog mLocationDialog;
    private LocationDialog mServerAddressDialog;
    private int mShowStatus = FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER;
    private String[] http= {"http", "https"};
    private String mHttp = "";
    private String mHost = "";
    private String mAddress = "";
    private String mNick = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        View view = inflater.inflate(R.layout.m_set_input_server_view, null);
        view.requestFocus();
        mServerET = (EditText) view.findViewById(R.id.edtServer);
        mTvIPError = (TextView) view.findViewById(R.id.tvIPError);
        mTVHttp = (TextView) view.findViewById(R.id.tvHttp);
        mTVInputTips = (TextView) view.findViewById(R.id.tvInputTips);

        mBtnOK = (Button) view.findViewById(R.id.btnInputServer);

        setIPErrorTipVisible(false);
        setBtnIPEnable();
        Bundle bundle = getArguments();
        if(bundle != null){
            mShowStatus = bundle.getInt("type");
        }
        log.E("onCreateView():mShowStatus:"+mShowStatus);
        mTVHttp.setText(LoginUtil.getInstance().getHttp(getContext()));
        mServerET.setText(LoginUtil.getInstance().getHost(getContext()));
        if(mShowStatus == FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER){
            changeTitle("");
            mTVInputTips.setText(R.string.m_set_server_address);
            mTVHttp.setVisibility(View.VISIBLE);
        }else if(mShowStatus == FragmentLogo.ShowUIStatus.SHOW_MODIFY_SERVER){
            changeTitle(getString(R.string.m_setting_account));
            mTVInputTips.setText(R.string.m_change_server_address);
            mTVHttp.setVisibility(View.VISIBLE);
        }
        mServerET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    setBtnIPEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mTVHttp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    UIUtil.closeSoftKeyboard(getContext(),getView());
                }
            }
        });
        mBtnOK.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    UIUtil.closeSoftKeyboard(getContext(),getView());
                }
            }
        });

        mTVHttp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHttpSelect();
            }
        });

        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String http = mTVHttp.getText().toString();
                String ip = mServerET.getText().toString().trim();
                String address = http + "://"+ip;
                log.E("+"+http+"  ip:"+ip);
                String nick = LoginUtil.getInstance().getNickName(getContext());
//                HistoryUtil.getInstance(getContext()).saveServerAddress(ip);
                if(mShowStatus == FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER){
//                    if(!StringUtil.isEmpty(nick)){
                        FragmentMange.getInstance().showFragmentLogo(FragmentInput.this, http, ip,nick);
//                        return;
//                    }
//                    FragmentMange.getInstance().showFragmentNick(FragmentInput.this, http, ip);
                }else if(mShowStatus == FragmentLogo.ShowUIStatus.SHOW_MODIFY_SERVER){
                    Intent intent = new Intent(getActivity(),LogoActivity.class);
                    intent.putExtra("type", FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER);
                    intent.putExtra("http", http);
                    intent.putExtra("address", address);
                    intent.putExtra("ip", ip);
                    intent.putExtra("nick", nick);
                    LoginUtil.getInstance().setLoginStatus(getContext(), SRIMConfigure.State.hj_Login_fail);
                    intent.putExtra("modifyType", 2);
                    startActivity(intent);
                }

            }
        });
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        log.E("onCreateView():mShowStatus:"+mShowStatus);
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

    private void sendResult() {
        String http = mTVHttp.getText().toString();
        String ip = mServerET.getText().toString().trim();
        String address = http + "://"+ip;
        mHttp = http;
        mHost = ip;
        mAddress = address;
        if(getTargetFragment() == null){
            log.W("getTargetFragment() is null");
//            showFragmentLogo();
            return;
        }else{
//            Intent i = new Intent();
//            if(mShowStatus == FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER){
//                i.putExtra("type",FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER);
//                i.putExtra("http",http);
//                i.putExtra("address",address);
//                i.putExtra("ip",ip);
//            }else if(mShowStatus == FragmentLogo.ShowUIStatus.SHOW_REGISTER_NAME){
//                i.putExtra("type",FragmentLogo.ShowUIStatus.SHOW_REGISTER_NAME);
//                i.putExtra("nick",address);
//            }
//
//
//            getTargetFragment().onActivityResult(getTargetRequestCode(),FragmentLogo.Back_From_Input_Server,i);
//            getFragmentManager().popBackStack();

        }
    }


    private void setNickViewVisible(){
        mTVInputTips.setText(R.string.m_set_nick);
        mTVHttp.setVisibility(View.GONE);
        setBtnNickEnable();
    }

    private void setIPErrorTipVisible(boolean isVisible){
        if(isVisible){
            mTvIPError.setVisibility(View.VISIBLE);
        }else{
            mTvIPError.setVisibility(View.GONE);
        }
    }
    private void setBtnIPEnable(){
        String serverHost = mServerET.getText().toString().trim();
        String serveType = mTVHttp.getText().toString().trim();
        if(!StringUtil.isEmpty(serverHost)){
//            setIPErrorTipVisible(false);
            if(mShowStatus == FragmentLogo.ShowUIStatus.SHOW_MODIFY_SERVER){
                if(!org.suirui.srpaas.util.StringUtil.isSameString(serverHost, LoginUtil.getInstance().getHost(getContext()))
                        && !org.suirui.srpaas.util.StringUtil.isSameString(serveType, LoginUtil.getInstance().getHttp(getContext()))){
                    mBtnOK.setEnabled(true);
                }
            }
            mBtnOK.setEnabled(true);
        }else{
//            setIPErrorTipVisible(true);
            mBtnOK.setEnabled(false);
        }
    }
    private void setBtnNickEnable(){
        if(!StringUtil.isEmpty(mServerET.getText().toString().trim())){
            mBtnOK.setEnabled(true);
        }else{
            mBtnOK.setEnabled(false);;
        }
    }
    private List<LocationBean> getdata(){
        List<LocationBean> list = new ArrayList<LocationBean>();
        for (int i = 0; i < http.length; i++) {
            LocationBean l = new LocationBean();
            l.setName(http[i]);
            list.add(l);
        }
        return list;
    }
    private void showHttpSelect(){
        if(mLocationDialog == null){
            mLocationDialog = new LocationDialog(getContext(),mTVHttp);
        }
        mLocationDialog.show(mTVHttp,getdata(),mTVHttp);
    }

//    private void showServerAddressHistory(){
//        if(mServerAddressDialog == null){
//            mServerAddressDialog = new LocationDialog(getContext(),mTVHttp);
//        }
//        List<LocationBean> list = HistoryUtil.getInstance(getContext()).getServerAddress();
//        if(list == null || list.size() <= 0){
//            return;
//        }
//        mServerAddressDialog.show(mServerET,list,mServerET);
//    }
}

