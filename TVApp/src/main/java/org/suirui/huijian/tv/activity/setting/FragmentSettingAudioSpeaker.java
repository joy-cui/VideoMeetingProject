package org.suirui.huijian.tv.activity.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.util.StringUtil;
import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.adapter.AudioAdapter;
import org.suirui.huijian.tv.bean.CamDevice;
import org.suirui.huijian.tv.util.CallNativeUtil;

import java.util.ArrayList;

/**
 * Created by hh on 2018/4/19.
 */
public class FragmentSettingAudioSpeaker extends BaseFragment implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {
    private static final SRLog log = new SRLog(FragmentSettingAudioSpeaker.class.getName(), TVAppConfigure.LOG_LEVE);

    private TextView mSpeakerInputTV;
    private SeekBar mSpeakerSeekbar;
    private ListView mListView;
    private boolean isTestSpeaker = false;
    private AudioAdapter mAdapter;
    private ArrayList<CamDevice> mDatas;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        View view = inflater.inflate(R.layout.m_setting_audio_speaker_layout, null);
        view.requestFocus();
        mSpeakerInputTV = (TextView) view.findViewById(R.id.speakerInputTV);
        mSpeakerSeekbar = (SeekBar) view.findViewById(R.id.speakerSeekbar);
        Button mBtnTestSpeaker = (Button) view.findViewById(R.id.btnTestSpeaker);
        mListView = (ListView) view
                .findViewById(R.id.micListview);

//        mMicSeekbar.setMax(255);
//        mMicSeekbar.setProgress((int) 0);

//        mMicSeekbar.setOnSeekBarChangeListener(this);
        mBtnTestSpeaker.setOnClickListener(this);
        mSpeakerInputTV.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        changeTitle(getString(R.string.m_setting_audio));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnTestSpeaker:
                onClickBtnTestSpeaker();
                break;
            case R.id.speakerInputTV:
                showData();
                break;
        }
    }

    private void onClickBtnTestSpeaker(){
        if(!isTestSpeaker){
            CallNativeUtil.getInstance().startTestMic(getContext());
            isTestSpeaker = true;
        }else{
            CallNativeUtil.getInstance().stopTestMic(getContext());
            isTestSpeaker = false;
        }
    }
    private void onClickOptionSpeaker(){

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == mSpeakerSeekbar) {
            if (!StringUtil.isEmpty(mSpeakerInputTV.getText().toString())) {
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

    private void showData() {
        log.E("showData():");
        mDatas = new ArrayList<CamDevice>();
        for ( int i=0; i < 10; i++) {
            CamDevice camDevice = new CamDevice();
            camDevice.setName("item"+i);
            camDevice.setSelectd(true);
            mDatas.add( camDevice);
        }
        if (mAdapter == null) {
            mAdapter = new AudioAdapter(getContext(), mDatas);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.changeList(mDatas);
            mAdapter.notifyDataSetChanged();
        }
        mListView.setVisibility(View.VISIBLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CamDevice device = mDatas.get(position);
                mSpeakerInputTV.setText(device.getName());
                mListView.setVisibility(View.GONE);
            }
        });
    }

}
