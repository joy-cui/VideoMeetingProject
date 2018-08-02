package org.suirui.huijian.tv.widget;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.SystemProperties;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.server.AudioCommon;
import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.bean.LocationBean;
import org.suirui.huijian.tv.util.CallNativeUtil;
import org.suirui.transfer.listener.AudioListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by hh on 2018/4/20.
 */

public class AudioManageView implements View.OnClickListener, View.OnKeyListener {
    private static final SRLog log = new SRLog(AudioManageView.class.getName(), TVAppConfigure.LOG_LEVE);
    private Activity mActivity;
    private ListView mListView;
    private TextView mAudioDevice;
    private CheckBox mChBAutoSpeaker;
    private boolean isTestMic = false;
    private boolean isTestSpeaker = false;
    private LocationDialog mLocationDialog;


    private static final String AUDIOPCMLISTUPDATE = "com.android.server.audiopcmlistupdate";
    private static final String WIREDACCESSORYLISTUPDATE = "com.android.server.WiredAccessorylistupdate";//zhg++,for syn udate audio list
    /* this is from kernel. */
    private static final String USBAUDIO_CARD_DRIVER_NAME = "USB-Audio";
    private static final String SPDIF_CARD_DRIVER_NAME = "SPDIF";
    private static final String PASSTHROUGH = "PASSTHROUGH";
    private static final String MULTICHANNEL = "5POINT1 MULTICHANNEL";

    private static String SOC_AND_SPDIF_NAME = "";
    //spdif passthrough
    private static String MEDIA_CFG_AUDIO_BYPASS = "media.cfg.audio.bypass";
    private static String HDMI_AUDIO_MULTICHANNEL = "media.cfg.audio.mul";
    private static final String WEDOINNOVAUDIOPOLICY = "persist.sys.wdaudio.policy";
    private static final String HAVEBUILTINSPEAKER = "ro.wd.havespeaker";
    private static final String SOC_AND_SPDIF_KEY = "9";
    private static final String SPDIF_PASSTHROUGH_KEY = "8";
    private static final String HDMI_MULTICHANNEL_KEY = "7";
    private static final String HDMI_PASSTHROUGH_KEY = "6";
    private static final String BUILTIN_SPEAKER_KEY = "5";//zhg++,buit-in speaker
    private static final String USB_AUDIO_PLAYBACK_KEY = "4";//zhg++,for usb audio
    private static final String HEADPHONE_PLAYBACK_KEY = "3";//zhg++,for headphone audio

    private static String SPDIF_PASSTHROUGH_NAME = "";
    private static String HDMI_MULTICHANNEL_NAME = "";
    private static String HDMI_PASSTHROUGH_NAME = "";

    private int mCaptureCounts = 0;
    private int mPlaybackCounts = 0;
    private static boolean bAudioPassthroughSupport = false;
    private String mSelectedCaptureKey;
    private String mSelectedPlaybackKey;

    private IntentFilter mAudioDevicesListUpdate_IF;
    private IntentFilter mUpdateListByWiredAccessory_IF;
    private ArrayList<SndElement> mCardsList;
    private List<Map> mDevicesList = new ArrayList<>();

    private onSelectNewListener mListener = null;

    public void setOnSelectNewListener(onSelectNewListener listener) {
        mListener = listener;
    }

    public interface onSelectNewListener {
        void setOnSelectNew(boolean isNew);
    }

    public AudioManageView(Activity activity) {
        mActivity = activity;

    }

    public View onCreateView() {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.m_setting_audio_manage_view, null);
        mAudioDevice = (TextView) view.findViewById(R.id.audioDeviceTV);
        mChBAutoSpeaker = (CheckBox) view.findViewById(R.id.chbAutoSpeaker);
        LinearLayout autoSpeakerLayout = (LinearLayout) view.findViewById(R.id.autoSpeakerLayout);
        boolean isAutoSpeaker = true;
        mChBAutoSpeaker.setChecked(isAutoSpeaker);
        setCheckBoxText(mChBAutoSpeaker, isAutoSpeaker);

        mAudioDevice.setOnClickListener(this);
        mChBAutoSpeaker.setOnKeyListener(this);


