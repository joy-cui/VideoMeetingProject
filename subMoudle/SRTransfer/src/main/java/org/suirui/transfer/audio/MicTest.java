package org.suirui.transfer.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import org.suirui.transfer.listener.AudioListener;

/**
 * Created by hh on 2018/4/2.
 */

/**
 原文中提到“平方和除以数据总长度，得到音量大小”，有些文章中提到这个音量值在不同的手机中表现得不一样，同样的发声，但出来的值相差很大。进而有通过一些计算，调整“音量”的算法，其中有两个，分别是：

 1、计算了噪音,对音量进行调整：
 value 的 值 控制 为 0 到 100 之间 0为最小 》= 100为最大！！
 int value = (int) (Math.abs((int)(v /(float)r)/10000) >> 1);

 2、计算分贝值：

 那个值应该是声音的振幅，并不是音量的大小，
 声音的大小应该是用分贝为单位的吧，
 double dB = 10*Math.log10(v/(double)r);

 即：经傅立叶变化后得到的复数数组是个二维数组，实部和虚部的平方和取对数后乘以10就大致等于我们通常表示音量的分贝了。
 */
public class MicTest extends Thread {
    private AudioRecord ar;
    private int bs;
    private static int SAMPLE_RATE_IN_HZ = 8000;
    private boolean isRun = false;

    public MicTest() {
        super();
        bs = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        ar = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bs);
    }

    public void run() {
        super.run();
        //开始录制音频
        try{
            // 防止某些手机崩溃，例如联想
            if(ar == null){
                ar = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ,
                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, bs);
            }
            ar.startRecording();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
        // 用于读取的 buffer
        byte[] buffer = new byte[bs];
        isRun = true;
        while (isRun) {
            int r = ar.read(buffer, 0, bs);
            int v = 0;
            // 将 buffer 内容取出，进行平方和运算
            for (int i = 0; i < buffer.length; i++) {
                // 这里没有做运算的优化，为了更加清晰的展示代码
                v += buffer[i] * buffer[i];
            }
            // 平方和除以数据总长度，得到音量大小。可以获取白噪声值，然后对实际采样进行标准化。
            // 如果想利用这个数值进行操作，建议用 sendMessage 将其抛出，在 Handler 里进行处理。

            int value = (int) (Math.abs((int)(v /(float)r)/10000) >> 1);
            double dB = 10*Math.log10(v/(double)r);
            Log.d("spl", String.valueOf(v / (float) r) + " value:="+value + " dB:"+dB);
            AudioListener.getInstance().onTestMicNotify((int)dB);
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ar.stop();
        ar.release();
        ar = null;
    }

    public void pause() {
        isRun = false;
    }

    public void start() {
        if (!isRun) {
            super.start();
        }
    }

}