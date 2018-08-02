package org.suirui.huijian.tv.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suirui.srpaas.video.util.ParamUtil;
import com.suirui.srpaas.video.util.StringUtil;

import org.suirui.huijian.tv.R;


/**
 * 会议列表选择item弹框
 */
public class ShowMeetingListOptionDialog extends Dialog {

    private Context mContext;
    private String contentMsg, buttonMsg1,buttonMsg2;
    private boolean isShow = false;
    ClickListenerInterface clickListener;

    /**
     * @param context
     * @param theme
     * @param contentMsg
     * @param buttonMsg1
     */
    public ShowMeetingListOptionDialog(Context context, int theme, String contentMsg, String buttonMsg1, String buttonMsg2) {
        super(context, theme);
        this.mContext = context;
        this.contentMsg = contentMsg;
        this.buttonMsg1 = buttonMsg1;
        this.buttonMsg2=buttonMsg2;

    }
    public ShowMeetingListOptionDialog(Context context) {
        super(context, R.style.hj_tv_custom_dialog);
        this.mContext = context;

    }
    public ShowMeetingListOptionDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.meeting_item_dialog_layout, null);
        setContentView(view);
        TextView mContent = (TextView) view.findViewById(R.id.content);
        TextView txt_button1 = (TextView) view.findViewById(R.id.txt_button1);
        TextView txt_button2 = (TextView) view.findViewById(R.id.txt_button2);
       if(!StringUtil.isEmpty(contentMsg)) {
           mContent.setText(contentMsg);
       }
        if(!StringUtil.isEmpty(buttonMsg1)) {
            txt_button1.setText(buttonMsg1);
        }
        if(!StringUtil.isEmpty(buttonMsg2)) {
            txt_button2.setText(buttonMsg2);
        }


        txt_button1.setOnClickListener(new clickListener());
        txt_button2.setOnClickListener(new clickListener());

        int screenW = ParamUtil.getScreenWidth(mContext);
        int screenH = ParamUtil.getScreenHeight(mContext);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (screenW < screenH) {//竖屏
            params.width = screenW /2;
//            params.height = screenH / 3 - 50;
        } else {//横屏
            params.width = screenW / 3;
//            params.height = screenH / 2-120;
        }
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListener = clickListenerInterface;
    }



    public interface ClickListenerInterface {
        //事件处理
        void clickButton1();

        //弹框取消
        void clickButton2();
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.txt_button1) {
                if (clickListener != null)
                    clickListener.clickButton1();

            } else if (i == R.id.txt_button2) {
                if (clickListener != null)
                    clickListener.clickButton2();
            }
        }
    }
}
