package org.suirui.huijian.box.dialog;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.huijian.box.AppConfigure;
import org.suirui.huijian.box.R;
import org.suirui.huijian.box.adapter.HistoryAdapter;
import org.suirui.huijian.box.bean.HistoryBean;
import org.suirui.huijian.box.manage.SRMiddleManage;
import org.suirui.huijian.box.util.HistoryUtil;

import java.util.List;

/**
 * 历史记录弹框
 *
 * @authordingna
 * @date2017-06-09
 **/
public class HistoryDialog extends PopupWindow implements View.OnClickListener {
    private static final SRLog log = new SRLog(HistoryDialog.class.getName(), AppConfigure.LOG_LEVE);
    private Context mContext;
    private ClickListenerInterface clickListener;
    private ListView hj_history_list;
    private HistoryBean history;

    public HistoryDialog(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.hj_history_layout, null);
        findview(view);
        this.setContentView(view);
        this.setWidth(ActionBar.LayoutParams.MATCH_PARENT);
        this.setHeight(ActionBar.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);//点击外部消失
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
    }

    private void findview(View view) {
        TextView hj_tv_cancel = (TextView) view.findViewById(R.id.hj_tv_cancel);
        TextView hj_tv_sure = (TextView) view.findViewById(R.id.hj_tv_sure);
        hj_history_list = (ListView) view.findViewById(R.id.hj_history_list);
        hj_tv_cancel.setOnClickListener(this);
        hj_tv_sure.setOnClickListener(this);
        initHistoryData();
    }

    private void initHistoryData() {
        String suid = SRMiddleManage.getInstance().getSuid();
        List<HistoryBean> confid_list_ = HistoryUtil.getInstance(mContext).getHistoryListBySuid(suid);
        if (confid_list_ == null || confid_list_.size() <= 0)
            return;
        HistoryAdapter mAdapter = new HistoryAdapter(mContext, confid_list_);
        hj_history_list.setAdapter(mAdapter);
        final List<HistoryBean> confid_list = confid_list_;
        hj_history_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                history = confid_list.get(position);
            }
        });
        setListViewHeight(mAdapter);
    }

    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        } else {
            this.dismiss();
        }
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListener = clickListenerInterface;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.hj_tv_cancel) {
            if (clickListener != null) {
                clickListener.onCancel();
            }

        } else if (v.getId() == R.id.hj_tv_sure) {
            if (clickListener != null) {
                clickListener.onComplete(history);
                clickListener.onCancel();
            }
        }
    }

    //根据item的高度，设置只显示5条数据
    public void setListViewHeight(HistoryAdapter mAdapter) {
        if (mAdapter == null || mAdapter.getCount() <= 0)
            return;
        View listItem = mAdapter.getView(0, null, hj_history_list);
        listItem.measure(0, 0); // 计算子项View 的宽高
        int list_child_item_height = listItem.getMeasuredHeight() + hj_history_list.getDividerHeight();
        //设置listview的高度
        ViewGroup.LayoutParams params = hj_history_list.getLayoutParams();
        params.height = list_child_item_height * 5;
        hj_history_list.setLayoutParams(params);
        log.E("HistoryDialog....onItemClick...list_child_item_height:" + list_child_item_height);
    }

    public interface ClickListenerInterface {
        /**
         * 取消
         */
        void onCancel();

        /**
         * 完成
         *
         * @param history
         */
        void onComplete(HistoryBean history);
    }
}
