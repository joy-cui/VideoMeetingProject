package org.suirui.huijian.tv.widget;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.IpConfiguration;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import com.srpaas.version.util.InstallUtil;
import com.suirui.srpaas.base.util.log.SRLog;
import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.util.AppUtil;
import org.suirui.huijian.tv.util.CallNativeUtil;
import org.suirui.transfer.listener.AudioListener;

import java.io.IOException;

/**
 * Created by hh on 2018/4/20.
 */

public class AudioView implements View.OnClickListener,View.OnKeyListener,
        SeekBar.OnSeekBarChangeListener,AudioListener.ITestMicListener{
    private static final SRLog log = new SRLog(AudioView.class.getName(), TVAppConfigure.LOG_LEVE);
    private Activity mActivity;
    private SeekBar mVolumeSeekbar;
    private SeekBar mMicTestSeekbar;
    private Button mBtnTestSpeaker;
    private Button mBtnTestMic;
    private boolean isTestMic = false;
    private boolean isTestSpeaker = false;

    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 50f;
    private boolean vibrate;
    private static final long VIBRATE_DURATION = 200L;


    AudioManageView mAudioManageView;

    public AudioView(Activity activity) {
        mActivity = activity;
        if(AppUtil.getInstance().isShowRootUI()){
            if(mAudioManageView == null){
                mAudioManageView = new AudioManageView(activity);
            }
        }

    }
    public View onCreateView() {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.m_setting_audio_view, null);
        mVolumeSeekbar = (SeekBar) view.findViewById(R.id.volumeSeekbar);
        mMicTestSeekbar = (SeekBar) view.findViewById(R.id.testMicSeekbar);
        mBtnTestMic = (Button) view.findViewById(R.id.btnTestMic);
        mBtnTestSpeaker = (Button) view.findViewById(R.id.btnTestSpeaker);
        LinearLayout audioManageView = (LinearLayout)view.findViewById(R.id.audioManageView);


        mBtnTestMic.setOnClickListener(this);
        mBtnTestSpeaker.setOnClickListener(this);
        mMicTestSeekbar.setOnSeekBarChangeListener(this);
        mVolumeSeekbar.setOnSeekBarChangeListener(this);
        mBtnTestSpeaker.setOnKeyListener(this);
        AudioListener.getInstance().addTestMicListener(this);


        if(mAudioManageView != null){
            audioManageView.addView(mAudioManageView.onCreateView());
            mAudioManageView.setOnSelectNewListener(new AudioManageView.onSelectNewListener() {
                @Override
                public void setOnSelectNew(boolean isNew) {
                    if(isNew){
                        stopTestMic();
                        if(isTestSpeaker){
                            mBtnTestSpeaker.setText(R.string.m_test);
                            stopTestSpeaker();
                            isTestSpeaker = false;
                        }
                    }
                }
            });
        }

        return view;
    }
    public void onResume() {
//        CallNativeUtil.getInstance().getAudioVolume(mActivity);
        mVolumeSeekbar.setMax(CallNativeUtil.getInstance().getMusicMaxVolume(mActivity));
        mVolumeSeekbar.setProgress(CallNativeUtil.getInstance().getMusicCurVolume(mActivity));
//        initPlaySound();

        if(mAudioManageView != null){
            mAudioManageView.onResume();
        }
    }

    public void onPause() {
        stopTestSpeaker();
        stopTestMic();
        if(mAudioManageView != null){
            mAudioManageView.onPause();
        }
    }

    public void onDestroy() {
        stopTestSpeaker();
        stopTestMic();
        AudioListener.getInstance().removTestMicListener(this);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.btnTestSpeaker:
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            setVolume(mVolumeSeekbar.getProgress() - 1);
                            return true;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            setVolume(mVolumeSeekbar.getProgress() + 1);
                            return true;
                    }
                    break;
            }

        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnTestMic:
                onClickBtnTestMic();
                break;
            case R.id.btnTestSpeaker:
                onClickBtnTestSpeaker();
                break;
//            case R.id.audioDeviceTV:
//                onClickSelectAudioDevice();
//                break;
        }
    }
    private void startTestMic(){
        if(!isTestMic){
            mBtnTestMic.setText(R.string.m_stop);
            CallNativeUtil.getInstance().startTestMic(mActivity);
            isTestMic = true;
        }
    }
    private void stopTestMic(){
        if(isTestMic){
            mBtnTestMic.setText(R.string.m_test);
            CallNativeUtil.getInstance().stopTestMic(mActivity);
            isTestMic = false;
        }
    }

    private void setCheckBoxText(CheckBox checkBox, boolean isChecked){
        if(isChecked){
            checkBox.setText(R.string.m_checkbox_open);
        }else {
            checkBox.setText(R.string.m_checkbox_close);
        }
    }

    private void onClickBtnTestMic(){
        if(!isTestMic){
            startTestMic();
        }else{
            stopTestMic();
        }
    }
    private void onClickBtnTestSpeaker(){
        if(!isTestSpeaker){
            mBtnTestSpeaker.setText(R.string.m_stop);
            startTestSpeaker();
            isTestSpeaker = true;
        }else{
            mBtnTestSpeaker.setText(R.string.m_test);
            pauseTestSpeaker();
            isTestSpeaker = false;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        log.E("onProgressChanged()::::"+progress+"  "+fromUser);
        if (seekBar == mVolumeSeekbar) {
            if(fromUser){
                setVolume(progress);
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


    private void setVolume(int progress){
        mVolumeSeekbar.setProgress(progress);
        CallNativeUtil.getInstance().setMusicVolume(mActivity,progress);
    }

    private void initPlaySound() {
        log.E("initPlaySound()");
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setLooping(true);
            AssetFileDescriptor file = mActivity.getResources().openRawResourceFd(
                    R.raw.play);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
//                file.close();
           mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void startTestSpeaker() {
        if (mediaPlayer != null) {
            log.E("startTestSpeaker()::::" + mediaPlayer.isPlaying());
        }
        initPlaySound();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            log.E("startTestSpeaker()--");
            mediaPlayer.start();
        }
    }
    private void pauseTestSpeaker(){
        log.E("pauseTestSpeaker()--");
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            log.E("pauseTestSpeaker()----------");
            mediaPlayer.pause();
        }
    }

    private void stopTestSpeaker(){
        try{
            if (mediaPlayer != null) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
}
