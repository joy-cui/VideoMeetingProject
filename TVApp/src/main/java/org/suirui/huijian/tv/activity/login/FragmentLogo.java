package org.suirui.huijian.tv.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.suirui.pub.business.contant.SRIMConfigure;
import com.suirui.pub.business.manage.IMiddleManage;
import com.suirui.pub.business.util.LoginSharePreferencesUtil;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.common.http.okhttp.utils.NetworkUtil;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.util.StringUtil;
import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.activity.setting.BaseFragment;
import org.suirui.huijian.tv.activity.MainActivity;
import org.suirui.huijian.tv.util.AppUtil;
import org.suirui.huijian.tv.util.ErrorCode;
import org.suirui.huijian.tv.util.LoginUtil;
import org.suirui.srpaas.sdk.SRPaas;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by hh on 2018/4/25.
 */

public class FragmentLogo extends BaseFragment implements Observer {
    private static final SRLog log = new SRLog(FragmentLogo.class.getName(), TVAppConfigure.LOG_LEVE);

    private FragmentInput mFragmentInput;
    private FragmentFail mFragmentFail;
    private FragmentAuthority mFragmentAuthority;
    private FragmentSetNet mFragmentSetNet;
    private int mShowStatus = -1;
    private View mRootView;
    private TextView mTVTips;
    private String mServerAddress = "";
    private String mNickName = "";
    private static final int SHOW_CHECK_NET = 100;
    private static final int SHOW_CONNECTING_SERVER = 101;
    private static final int SHOW_REGISTING = 102;
    private static final int SHOW_AUTHORIZING = 103;
    public static final int Back_From_Input_Server = 1000;

