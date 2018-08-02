package org.suirui.huijian.tv.activity.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.util.StringUtil;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.util.CallNativeUtil;
import org.suirui.huijian.tv.widget.AudioDeviceDialog;
import org.suirui.transfer.listener.AudioListener;

/**
 * Created by hh on 2018/4/18.
 */
public class FragmentSettingAudioMic extends BaseFragment implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,AudioListener.ITestMicListener {
    private static final SRLog log = new SRLog(FragmentSettingAudioMic.class.getName(), TVAppConfigure.LOG_LEVE);

    private TextView mMicInputTV;
    private SeekBar mMicSeekbar;
    private SeekBar mMicTestSeekbar;
    private ListView mListView;
    private boolean isTestMic = false;
    private AudioDeviceDialog mDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        View view = inflater.inflate(R.layout.m_setting_audio_mic_layout, null);
        view.requestFocus();
        mMicInputTV = (TextView) view.findViewById(R.id.micInputTV);
        mMicSeekbar = (SeekBar) view.findViewById(R.id.micSeekbar);
        mMicTestSeekbar = (SeekBar) view.findViewById(R.id.testMicSeekbar);
        Button mBtnTestMic = (Button) view.findViewById(R.id.btnTestMic);
        mListView = (ListView) view
                .findViewById(R.id.micListview);

//        mMicSeekbar.setMax(255);
//        mMicSeekbar.setProgress((int) 0);

//        mMicSeekbar.setOnSeekBarChangeListener(this);
        mBtnTestMic.setOnClickListener(this);
        mMicInputTV.setOnClickListener(this);
        mMicTestSeekbar.setOnSeekBarChangeListener(this);
        AudioListener.getInstance().addTestMicListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        log.E("onActivityCreated()");
//        AppUtil.getInstance().testFocusView(getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();
        changeTitle(getString(R.string.m_setting_audio));
    }

    @Override
    public void onPause() {
        super.onPause();
        CallNativeUtil.getInstance().stopTestMic(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioListener.getInstance().removTestMicListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnTestMic:
                onClickBtnTestMic();
                break;
            case R.id.micInputTV:
                onClickSelectMic();
                break;
        }
    }

    private void onClickBtnTestMic(){
        if(!isTestMic){
            CallNativeUtil.getInstance().startTestMic(getContext());
            isTestMic = true;
        }else{
            CallNativeUtil.getInstance().stopTestMic(getContext());
            isTestMic = false;
        }
    }
    private void onClickSelectMic(){
        showDeviceDialog();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == mMicSeekbar) {
            if (!StringUtil.isEmpty(mMicInputTV.getText().toString())) {
//                                mPtApp.setMicrophoneVolume(mProgress,
//                                        micDeviceId, micDeviceName);
            }

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onTestMicNotify(int volume) {
        mMicTestSeekbar.setProgress(volume);
    }

    private void showDeviceDialog(){
        if(mDialog == null){
            mDialog = new AudioDeviceDialog(getContext(),mMicInputTV);
        }
        mDialog.show(mMicInputTV,null,mMicInputTV);
    }
}
