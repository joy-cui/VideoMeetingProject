package org.suirui.huijian.tv.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.suirui.srpaas.base.util.log.SRLog;
import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.bean.CamDevice;

import java.util.List;

/**
 * Created by cui on 2018/4/8.
 */

public class CameraAdapter extends BaseAdapter {
    private static final SRLog log = new SRLog(CameraAdapter.class.getName(), TVAppConfigure.LOG_LEVE);

    private Context mContext = null;
    private List<CamDevice> mList;
    private int selectedPos = 0;

    public CameraAdapter(Context ctx, List<CamDevice> data) {
        mContext = ctx;
        this.mList = data;
    }

    public void clear() {
        if (mList == null) {
            return;
        }
        mList.clear();
    }

    public void changeList(List<CamDevice> list) {
        clear();
        this.mList = list;
    }

    @Override
    public int getCount() {
        if (mList == null || mList.size() <= 0) {
            return 0;
        }
        log.E("getCount():size:"+mList.size());
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
                    R.layout.m_camera_list_item, parent, false);
            holder = new ViewHolder();
            holder.item = convertView.findViewById(R.id.item);
            holder.nameTV = (TextView) convertView
                    .findViewById(R.id.nameTV);
            holder.typeTV = (TextView) convertView
                    .findViewById(R.id.typeTV);
            holder.selectedTV = (TextView) convertView
                    .findViewById(R.id.selectTV);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CamDevice member = mList.get(position);
        boolean isSelected = false;
        if (member != null) {
            String name = member.getName();
            isSelected = member.isSelectd();
            holder.nameTV.setText(name);
            if(isSelected){
                holder.selectedTV.setBackgroundResource(R.drawable.m_btn_default_selected);
            }else {
                holder.selectedTV.setBackgroundResource(R.drawable.m_btn_default_normal);
            }
			log.I("Listener：getView " + " name:" + name
					+ "  isSelected:" + isSelected
//					+ "  time:"+time
					);


//            holder.typeTV.setText(meetingName);
        }
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CamDevice cam = mList.get(position);
                boolean isSelect = cam.isSelectd();
                cam.setSelectd(!isSelect);
                setFoucusChangeBg(isSelect,holder);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        View item;
        TextView nameTV;
        TextView typeTV;
        TextView selectedTV;
    }


    private void setFoucusChangeBg(boolean isSelect,ViewHolder holder){
        if(isSelect){
            holder.selectedTV.setBackgroundResource(R.drawable.m_btn_default_normal);
            return;
        }else {
            holder.selectedTV.setBackgroundResource(R.drawable.m_btn_default_selected);
        }
    }

}
