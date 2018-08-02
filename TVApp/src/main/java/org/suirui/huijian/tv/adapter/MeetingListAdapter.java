package org.suirui.huijian.tv.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.util.StringUtil;
import com.suirui.srpaas.video.widget.dialog.ToastCommom;
import com.suirui.srpaas.video.widget.dialog.ToastDialog;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.prestener.impl.MeetingListPrestener;
import org.suirui.huijian.tv.view.IMeetingListView;
import org.suirui.srpaas.entry.ConferenceInfo;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by cui on 2018/4/8.
 */

public class MeetingListAdapter   extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    SRLog log=new SRLog(this.getClass().getName(), TVAppConfigure.LOG_LEVE);
    List<ConferenceInfo> datas=null;
    Context context=null;
    ConferenceInfo conferenceInfo=null;
    boolean isMeeting=false;
    IMeetingListView iMeetingListView;
    MeetingListPrestener meetingListPrestener;



    public MeetingListAdapter(Context context, List<ConferenceInfo> lists,boolean isMeeting,IMeetingListView IlistView) {
        this.context = context;
        this.datas=lists;
        this.isMeeting=isMeeting;
        this.iMeetingListView=IlistView;
        meetingListPrestener=new MeetingListPrestener(context,iMeetingListView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(context).inflate(R.layout.meeting_list_item_layout,parent, false);
//        log.E("onCreateViewHolder.....");
        return new MeetingViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        log.E("onBindViewHolder.....");
        ((MeetingViewHolder) holder).setData(position);
    }
    public void setDatas(List<ConferenceInfo> lists){
            this.datas=lists;
    }
    @Override
    public int getItemCount() {
        return datas != null ? datas.size() :  0;
    }
    public class MeetingViewHolder extends RecyclerView.ViewHolder{
        LinearLayout meeting_item_layout;
        private  TextView list_index,subjectTxt,confid_txt,start_time,meetgingstatus,master;

        public MeetingViewHolder(View itemView) {
            super(itemView);
            meeting_item_layout=(LinearLayout)itemView.findViewById(R.id.meeting_item_layout);
            list_index = (TextView) itemView.findViewById(R.id.list_index);
            subjectTxt=(TextView)itemView.findViewById(R.id.subject);
            confid_txt=(TextView)itemView.findViewById(R.id.confid_txt);
            start_time=(TextView)itemView.findViewById(R.id.start_time);
            meetgingstatus=(TextView)itemView.findViewById(R.id.meetgingstatus);
            master=(TextView)itemView.findViewById(R.id.master);
            if(isMeeting){
                meetgingstatus.setVisibility(View.VISIBLE);
            }else{
                meetgingstatus.setVisibility(View.GONE);
            }


        }
        public void setData(final int position) {
             conferenceInfo=datas.get(position);
             if(conferenceInfo!=null){
                 list_index.setText(position+1+"");
                 log.E("conferenceInfo..setData: "+"getConfName : "+conferenceInfo.getConfName()+" getSubject: "+conferenceInfo.getSubject()+"  nickName : "+conferenceInfo.getNickname()+" getEndTime:"+conferenceInfo.getEndTime()+" getStartTime: "+conferenceInfo.getStartTime());
                 String confName=conferenceInfo.getConfName();
                 if(StringUtil.isEmpty(confName)){
                     confName=conferenceInfo.getSubject();
                 }
//                 String sTime=conferenceInfo.getStartTime();
//                 String eTime=conferenceInfo.getEndTime();
//                 if(StringUtil.isEmpty(sTime) && sTime.length()>16){
//                     sTime.substring(0,16);
//                 }
                 subjectTxt.setText(confName);
                 confid_txt.setText(conferenceInfo.getSubject());
                 master.setText(context.getResources().getString(R.string.hj_master)+conferenceInfo.getNickname());
                 start_time.setText(dateToString(conferenceInfo.getStartTime()));//+"~"+dateToString(conferenceInfo.getEndTime())
                 final String subject=conferenceInfo.getSubject();
                 final String confId=conferenceInfo.getConfId();
                 meeting_item_layout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                     @Override
                     public void onFocusChange(View v, boolean hasFocus) {
                         //焦点
                         if(hasFocus ) {
                            meetingListPrestener.onFoucus(isMeeting,position);
                         }

                     }
                 });
                 meeting_item_layout.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         log.E("iMeetingListView...."+iMeetingListView+" subject:"+subject+" : "+confId);
                           if(iMeetingListView!=null) {
                               meetingListPrestener.getMeetDetail(confId);
                           }

                     }
                 });
             }
        }

        public String dateToString(String dateTime) {
            String dateStr ="";
//            log.E("dateToString..."+dateTime);
            try {

                if (!StringUtil.isEmpty(dateTime)) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                    ParsePosition pos = new ParsePosition(0);
                    Date strtodate = formatter.parse(dateTime);
                    dateStr = dateToStrLong(strtodate);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return dateStr;
        }

        public  String dateToStrLong(java.util.Date dateDate) {
            SimpleDateFormat formatter;
            if(isNow(dateDate)){//今天的会议
                formatter = new SimpleDateFormat("HH:mm");
            }else if(isNowYears(dateDate)){//今年的会议
                formatter = new SimpleDateFormat("MM-dd HH:mm");
            }else{
                formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            }
                String dateString = formatter.format(dateDate);
               return dateString;
        }
        /**
         * 判断时间是不是今天
         * @param date
         * @return    是返回true，不是返回false
         */
        private  boolean isNow(Date date) {
            //当前时间
            Date now = new Date();
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
            //获取今天的日期
            String nowDay = sf.format(now);
            log.E("isNow....nowDay:  "+nowDay);


            //对比的时间
            String day = sf.format(date);
            log.E("isNow....day:  "+day);
            return day.equals(nowDay);



        }
        private  boolean isNowYears(Date date) {
            //当前时间
            Date now = new Date();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy");
            //获取今天的日期
            String nowDay = sf.format(now);


            //对比的时间
            String day = sf.format(date);

            return day.equals(nowDay);

        }
    }
}
