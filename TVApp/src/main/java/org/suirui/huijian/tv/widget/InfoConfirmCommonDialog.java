package org.suirui.huijian.tv.widget;

import org.suirui.huijian.tv.R;

import android.content.Context;
import android.widget.TextView;

public class InfoConfirmCommonDialog extends ConfirmCommonDialog{

	private Context mContext;

	public InfoConfirmCommonDialog(Context context) {
		super(context,ConfirmCommonDialog.TYPE_WARNING_TIPS);
		mContext = context;
		init();
	}
	public InfoConfirmCommonDialog(Context context,int type) {
		super(context,type);
		mContext = context;
		init();
	}
//	public InfoConfirmCommonDialog(Context context, int theme) {
//		super(context, theme,ConfirmCommonDialog.TYPE_WARNING_TIPS);
//		mContext = context;
//		init();
//	}

	private void init() {
		findViews();
	}

	private void findViews() {
		setContentView(R.layout.m_confirm_layout);
		TextView tipView = (TextView) findViewById(R.id.content);
	}

    public void setInfo(int strId){
    	TextView tipView = (TextView) findViewById(R.id.content);
    	tipView.setText(mContext.getResources().getString(strId));
    }
    
    public void setInfo(String str){
    	TextView tipView = (TextView) findViewById(R.id.content);
    	tipView.setText(str);
    }

}
