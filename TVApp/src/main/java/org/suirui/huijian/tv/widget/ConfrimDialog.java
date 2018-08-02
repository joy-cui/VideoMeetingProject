package org.suirui.huijian.tv.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.suirui.huijian.tv.R;

public class ConfrimDialog extends ConfirmCommonDialog {

    private Context mContext;

    public ConfrimDialog(Context context) {
        super(context, ConfirmCommonDialog.TYPE_UPDATE_VERSION_TIPS);
        mContext = context;
        init();
    }

    public ConfrimDialog(Context context, int theme) {
        super(context, theme, ConfirmCommonDialog.TYPE_UPDATE_VERSION_TIPS);
        mContext = context;
        init();
    }

    private void init() {
        findViews();
    }

    private void findViews() {
        setContentView(R.layout.m_confirm_layout);
    }

    private void dismissDialog() {
        dismiss();

    }


    public void setWarningMsg(int msgId) {
        TextView tipView = (TextView) findViewById(R.id.content);
        tipView.setText(mContext.getString(msgId));
    }

    public void setWarningMsg(String msg) {
        TextView tipView = (TextView) findViewById(R.id.content);
        tipView.setText(msg);
    }
}