        String mHaveBuiltinSpeaker = SystemProperties.get(HAVEBUILTINSPEAKER);
        if (mHaveBuiltinSpeaker != null && mHaveBuiltinSpeaker.equals("true")) {
//            getPreferenceScreen().removePreference(mAudioPolicy);
            autoSpeakerLayout.setVisibility(View.VISIBLE);
        }else{
            autoSpeakerLayout.setVisibility(View.GONE);
        }
        mCardsList = new ArrayList<SndElement>();

        // receive intent from WiredAccessoryObserver
        mAudioDevicesListUpdate_IF = new IntentFilter();
        mAudioDevicesListUpdate_IF.addAction(AUDIOPCMLISTUPDATE);

        // receive intent from WiredAccessoryManager
        mUpdateListByWiredAccessory_IF = new IntentFilter();//zhg++,for syn audio list
        mUpdateListByWiredAccessory_IF.addAction(WIREDACCESSORYLISTUPDATE);


        //whether the device support audio passthrough
        if (!SystemProperties.get(MEDIA_CFG_AUDIO_BYPASS).isEmpty()) {
            bAudioPassthroughSupport = true;
        }
        //Log.d(TAG, "onCreate--,set audio channel by key");
        //AudioCommon.setPlaybackChannelbyKey(AudioCommon.getCurrentPlaybackDevice());

