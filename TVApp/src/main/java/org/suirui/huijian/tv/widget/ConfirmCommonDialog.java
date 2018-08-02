package org.suirui.huijian.tv.widget;

import org.suirui.huijian.tv.R;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConfirmCommonDialog extends Dialog{

	private Context mContext;
	private int mType;
	public static final int TYPE_WARNING_TIPS = 0;//提示信息，一个或两个button
	public static final int TYPE_CONTENT_TIPS = 1;//有其他信息显示，例如密码框等；目前使用到的为密码框
	public static final int TYPE_UPDATE_VERSION_TIPS = 2;//提示信息，一个或两个button

	//	public ConfirmCommonDialog(Context context) {
//		super(context,R.style.BDialog_Slide);
//		mContext = context;
//		init(context);
//	}
	public ConfirmCommonDialog(Context context,int type) {
		super(context,R.style.BDialog_Slide);
		mContext = context;
		mType = type;
		init(context);
	}

	public ConfirmCommonDialog(Context context, int theme,int type) {
		super(context,R.style.BDialog_Slide);
		mContext = context;
		mType = type;
		init(context);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	private void init(Context context){
		super.setContentView(R.layout.m_confirm_common_layout);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = (int) (getDensityW(context) * 0.35);
		if(mType == TYPE_WARNING_TIPS){
			params.height = (int) (getDensityH(context) * 0.3);
		}else if(mType == TYPE_CONTENT_TIPS){
			params.height = (int) (getDensityH(context) * 0.28);
		}else if(mType == TYPE_UPDATE_VERSION_TIPS){
			params.height = (int) (getDensityH(context) * 0.35);
		}
		params.gravity = Gravity.CENTER;
		params.alpha = 1.0f; // 设置本身透明度
		params.dimAmount = 0.5f; // 设置黑暗度
		window.setAttributes(params);
		setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
	}



	@Override
	public void setContentView(int layoutResID) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		((LinearLayout)findViewById(R.id.layout_content)).addView(inflater.inflate(layoutResID, null));
	}

	private int getDensityW(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		return metrics.widthPixels;
	}

	private int getDensityH(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		return metrics.heightPixels;
	}


	public void setButtonPanelVisible(boolean isVisible){
		((View)findViewById(R.id.Panel2)).setVisibility(isVisible?View.VISIBLE:View.GONE);
	}

	public void setProgressBarEnable(boolean isEnable){
		(findViewById(R.id.progressBar)).setVisibility(isEnable?View.VISIBLE:View.GONE);
	}

	public void setButton1Visible(boolean isEnable){
		((Button)findViewById(R.id.button1)).setVisibility(isEnable?View.VISIBLE:View.GONE);
	}

	public void setButton1(android.view.View.OnClickListener listener){
		Button leftButton = (Button)findViewById(R.id.button1);
		leftButton.setOnClickListener((android.view.View.OnClickListener)listener);
	}

	public void setButton1(String title, android.view.View.OnClickListener listener){
		Button leftButton = (Button)findViewById(R.id.button1);
		leftButton.setText(title);
		leftButton.setOnClickListener((android.view.View.OnClickListener)listener);
	}

	public void setButton1(int strId, android.view.View.OnClickListener listener){
		Button leftButton = (Button)findViewById(R.id.button1);
		leftButton.setText(mContext.getResources().getString(strId));
		leftButton.setOnClickListener(listener);
	}

	public void setButton1BgNull(){
		Button leftButton = (Button)findViewById(R.id.button1);
		leftButton.setBackgroundDrawable(null);
	}

	public void setButton2Visible(boolean isEnable){
		((Button)findViewById(R.id.button2)).setVisibility(isEnable?View.VISIBLE:View.GONE);
	}

	public void setButton2Enabled(boolean isEnable){
		Button btn2 = (Button)findViewById(R.id.button2);
		btn2.setEnabled(isEnable);
		if(isEnable){
			btn2.setTextColor(mContext.getResources().getColor(R.color.text_color_dialog_1));
		}else{
			btn2.setTextColor(mContext.getResources().getColor(R.color.text_gray));
		}
	}

	public void setButton2(android.view.View.OnClickListener listener){
		Button leftButton = (Button)findViewById(R.id.button2);
		leftButton.setOnClickListener((android.view.View.OnClickListener)listener);
	}

	public void setButton2Background(int res){
		Button leftButton = (Button)findViewById(R.id.button2);
		leftButton.setBackgroundResource(res);
	}

	public void setButton2(String title, android.view.View.OnClickListener listener){
		Button leftButton = (Button)findViewById(R.id.button2);
		leftButton.setText(title);
		leftButton.setOnClickListener((android.view.View.OnClickListener)listener);
	}

	public void setButton2(int strId, android.view.View.OnClickListener listener){
		Button leftButton = (Button)findViewById(R.id.button2);
		leftButton.setText(mContext.getResources().getString(strId));
		leftButton.setOnClickListener((android.view.View.OnClickListener) listener);
	}

	public void setButton2Focused(){
		Button leftButton = (Button)findViewById(R.id.button2);
		leftButton.requestFocus();
	}

	public void setButton3Enable(boolean isEnable){
		((Button)findViewById(R.id.button3)).setVisibility(isEnable?View.VISIBLE:View.GONE);
	}

	public void setButton3(android.view.View.OnClickListener listener){
		Button leftButton = (Button)findViewById(R.id.button3);
		leftButton.setOnClickListener((android.view.View.OnClickListener)listener);
	}

	public void setButton3(String title, android.view.View.OnClickListener listener){
		Button leftButton = (Button)findViewById(R.id.button3);
		leftButton.setText(title);
		leftButton.setOnClickListener((android.view.View.OnClickListener)listener);
	}

	public void setButton3(int strId, OnClickListener listener){
		Button leftButton = (Button)findViewById(R.id.button3);
		leftButton.setText(mContext.getResources().getString(strId));
		leftButton.setOnClickListener((android.view.View.OnClickListener) listener);
	}


}
