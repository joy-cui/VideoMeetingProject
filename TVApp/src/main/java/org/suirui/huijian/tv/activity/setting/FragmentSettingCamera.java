package org.suirui.huijian.tv.activity.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.suirui.srpaas.base.util.log.SRLog;
import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.adapter.CameraAdapter;
import org.suirui.huijian.tv.bean.CamDevice;

import java.util.ArrayList;


/**
 * Created by hh on 2018/4/16.
 */
public class FragmentSettingCamera extends BaseFragment {
    private static final SRLog log = new SRLog(FragmentSettingCamera.class.getName(), TVAppConfigure.LOG_LEVE);

    private GridView mListView;
    private CameraAdapter mAdapter;
    private View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSt) {
        log.E("onCreateView()");
        View view = inflater.inflate(R.layout.m_setting_camera_layout, null);
        view.requestFocus();
        mListView = (GridView) view.findViewById(R.id.cameraListview);
        mView = view;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        changeTitle(getString(R.string.m_setting_camera));
        showData();
        log.E("onResume():::mListview:"+mListView.isFocusable());
        mView.requestFocus();
        log.E("onResume():::mView:1"+mView.isFocusable());

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAdapter != null){
            mAdapter = null;
        }
    }

    private void showData() {
        log.E("showData():");
        ArrayList mDatas = new ArrayList<CamDevice>();
        for ( int i=0; i < 10; i++) {
            CamDevice camDevice = new CamDevice();
            camDevice.setName("item"+i);
            camDevice.setSelectd(true);
            mDatas.add( camDevice);
        }
        if (mAdapter == null) {
            log.E("showData():11111");
            mAdapter = new CameraAdapter(getContext(), mDatas);
            mListView.setAdapter(mAdapter);
        } else {
            log.E("showData():2222222");
            mAdapter.changeList(mDatas);
            mAdapter.notifyDataSetChanged();
        }

        mListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }
            }
        });

    }
}
