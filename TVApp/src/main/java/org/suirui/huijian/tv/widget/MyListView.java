package org.suirui.huijian.tv.widget;

import org.suirui.huijian.tv.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MyListView extends ListView implements OnTouchListener {
 private int mLastX = 0;
 private int mLastY = 0;
 private int mDeltaY = 0;
 private boolean mIsFingerReleased = true;
 private int mOverScrollSize = 0;
 private int mYOnOverScroll = 0;
 private NotificationView mNotificationView;
 private boolean mPullDownRefreshEnabled = true;
 private boolean mIsRefreshing = false;
 private boolean mbOverScrolled = false;
 private boolean mbScrollingHorizontal = false;
 private boolean mbFirstMove = false;
 private PullDownRefreshListener mListener;

 public MyListView(Context context, AttributeSet attrs, int defStyle) {
     super(context, attrs, defStyle);
     this.initView(context);
 }

 public MyListView(Context context, AttributeSet attrs) {
     super(context, attrs);
     this.initView(context);
 }

 public MyListView(Context context) {
     super(context);
     this.initView(context);
 }

 public void setPullDownRefreshListener(PullDownRefreshListener l) {
     this.mListener = l;
 }

 public void setTextResources(int textReleaseToRefresh, int textPullDownToRefresh, int textLoading) {
     this.mNotificationView.setTextResources(textReleaseToRefresh, textPullDownToRefresh, textLoading);
 }

 private void initView(Context context) {
     this.setOnTouchListener(this);
     this.mNotificationView = new NotificationView(this.getContext());
//     this.addHeaderView(this.mNotificationView);
     this.mNotificationView.show(false);
 }

 public void removeHeaderView(){
     this.removeHeaderView(this.mNotificationView);
 }

 public void setPullDownRefreshEnabled(boolean enabled) {
     this.mPullDownRefreshEnabled = enabled;
 }

 public boolean isPullDownRefreshEnabled() {
     return this.mPullDownRefreshEnabled;
 }

 protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
 }

 protected void ZM_onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
     if(this.mPullDownRefreshEnabled && !this.mIsRefreshing) {
         int dis = this.mDeltaY / 2;
         this.scrollBy(0, dis);
         if(this.mIsFingerReleased) {
             this.scrollTo(0, 0);
         }

         if(this.mYOnOverScroll == 0) {
             this.mYOnOverScroll = this.mLastY;
         }

     }
 }

 public boolean onTouch(View v, MotionEvent event) {
     if(this.mPullDownRefreshEnabled && !this.mIsRefreshing) {
         int action = event.getActionMasked();
//         Log.d("PullDownRefreshListView", "onTouch, action=%d, mIsFingerReleased=%b, y=%d", new Object[]{Integer.valueOf(action), Boolean.valueOf(this.mIsFingerReleased), Integer.valueOf((int)event.getY())});
         if(action == 2 && this.mIsFingerReleased) {
             action = 0;
         }

         switch(action) {
         case 0:
             this.mLastX = (int)event.getX();
             this.mLastY = (int)event.getY();
             this.mIsFingerReleased = false;
             this.mbScrollingHorizontal = false;
             this.mbFirstMove = true;
             this.mOverScrollSize = 0;
             this.mYOnOverScroll = 0;
             break;
         case 1:
             this.mbFirstMove = true;
             if(this.mbScrollingHorizontal) {
                 this.mbScrollingHorizontal = false;
                 return false;
             }

             this.mLastY = 0;
             this.mIsFingerReleased = true;
             if(this.mNotificationView.isNotificationVisible() && this.mNotificationView.needRefreshOnReleased()) {
                 this.doRefresh();
             } else if(this.mNotificationView.isNotificationVisible()) {
                 this.mNotificationView.show(false);
                 this.mNotificationView.clearStatus();
             }

             if(this.mbOverScrolled) {
                 this.scrollTo(0, 0);
             }

             this.mOverScrollSize = 0;
             this.mYOnOverScroll = 0;
             this.mbOverScrolled = false;
             break;
         case 2:
             if(this.mbScrollingHorizontal) {
                 return false;
             }

             int x = (int)event.getX();
             int y = (int)event.getY();
             this.mDeltaY = this.mLastY - y;
             int deltaX = this.mLastX - x;
             if(this.mbFirstMove && Math.abs(this.mDeltaY) < Math.abs(deltaX)) {
                 this.mbScrollingHorizontal = true;
                 this.mbFirstMove = false;
                 return false;
             }

             this.mbFirstMove = false;
             if(Math.abs(this.mDeltaY) < 4) {
                 return false;
             }

             this.mLastY = y;
             boolean overScrolledTop = this.isOverScrolledTop(this.mDeltaY);
             boolean overScrolledBottom = this.isOverScrolledBottom(this.mDeltaY);
             boolean overscroll = overScrolledTop || overScrolledBottom;
             if(overscroll) {
                 this.ZM_onOverScrolled(this.getScrollX(), this.getScrollY(), false, true);
                 this.mbOverScrolled = true;
             }

             if(this.mYOnOverScroll > 0) {
                 this.mOverScrollSize = y - this.mYOnOverScroll;
                 this.mNotificationView.updateUI((float)this.mOverScrollSize);
                 if(overScrolledTop && !this.mNotificationView.isNotificationVisible()) {
                     this.mNotificationView.show(true);
                     this.scrollTo(0, this.mNotificationView.getNotificationHeight());
                 }

                 if(!overscroll) {
                     this.scrollBy(0, this.mDeltaY / 2);
                 }
             }

             return false;
         }

         return false;
     } else {
         return false;
     }
 }

 protected void onPullDownRefresh() {
 }

 public void showRefreshing(boolean show) {
     if(show) {
         this.mIsRefreshing = true;
         this.mNotificationView.showLoading();
         this.setSelection(0);
         this.scrollTo(0, 0);
     } else {
         this.mIsRefreshing = false;
         this.mNotificationView.clearStatus();
         this.mNotificationView.show(false);
     }

 }

 public boolean isRefreshing() {
     return this.mIsRefreshing;
 }

 public void notifyRefreshDone() {
     this.showRefreshing(false);
 }

 private boolean isOverScrolledTop(int deltaY) {
     if(deltaY < 0) {
         int pos = this.getFirstVisiblePosition();
         if(pos > 0) {
             return false;
         } else {
             View viewFirst = this.getChildAt(0);
             if(viewFirst == null){
            	 return false;
             }
             return viewFirst.getTop() >= 0;
         }
     } else {
         return false;
     }
 }

 private boolean isOverScrolledBottom(int deltaY) {
     if(deltaY > 0) {
         int pos = this.getLastVisiblePosition();
         if(pos < this.getCount() - 1) {
             return false;
         } else {
             View viewLast = this.getChildAt(this.getChildCount() - 1);
             if(viewLast == null){
            	 return false;
             }
             return viewLast.getBottom() <= this.getHeight();
         }
     } else {
         return false;
     }
 }

 private void doRefresh() {
     this.showRefreshing(true);
     if(this.mListener != null) {
         this.mListener.onPullDownRefresh();
     }

     this.onPullDownRefresh();
 }

 static class NotificationView extends LinearLayout {
     static final int MIN_SIZE_TO_REFRESH = 120;
     ImageView mImgIcon = null;
     TextView mTxtMsg = null;
     View mItemContainer = null;
     View mProgressBar = null;
     float mOverScrollSize = 0.0F;
     int mTextReleaseToRefresh;
     int mTextPullDownToRefresh;
     int mTextLoading;

     public NotificationView(Context context) {
         super(context);
         this.mTextReleaseToRefresh = R.string.m_lbl_pull_down_refresh_list_release_to_refresh;
         this.mTextPullDownToRefresh = R.string.m_lbl_pull_down_refresh_list_pull_down_to_refresh;
         this.mTextLoading = R.string.m_lbl_pull_down_refresh_list_loading;
         View.inflate(context, R.layout.m_pull_down_refresh_message, this);
         this.mImgIcon = (ImageView)this.findViewById(R.id.imgIcon);
         this.mTxtMsg = (TextView)this.findViewById(R.id.txtMsg);
         this.mItemContainer = this.findViewById(R.id.itemContainer);
         this.mProgressBar = this.findViewById(R.id.progressBar1);
         this.mProgressBar.setVisibility(View.GONE);
     }

     public void clearStatus() {
         this.mOverScrollSize = 0.0F;
         this.mImgIcon.clearAnimation();
         this.mTxtMsg.setText(this.mTextPullDownToRefresh);
         this.mProgressBar.setVisibility(View.GONE);
         this.mImgIcon.setVisibility(View.VISIBLE);
     }

     public void setTextResources(int textReleaseToRefresh, int textPullDownToRefresh, int textLoading) {
         this.mTextReleaseToRefresh = textReleaseToRefresh;
         this.mTextPullDownToRefresh = textPullDownToRefresh;
         this.mTextLoading = textLoading;
         this.updateUI(this.mOverScrollSize);
     }

     public void showLoading() {
         this.mImgIcon.clearAnimation();
         this.mImgIcon.setVisibility(View.GONE);
         this.mProgressBar.setVisibility(View.VISIBLE);
         this.mTxtMsg.setText(this.mTextLoading);
         this.mItemContainer.setVisibility(View.VISIBLE);
     }

     public void updateUI(float overScrollSize) {
         boolean isOldDown = this.mOverScrollSize < (float)UIUtil.dip2px(this.getContext(), 120.0F);
         this.mOverScrollSize = overScrollSize;
         boolean isNewDown = this.mOverScrollSize < (float)UIUtil.dip2px(this.getContext(), 120.0F);
         if(isOldDown != isNewDown) {
             Animation anim;
             LinearInterpolator lir;
             if(isNewDown) {
                 this.mTxtMsg.setText(this.mTextPullDownToRefresh);
                 anim = AnimationUtils.loadAnimation(this.getContext(), R.anim.m_pull_down_refresh_rotate_to_down);
                 lir = new LinearInterpolator();
                 anim.setInterpolator(lir);
                 anim.setFillAfter(true);
                 this.mImgIcon.startAnimation(anim);
             } else {
                 this.mTxtMsg.setText(this.mTextReleaseToRefresh);
                 anim = AnimationUtils.loadAnimation(this.getContext(), R.anim.m_pull_down_refresh_rotate_to_up);
                 lir = new LinearInterpolator();
                 anim.setInterpolator(lir);
                 anim.setInterpolator(lir);
                 anim.setFillAfter(true);
                 this.mImgIcon.startAnimation(anim);
             }

         }
     }

     public boolean needRefreshOnReleased() {
         return this.mOverScrollSize > (float)UIUtil.dip2px(this.getContext(), 120.0F);
     }

     public boolean isNotificationVisible() {
         return this.mItemContainer.getVisibility() == View.VISIBLE;
     }

     public void setMessage(String msg) {
         this.mTxtMsg.setText(msg);
     }

     public void show(boolean bShow) {
         this.mItemContainer.setVisibility(bShow?View.VISIBLE:View.GONE);
     }

     public int getNotificationHeight() {
         int widthMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);//2147483647;MeasureSpec.AT_MOST -2147483647
         int heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
         this.measure(widthMeasureSpec, heightMeasureSpec);
         return this.getMeasuredHeight();
     }
 }

 public interface PullDownRefreshListener {
     void onPullDownRefresh();
 }
}