        return view;
    }

    public void onResume() {
        updateSwitch();
        mActivity.registerReceiver(mAudioDevicesListUpdate_BR, mAudioDevicesListUpdate_IF);
        mActivity.registerReceiver(mUpdateListByWiredAccessory_BR, mUpdateListByWiredAccessory_IF);//zhg++,for dyn update list
        update();
    }

    public void onPause() {
        mActivity.unregisterReceiver(mAudioDevicesListUpdate_BR);
        mActivity.unregisterReceiver(mUpdateListByWiredAccessory_BR);//zhg++,for dyn update list
    }

    public void onDestroy() {
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            log.E("keyCode:" + keyCode);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.chbAutoSpeaker:
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            onClickChbAutoSpeaker(!mChBAutoSpeaker.isChecked());
                            return true;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            onClickChbAutoSpeaker(!mChBAutoSpeaker.isChecked());
                            return true;
                        case KeyEvent.KEYCODE_ENTER:
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                            onClickChbAutoSpeaker(!mChBAutoSpeaker.isChecked());
                            return true;
                    }
                    break;
            }

        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.audioDeviceTV:
                onClickSelectAudioDevice();
                break;
        }
    }

    private void setCheckBoxText(CheckBox checkBox, boolean isChecked) {
        if (isChecked) {
            checkBox.setText(R.string.m_checkbox_open);
        } else {
            checkBox.setText(R.string.m_checkbox_close);
        }
    }

    private void onClickChbAutoSpeaker(boolean isChecked) {
        setCheckBoxText(mChBAutoSpeaker, isChecked);
        mChBAutoSpeaker.setChecked(isChecked);
        if(isChecked){
            SystemProperties.set(WEDOINNOVAUDIOPOLICY, "true");
        }else{
            SystemProperties.set(WEDOINNOVAUDIOPOLICY, "false");
        }
        String mStringAudioPolicy = SystemProperties.get(WEDOINNOVAUDIOPOLICY);
        log.E("onClickChbAutoSpeaker()::mStringAudioPolicy :"+mStringAudioPolicy);
    }

    private void onClickSelectAudioDevice() {
        showAudioDevices();
    }


    private List<LocationBean> getdata() {
        List<LocationBean> list = new ArrayList<LocationBean>();
        for (int i = 0; i < mDevicesList.size(); i++) {
            Map<String,Object> map = mDevicesList.get(i);
            if(map != null){
                LocationBean l = new LocationBean();
                l.setName("" + map.get("title"));
                l.setObject(map);
                list.add(l);
            }

        }
        return list;
    }

    private void showAudioDevices() {
        if (mLocationDialog == null) {
            mLocationDialog = new LocationDialog(mActivity, mAudioDevice);
        }
        mLocationDialog.show(mAudioDevice, getdata(), mAudioDevice);
        mLocationDialog.setOnClickTypeListener(new LocationDialog.onClickTypeListener() {
            @Override
            public void setOnClickType(int type) {
                log.E("setOnClickTypeListener  ---type:" + type);

                Map<String,Object> map = mDevicesList.get(type);
                log.E("setOnClickTypeListener():Key: " + map.get("key") + ", last key: "
                        + mSelectedPlaybackKey + " title: "
                        + map.get("title"));
//                RadioPreference rp;
                if (mSelectedPlaybackKey.equals(map.get("key"))) {
//                    rp = (RadioPreference) preference;
//                    rp.setChecked(true);
                    mAudioDevice.setText((String)map.get("title"));
                    if(mListener != null){
                        mListener.setOnSelectNew(false);
                    }
                    return;
                }
                if(mListener != null){
                    mListener.setOnSelectNew(true);
                }
                mAudioDevice.setText((String)map.get("title"));
                mSelectedPlaybackKey = (String)map.get("key");
//                rp = (RadioPreference) mSoundOutput_List
//                        .findPreference(mSelectedPlaybackKey);
//                if (rp != null)
//                    rp.setChecked(false);
//                mSelectedPlaybackKey = preference.getKey();
//                rp = (RadioPreference) preference;
//                rp.setChecked(true);

                int deviceType = AudioCommon.SND_DEV_TYPE_DEFAULT;
                String cardName = (String)map.get("title");
                boolean bSpdifPassThrough = false;
                boolean bHdmiMultichannel = false;

                if(cardName.equals(mActivity.getString(R.string.m_sound_output_default))){
                    deviceType = AudioCommon.SND_DEV_TYPE_DEFAULT;
                }else if(cardName.equals(mActivity.getString(R.string.m_sound_output_spdif_passthrough))) {
                    deviceType = AudioCommon.SND_DEV_TYPE_SPDIF_PASSTHROUGH;
                    bSpdifPassThrough = true;
                }else if(cardName.equals(mActivity.getString(R.string.m_sound_output_usb))){//isUsbAudio(mSelectedPlaybackKey)
                    deviceType = AudioCommon.SND_DEV_TYPE_USB;
                }else if(cardName.equals(mActivity.getString(R.string.m_sound_output_hdmi_passthrough))){
                    bSpdifPassThrough = true;
                    deviceType = AudioCommon.SND_DEV_TYPE_HDMI_PASSTHROUGH;
                }else if(cardName.equals(mActivity.getString(R.string.m_sound_output_hdmi_multilpcm))){
                    bHdmiMultichannel = true;
                    deviceType = AudioCommon.SND_DEV_TYPE_HDMI_MULTILPCM;
                }else if(cardName.equals(mActivity.getString(R.string.m_sound_output_i2speaker))){//zhg++,for built-in speaker
                    deviceType = AudioCommon.SND_DEV_TYPE_I2SPEAKER;
                }else if(cardName.equals(mActivity.getString(R.string.m_sound_output_headphone))){//zhg++,for headphone
                    deviceType = AudioCommon.SND_DEV_TYPE_HEADPHONE;
                }

                if(bSpdifPassThrough)
                    SystemProperties.set(MEDIA_CFG_AUDIO_BYPASS, "true");
                else
                    SystemProperties.set(MEDIA_CFG_AUDIO_BYPASS, "false");

                if(bHdmiMultichannel){
                    SystemProperties.set(HDMI_AUDIO_MULTICHANNEL, "true");
                } else {
                    SystemProperties.set(HDMI_AUDIO_MULTICHANNEL, "false");
                }
                log.E("-setOnClickTypeListener, doAudioDevicesRouting for click,cardName: "+cardName+", deviceType: "+deviceType);
                AudioCommon.doAudioDevicesRouting(mActivity, deviceType, AudioCommon.SND_PCM_STREAM_PLAYBACK, mSelectedPlaybackKey);
                return;

            }
        });
    }

    private BroadcastReceiver mAudioDevicesListUpdate_BR = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            update();

        }
    };

    private BroadcastReceiver mUpdateListByWiredAccessory_BR = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            update();

        }
    };

    private void update() {
        fillList();
//        showAudioDevices();
    }

    private void fillList() {
        boolean hasSocAudio_playback = false;
        boolean hasSpdifAudio_playback = false;
        SOC_AND_SPDIF_NAME = "";
        mSelectedCaptureKey = "0";
        mSelectedPlaybackKey = "0";

//        mSoundOutput_List.removeAll();
        mCaptureCounts = 0;
        mPlaybackCounts = 0;
        mCardsList.clear();

        readProcFileAndInit();
        initSelectedKey();
        //int deviceType = AudioCommon.SND_DEV_TYPE_DEFAULT;

        mDevicesList.clear();

        Iterator<SndElement> it = mCardsList.iterator();
        while (it.hasNext()) {
            SndElement se = it.next();
            log.E("wwwxh  fillList(), "+se.getToString());
            //if (se.devType == AudioCommon.SND_DEV_TYPE_USB)//zhg@@
            //	continue;

            if (se.hasPlayback) {
//                RadioPreference rp = new RadioPreference(this, null);
//                rp.setKey(se.idx);
//                rp.setPersistent(false);
//                rp.setWidgetLayoutResource(R.layout.preference_radio);
//                rp.setOnPreferenceClickListener(mPlaybackListClickListener);
//                rp.setOnPreferenceChangeListener(mPlaybackChangeListener);
//                mSoundOutput_List.addPreference(rp);
                mPlaybackCounts++;

                Map<String, Object> map = new HashMap<>();
                map.put("type", se.devType);
                map.put("key", se.idx);
                if (se.devType == AudioCommon.SND_DEV_TYPE_DEFAULT) {
//                    rp.setTitle(R.string.sound_output_default);
                    //zhg--, hasSocAudio_playback = true;
                    map.put("title", mActivity.getString(R.string.m_sound_output_default));
                } else if (se.devType == AudioCommon.SND_DEV_TYPE_SPDIF_PASSTHROUGH) {
//                    rp.setTitle(R.string.sound_output_spdif_passthrough);
//                    rp.setKey(SPDIF_PASSTHROUGH_KEY);
                    hasSpdifAudio_playback = true;

                    map.put("title", mActivity.getString(R.string.m_sound_output_spdif_passthrough));
                    map.put("key", SPDIF_PASSTHROUGH_KEY);
                } else if (se.devType == AudioCommon.SND_DEV_TYPE_I2SPEAKER) {//zhg++,for built-in speaker
//                    rp.setTitle(R.string.sound_output_i2speaker);
//                    rp.setKey(BUILTIN_SPEAKER_KEY);
                    map.put("title", mActivity.getString(R.string.m_sound_output_i2speaker));
                    map.put("key", BUILTIN_SPEAKER_KEY);
                } else if (se.devType == AudioCommon.SND_DEV_TYPE_USB) {//zhg++,for usb audio
//                    rp.setTitle("USB AUDIO");
//                    rp.setKey(USB_AUDIO_PLAYBACK_KEY);
                    map.put("title", mActivity.getString(R.string.m_sound_output_usb));
                    map.put("key", USB_AUDIO_PLAYBACK_KEY);
                } else if (se.devType == AudioCommon.SND_DEV_TYPE_HEADPHONE) {//zhg++,for usb audio
//                    rp.setTitle("Headphone");
//                    rp.setKey(HEADPHONE_PLAYBACK_KEY);
                    map.put("title", "Headphone");
                    map.put("key", HEADPHONE_PLAYBACK_KEY);
                }

                mDevicesList.add(map);
                if (mSelectedPlaybackKey.equals(map.get("key"))) {
//                    rp.setChecked(true);
                    mAudioDevice.setText((String)map.get("title"));
                }
            }
            //deviceType = se.devType;
        }
        log.E("wwwxh  fillList(), hasSocAudio_playback:"+hasSocAudio_playback);
        if (hasSocAudio_playback) {
//            RadioPreference rp = new RadioPreference(this, null);
//            rp.setKey(HDMI_PASSTHROUGH_KEY);
//            rp.setTitle(R.string.sound_output_hdmi_passthrough);
//            rp.setPersistent(false);
//            rp.setWidgetLayoutResource(R.layout.preference_radio);
//            rp.setOnPreferenceClickListener(mPlaybackListClickListener);
//            rp.setOnPreferenceChangeListener(mPlaybackChangeListener);
//            mSoundOutput_List.addPreference(rp);

            Map<String, Object> map = new HashMap<>();
//            map.put("type",se.devType);
            map.put("title", mActivity.getString(R.string.m_sound_output_hdmi_passthrough));
            map.put("key", HDMI_PASSTHROUGH_KEY);
            mDevicesList.add(map);
            if (mSelectedPlaybackKey.equals(map.get("key"))) {
//                rp.setChecked(true);
                mAudioDevice.setText((String)map.get("title"));
            }

			/*if(!"rk31sdk".equals(Build.BOARD) && !"rk3028sdk".equals(Build.BOARD)){

				rp = new RadioPreference(this, null);
				rp.setKey(HDMI_MULTICHANNEL_KEY);
				rp.setTitle(R.string.sound_output_hdmi_multilpcm);
				rp.setPersistent(false);
				rp.setWidgetLayoutResource(R.layout.preference_radio);
				rp.setOnPreferenceClickListener(mPlaybackListClickListener);
				rp.setOnPreferenceChangeListener(mPlaybackChangeListener);
				mSoundOutput_List.addPreference(rp);

				if (mSelectedPlaybackKey.equals(rp.getKey())) {
					rp.setChecked(true);
				}
			}*/

        }

        // show tips when there is no devices
        if (0 == mPlaybackCounts) {
//            Preference tip = new Preference(this);
//            tip.setTitle("There is no Sound Output Devices");
//            mSoundOutput_List.addPreference(tip);
            Map<String, Object> map = new HashMap<>();
            map.put("title", "There is no Sound Output Devices");
            mDevicesList.add(map);
        }

        for (int i = 0; i < mDevicesList.size(); i++) {
            Map<String, Object> map1 = mDevicesList.get(i);
            log.E("wwwxh  fillList() map1 "+map1.toString());
            log.E("wwwxh  fillList(), device:"+map1.get("title")
                    + "   key:"+map1.get("key")
                    + "   type:"+map1.get("type"));
        }
        log.E("wwwxh  fillList(), for init set doAudioDevicesRouting");
        //AudioCommon.doAudioDevicesRouting(mContext, deviceType, AudioCommon.SND_PCM_STREAM_PLAYBACK, mSelectedPlaybackKey);//zhg++,20160802,for init sound set

    }

    private void initSelectedKey() {
        // capture
        mSelectedCaptureKey = AudioCommon.getCurrentCaptureDevice();
        // playback
        mSelectedPlaybackKey = AudioCommon.getCurrentPlaybackDevice();

        log.E("wwwxh   initSelectedKey() mSelectedCaptureKey:" + mSelectedCaptureKey + "; mSelectedPlaybackKey" + mSelectedPlaybackKey);
    }

    private void readProcFileAndInit() {
        // init
        mCaptureCounts = 0;
        mPlaybackCounts = 0;
        mCardsList.clear();

        String CardsPath = "/proc/asound/cards";
        String PcmPath = "/proc/asound/pcm";
        FileReader cards_fr = null;
        FileReader pcms_fr = null;
        BufferedReader cards_br = null;
        BufferedReader pcms_br = null;
        try {
            try {
                cards_fr = new FileReader(CardsPath);
                pcms_fr = new FileReader(PcmPath);
            } catch (FileNotFoundException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            cards_br = new BufferedReader(cards_fr);
            pcms_br = new BufferedReader(pcms_fr);
            String Line;
            // cards
            while ((Line = cards_br.readLine()) != null) {
                int pos = Line.lastIndexOf(" - ");
                if (pos > 0) {
                    SndElement se = new SndElement();
                    String idx = Line.substring(0, 2).trim();
                    String cardName = Line.substring(pos + 3);
                    pos = Line.indexOf(USBAUDIO_CARD_DRIVER_NAME);
                    if (pos > 0) {
                        se.devType = AudioCommon.SND_DEV_TYPE_USB;
                    }
                    pos = Line.indexOf(SPDIF_CARD_DRIVER_NAME);
                    if (pos > 0) {
                        se.devType = AudioCommon.SND_DEV_TYPE_SPDIF_PASSTHROUGH;
                    }
                    se.idx = idx;
                    se.cardName = cardName;
                    log.E("wwwxh readProcFileAndInit()  idx: " + idx + " cardname: " + cardName);
                    mCardsList.add(se);
                }
            }
            //zhg++,for built-in speaker
            String mHaveBuiltinSpeaker = SystemProperties.get(HAVEBUILTINSPEAKER);
            if (mHaveBuiltinSpeaker != null && mHaveBuiltinSpeaker.equals("true")) {
                SndElement seSpeaker = new SndElement();
                seSpeaker.idx = "1";
                seSpeaker.cardName = "Built-in Speaker";
                seSpeaker.hasPlayback = true;
                seSpeaker.devType = AudioCommon.SND_DEV_TYPE_I2SPEAKER;
                mCardsList.add(seSpeaker);

            }

            //zhg++,for headphone
            String mCurPlayBackDev = AudioCommon.getCurrentPlaybackDevice();
            if (1 == getHeadphoneState()) {
                log.E("wwwxh readProcFileAndInit(),headphone insert already,so add item");
                SndElement seHeadphone = new SndElement();
                seHeadphone.idx = "2";
                seHeadphone.cardName = "Headphone";
                seHeadphone.hasPlayback = true;
                seHeadphone.devType = AudioCommon.SND_DEV_TYPE_HEADPHONE;
                mCardsList.add(seHeadphone);
            }

            // pcms
            int pos;
            while ((Line = pcms_br.readLine()) != null) {
                String idx = Line.substring(1, 2);
                log.E("wwwxh readProcFileAndInit() idx: " + idx);
                Iterator<SndElement> it = mCardsList.iterator();
                while (it.hasNext()) {
                    SndElement se = it.next();
                    if (se.idx.equals(idx)) {
                        pos = Line.indexOf("capture");
                        if (pos > 0)
                            se.hasCapture = true;
                        pos = Line.indexOf("playback");
                        if (pos > 0)
                            se.hasPlayback = true;
                        break;
                    }
                }
            }

        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            try {
                if (cards_br != null)
                    cards_br.close();
                if (cards_fr != null)
                    cards_fr.close();
                if (pcms_br != null)
                    pcms_br.close();
                if (pcms_fr != null)
                    pcms_fr.close();

            } catch (IOException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

    }

    private int getHeadphoneState() {
        int mHeadphoneState = -1;
        char[] buffer = new char[1024];
        try {
            int curState;
            FileReader file = new FileReader("/sys/class/switch/headphone/state");
            int len = file.read(buffer, 0, 1024);
            file.close();
            mHeadphoneState = Integer.valueOf((new String(buffer, 0, len)).trim());
        } catch (FileNotFoundException e) {
            log.E("wwwxh getHeadphoneState() not found while attempting to determine initial headphone switch state");
            e.printStackTrace();
        } catch (Exception e) {
            log.E("wwwxh getHeadphoneState()" + e.getMessage());
            e.printStackTrace();
        }
        return mHeadphoneState;
    }

    private boolean isUsbAudio(String cardidx) {
        boolean isUsb = false;
        Iterator<SndElement> it = mCardsList.iterator();
        while (it.hasNext()) {
            SndElement se = it.next();
            if (se.idx.equals(cardidx)) {
                isUsb = (se.devType == AudioCommon.SND_DEV_TYPE_USB);
                break;
            }
        }

        return isUsb;
    }

    private void updateSwitch() {
        boolean mPolicySwitchStatus;
        String mStringAudioPolicy = SystemProperties.get(WEDOINNOVAUDIOPOLICY);
        if (mStringAudioPolicy.equals("true")) {
            mPolicySwitchStatus = true;
        } else {
            mPolicySwitchStatus = false;
        }

        onClickChbAutoSpeaker(mPolicySwitchStatus);
//        if (mPolicySwitch != null) {
//            mPolicySwitch.setChecked(mPolicySwitchStatus);
//        }
    }

    private class SndElement {

        public SndElement() {
            idx = "";
            cardName = "";
            hasCapture = false;
            hasPlayback = false;
            devType = AudioCommon.SND_DEV_TYPE_DEFAULT;
        }

        public String getToString(){
            return "SndElement: idx:"+idx
                    + "  cardName:"+cardName
                    + "  hasCapture:"+hasCapture
                    + "  hasPlayback:"+hasPlayback
                    + "  devType:"+devType;
        }

        public String idx;
        public String cardName;
        public boolean hasPlayback;
        public boolean hasCapture;
        public int devType;
    }
}
