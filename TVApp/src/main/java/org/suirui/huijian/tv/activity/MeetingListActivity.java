package org.suirui.huijian.tv.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suirui.pub.business.util.LoginSharePreferencesUtil;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.util.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.event.UpdateEvent;
import org.suirui.huijian.tv.prestener.impl.MeetingListPrestener;
import org.suirui.huijian.tv.view.IMeetingListView;
import org.suirui.huijian.tv.widget.ShowMeetDetailDialog;
import org.suirui.huijian.tv.widget.ShowMeetingListOptionDialog;
import org.suirui.srpaas.entry.ConferenceInfo;
import org.suirui.srpaas.entry.MeetDetailInfo;

import java.util.ArrayList;
import java.util.List;

public class MeetingListActivity extends FragmentActivity implements View.OnClickListener,View.OnFocusChangeListener,IMeetingListView {
    SRLog log=new SRLog(MeetingListActivity.class.getName(), TVAppConfigure.LOG_LEVE);
    ViewPager viewPager;
    LinearLayout tab_meeting, tab_no_meeting;
    TextView meeting_txt,no_meeting_txt;
    View meeting_view,no_meeting_view;
    EditText search;
    Button back_btn;
    private FragmentPagerAdapter adapter;
    private List<Fragment> listFragment;
    MeetingFragment meetingFragment;
    NoMeetingFragment noMeetingFragment;
    static MeetingListPrestener meetingListPrestener=null;
    boolean isMoveLeft=true;
    boolean isMoveRight=true;
    boolean isMoveTop=false;
    boolean isMeeting=false;
    public static final int GET_MEETING_LIST=100;
    public static final int CLEAR_DATA_MEETINGLIST=101;
    public static final int UPDATE_MEETING_LIST=102;
    IMeetingListView iMeetingListView=null;

