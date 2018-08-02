package org.suirui.huijian.box.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.contant.Configure;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.util.StringUtil;

import org.suirui.huijian.box.AppConfigure;
import org.suirui.huijian.box.R;
import org.suirui.huijian.box.view.widget.EditTextWatcher;


/**
 * 服务器设置
 *
 * @authordingna
 * @date2016-11-09
 **/
public class SettingDialog extends Dialog {
    private static final SRLog log = new SRLog(SettingDialog.class.getName(),AppConfigure.LOG_LEVE);

    private Context mContext;
    private ClickListenerInterface clickListener;
    private EditText server_adress_edit, request_http_edit;

    public SettingDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.setting_layout, null);
        setContentView(view);
        Button cancel = (Button) view.findViewById(R.id.cancel_setting_btn);
        Button complete = (Button) view.findViewById(R.id.complete_setting_btn);
        server_adress_edit = (EditText) view.findViewById(R.id.server_adress_edit);
        request_http_edit = (EditText) view.findViewById(R.id.request_http);
        server_adress_edit.addTextChangedListener(new EditTextWatcher(server_adress_edit));
        request_http_edit.addTextChangedListener(new EditTextWatcher(request_http_edit));
        String serverName = ThirdApi.getIntance(mContext).getServerAdress();
        String requestHttp = ThirdApi.getIntance(mContext).getServerHttp();
        if (serverName != null && !serverName.equals(""))
            server_adress_edit.setText(serverName);
        else
            server_adress_edit.setText(AppConfigure.SERVER_ADRESS);
        if (requestHttp != null && !requestHttp.equals(""))
            request_http_edit.setText(requestHttp);
        else
            request_http_edit.setText(AppConfigure.HTTPREQUEST);
        cancel.setOnClickListener(new clickListener());
        complete.setOnClickListener(new clickListener());
        DisplayMetrics defaultDisplay = mContext.getResources().getDisplayMetrics();
        int screenW = defaultDisplay.widthPixels;
        int screenH = defaultDisplay.heightPixels;
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (screenW > screenH) {//横屏
            params.width = screenW / 3;
            params.height = screenH / 2;
        } else {//竖屏
            params.width = screenW - 100;
            params.height = screenH / 3;
        }
        window.setAttributes(params);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListener = clickListenerInterface;
    }

    public interface ClickListenerInterface {
        void onCancel();

        void onComplete(String newName, String httpEquest);
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.cancel_setting_btn) {
                if (clickListener != null) {
                    clickListener.onCancel();
                }

            } else if (i == R.id.complete_setting_btn) {
                String server_adress = server_adress_edit.getText().toString();
                String requestHttp = request_http_edit.getText().toString();
                if (clickListener != null && !StringUtil.isEmpty(server_adress) && !StringUtil.isEmpty(requestHttp)) {
                    clickListener.onComplete(server_adress, requestHttp);
                }
            }
        }
    }
}