    private boolean isRegister = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.m_logo_view, null);
        }
        // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        mTVTips = (TextView) mRootView.findViewById(R.id.progress_txt);
        ImageView mImgProgress = (ImageView) mRootView.findViewById(R.id.progress_img);
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                getContext(), R.anim.loading_animation);
        // 使用ImageView显示动画
        mImgProgress.startAnimation(hyperspaceJumpAnimation);
        log.E("onCreateView--111--:mShowStatus:" + mShowStatus);
        Bundle bundle = getArguments();
        if (bundle != null) {
            log.E("onCreateView--bundle isnot null!" );
            int type = bundle.getInt("type");
            mShowStatus = type;
        }
        if(mShowStatus == -1) {
            showTips(ShowUIStatus.SHOW_CHECK_NET);
        }
        IMiddleManage.getInstance().addObserver(this);

        return mRootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            IMiddleManage.getInstance().deleteObserver(this);
            return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        log.E("onResume----:mShowStatus:" + mShowStatus + " isLogin:"+LoginUtil.getInstance().isLogin(getActivity()) + " suid:"+ LoginUtil.getInstance().getSuid(getContext()));
        if (NetworkUtil.hasDataNetwork(getContext())) {//
//            if (LoginUtil.getInstance().isLogin(getActivity())) {
//                showTips(SHOW_CONNECTING_SERVER);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        enterMainActivity();
//                    }
//                }, 1 * 1000);
//                return;
//            }
            showTips(mShowStatus);
            if (mShowStatus != -1) {
                Bundle bundle = getArguments();
                if (bundle != null) {
                    int type = bundle.getInt("type");
                    mServerAddress = bundle.getString("address");
                    String serverType = bundle.getString("http");
                    String serverHost = bundle.getString("ip");
                    mNickName = bundle.getString("nick");
                    if (type == ShowUIStatus.SHOW_CONNECT_SERVER) {
//                        modifyType = bundle.getString("modifyType");
                        LoginUtil.getInstance().setNickName(getContext(), mNickName);
                        log.E("onResume-----mServerAddress:" + mServerAddress + "  serverType:" + serverType + "  serverHost:" + serverHost + "nick:"+mNickName + " ");
                        if (!StringUtil.isEmpty(mServerAddress)) {
//                            ThirdApi.getIntance(getContext()).updateAdressUrl(serverHost, serverType, true);

//                            if(!StringUtil.isEmpty(LoginUtil.getInstance().getSuid(getContext()))){
                                showTips(ShowUIStatus.SHOW_LOGINING);
                                LoginUtil.getInstance().loginAndChangeServer(getContext(),serverHost,serverType,mServerAddress);
//                            }else {
//                                showTips(ShowUIStatus.SHOW_CONNECT_SERVER);
//                                LoginUtil.getInstance().register(getContext(),mServerAddress,mNickName);
//                            }
                        } else {//本地无serverAddress
                            showFragmentInput();
                        }
                    }else if(type ==ShowUIStatus.SHOW_REGISTER_NAME){
                        showTips(ShowUIStatus.SHOW_CONNECT_SERVER);
                        LoginUtil.getInstance().register(getContext(),mServerAddress,mNickName);
                    }else if(type == FragmentLogo.ShowUIStatus.SHOW_RELOGIN){
                        login(false);
                    }
                    if (type == ShowUIStatus.SHOW_AUTHORIZING) {
                        login(true);
                    }
                } else {
                    showTips(ShowUIStatus.SHOW_CHECK_NET);
                }
                return;
            }
            mServerAddress = LoginUtil.getInstance().getServerAddress(getContext());
            mNickName = LoginUtil.getInstance().getNickName(getContext());
            log.E("onResume-----mServerAddress:" + mServerAddress + " "+LoginUtil.getInstance().getHost(getContext()) + "  "+LoginUtil.getInstance().getHttp(getContext()));

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!StringUtil.isEmpty(mServerAddress)) {
//                        LoginUtil.getInstance().register(getContext(),mServerAddress,"KKKKKLLLLL");
                        LoginUtil.getInstance().loginAndChangeServer(getContext(),LoginUtil.getInstance().getHost(getContext()),LoginUtil.getInstance().getHttp(getContext()),mServerAddress);
//                        LoginUtil.getInstance().register(getContext(),mServerAddress,mNickName);
                        showRegisterTips();
                    } else {//本地无serverAddress
                        showFragmentInput();
                    }

                }
            }, 1 * 1000);

        } else {//网络连接失败
            FragmentMange.getInstance().showFragmentNetFail(FragmentLogo.this);
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        log.E("onActivityResult()---");
//        if(resultCode == Back_From_Input_Server){
//            int status = data.getIntExtra("type",-1);
//            if(status == FragmentLogo.ShowUIStatus.SHOW_CONNECT_SERVER){
//                mServerAddress = data.getStringExtra("ip");
//                String serverType = data.getStringExtra("http");
//                String serverHost = data.getStringExtra("address");
//                log.E("onResume-----mServerAddress:"+mServerAddress + "  serverType:"+ serverType+ "  serverHost:"+ serverHost);
//                ThirdApi.getIntance(getContext()).updateAdressUrl(serverHost, serverType, true);
//                showTips(ShowUIStatus.SHOW_CONNECT_SERVER);
//                register();
//
//            }else if(status == FragmentLogo.ShowUIStatus.SHOW_REGISTER_NAME){
//                mNickName = data.getStringExtra("nick");
//            }
//
////            log.E("selectItem---"+status);
////            register();
////            showTips(ShowUIStatus.SHOW_CONNECT_SERVER);
//        }
//    }

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
                    case LOGIN_FAIL:
                        log.E("...LOGIN_FAIL..."+cmd.data);
                        int code = (int)cmd.data;
                        if (code == LoginCode.ACCOUNT_FREEZE_OR_NO_AUTHORITY) {
                            showFragmentAuthority();
                        }  else if (code == LoginCode.NO_REGISTE) {
                            LoginUtil.getInstance().setSuid(getContext(), "");
                            LoginUtil.getInstance().setNickName(getContext(), "");

                            FragmentMange.getInstance().showFragmentNick(this, LoginUtil.getInstance().getHttp(getContext()), LoginUtil.getInstance().getHost(getContext()));
                        } else {
                            showTips(ShowUIStatus.SHOW_LOGIN_FAIL);
                            showFragmentFail(true);
                        }
                        break;
                    case LOGIN_STATE:
                        boolean status = (boolean)cmd.data;
                        if(status){
                            showTips(ShowUIStatus.SHOW_LOGIN_SUCCESS);
                            enterMainActivity();
                        }
                        break;
                    case REGIST_FAIL:
                        log.E("onHttpAuthError...register..."+cmd.data);
                        int i = (int)cmd.data;
                        if (i == LoginCode.REGISTE_INFO_ALREADY_EXIST) {
                            showTips(ShowUIStatus.SHOW_LOGINING);
                            login(false);
                        } else if (i == LoginCode.NO_REGISTE) {
//                            LoginUtil.getInstance().register(getContext(),mServerAddress,mNickName);
                            FragmentMange.getInstance().showFragmentNick(this, LoginUtil.getInstance().getHttp(getContext()), LoginUtil.getInstance().getHost(getContext()));
                        } else if (i == LoginCode.REGISTE_NAME_NULL) {
                            FragmentMange.getInstance().showFragmentNick(this, LoginUtil.getInstance().getHttp(getContext()), LoginUtil.getInstance().getHost(getContext()));
                        } else if (i == LoginCode.ACCOUNT_FREEZE_OR_NO_AUTHORITY) {
                            if (mShowStatus == ShowUIStatus.SHOW_AUTHORIZING) {
                                showFragmentAuthority();
                            } else {
                                showFragmentAuthority();
                            }
                        }else if (i == SRIMConfigure.State.s_Register_Fail) {
                            showFragmentFail(false);
                        }else if(i == SRIMConfigure.Error.eHttp
                                || i == SRPaas.eRegisterError.eRegisterError_Url.ordinal()
                                || i == SRPaas.eRegisterError.eRegisterError_UNknow.ordinal()){
                            showFragmentFail(true);
                        }
//                        else if(i == ErrorCode.ERROR_20002){
//                            FragmentMange.getInstance().showFragmentFail(FragmentLogo.this,true);
//                        }
                        else {
//                            AppUtil.getInstance().showWaringDialog(getContext(), "onHttpAuthError:" + i);
                            Toast.makeText(getActivity(),"REGIST_FAIL，errorcode: "+ i,Toast.LENGTH_SHORT).show();
                            FragmentMange.getInstance().showFragmentFail(FragmentLogo.this,true);
                        }
                        break;
                    case REGIST_SUCCESS:
                        log.E("REGIST_SUCCESS: suid:"+ LoginSharePreferencesUtil.getLoginSuid(getContext()) +"   nick:"+LoginSharePreferencesUtil.getLoginNikeName(getContext()));
                        showTips(ShowUIStatus.SHOW_LOGINING);
                        login(false);
                        break;
                }
            }
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_CHECK_NET:
                    mTVTips.setText(R.string.m_progress_check_net);
                    break;
                case SHOW_CONNECTING_SERVER:
                    mTVTips.setText(R.string.m_connecting_server);
                    break;
                case SHOW_REGISTING:
                    mTVTips.setText(R.string.m_registing);
                    break;
                case SHOW_AUTHORIZING:
                    mTVTips.setText(R.string.m_authorizing);
                    break;
                case ShowUIStatus.SHOW_LOGINING:
                    mTVTips.setText(R.string.m_logining);
                    break;
                case ShowUIStatus.SHOW_LOGIN_SUCCESS:
                    mTVTips.setText(R.string.m_login_success);
                    break;
                case ShowUIStatus.SHOW_LOGIN_FAIL:
                    mTVTips.setText(R.string.m_login_fail);
                    break;
            }

            super.handleMessage(msg);
            //执行逻辑
        }
    };

    private void showFragmentAuthority() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(NetworkUtil.hasDataNetwork(getContext())){
                    if (mFragmentAuthority == null) {
                        mFragmentAuthority = new FragmentAuthority();
                    }
                    switchFragment(mFragmentAuthority);
                }

            }
        }, 2 * 1000);

    }

    private void showFragmentInput() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentMange.getInstance().showFragmentInput(FragmentLogo.this);
            }
        }, 2 * 1000);

    }

    private void showFragmentFail(final boolean isServerFail) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentMange.getInstance().showFragmentFail(FragmentLogo.this,true);
