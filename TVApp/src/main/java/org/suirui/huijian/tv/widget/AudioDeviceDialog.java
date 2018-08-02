package org.suirui.huijian.tv.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.adapter.AudioAdapter;
import org.suirui.huijian.tv.bean.CamDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hh on 2018/4/19.
 */

public class AudioDeviceDialog extends Dialog {
    private Context mContext;
    private ListView mListView;
    private String mAccountName;
    TextView mDisplayTV;
    private ArrayList<CamDevice> mDatas;
    private AudioAdapter mAdapter;

    public AudioDeviceDialog(Context context,View displayView) {
        super(context, R.style.BDialog_Slide);
        this.mContext = context;
        init(displayView);
    }
    private void init(View displayView) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.m_audio_device_listview);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        window .setGravity(Gravity.LEFT | Gravity.TOP);
        params.width = (int) (getDensityW(mContext) * 0.2);
        params.height = (int) (getDensityH(mContext) * 0.4);
        params.alpha = 1.0f; // 设置本身透明度
        params.dimAmount = 0.0f; // 设置黑暗度
        float btnX = displayView.getX();
        float btnY = displayView.getY();
        int left = displayView.getLeft();
        float PivotX = displayView.getPivotX();
//			log.E("xxx   btnX:"+btnX + "  btnY: "+btnY + "  left:"+left + "  PivotX:"+PivotX);
        int[] location = new int[2];
        displayView.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int btnW = displayView.getWidth();
        int btnH = displayView.getHeight();
        int btnPaddingLeft = displayView.getPaddingLeft();
        int screenW = getDensityW(mContext);
        int screenH = getDensityH(mContext);
        int winW = params.width;
        int winH = params.height;
//			 log.E("test Screenx--->" + x + "  " + "Screeny--->" + y
//			 + "  params.width:" + params.width + "  params.height:"
//			 + params.height + "  screenW:" + screenW + "  screenH:"
//			 + screenH);
        displayView.getLocationInWindow(location);
        x = location[0];
        y = location[1];
//			 log.E("test Window--->" + x + "  " + "Window--->" + y);
//			 log.E("test left:" + displayView.getLeft());
//			 log.E("test right:" + displayView.getRight());
//			 log.E("test Top:" + displayView.getTop());
//			 log.E("test Bottom:" + displayView.getBottom());
//			 log.E("test getPaddingLeft:" + displayView.getPaddingLeft());
//			 log.E("test Width:"+displayView.getWidth());
//			 log.E("test Height:"+displayView.getHeight());

        int winLeft = x + btnW + btnPaddingLeft - winW;
        int winTop = y;
        params.x = winLeft - 8 ;
        params.y = winTop + btnH/2 + 7 ;
        params.width = btnW;
//			log.E("test winLeft:" + winLeft + "  " + "winTop:" + winTop);
        window.setAttributes(params);
        setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失


        mListView = (ListView) findViewById(R.id.audiodeviceListview);
    }

    @Override
    public void show() {
        super.show();
    }

    public void show(View displayView, List<Object> supDiaplayList,  TextView displayTV) {
        super.show();
        mDisplayTV = displayTV;
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CamDevice device = mDatas.get(position);
                mDisplayTV.setText(device.getName());
                dismiss();
            }
        });
    }

    public int getDensityW(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.widthPixels;
    }

    public int getDensityH(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.heightPixels;
    }

}
