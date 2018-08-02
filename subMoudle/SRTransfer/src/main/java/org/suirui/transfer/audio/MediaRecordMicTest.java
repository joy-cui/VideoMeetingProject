package org.suirui.transfer.audio;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.suirui.srpaas.base.util.BaseUtil;

import org.suirui.transfer.listener.AudioListener;

import java.io.File;
import java.io.IOException;

/**
 * Created by hh on 2018/6/1.
 */

public class MediaRecordMicTest {

    private final String TAG = "MediaRecord";

    private MediaRecorder mMediaRecorder;

    public static final int MAX_LENGTH = 1000 * 60 * 10;// 最大录音时长1000*60*10;

    private String filePath;
    private static final String dirName = "/org.suirui.huijian/mic/";
    private static final String fileName = "testMic";

    public MediaRecordMicTest() {

        this.filePath = saveFile(dirName,fileName);

    }


    public MediaRecordMicTest(File file) {

        this.filePath = file.getAbsolutePath();

    }


    private long startTime;

    private long endTime;


    /**
     * 开始录音 使用amr格式
     * <p>
     * <p>
     * <p>
     * 录音文件
     *
     * @return
     */

    public void startRecord() {

        // 开始录音

 /* ①Initial：实例化MediaRecorder对象 */

        if (mMediaRecorder == null)

            mMediaRecorder = new MediaRecorder();

        try {

  /* ②setAudioSource/setVedioSource */

            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风

  /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */

            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);

        /*

  * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式

  * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)

  */

            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);



  /* ③准备 */

            mMediaRecorder.setOutputFile(filePath);

            mMediaRecorder.setMaxDuration(MAX_LENGTH);

            mMediaRecorder.prepare();

  /* ④开始 */

            mMediaRecorder.start();

            // AudioRecord audioRecord.

  /* 获取开始时间* */

            startTime = System.currentTimeMillis();

            updateMicStatus();

//            Log.i("ACTION_START", "wwww--startTime" + startTime);

        } catch (IllegalStateException e) {

            Log.e(TAG, "wwww--call startAmr(File mRecAudioFile) failed!"

                            + e.getMessage());

        } catch (IOException e) {

            Log.e(TAG, "wwww--call startAmr(File mRecAudioFile) failed!"

                            + e.getMessage());

        }

    }


    /**
     * 停止录音
     */

    public long stopRecord() {

        if (mMediaRecorder == null)

            return 0L;

        endTime = System.currentTimeMillis();

//        Log.e("ACTION_END", "wwww--endTime" + endTime);

        mMediaRecorder.stop();

        mMediaRecorder.reset();

        mMediaRecorder.release();

        mMediaRecorder = null;

//        Log.e("ACTION_LENGTH", "wwww--Time" + (endTime - startTime));

        return endTime - startTime;

    }


    private final Handler mHandler = new Handler();

    private Runnable mUpdateMicStatusTimer = new Runnable() {

        public void run() {

            updateMicStatus();

        }

    };


    /**
     * 更新话筒状态
     */

    private int BASE = 1;

    private int SPACE = 100;// 间隔取样时间


    private void updateMicStatus() {

        if(mMediaRecorder != null) {

            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;

            double db = 0;// 分贝

            if (ratio > 1)

                db = 20 * Math.log10(ratio);

//            Log.e(TAG, "wwww--分贝值：" + db);
            AudioListener.getInstance().onTestMicNotify((int)db);
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);

        }

    }



    public static String saveFile(String dirName,String fileName) {
        try {
            if (BaseUtil.checkSDCard()) {
                long available = BaseUtil.getAvailableExternalMemorySize();
                if (available < 1024 * 1024) {
//                    Toast.makeText(mContext, "没有SD卡", Toast.LENGTH_SHORT).show();
                    return null;
                }
                String dirPath = Environment.getExternalStorageDirectory()
                        .getPath() + dirName;
                if (dirPath == null) {
                    return null;
                }
                File file = new File(dirPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (fileName == null || fileName.equals("")) {
                    return null;
                }
                String filePath = file.getAbsolutePath() + "/" + fileName;
//                Log.e("", "wwww----filePath:" + filePath);
                return filePath;
            } else {
//                Toast.makeText(mContext, "空间不足", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
