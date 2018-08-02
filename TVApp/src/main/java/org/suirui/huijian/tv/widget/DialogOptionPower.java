package org.suirui.huijian.tv.widget;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.util.CallNativeUtil;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.suirui.srpaas.base.util.log.SRLog;

/**
 * 电源管理
 *
 * @author xh.wang
 */
public class DialogOptionPower extends Dialog implements
        OnClickListener {
    private static final SRLog log = new SRLog(DialogOptionPower.class.getName(), TVAppConfigure.LOG_LEVE);

    private WaitingDialog mWatDialog;
    private Handler mHandler = new Handler();
    private Context mContext;
    private Activity mActivity;
    private Button mLeftBtn;

    public DialogOptionPower(Context context, Activity activity) {
        super(context, R.style.BDialog_Slide);
        mContext = context;
        mActivity = activity;
        init();
    }

    public DialogOptionPower(Context context, int theme) {
        super(context, R.style.BDialog_Slide);
        mContext = context;
        init();
    }

    private void init() {
        findViews();
    }

    private void findViews() {
        setContentView(R.layout.m_power_marg_view);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (getDensityW(mContext) * 0.3);
        params.height = (int) LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        params.alpha = 1.0f; // 设置本身透明度
        params.dimAmount = 0.0f; // 设置黑暗度
        window.setAttributes(params);
        setCanceledOnTouchOutside(false);

        mLeftBtn = (Button) findViewById(R.id.btnLeft);
        Button rightBtn = (Button) findViewById(R.id.btnRight);
        Button middleBtn = (Button) findViewById(R.id.btnMiddle);

        mLeftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        middleBtn.setOnClickListener(this);
    }


    @Override
    public void show() {
        super.show();
    }

    private void showWaitingDialog() {
        if (mWatDialog == null) {
            mWatDialog = new WaitingDialog(mContext);
        }
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mWatDialog != null) {
                    mWatDialog.show();
                } else {
                    log.E("showWaitingDialog(): mWatDialog is null!!!");
                }
            }
        });
    }

    private void dismissWaitingDialog() {
        if (mWatDialog != null) {
            mWatDialog.dismiss();
            mWatDialog = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLeft://关机
                onClickBtnLeft();
                break;
            case R.id.btnRight://
                onClickBtnRight();
                break;
            case R.id.btnMiddle://重启
                onClickBtnMiddle();
                break;
        }
    }

    /**
     * 关机
     */
    private void onClickBtnLeft() {
        CallNativeUtil.getInstance().shutDown();
        dismiss();
    }

    private void onClickBtnRight() {
        dismiss();

    }

    /**
     * 重启
     */
    private void onClickBtnMiddle() {
        CallNativeUtil.getInstance().restart();
        dismiss();
    }

    private void finishAndDismiss() {
        dismiss();
        mActivity.finish();
    }

    public int getDensityW(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.widthPixels;
    }

    public int getDensityH(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.heightPixels;
    }

}
