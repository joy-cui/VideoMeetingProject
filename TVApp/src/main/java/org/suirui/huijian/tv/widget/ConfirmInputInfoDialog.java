package org.suirui.huijian.tv.widget;

import org.suirui.huijian.tv.R;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

public class ConfirmInputInfoDialog extends ConfirmCommonDialog{

	private Context mContext;

	public ConfirmInputInfoDialog(Context context) {
		super(context,ConfirmCommonDialog.TYPE_CONTENT_TIPS);
		mContext = context;
		init();
	}

	public ConfirmInputInfoDialog(Context context, int theme) {
		super(context, theme,ConfirmCommonDialog.TYPE_CONTENT_TIPS);
		mContext = context;
		init();
	}
	
	private void init() {
		findViews();
	}

	private void findViews() {
		setContentView(R.layout.m_confirm_input_layout);
		EditText editTV = (EditText) findViewById(R.id.editText);
	}
	
    public void setTips(int strId){
    	TextView tipView = (TextView) findViewById(R.id.tips);
    	tipView.setText(mContext.getResources().getString(strId));
    }
    
    public void setTips(String str){
    	TextView tipView = (TextView) findViewById(R.id.tips);
    	tipView.setText(str);
    }
    
    public String getInputContent(){
    	EditText editTV = (EditText) findViewById(R.id.editText);
    	return editTV.getText().toString();
    }
}
