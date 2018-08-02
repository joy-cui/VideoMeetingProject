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
import com.suirui.srpaas.video.util.TvToolsUtil;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.adapter.MeetingListAdapter;
import org.suirui.huijian.tv.util.TVStringUtil;
import org.suirui.huijian.tv.view.IMeetingListView;
import org.suirui.srpaas.entry.ConferenceInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/12/10.
 */

public class NoMeetingFragment extends Fragment {
    SRLog log=new SRLog(MeetingFragment.class.getName(), TVAppConfigure.LOG_LEVE);
    RecyclerView mRecyclerView;
    Activity activity=null;
    MeetingListAdapter mAdapter=null;
    List<ConferenceInfo> noMeetList=null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.meeting_list_fragment_layout, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.meeting_list_view);
        noMeetList=new ArrayList<ConferenceInfo>();
        this.activity=this.getActivity();
//        log.E("NoMeetingFragment.....onCreateView.....");
        return view;


    }

    @Override
    public void onStart() {
        super.onStart();
//        log.E("NoMeetingFragment..onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
//        log.E("NoMeetingFragment..onResume");
    }
    //
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//    }


    public void setAdapter(List<ConferenceInfo> list, IMeetingListView IlistView){
      log.E("NoMeetingFragment.....setAdapter....." + TVStringUtil.getListSize(list)+"  noMeetList:"+TVStringUtil.getListSize(noMeetList));
      this.noMeetList=list;
        if(mAdapter==null) {
            mAdapter = new MeetingListAdapter(activity, this.noMeetList,false,IlistView);
            RecyclerView.LayoutManager layoutManager=new GridLayoutManager(this.getActivity(),4);
          if(mRecyclerView!=null) {
              mRecyclerView.setLayoutManager(layoutManager);
              mRecyclerView.setAdapter(mAdapter);
          }
        }else{
            mAdapter.setDatas(noMeetList);
            mAdapter.notifyDataSetChanged();
        }
    }

}