    private  Handler mHander=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_MEETING_LIST:
                    log.E("meetingListPrestener...获取会议列表");
                    meetingListPrestener=new MeetingListPrestener(MeetingListActivity.this,iMeetingListView);
                    meetingListPrestener.getMeetingList();
                    break;
                case CLEAR_DATA_MEETINGLIST:
                    if(meetingListPrestener!=null) {
                        meetingListPrestener.clearData();
                    }
                    break;
                    case UPDATE_MEETING_LIST:
                        if(search.getText()!=null || !StringUtil.isEmpty(search.getText().toString())) {
                            log.E("search....List..."+search.getText().toString());
                            meetingFragment.setAdapter(meetingListPrestener.getMeetingList(true,true), iMeetingListView);
                            noMeetingFragment.setAdapter(meetingListPrestener.getMeetingList(false,true), iMeetingListView);
                        }else{
                            log.E("update....List");
                            meetingFragment.setAdapter(meetingListPrestener.getMeetingList(true,false), iMeetingListView);
                            noMeetingFragment.setAdapter(meetingListPrestener.getMeetingList(false,false), iMeetingListView);
                        }
                        break;
                    default:
                        break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.E("EventBus.setupWebSocket.setupWebSocket..onCreate....MeetingListActivity");
        setContentView(R.layout.meeting_list_layout);
        initView();
        initEvent();
        setSelect(0);
        iMeetingListView=this;
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        log.E("EventBus.setupWebSocket.setupWebSocket..onResume....MeetingListActivity");

    }

    private void initView(){
        back_btn=(Button)this.findViewById(R.id.back_btn) ;
        search=(EditText)this.findViewById(R.id.search);
        no_meeting_txt=(TextView)this.findViewById(R.id.no_meeting_txt);
        meeting_txt=(TextView)this.findViewById(R.id.meeting_txt);
        meeting_view=(View)this.findViewById(R.id.meeting_view);
        no_meeting_view=(View)this.findViewById(R.id.no_meeting_view);
        viewPager=(ViewPager)this.findViewById(R.id.view_page);
        tab_meeting=(LinearLayout)this.findViewById(R.id.tab_meeting);
        tab_no_meeting=(LinearLayout)this.findViewById(R.id.tab_no_meeting);


         listFragment = new ArrayList<>();
         meetingFragment=new MeetingFragment();
         noMeetingFragment=new NoMeetingFragment();
        listFragment.add(meetingFragment);
        listFragment.add(noMeetingFragment);
        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return listFragment.get(position);
            }

            @Override
            public int getCount() {
                return listFragment.size();
            }
        };
        viewPager.setAdapter(adapter);
    }

    private void initEvent() {
        meeting_txt.setOnClickListener(this);
        no_meeting_txt.setOnClickListener(this);
        meeting_txt.setOnFocusChangeListener(this);
        no_meeting_txt.setOnFocusChangeListener(this);
        back_btn.setOnClickListener(this);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                setTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    isMoveTop=false;
                }
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                log.E("addTextChangedListener....."+s.toString());

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                log.E("addTextChangedListener......"+s.toString());
                if(meetingListPrestener!=null) {
                    if (s != null && s.toString().length() > 0) {
                        meetingListPrestener.search(s.toString());
                    } else {
                        meetingListPrestener.getMeetingList();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

//                log.E("addTextChangedListener......"+s.toString());

            }
        });
    }

    private void setTab(int i) {
        rest();
        switch (i){
            case 0:
                meeting_txt.setFocusable(true);
                meeting_txt.setTextColor(getResources().getColor(R.color.white_color));
                meeting_txt.setBackground(getDrawable(R.drawable.txt_long_bg));
                meeting_view.setVisibility(View.VISIBLE);
                meeting_txt.requestFocus();
                break;
            case 1:
                no_meeting_txt.setFocusable(true);
                no_meeting_txt.setTextColor(getResources().getColor(R.color.white_color));
                no_meeting_txt.setBackground(getDrawable(R.drawable.txt_long_bg));
                no_meeting_view.setVisibility(View.VISIBLE);
                no_meeting_txt.requestFocus();

                break;

        }
    }
    private void rest(){
        isMoveRight=true;
        isMoveLeft=true;
        isMoveTop=false;
        isMeeting=false;
        no_meeting_view.setVisibility(View.INVISIBLE);
        no_meeting_txt.setBackground(null);
        no_meeting_txt.setTextColor(getResources().getColor(R.color.tv_txt_font));
        meeting_view.setVisibility(View.INVISIBLE);
        meeting_txt.setBackground(null);
        meeting_txt.setTextColor(getResources().getColor(R.color.tv_txt_font));
    }

    private void setSelect(int i) {
         setTab(i);
        viewPager.setCurrentItem(i);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                this.finish();
                break;

                default:
                    setFocus(v);
                    break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
            setFocus(v);
        }
    }
    private void setFocus(View v){
        switch (v.getId()){
            case R.id.meeting_txt :
                setSelect(0);
                break;
            case R.id.no_meeting_txt :
                setSelect(1);
                break;


        }
    }

    @Override
    public void loadMeeting() {
        log.E("会议加载中");
    }

    /**
     * 获取会议列表
     * @param meetingList
     * @param noMeetingList
     */
    @Override
    public void onSuccess(List<ConferenceInfo> meetingList,List<ConferenceInfo> noMeetingList) {
        meetingFragment.setAdapter(meetingList,this);
        noMeetingFragment.setAdapter(noMeetingList,this);
    }

    /**
     * 获取会议列表返回失败
     * @param msg
     */
    @Override
    public void onFailer(String msg) {
        log.E("会议列表获取失败");

    }
    ShowMeetingListOptionDialog dialog;
    @Override
    public void showMeetListDialog(final MeetDetailInfo conferenceInfo) {
        try {
            log.E("setupWebSocket...showMeetListDialog..." + conferenceInfo.getConfId());

            dialog = new ShowMeetingListOptionDialog(this);
            dialog.show();
            dialog.setClicklistener(new ShowMeetingListOptionDialog.ClickListenerInterface() {
                @Override
                public void clickButton1() {
//                Toast.makeText(MeetingListActivity.this,"加入会议",Toast.LENGTH_SHORT).show();
                    ThirdApi.getIntance(MeetingListActivity.this).joinMeeting(MeetingListActivity.this, TVAppConfigure.pass_url_root, TVAppConfigure.appId, TVAppConfigure.secretKey, ThirdApi.getIntance(MeetingListActivity.this).getSharePreferDoMain(TVAppConfigure.DO_MAIN), TVAppConfigure.CONNECT_JOIN_URL, LoginSharePreferencesUtil.getLoginSuid(MeetingListActivity.this), LoginSharePreferencesUtil.getLoginNikeName(MeetingListActivity.this), conferenceInfo.getSubject(), conferenceInfo.getConfPwd());
                    dialog.dismiss();
                }

                @Override
                public void clickButton2() {

//                Toast.makeText(MeetingListActivity.this,"会议详情...getConfName"+conferenceInfo.getConfName()+" getSubject: "+conferenceInfo.getSubject()+" getConfId: "+conferenceInfo.getConfId(),Toast.LENGTH_LONG).show();
                    ShowMeetDetailDialog detailDialog = new ShowMeetDetailDialog(dialog.getContext(), conferenceInfo);
                    detailDialog.show();
                    dialog.dismiss();

                }
            });
        }catch (Exception e){
            log.E("setupWebSocket...showMeetListDialog.  exception.."+e.toString());
        }

    }

    @Override
    public void showSeachMeetList(List<ConferenceInfo> searchList, List<ConferenceInfo> searchNoMeetList) {
        if(search.getText()!=null) {
            log.E("showSeachMeetList..." );
            meetingFragment.setAdapter(searchList, this);
            noMeetingFragment.setAdapter(searchNoMeetList, this);
        }
    }

    @Override
    public void moveFocus(boolean isMoveLeft, boolean isMoveRight) {
        this.isMoveLeft=isMoveLeft;
        this.isMoveRight=isMoveRight;
    }

    @Override
    public void moveTopFocus(boolean isTop,boolean isMeeting) {
        this.isMoveTop=isTop;
        this.isMeeting=isMeeting;
        log.E("dispatchKeyEvent...moveTopFocus:"+isTop);
    }

    long preTime=0;
    boolean isOnKey=false;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        log.E("dispatchKeyEvent..dispatchKeyEvent....."+event.getKeyCode()+" isMoveTop:"+isMoveTop+" isMoveLeft:"+isMoveLeft+" isMoveRight: "+isMoveRight+" getId:"+this.getCurrentFocus().getId()+" : "+R.id.meeting_txt);

        boolean isDispath=false;
        try {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if(onKey()){
                        isDispath= true;
                    }else {
                        if (!isMoveLeft || this.getCurrentFocus().getId() == R.id.meeting_txt) {//不可左移动
                            isDispath = true;
//                    log.E("dispatchKeyEvent..不可左移动..isMoveLeft....");
                        }
                    }

                    break;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if(onKey()){
                        isDispath= true;
                    }else {
                        if (!isMoveRight || this.getCurrentFocus().getId() == R.id.search) {//不可右移动
                            isDispath = true;
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP://朝上
                    if(onKey()){
                        isDispath= true;
                    }else {
                        if (this.getCurrentFocus().getId() == R.id.meeting_txt || this.getCurrentFocus().getId() == R.id.no_meeting_txt) {
                            isDispath = true;
                        } else {
                            if (isMoveTop) {//已经到顶了
                                if (isMeeting) {
                                    setSelect(0);
                                } else {
                                    setSelect(1);
                                }
                                isDispath = true;
                            }
                        }
                    }
                    break;
                default:

                    break;


            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        if(isDispath){
            return true;
        }else {
            return super.dispatchKeyEvent(event);
        }
    }

    private boolean  onKey(){
        if(!isOnKey) {
            isOnKey = true;
            return false;
        }else {
            isOnKey=false;
            return true;
        }
    }
    @Subscribe
    public void onEvent(UpdateEvent event) {
       log.E( "setupWebSocket  MeetingListActivity...update view: ");
        if(event!=null){
            switch (event.getType()){
                case UpdateEvent.TypeEvent.GET_MEETINGLIST://获取会议列表
                    mHander.sendEmptyMessage(GET_MEETING_LIST);
                    break;
                case UpdateEvent.TypeEvent.UPDATE_MEETINGLIST:
                    log.E( "setupWebSocket  MeetingListActivity...更新列表。。。update view: ");
                    mHander.sendEmptyMessage(UPDATE_MEETING_LIST);

                    break;

            }


        }


    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log.E("onDestory.MeetListActivity...setupWebSocket");
        mHander.sendEmptyMessage(CLEAR_DATA_MEETINGLIST);
        EventBus.getDefault().unregister(this);
    }
}
