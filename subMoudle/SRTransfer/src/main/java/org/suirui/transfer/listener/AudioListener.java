package org.suirui.transfer.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
public class AudioListener {
    private static AudioListener mAudioListener;

    private List<ITestMicListener> mITestMicListeners = new ArrayList<ITestMicListener>();

    public synchronized static AudioListener getInstance(){
        if(mAudioListener == null){
            mAudioListener = new AudioListener();
        }
        return mAudioListener;
    }



    public interface ITestMicListener {
        void onTestMicNotify(int volume);
    }

    public void addTestMicListener(ITestMicListener listener){
        mITestMicListeners.add(listener);
    }

    public void removTestMicListener(ITestMicListener listener) {
        Iterator iterator = this.mITestMicListeners.iterator();

        ITestMicListener iListener;
        do {
            if (!iterator.hasNext()) {
                return;
            }

            iListener = (ITestMicListener) iterator.next();
        } while (iListener != listener);

        iterator.remove();
    }

    public void onTestMicNotify(int volume){
        Iterator var1 = this.mITestMicListeners.iterator();

        while (var1.hasNext()) {
            ITestMicListener listener = (ITestMicListener) var1.next();
            listener.onTestMicNotify(volume);
        }
    }
}