package org.suirui.huijian.tv.activity.setting;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.srpaas.version.util.InstallUtil;
import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.TVSrHuiJianProperties;
import org.suirui.huijian.tv.util.AppUtil;
import org.suirui.huijian.tv.widget.ConfrimDialog;
import org.suirui.huijian.tv.widget.DialogOptionPower;

/**
 * Created by hh on 2018/4/19.
 */
public class FragmentSettingSys extends BaseFragment implements View.OnClickListener, View.OnKeyListener {
    private static final SRLog log = new SRLog(FragmentSettingSys.class.getName(), TVAppConfigure.LOG_LEVE);

    private CheckBox mChbNotDisturb;
    private CheckBox mChbAutoAnswer;
    private CheckBox mChbLowBattery;
    private View mOptionResumeSysView;
    private View mOptionPowerView;
    private DialogOptionPower mDialogOptionPower;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        View view = inflater.inflate(R.layout.m_setting_sys_layout, null);
        mChbNotDisturb = (CheckBox) view.findViewById(R.id.chbNotDisturb);
        mChbAutoAnswer = (CheckBox) view.findViewById(R.id.chbAutoAnswer);
        mChbLowBattery = (CheckBox) view.findViewById(R.id.chbLowBattery);
        mOptionResumeSysView = view.findViewById(R.id.optionResumeSys);
        mOptionPowerView = view.findViewById(R.id.optionPower);


        boolean isNotDisturb = TVSrHuiJianProperties.isNotDisturb();
        boolean isAutoAnswer = TVSrHuiJianProperties.isAutoAnswer();
        mChbNotDisturb.setChecked(isNotDisturb);
        mChbAutoAnswer.setChecked(isAutoAnswer);
        mChbLowBattery.setChecked(mChbLowBattery.isChecked());
        setCheckBoxText(mChbNotDisturb, isNotDisturb);
        setCheckBoxText(mChbAutoAnswer, isAutoAnswer);
        setCheckBoxText(mChbLowBattery, mChbLowBattery.isChecked());

        mOptionResumeSysView.setOnClickListener(this);
        mOptionPowerView.setOnClickListener(this);
        mChbNotDisturb.setOnKeyListener(this);
        mChbAutoAnswer.setOnKeyListener(this);

        if(!AppUtil.getInstance().isShowRootUI()){
            mOptionResumeSysView.setVisibility(View.GONE);
            mOptionPowerView.setVisibility(View.GONE);
        }

        changeTitle(getString(R.string.m_setting_sys));
        view.requestFocus();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.optionResumeSys:
                onClickOptionResumeSys();
                break;
            case R.id.optionPower:
                onClickOptionPower();
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        log.E("onKey():"+keyCode);
        int viewId = v.getId();
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch (keyCode){
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    setCheckBox(viewId,false);

                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    setCheckBox(viewId,true);
                    return true;
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    switch (viewId){
                        case R.id.chbNotDisturb:
                            onClickChbNotDisturb(!mChbNotDisturb.isChecked());
                            break;
                        case R.id.chbAutoAnswer:
                            onClickChbAutoAnswer(!mChbAutoAnswer.isChecked());
                            break;
                    }
                    return true;
            }
        }
        return false;
    }

    private void setCheckBox(int viewId,boolean ischeck){
        switch (viewId){
            case R.id.chbNotDisturb:
//                onClickChbNotDisturb(ischeck);
                onClickChbNotDisturb(!mChbNotDisturb.isChecked());
                break;
            case R.id.chbAutoAnswer:
//                onClickChbAutoAnswer(ischeck);
                onClickChbAutoAnswer(!mChbAutoAnswer.isChecked());
                break;
        }
    }

    private void onClickChbNotDisturb(boolean isChecked) {
        TVSrHuiJianProperties.setNotDisturb(isChecked);
        setCheckBoxText(mChbNotDisturb, isChecked);
        mChbNotDisturb.setChecked(isChecked);
    }

    private void onClickChbAutoAnswer(boolean isChecked) {
        TVSrHuiJianProperties.setAutoAnswer(isChecked);
        setCheckBoxText(mChbAutoAnswer, isChecked);
        mChbAutoAnswer.setChecked(isChecked);
    }

    private void onClickChbLowBattery(boolean isChecked) {
    }

    private void onClickOptionResumeSys() {
        final ConfrimDialog dialog = new ConfrimDialog(getActivity());

        dialog.setWarningMsg(R.string.m_device_resume_tips);
        dialog.show();
        dialog.setButton1(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setButton2(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MasterClear.getInstance().deviceResume(getActivity());
                dialog.dismiss();
            }
        });
    }
    private void onClickOptionPower() {
        showPowerMargDialog();
    }

    private void setCheckBoxText(CheckBox checkBox, boolean isChecked){
        if(isChecked){
            checkBox.setText(R.string.m_checkbox_open);
        }else {
            checkBox.setText(R.string.m_checkbox_close);
        }
    }

    private void showPowerMargDialog(){
        if(mDialogOptionPower == null){
            mDialogOptionPower = new DialogOptionPower(getContext(),getActivity());
        }
        mDialogOptionPower.show();
    }

    private void dismissDialog(){
        if(mDialogOptionPower != null){
            mDialogOptionPower.dismiss();
        }
    }

}
