package org.suirui.huijian.tv.widget;

import org.suirui.huijian.tv.R;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.suirui.srpaas.video.util.StringUtil;

public class ConfirmInputPasswordDialog extends Dialog implements View.OnClickListener{

    private Context mContext;
    private TextView editTV;

    public ConfirmInputPasswordDialog(Context context) {
        super(context,R.style.BDialog_Slide);
        mContext = context;
        init(context);
    }
    public ConfirmInputPasswordDialog(Context context, int theme,int type) {
        super(context,R.style.BDialog_Slide);
        mContext = context;
        init(context);
    }


    @Override
    public void show() {
        super.show();
        editTV.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pwd_num_bt_0:
            case R.id.pwd_num_bt_1:
            case R.id.pwd_num_bt_2:
            case R.id.pwd_num_bt_3:
            case R.id.pwd_num_bt_4:
            case R.id.pwd_num_bt_5:
            case R.id.pwd_num_bt_6:
            case R.id.pwd_num_bt_7:
            case R.id.pwd_num_bt_8:
            case R.id.pwd_num_bt_9:
                setNumTVText(v);
                break;
            case R.id.pwd_num_del:
                setDelNumTV();
                break;
            case R.id.pwd_num_j:
                break;
        }
    }

    private void setNumTVText(View v){
        Button joinBtn = (Button) v;
        editTV.setText(editTV.getText().toString()+joinBtn.getText().toString().trim());
    }
    private void setDelNumTV(){
        String num = editTV.getText().toString();
        if(!StringUtil.isEmpty(num) && num.length() >=1){
            num = num.substring(0,num.length() -1);
        }
        editTV.setText(num);
    }


    private void init(Context context) {
        super.setContentView(R.layout.m_confirm_input_pwd_layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        window.setSoftInputMode(params.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        params.width = (int) (getDensityW(context) * 0.4);
        params.height = (int) (getDensityH(context) * 0.5);
        params.gravity = Gravity.CENTER;
        params.alpha = 1.0f; // 设置本身透明度
        params.dimAmount = 0.5f; // 设置黑暗度
        window.setAttributes(params);
        setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        findViews();
    }

    private void findViews() {
        editTV = (TextView) findViewById(R.id.PasswordTV);
        editTV.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editTV.setTransformationMethod(new MyPasswordTransformationMethod());
        Button numBtn1 = (Button) findViewById(R.id.pwd_num_bt_1);
        Button numBtn2 = (Button) findViewById(R.id.pwd_num_bt_2);
        Button numBtn3 = (Button) findViewById(R.id.pwd_num_bt_3);
        Button numBtn4 = (Button) findViewById(R.id.pwd_num_bt_4);
        Button numBtn5 = (Button) findViewById(R.id.pwd_num_bt_5);
        Button numBtn6 = (Button) findViewById(R.id.pwd_num_bt_6);
        Button numBtn7 = (Button) findViewById(R.id.pwd_num_bt_7);
        Button numBtn8 = (Button) findViewById(R.id.pwd_num_bt_8);
        Button numBtn9 = (Button) findViewById(R.id.pwd_num_bt_9);
        Button numBtn0 = (Button) findViewById(R.id.pwd_num_bt_0);
        ImageButton numBtnDel = (ImageButton) findViewById(R.id.pwd_num_del);
        Button numBtnJ = (Button) findViewById(R.id.pwd_num_j);
        numBtn0.setOnClickListener(this);
        numBtn1.setOnClickListener(this);
        numBtn2.setOnClickListener(this);
        numBtn3.setOnClickListener(this);
        numBtn4.setOnClickListener(this);
        numBtn5.setOnClickListener(this);
        numBtn6.setOnClickListener(this);
        numBtn7.setOnClickListener(this);
        numBtn8.setOnClickListener(this);
        numBtn9.setOnClickListener(this);
        numBtnDel.setOnClickListener(this);
//        numBtnJ.setOnClickListener(this);
    }

    public void setOnJoinClickListener(android.view.View.OnClickListener listener){
        Button numBtnJ = (Button) findViewById(R.id.pwd_num_j);
        numBtnJ.setOnClickListener((android.view.View.OnClickListener)listener);
    }

    public String getInputText() {
        String string = editTV.getText().toString();
        return string;

    }

    public TextView getEditText() {
        return editTV;

    }

    public void setEditTextTextHint(String edit) {
        editTV.setHint(edit);
    }

    public void setTips(int strId) {
        TextView tipView = (TextView) findViewById(R.id.tips);
        tipView.setText(mContext.getResources().getString(strId));
    }

    public void setTips(String str) {
        TextView tipView = (TextView) findViewById(R.id.tips);
        tipView.setText(str);
    }

    class MyPasswordTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;

            public PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }

            public char charAt(int index) {
                return '*'; // This is the important part
            }

            public int length() {
                return mSource.length(); // Return default
            }

            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
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
}
