package org.suirui.huijian.box.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.contant.Configure;

import org.suirui.huijian.box.AppConfigure;
import org.suirui.huijian.box.R;
import org.suirui.huijian.box.bean.HistoryBean;

import java.util.List;

/**
 * @authordingna
 * @date2017-06-09
 **/
public class HistoryAdapter extends BaseAdapter {
    private static final SRLog log = new SRLog(HistoryAdapter.class.getName(), AppConfigure.LOG_LEVE);
    private List<HistoryBean> confid_list_;
    private LayoutInflater inflater = null;

    public HistoryAdapter(Context mContext, List<HistoryBean> confid_list_) {
        this.confid_list_ = confid_list_;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        if (confid_list_ == null || confid_list_.size() <= 0)
            return 0;
        return confid_list_.size();
    }

    @Override
    public Object getItem(int position) {
        return confid_list_.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.hj_history_item_layout, null);
            holder = new ViewHolder();
            holder.hj_tv_history_confid = (TextView) view.findViewById(R.id.hj_tv_history_confid);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        HistoryBean history = confid_list_.get(position);
        if (history != null) {
            holder.hj_tv_history_confid.setText(history.getConfId());
        }
        return view;
    }

    public class ViewHolder {
        TextView hj_tv_history_confid;
    }
}