//                if (mFragmentFail == null) {
//                    mFragmentFail = new FragmentFail();
//                }else{
//                    mFragmentFail = null;
//                    mFragmentFail = new FragmentFail();
//                }
//                Bundle args = new Bundle();
//                if (isServerFail) {
//                    args.putInt("type", ShowUIStatus.SERVER_CONNECT_FAIL);
//                } else {
//                    args.putInt("type", FragmentLogo.ShowUIStatus.REGISTER_FAIL);
//                }
//
//                mFragmentFail.setArguments(args);
//                switchFragment(mFragmentFail);
            }
        }, 2 * 1000);
    }

    private void showTips(int status) {
        if (status == ShowUIStatus.SHOW_CHECK_NET) {
            mHandler.sendEmptyMessage(SHOW_CHECK_NET);
        } else if (status == ShowUIStatus.SHOW_CONNECT_SERVER) {
            showRegisterTips();
        } else if (status == ShowUIStatus.SHOW_AUTHORIZING) {
            mHandler.sendEmptyMessage(SHOW_AUTHORIZING);
        } else {
            mHandler.removeMessages(SHOW_REGISTING);
            mHandler.sendEmptyMessage(status);
        }
    }

    private void showRegisterTips() {
        mHandler.sendEmptyMessage(SHOW_CONNECTING_SERVER);
        mHandler.sendEmptyMessageDelayed(SHOW_REGISTING, 1 * 1000);
    }

    private void enterMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        getActivity().finish();
    }

    public void login(boolean isAuthority) {
        if (isAuthority) {
            showTips(ShowUIStatus.SHOW_AUTHORIZING);
        } else {
            showTips(ShowUIStatus.SHOW_LOGINING);
        }
        mServerAddress = LoginUtil.getInstance().getServerAddress(getContext());
        LoginUtil.getInstance().login(getContext());
    }



    public static class ShowUIStatus {
        public static final int SHOW_CHECK_NET = 1;
        public static final int SHOW_CONNECT_SERVER = 2;
        public static final int SERVER_CONNECT_FAIL = 3;
        public static final int REGISTER_FAIL = 4;
        public static final int REGISTER_SUCCESS = 5;
        public static final int SHOW_REGISTER_NAME = 6;
        public static final int SHOW_AUTHORIZING = 7;
        public static final int SHOW_LOGINING = 8;
        public static final int SHOW_LOGIN_SUCCESS = 9;
        public static final int SHOW_LOGIN_FAIL = 10;
        public static final int SHOW_NET_FAIL = 11;

        public static final int SHOW_MODIFY_NICK = 12;//设置中更改昵称
        public static final int SHOW_MODIFY_SERVER = 13;//设置中更改服务器
        public static final int SHOW_RELOGIN = 14;//修改ip后重新登录
    }

}

