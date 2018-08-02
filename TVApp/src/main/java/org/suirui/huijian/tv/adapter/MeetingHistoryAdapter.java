package org.suirui.huijian.tv.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.util.StringUtil;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.bean.HistoryBean;

import java.util.List;

/**
 * Created by cui on 2018/4/8.
 */

public class MeetingHistoryAdapter extends BaseAdapter {
    private static final SRLog log = new SRLog(MeetingHistoryAdapter.class.getName(), TVAppConfigure.LOG_LEVE);

    private Context mContext = null;
    private List<HistoryBean> mList;
    private int selectedPos = 0;

    public MeetingHistoryAdapter(Context ctx, List<HistoryBean> data) {
        mContext = ctx;
        this.mList = data;
    }

    public void clear() {
        if (mList == null) {
            return;
        }
        mList.clear();
    }

    public void changeList(List<HistoryBean> list) {
        clear();
        this.mList = list;
    }

    @Override
    public int getCount() {
        if (mList == null || mList.size() <= 0) {
            return 0;
        }
        log.E("getCount():size:" + mList.size());
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.m_meeting_history_list_item_view, parent, false);
            holder = new ViewHolder();
            holder.item = convertView.findViewById(R.id.item);
            holder.meetTime = (TextView) convertView
                    .findViewById(R.id.meetTime);
            holder.meetTimeDec = (TextView) convertView
                    .findViewById(R.id.meetTimeDec);
            holder.meetName = (TextView) convertView
                    .findViewById(R.id.meetName);
            holder.meetId = (TextView) convertView.findViewById(R.id.meetId);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HistoryBean member = mList.get(position);
        if (member != null) {
            String startTime = member.getCreateTime();
            String duration = member.getDuration();
            String meetingName = member.getMeetName();
            String meetingNumber = member.getConfId();
//            Date startTimeDate = TimeFormatUtil
//                    .formatStringToDate(TimeFormatUtil
//                            .formatTimeZoneToDate(startTime));
//            String time = TimeUtil.formatTime(mContext,
//                    startTimeDate);
            log.I("Listener：getView " + " meetingName:" + meetingName
                            + "  meetingNumber:" + meetingNumber + "  duration:"
                            + duration
//                          + "  time:"+time
            );

            holder.meetTime.setText(startTime);
            holder.meetName.setText(meetingName);
            holder.meetId.setText(meetingNumber);
            holder.meetTimeDec.setText(StringUtil.isEmpty(duration) ? "- : - : -" : duration);
        }
//        if(selectedPos == position){
//            holder.item.setBackgroundResource(R.color.setting_list_selected);
//            if(position == 0){
//                SZRCMeetingListItem sMeetingItem = mList.get(position);
//                if (mListener != null) {
//                    mListener.setOnShowFirstMeetingItemInfo(sMeetingItem);
//                }
//            }
//        }else{
//            holder.item.setBackgroundResource(android.R.color.transparent);
//        }

        return convertView;
    }

    static class ViewHolder {
        View item;
        TextView meetTime;
        TextView meetTimeDec;
        TextView meetName;
        TextView meetId;
    }

}
