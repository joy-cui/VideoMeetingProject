package org.suirui.huijian.tv.widget;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.util.AppUtil;
import org.suirui.srpaas.util.StringUtil;

import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class WaitingDialog extends Dialog {
	private TextView mProgressTV;// progress_txt
	private ImageView mProgressImage;

	public WaitingDialog(Context context) {
		super(context, R.style.BDialog_Slide_Waiting);
		init(context);
	}

	public WaitingDialog(Context context, int theme) {
		super(context, R.style.BDialog_Slide_Waiting);
		init(context);
	}

	private void init(Context context) {
		setContentView(R.layout.progress);
		mProgressImage = (ImageView) this
				.findViewById(R.id.progress_dialog_img);
		mProgressTV = (TextView) this.findViewById(R.id.progress_txt);
		mProgressTV.setVisibility(View.GONE);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.loading_animation);
		// 使用ImageView显示动画
		mProgressImage.startAnimation(hyperspaceJumpAnimation);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = (int) (UIUtil.getDensityW(context) * 0.3);
		params.height = (int) (UIUtil.getDensityH(context) * 0.3);
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
		setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
		setProgressMsg("");
	}

	public void setProgressMsg(String txt) {
		mProgressTV.setText(txt);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		if (StringUtil.isEmptyOrNull(txt)) {
			mProgressTV.setVisibility(View.GONE);
			params.gravity = Gravity.CENTER;
			mProgressImage.setLayoutParams(params);
		} else {
			params.gravity = Gravity.CENTER_HORIZONTAL;
			mProgressImage.setLayoutParams(params);
			mProgressTV.setVisibility(View.VISIBLE);
		}
	}



	@Override
	public void show() {
		super.show();
	}

	private int getDensity(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		return metrics.widthPixels;
	}
}
