package org.suirui.huijian.tv.activity.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suirui.srpaas.base.util.log.SRLog;
import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.widget.AudioView;

/**
 * Created by hh on 2018/4/16.
 */
public class FragmentSettingAudio extends BaseFragment implements View.OnClickListener{
    private static final SRLog log = new SRLog(FragmentSettingAudio.class.getName(), TVAppConfigure.LOG_LEVE);
    private FragmentSettingAudioMic mFragmentSettingAudioMic;
    private FragmentSettingAudioSpeaker mFragmentSettingAudioSpeaker;
    private AudioView mAudioView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        log.E("onCreateView()");
//        View view = inflater.inflate(R.show_invite_dialog_layout.m_setting_audio_layout, null);
//        view.requestFocus();
//        View micView = view.findViewById(R.id.optionMic);
//        View speakView = view.findViewById(R.id.optionSpeaker);
//        micView.setOnClickListener(this);
//        speakView.setOnClickListener(this);
        mAudioView = new AudioView(getActivity());
        View view = mAudioView.onCreateView();
        view.requestFocus();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        log.E("onActivityCreated()");

    }

    @Override
    public void onResume() {
        super.onResume();
        changeTitle(getString(R.string.m_setting_audio));
        if(mAudioView != null){
            mAudioView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mAudioView != null){
            mAudioView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAudioView != null){
            mAudioView.onDestroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.optionMic:
                onClickOptionMic();
                break;
            case R.id.optionSpeaker:
                onClickOptionSpeaker();
                break;
        }
    }

    private void onClickOptionMic(){
        if(mFragmentSettingAudioMic == null){
            mFragmentSettingAudioMic = new FragmentSettingAudioMic();
        }
        switchFragment(mFragmentSettingAudioMic);
    }
    private void onClickOptionSpeaker(){
        if(mFragmentSettingAudioSpeaker == null){
            mFragmentSettingAudioSpeaker = new FragmentSettingAudioSpeaker();
        }
        switchFragment(mFragmentSettingAudioSpeaker);
    }
}
