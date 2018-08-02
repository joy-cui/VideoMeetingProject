package org.suirui.huijian.tv.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suirui.srpaas.base.util.log.SRLog;

import org.greenrobot.eventbus.EventBus;
import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.adapter.MeetingListAdapter;
import org.suirui.huijian.tv.event.UpdateEvent;
import org.suirui.huijian.tv.util.TVStringUtil;
import org.suirui.huijian.tv.view.IMeetingListView;
import org.suirui.srpaas.entry.ConferenceInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/12/10.
 */

public class MeetingFragment extends Fragment {
    SRLog log=new SRLog(MeetingFragment.class.getName(), TVAppConfigure.LOG_LEVE);
    RecyclerView mRecyclerView;
    Activity activity=null;
    MeetingListAdapter  mAdapter=null;
    List<ConferenceInfo> meetList=null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.meeting_list_fragment_layout, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.meeting_list_view);
        this.activity=this.getActivity();
        meetList=new ArrayList<ConferenceInfo>();
        meetList=new ArrayList<ConferenceInfo>();
//        log.E("MeetingFragment...setupWebSocket..onCreateView.....");
        return view;


    }

    @Override
    public void onStart() {
        super.onStart();
//        log.E("MeetingFragment...setupWebSocket..onStart.....");
        EventBus.getDefault().post(new UpdateEvent(UpdateEvent.TypeEvent.GET_MEETINGLIST,null));
    }

    @Override
    public void onResume() {
        super.onResume();
//        log.E("MeetingFragment...setupWebSocket..onResume.....");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        log.E("MeetingFragment...setupWebSocket..oncreate.....");
    }
    //
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//    }


    public void setAdapter(List<ConferenceInfo> list, IMeetingListView IlistView){
       log.E("MeetingFragment.....setAdapter....." + " meetListData: meetlist:" + TVStringUtil.getListSize(meetList)+" list:"+TVStringUtil.getListSize(list));

        this.meetList=list;
        if(mAdapter==null) {
            mAdapter = new MeetingListAdapter(activity, this.meetList,true,IlistView);
            RecyclerView.LayoutManager layoutManager=new GridLayoutManager(this.getActivity(),4);
            if(mRecyclerView!=null) {
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(mAdapter);
            }
        }else{
            mAdapter.setDatas(meetList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
