package org.suirui.huijian.tv.widget;

import org.suirui.huijian.tv.R;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class WarningDialog extends ConfirmCommonDialog {

    private Context mContext;

    public WarningDialog(Context context) {
        super(context, ConfirmCommonDialog.TYPE_WARNING_TIPS);
        mContext = context;
        init();
    }

    public WarningDialog(Context context, int theme) {
        super(context, theme, ConfirmCommonDialog.TYPE_WARNING_TIPS);
        mContext = context;
        init();
    }

    private void init() {
        findViews();
    }

    private void findViews() {
        setContentView(R.layout.m_confirm_layout);
        setButton1Visible(false);
        setButton2Focused();
        setButton2(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

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