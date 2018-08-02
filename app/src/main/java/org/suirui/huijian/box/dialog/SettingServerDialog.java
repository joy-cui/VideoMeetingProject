package org.suirui.huijian.box.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.util.StringUtil;

import org.suirui.huijian.box.AppConfigure;
import org.suirui.huijian.box.R;
import org.suirui.huijian.box.util.KeyboardLayout;
import org.suirui.huijian.box.view.widget.EditTextWatcher;

/**
 * @authordingna
 * @date2017-07-17
 **/
public class SettingServerDialog extends Dialog {
    private static final SRLog log = new SRLog(SettingServerDialog.class.getName(),AppConfigure.LOG_LEVE);

    private Context mContext;
    private ClickListenerInterface clickListener;
    private EditText sr_request_addr;
    private ImageView hj_default_set_btn, hj_private_set_btn;
    private boolean isSetting = false;//是否已经设置过了
    private ImageView sr_private_http, sr_private_https;
    private TextView sr_btn_default_http, sr_btn_default_server;
    private boolean isUpdateConfigure = false;
    private boolean isSelectH = false;//记录选中http还是https
    private TextView sr_btn_private_http, sr_btn_private_https;
    private KeyboardLayout hj_root;
    private ScrollView hj_scrollview;

    public SettingServerDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.init();
        addLayoutListener();//解决edittext被软键盘遮挡
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.hj_setting_server_layout, null);
        setContentView(view);
        hj_root = (KeyboardLayout) view.findViewById(R.id.hj_root);
        hj_scrollview = (ScrollView) view.findViewById(R.id.hj_scrollview);
        ImageView mBack = (ImageView) view.findViewById(R.id.hj_title_layout).findViewById(R.id.btnBack);
        TextView tvContent = (TextView) view.findViewById(R.id.hj_title_layout).findViewById(R.id.tvContent);
        tvContent.setText(mContext.getResources().getString(R.string.server_adress));

        //默认
        hj_default_set_btn = (ImageView) view.findViewById(R.id.hj_default_set_btn);
        sr_btn_default_http = (TextView) view.findViewById(R.id.sr_btn_default_http);
        sr_btn_default_server = (TextView) view.findViewById(R.id.sr_btn_default_server);
        sr_btn_default_http.setText(AppConfigure.HTTPREQUEST);
        sr_btn_default_server.setText(AppConfigure.SERVER_ADRESS);

        //私有化部署
        hj_private_set_btn = (ImageView) view.findViewById(R.id.hj_private_set_btn);
        sr_btn_private_http = (TextView) view.findViewById(R.id.sr_btn_private_http);
        sr_private_http = (ImageView) view.findViewById(R.id.sr_private_http);
        sr_btn_private_https = (TextView) view.findViewById(R.id.sr_btn_private_https);
        sr_private_https = (ImageView) view.findViewById(R.id.sr_private_https);
        sr_request_addr = (EditText) view.findViewById(R.id.sr_request_addr);
        sr_request_addr.addTextChangedListener(new EditTextWatcher(sr_request_addr));
        isUpdateConfigure = ThirdApi.getIntance(mContext).isUpdateConfigure();//是否默认
        log.E("SettingServerDialog....isUpdateConfigure:" + isUpdateConfigure);
        doSetDefaultState(isUpdateConfigure);

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        mBack.setOnClickListener(new clickListener());
        hj_default_set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSetDefaultState(true);
            }
        });
        hj_private_set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSetCustomState();
            }
        });

        sr_private_http.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doHttpState(true);
            }
        });
        sr_private_https.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doHttpState(false);
            }
        });
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!isSetting) {
                    doSetting();
                }
            }
        });
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(false);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListener = clickListenerInterface;
    }

    /**
     * 设置当前服务器的参数配置
     */
    private void doSetting() {
        isSetting = true;
        String server_adress = "";
        String requestHttp = "";
        if (isUpdateConfigure) {
            server_adress = sr_btn_default_server.getText().toString();
            requestHttp = sr_btn_default_http.getText().toString();
        } else {
            server_adress = sr_request_addr.getText().toString();
            if (isSelectH) {
                requestHttp = sr_btn_private_https.getText().toString();
            } else {
                requestHttp = sr_btn_private_http.getText().toString();
            }
        }
        log.E("SettingServerDialog....doSetting()...server_adress:" + server_adress + "  requestHttp:" + requestHttp + "  isUpdateConfigure:" + isUpdateConfigure);
        if (clickListener != null && !StringUtil.isEmpty(server_adress) && !StringUtil.isEmpty(requestHttp)) {
            clickListener.onComplete(server_adress, requestHttp, isUpdateConfigure);
            clearData();
        } else {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.sr_please_set_addr), Toast.LENGTH_SHORT).show();
        }
    }


    private void doPrivateCheckBoxState() {
        sr_private_http.setEnabled(true);
        sr_private_https.setEnabled(true);
        sr_request_addr.setEnabled(true);
    }

    private void doClearHttpState() {
        sr_private_https.setEnabled(false);
        sr_private_http.setEnabled(false);
        sr_request_addr.setEnabled(false);
        sr_private_https.setImageResource(R.drawable.login_auto_unselcet);
        sr_private_http.setImageResource(R.drawable.login_auto_unselcet);
        sr_request_addr.setText("");

    }

    private void doHttpState(boolean ishttp) {
        if (ishttp) {
            isSelectH = false;
            sr_private_http.setImageResource(R.drawable.login_auto_selcet);
            sr_private_https.setImageResource(R.drawable.login_auto_unselcet);
        } else {
            isSelectH = true;
            sr_private_http.setImageResource(R.drawable.login_auto_unselcet);
            sr_private_https.setImageResource(R.drawable.login_auto_selcet);
        }
    }

    private void doSetCustomState() {
        log.E("SettingServerDialog...doSetCustomState()... isUpdateConfigure:" + isUpdateConfigure);
        if (isUpdateConfigure) {
            isUpdateConfigure = false;
            hj_private_set_btn.setImageResource(R.drawable.login_auto_selcet);
            hj_default_set_btn.setImageResource(R.drawable.login_auto_unselcet);
            doClearHttpState();
            doPrivateCheckBoxState();
        }
    }

    private void doSetDefaultState(boolean isDefault) {
        log.E("SettingServerDialog...isDefault:" + isDefault + " isUpdateConfigure:" + isUpdateConfigure);
        if (isDefault) {
            isUpdateConfigure = true;
            hj_default_set_btn.setImageResource(R.drawable.login_auto_selcet);
            hj_private_set_btn.setImageResource(R.drawable.login_auto_unselcet);
            doClearHttpState();
        } else {
            isUpdateConfigure = false;
            hj_private_set_btn.setImageResource(R.drawable.login_auto_selcet);
            hj_default_set_btn.setImageResource(R.drawable.login_auto_unselcet);
            doPrivateCheckBoxState();
            doSetPrivate();
        }
    }

    private void doSetPrivate() {
        String serverName = ThirdApi.getIntance(mContext).getServerAdress();
        String requestHttp = ThirdApi.getIntance(mContext).getServerHttp();
        log.E("SettingServerDialog...serverName:" + serverName + " requestHttp:" + requestHttp + "  HTTPREQUEST:" + AppConfigure.HTTPREQUEST + " SERVER_ADRESS:" + AppConfigure.SERVER_ADRESS);
        if (serverName != null && !serverName.equals("")) {
            sr_request_addr.setText(serverName);
        } else {
            sr_request_addr.setText(AppConfigure.SERVER_ADRESS);
        }
        if (requestHttp != null && !requestHttp.equals("")) {
            if (requestHttp.equals("http")) {
                doHttpState(true);
            } else if (requestHttp.equals("https")) {
                doHttpState(false);
            } else {
                doClearHttpState();
            }
        } else {
            doHttpState(false);
        }
    }

    /**
     * 监听键盘状态，布局有变化时，靠scrollView去滚动界面
     */
    public void addLayoutListener() {
        hj_root.setKeyboardListener(new KeyboardLayout.KeyboardLayoutListener() {
            @Override
            public void onKeyboardStateChanged(boolean isActive, int keyboardHeight) {
                if (isActive) {
                    scrollToBottom();
                }
            }
        });
    }

    /**
     * 弹出软键盘时将SVContainer滑到底
     */
    private void scrollToBottom() {
        hj_scrollview.postDelayed(new Runnable() {
            @Override
            public void run() {
                hj_scrollview.smoothScrollTo(0, hj_scrollview.getBottom() + getStatusBarHeight(mContext));
            }
        }, 100);
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    public int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    public interface ClickListenerInterface {
        void onComplete(String newName, String httpEquest, boolean isDefalut);
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnBack) {
                doSetting();
            }
        }
    }

    private void clearData() {
        mContext = null;
        clickListener = null;
    }
}
