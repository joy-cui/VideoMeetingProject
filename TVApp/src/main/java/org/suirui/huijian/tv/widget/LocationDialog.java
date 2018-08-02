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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.adapter.AudioAdapter;
import org.suirui.huijian.tv.bean.CamDevice;
import org.suirui.huijian.tv.bean.LocationBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hh on 2018/4/19.
 */

public class LocationDialog extends Dialog {
    private static final SRLog log = new SRLog(LocationDialog.class.getName(), TVAppConfigure.LOG_LEVE);

    private Context mContext;
    private ListView mListView;
    TextView mDisplayTV;
    private LocationAdapter mAdapter;

    private onClickTypeListener mListener = null;

    public void setOnClickTypeListener(onClickTypeListener listener) {
        mListener = listener;
    }

    public interface onClickTypeListener {
        void setOnClickType(int type);
    }

    public LocationDialog(Context context, View displayView) {
        super(context, R.style.BDialog_Slide);
        this.mContext = context;
        init(displayView);
    }

    private void init(View displayView) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.m_location_listview);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        params.width = (int) (getDensityW(mContext) * 0.2);
        params.height = (int) WindowManager.LayoutParams.WRAP_CONTENT;
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
//        int btnPaddingLeft = displayView.getPaddingLeft();
        int btnPaddingLeft = 0;
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
//        log.E("test Window--->" + x + "  " + "Window--->" + y);
//        log.E("test left:" + displayView.getLeft());
//        log.E("test right:" + displayView.getRight());
//        log.E("test Top:" + displayView.getTop());
//        log.E("test Bottom:" + displayView.getBottom());
//        log.E("test getPaddingLeft:" + displayView.getPaddingLeft());
//        log.E("test Width:" + displayView.getWidth());
//        log.E("test Height:"+displayView.getHeight());

//        int winLeft = x + btnW + btnPaddingLeft - winW;
//        int winTop = y;
//        params.x = winLeft - 8;
//        params.y = winTop + btnH / 2 + 7;
//        params.width = btnW;
//        log.E("test winLeft:" + winLeft + "  " + "winTop:" + winTop);

        int winLeft = x;// + btnW + btnPaddingLeft - winW;
        int winTop = y;
        params.x = winLeft;// - 8 ;
        params.y = winTop + btnH;///2 + 7 ;
        params.width = btnW;
//        log.E("test winLeft:" + winLeft + "  " + "winTop:" + winTop);

        window.setAttributes(params);
        setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失


        mListView = (ListView) findViewById(R.id.locationListview);
    }

    @Override
    public void show() {
        super.show();
    }

    public void show(View displayView, final List<LocationBean> dataList, TextView displayTV) {
        super.show();
        mDisplayTV = displayTV;
//        mDatas = new ArrayList<CamDevice>();
//        for ( int i=0; i < 10; i++) {
//            CamDevice camDevice = new CamDevice();
//            camDevice.setName("item"+i);
//            camDevice.setSelectd(true);
//            mDatas.add( camDevice);
//        }
        if (mAdapter == null) {
            mAdapter = new LocationAdapter(getContext(), dataList);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.changeList(dataList);
            mAdapter.notifyDataSetChanged();
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mListener != null){
                    mListener.setOnClickType(position);
                }
                LocationBean location = dataList.get(position);
                log.E("setOnItemClickListener():name:"+location.getName());
                mDisplayTV.setText(location.getName());
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


    class LocationAdapter extends BaseAdapter {
        private Context mContext = null;
        private List<LocationBean> mList;
        private int selectedPos = 0;

        public LocationAdapter(Context ctx, List<LocationBean> data) {
            mContext = ctx;
            this.mList = data;
        }

        public void clear() {
            if (mList == null) {
                return;
            }
            mList.clear();
        }

        public void changeList(List<LocationBean> list) {
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
                        R.layout.m_location_item, parent, false);
                holder = new ViewHolder();
                holder.item = (TextView) convertView.findViewById(R.id.item);
                holder.driver = (View)convertView.findViewById(R.id.itemDriver);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(position == mList.size() -1){
                holder.driver.setVisibility(View.INVISIBLE);
            }
            final LocationBean member = mList.get(position);
            boolean isSelected = false;
            if (member != null) {
                String name = member.getName();
                holder.item.setText(name);

                log.I("Listener：getView " + " name:" + name
                                + "  isSelected:" + isSelected
//					+ "  time:"+time
                );


//            holder.typeTV.setText(meetingName);
            }
//        holder.item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CamDevice cam = mList.get(position);
//                boolean isSelect = cam.isSelectd();
//                cam.setSelectd(!isSelect);
//                setFoucusChangeBg(isSelect,holder);
//            }
//        });
            return convertView;
        }

        class ViewHolder {
            TextView item;
            View driver;
        }

    }

}
