package org.suirui.huijian.tv.activity.login;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.suirui.pub.business.contant.SRIMConfigure;
import com.suirui.pub.business.manage.IMiddleManage;
import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.setting.BaseFragment;
import org.suirui.huijian.tv.util.AppUtil;
import org.suirui.huijian.tv.util.LoginUtil;
import org.suirui.huijian.tv.widget.LocationDialog;
import org.suirui.huijian.tv.widget.UIUtil;
import org.suirui.srpaas.util.StringUtil;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by hh on 2018/4/25.
 * 输入昵称
 */

public class FragmentInputNick extends BaseFragment implements Observer {
    private static final SRLog log = new SRLog(FragmentInputNick.class.getName(), TVAppConfigure.LOG_LEVE);

    //    private FragmentSettingAccount mFragmentSettingAccount;
    private FragmentLogo mFragmentLogo;
    private EditText mNickET;
    private Button mBtnOK;
    private LocationDialog mLocationDialog;
    private int mShowStatus = FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER;
    private String[] http = {"http", "https"};
    private String mHttp;
    private String mHost;
    private String mAddress;
    private String mNick;
    private Handler mHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        View view = inflater.inflate(R.layout.m_set_nick_view, null);
        view.requestFocus();
        log.E("----onCreateView():"+(getContext() == null));
        IMiddleManage.getInstance().addObserver(this);
        mNickET = (EditText) view.findViewById(R.id.edtNick);
        mBtnOK = (Button) view.findViewById(R.id.btnOK);
        setBtnNickEnable();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mShowStatus = bundle.getInt("type");
            mHttp = bundle.getString("http");
            mHost = bundle.getString("ip");
            mAddress = bundle.getString("address");
        }
        if(mShowStatus == FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER){
            changeTitle("");
        }else if(mShowStatus == FragmentLogo.ShowUIStatus.SHOW_MODIFY_NICK){
            mNickET.setText(LoginUtil.getInstance().getNickName(getContext()));
            changeTitle(getString(R.string.m_setting_account));
        }
        mNickET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setBtnNickEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNick = mNickET.getText().toString().trim();
                if(mShowStatus == FragmentLogo.ShowUIStatus.SHOW_MODIFY_NICK){
                    LoginUtil.getInstance().updateUser(getContext(),mNick);
                }else {
                    FragmentMange.getInstance().showFragmentLogo(FragmentInputNick.this, FragmentLogo.ShowUIStatus.SHOW_REGISTER_NAME,mHttp, mHost, mNick);
                }
            }
        });
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        log.E("----onHiddenChanged():hidden:"+hidden);
        if (hidden) {
            return;
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            mShowStatus = bundle.getInt("type");
            mHttp = bundle.getString("http");
            mHost = bundle.getString("ip");
            mAddress = bundle.getString("address");
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
        IMiddleManage.getInstance().deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof IMiddleManage) {
            if (data instanceof IMiddleManage.NotifyCmd) {
                final IMiddleManage.NotifyCmd cmd = (IMiddleManage.NotifyCmd) data;
                switch (cmd.type) {
                    case UPDATE_USER_SUCCESS:
                        log.E("...UPDATE_USER_SUCCESS..."+(getContext() == null));
                        if(getContext() != null){
                            Toast.makeText(getContext(),R.string.m_modify_success,Toast.LENGTH_SHORT).show();
                        }

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onBack();
                            }
                        },1000);

                        break;
                    case UPDATE_USER_FAIL:
                        mNickET.setText(LoginUtil.getInstance().getNickName(getContext()));
                        boolean status = (boolean)cmd.data;
                        Toast.makeText(getContext(),getString(R.string.m_modify_fail)+":"+status,Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

    private void setBtnNickEnable() {
        String nick = mNickET.getText().toString().trim();
        if (!StringUtil.isEmptyOrNull(nick)) {
            if(mShowStatus == FragmentLogo.ShowUIStatus.SHOW_MODIFY_NICK){
                if(!StringUtil.isSameString(nick, LoginUtil.getInstance().getNickName(getContext()))){
                    mBtnOK.setEnabled(true);
                }
            }else {
                mBtnOK.setEnabled(true);
            }
        } else {
            mBtnOK.setEnabled(false);
        }
    }


}

