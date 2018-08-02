package org.suirui.huijian.tv.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.srpaas.version.contant.VersionConfigure;
import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;

/**
 * 简单的基础弹框
 *
 * @authordingna
 * @date2017-06-30
 **/
public class CustomDialog extends Dialog {
    private static final SRLog log = new SRLog(CustomDialog.class.getName(), TVAppConfigure.LOG_LEVE);
    private Context mContext;
    private ClickListenerInterface clickListener;
    private int isFroce = 0;
    private String versionNum;
    private String updateLog;

    public CustomDialog(Context context, int theme, int isforce, String versionNum, String updateLog) {
        super(context, theme);
        this.mContext = context;
        this.isFroce = isforce;
        this.versionNum = versionNum;
        this.updateLog = updateLog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sr_version_update_layout, null);
        setContentView(view);
        TextView sr_version_num = (TextView) view.findViewById(R.id.sr_version_num);
        TextView sr_version_log = (TextView) view.findViewById(R.id.sr_version_log);
        LinearLayout sr_nofroce_update_layout = (LinearLayout) view.findViewById(R.id.sr_nofroce_update_layout);
        Button sr_btn_cancel = (Button) view.findViewById(R.id.sr_btn_cancel);
        Button sr_btn_sure = (Button) view.findViewById(R.id.sr_btn_sure);
        sr_btn_cancel.setOnClickListener(new clickListener());
        sr_btn_sure.setOnClickListener(new clickListener());

        LinearLayout sr_force_update_layout = (LinearLayout) view.findViewById(R.id.sr_force_update_layout);
        Button sr_btn_complete = (Button) view.findViewById(R.id.sr_btn_complete);
        sr_btn_complete.setOnClickListener(new clickListener());

        //根据是否强制更新的状态，加载不同的界面
        switch (isFroce) {
            case VersionConfigure.Version.isNForce:
                sr_btn_sure.setText(mContext.getResources().getString(R.string.sr_update));
                sr_version_num.setText(mContext.getResources().getString(R.string.m_new_version_info,new Object[]{versionNum}));
                sr_version_log.setText(updateLog);
                sr_nofroce_update_layout.setVisibility(View.VISIBLE);
                sr_force_update_layout.setVisibility(View.GONE);
                this.setCancelable(true);
                break;
            case VersionConfigure.Version.isForce:
                sr_btn_complete.setText(mContext.getResources().getString(R.string.sr_update));
                sr_version_num.setText(mContext.getResources().getString(R.string.m_new_version_info,new Object[]{versionNum}));
                sr_version_log.setText(updateLog);
                sr_nofroce_update_layout.setVisibility(View.GONE);
                sr_force_update_layout.setVisibility(View.VISIBLE);
                this.setCancelable(false);
                break;
            case VersionConfigure.Version.isReload:
                sr_version_num.setText(mContext.getResources().getString(R.string.m_download_fail));
                sr_version_log.setText("");
                sr_btn_sure.setText(mContext.getResources().getString(R.string.m_reload));
                sr_nofroce_update_layout.setVisibility(View.VISIBLE);
                sr_force_update_layout.setVisibility(View.GONE);
                this.setCancelable(true);
                break;
        }
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

        void onUpdate();
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.sr_btn_cancel) {
                if (clickListener != null) {
                    clickListener.onCancel();
                }

            } else if (v.getId() == R.id.sr_btn_sure) {
                if (clickListener != null) {
                    clickListener.onUpdate();
                    clickListener.onCancel();
                }

            } else if (v.getId() == R.id.sr_btn_complete) {
                if (clickListener != null) {
                    clickListener.onUpdate();
                    clickListener.onCancel();
                }
            }
        }
    }
}
