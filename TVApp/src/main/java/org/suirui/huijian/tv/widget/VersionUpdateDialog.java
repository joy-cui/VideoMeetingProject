package org.suirui.huijian.tv.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.widget.arcProgress.ArcProgress;
import org.suirui.huijian.tv.widget.arcProgress.onImageCenter;


public class VersionUpdateDialog extends Dialog {
    private Context mContext;
    private TextView mTipsTV;
    private TextView mProgressTV;
    private TextView mProgressOKTV;// progress_txt
    private ImageView mProgressImage;
    private SeekBar mSeekBar;
    private ArcProgress mArcProgress;

    public VersionUpdateDialog(Context context) {
        super(context, R.style.BDialog_Slide_Waiting);
        init(context);
    }

    public VersionUpdateDialog(Context context, int theme) {
        super(context, R.style.BDialog_Slide_Waiting);
        init(context);
    }

    private void init(final Context context) {
        mContext = context;
        setContentView(R.layout.progress_version_update);
//		mProgressImage = (ImageView) this
//				.findViewById(R.id.updateProgressBar);
//        mSeekBar = (SeekBar) this.findViewById(R.id.updateSeekbar);
        //        mProgressTV = (TextView) this.findViewById(R.id.progress_txt);
        mArcProgress = (ArcProgress) this.findViewById(R.id.updateProgress1);
        mTipsTV = (TextView) this.findViewById(R.id.progresstips);
        mArcProgress.setOnCenterDraw(new ArcProgress.OnCenterDraw() {
            @Override
            public void draw(Canvas canvas, RectF rectF, float x, float y, float storkeWidth, int progress) {
                Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                textPaint.setStrokeWidth(35);
                textPaint.setColor(context.getResources().getColor(R.color.tv_txt_font));
                textPaint.setTextSize(30);
                String progressStr = String.valueOf(progress+"%");
                float textX = x-(textPaint.measureText(progressStr)/2);
                float textY = y-((textPaint.descent()+textPaint.ascent())/2);
                canvas.drawText(progressStr,textX,textY,textPaint);
            }
        });
//        mProgressOKTV = (TextView) this.findViewById(R.id.updateOk);
//        mProgressOKTV.setVisibility(View.GONE);
//        mProgressTV.setVisibility(View.GONE);
//        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
//                context, R.anim.loading_animation);
//        // 使用ImageView显示动画
//        mProgressImage.startAnimation(hyperspaceJumpAnimation);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (UIUtil.getDensityW(context) * 1);
        params.height = (int) (UIUtil.getDensityH(context) * 1);
        params.gravity = Gravity.CENTER;
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setAttributes(params);
        setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        setPercent(0);
//        mSeekBar.setMax(100);
    }

    public void setTips(int tipsID){
        mTipsTV.setText(tipsID);
    }

    public void setTips(String tips){
        mTipsTV.setText(tips);
    }

    public void setPercent(int txt) {
//        mProgressTV.setText(txt+"%");
        if(mArcProgress != null) {
            mArcProgress.setProgress(txt);
        }
//        if(txt ==100){
//            mProgressTV.setVisibility(View.INVISIBLE);
//            mProgressImage.setVisibility(View.GONE);
//            mProgressOKTV.setVisibility(View.VISIBLE);
//        }

    }

    public void setGradualColor(Object startValue, Object endValue) {
        mArcProgress.setGradualColor(startValue, endValue);
    }

//    public void setCenterImg(int resId){
//        if(mArcProgress != null){
//            mArcProgress.setOnCenterDraw(new onImageCenter(mContext,resId));
//        }
//    }
    public void setCenterOk(){
//        if(mArcProgress != null){
//            mArcProgress.setOnCenterDraw(new onImageCenter(mContext,resId));
//        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private int getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.widthPixels;
    }
}
