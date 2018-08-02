package org.suirui.huijian.box.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.suirui.srpaas.base.util.CommonUtils;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.contant.Configure;
import com.suirui.srpaas.video.model.impl.ConfigureModelImpl;
import com.suirui.srpaas.video.util.StringUtil;

import org.suirui.huijian.box.AppConfigure;
import org.suirui.huijian.box.bean.HistoryBean;
import org.suirui.srpaas.entry.MeetingInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 近10条历史记录
 *
 * @authordingna
 * @date2017-08-10
 **/
public class HistoryUtil {
    private static String TAG = HistoryUtil.class.getName();
    private static final SRLog log = new SRLog(TAG,AppConfigure.LOG_LEVE);
    private static HistoryUtil instance;
    private static Context context;

    public HistoryUtil() {
    }

    public static HistoryUtil getInstance(Context mContext) {
        context = mContext.getApplicationContext();
        if (instance == null) {
            instance = new HistoryUtil();
        }
        return instance;
    }

    //保存临时的会议号
    public void saveTempJoinConfid(String confid, String suid) {
        //保存临时的历史记录
        SharedPreferences userPf = context.getSharedPreferences(ConfigureModelImpl.SPData.SPDATA, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = userPf.edit();
        editor.putString(Configure.Meet.TEMP_CONFID, confid);
        editor.putString(Configure.Meet.TEMP_SUID, suid);
        editor.commit();
    }

    //保存正式的会议历史记录
    public void saveHistoryMeetBySuid(MeetingInfo meetingInfo, String suid) {
        if (meetingInfo == null || StringUtil.isEmpty(suid)) return;
        List<HistoryBean> confid_list_ = new ArrayList<>();//正式的会议列表
        List<HistoryBean> temp_confid_list_ = new ArrayList<>();
        List<HistoryBean> sameConfid_list = new ArrayList<>();
        List<HistoryBean> diffConfid_list = new ArrayList<>();
        HistoryBean history = null;
        SharedPreferences userPf = context.getSharedPreferences(ConfigureModelImpl.SPData.SPDATA, Context.MODE_MULTI_PROCESS);
        String tempConfid = userPf.getString(Configure.Meet.TEMP_CONFID, "");
        String tempSuid = userPf.getString(Configure.Meet.TEMP_SUID, "");
        log.E("历史记录....saveHistoryMeetBySuid...tempConfid:" + tempConfid + " tempSuid:" + tempSuid + " suid:" + suid);
        StringUtil.writeToFile(TAG, "历史记录....saveHistoryMeetBySuid...tempConfid:" + tempConfid + "...getM_subject" + meetingInfo.getM_subject() + " tempSuid:" + tempSuid + " suid:" + suid);
        String confid_list_str_ = userPf.getString(Configure.Meet.CONFID_LIST, "");
        if (StringUtil.isEmpty(tempConfid) || StringUtil.isEmpty(tempSuid)) return;
        if (StringUtil.isEmpty(confid_list_str_)) {
            if (tempConfid.equals(meetingInfo.getM_subject())) {
                history = new HistoryBean();
                history.setSuid(suid);
                history.setConfId(meetingInfo.getM_subject());
                history.setCreateTime(CommonUtils.getFormatTime(System.currentTimeMillis()));
                history.setNickName(meetingInfo.getUserName());
                history.setPassWord(meetingInfo.getConfPwd());
                confid_list_.add(history);
                saveTempJoinConfid("", "");
            }
        } else {
            //判断临时的是否跟入会的会议相同，则加入
            if (suid.equals(tempSuid) && tempConfid.equals(meetingInfo.getM_subject())) {//同一账号(加入会议)
                log.E("历史记录....saveHistoryMeetBySuid...同一账号....");
                //加入
                temp_confid_list_ = CommonUtils.String2SceneList(confid_list_str_);
                if (temp_confid_list_ == null)
                    temp_confid_list_ = new ArrayList<>();
                int size = temp_confid_list_.size();
                boolean isAdddiff = false;
                for (int i = 0; i < size; i++) {
                    history = temp_confid_list_.get(i);
                    if (history != null && history.getSuid().equals(suid)) {
                        sameConfid_list.add(history);
                        isAdddiff = true;
                    } else {
                        diffConfid_list.add(history);
                    }
                }

                if (sameConfid_list != null && sameConfid_list.size() > 0) {
                    int size1 = sameConfid_list.size();
                    boolean isAdd = false;
                    for (int i = 0; i < size1; i++) {
                        history = sameConfid_list.get(i);
                        if (history != null && history.getConfId().equals(meetingInfo.getM_subject())) {
                            history.setCreateTime(CommonUtils.getFormatTime(System.currentTimeMillis()));
                            history.setPassWord(meetingInfo.getConfPwd());
                            history.setSuid(suid);
                            history.setConfId(meetingInfo.getM_subject());
                            history.setNickName(meetingInfo.getUserName());
                            saveTempJoinConfid("", "");
                            isAdd = true;
                            break;
                        }
                    }

                    if (!isAdd) {
                        if (size1 < Configure.history_size) {
                            history = new HistoryBean();
                            history.setSuid(suid);
                            history.setConfId(meetingInfo.getM_subject());
                            history.setCreateTime(CommonUtils.getFormatTime(System.currentTimeMillis()));
                            history.setNickName(meetingInfo.getUserName());
                            history.setPassWord(meetingInfo.getConfPwd());
                            sameConfid_list.add(history);
                            saveTempJoinConfid("", "");
                        } else {
                            sameConfid_list.remove(9);
                            history = new HistoryBean();
                            history.setSuid(suid);
                            history.setConfId(meetingInfo.getM_subject());
                            history.setCreateTime(CommonUtils.getFormatTime(System.currentTimeMillis()));
                            history.setNickName(meetingInfo.getUserName());
                            history.setPassWord(meetingInfo.getConfPwd());
                            sameConfid_list.add(history);
                            saveTempJoinConfid("", "");
                        }
                    }
                    Collections.sort(sameConfid_list, new Comparator<HistoryBean>() {//按时间排列
                        @Override
                        public int compare(HistoryBean o1, HistoryBean o2) {
                            return o2.getCreateTime().compareTo(o1.getCreateTime());
                        }
                    });

//                    for (int i = 0; i < sameConfid_list.size(); i++) {
//                        history = sameConfid_list.get(i);
//                        if (history != null) {
//                            log.E("历史记录（相同）...getSuid:" + history.getSuid() + " getConfId:" + history.getConfId() + " getCreateTime:" + history.getCreateTime());
//                        }
//                    }

                    confid_list_.addAll(sameConfid_list);
                }

                if (diffConfid_list != null && diffConfid_list.size() > 0) {
                    if (!isAdddiff) {
                        history = new HistoryBean();
                        history.setSuid(suid);
                        history.setConfId(meetingInfo.getM_subject());
                        history.setCreateTime(CommonUtils.getFormatTime(System.currentTimeMillis()));
                        history.setNickName(meetingInfo.getUserName());
                        history.setPassWord(meetingInfo.getConfPwd());
                        diffConfid_list.add(history);
                        saveTempJoinConfid("", "");
                    }

//                    for (int i = 0; i < diffConfid_list.size(); i++) {
//                        history = diffConfid_list.get(i);
//                        if (history != null) {
//                            log.E("历史记录（不同）...getSuid:" + history.getSuid() + " getConfId:" + history.getConfId() + " getCreateTime:" + history.getCreateTime());
//                        }
//                    }
                    confid_list_.addAll(diffConfid_list);
                }
            } else {
                log.E("历史记录...开始会议");
                saveTempJoinConfid("", "");
                confid_list_ = CommonUtils.String2SceneList(confid_list_str_);
                if (confid_list_ == null)
                    confid_list_ = new ArrayList<>();
            }
        }

        //保存剩下的会议历史记录
        SharedPreferences.Editor editor = userPf.edit();
        if (confid_list_ != null && confid_list_.size() > 0) {
            for (int i = 0; i < confid_list_.size(); i++) {
                history = confid_list_.get(i);
                if (history != null) {
//                    log.E("历史记录(保存)...getSuid:" + history.getSuid() + " getConfId:" + history.getConfId() + " getCreateTime:" + history.getCreateTime());
                    StringUtil.writeToFile(TAG, "历史记录(保存)....suid:" + suid + " getM_subject:" + meetingInfo.getM_subject() + "******getSuid:" + history.getSuid() + " getConfId:" + history.getConfId());
                }
            }
            editor.putString(Configure.Meet.CONFID_LIST, CommonUtils.SceneList2String(confid_list_));
        } else {
            editor.putString(Configure.Meet.CONFID_LIST, "");
        }
        editor.commit();
    }

    //获取当前的历史会议列表
    public List<HistoryBean> getHistoryListBySuid(String suid) {
        SharedPreferences userPf = context.getSharedPreferences(ConfigureModelImpl.SPData.SPDATA, Context.MODE_MULTI_PROCESS);
        String confid_list_str = userPf.getString(Configure.Meet.CONFID_LIST, "");
        if (StringUtil.isEmpty(confid_list_str)) return null;
        List<HistoryBean> confid_list_ = CommonUtils.String2SceneList(confid_list_str);
        if (confid_list_ == null || confid_list_.size() <= 0) return null;
        int size = confid_list_.size();
        List<HistoryBean> confid_list = new ArrayList<HistoryBean>();
        HistoryBean historyBean = null;
        for (int i = 0; i < size; i++) {
            historyBean = confid_list_.get(i);
            if (historyBean != null && historyBean.getSuid().equals(suid)) {
                confid_list.add(historyBean);
            }
        }
        return confid_list;
    }

    //清除历史记录
    public void clearHistoryListBySuid(String suid) {
        List<HistoryBean> sameConfid_list = new ArrayList<>();
        SharedPreferences userPf = context.getSharedPreferences(ConfigureModelImpl.SPData.SPDATA, Context.MODE_MULTI_PROCESS);
        String confid_list_str = userPf.getString(Configure.Meet.CONFID_LIST, "");
        if (StringUtil.isEmpty(confid_list_str)) return;
        List<HistoryBean> confid_list_ = CommonUtils.String2SceneList(confid_list_str);
        if (confid_list_ == null || confid_list_.size() <= 0) return;
        int size = confid_list_.size();
        HistoryBean historyBean = null;
        for (int i = 0; i < size; i++) {
            historyBean = confid_list_.get(i);
            if (historyBean != null && historyBean.getSuid().equals(suid)) {
                sameConfid_list.add(historyBean);
            }
        }

        if (sameConfid_list != null) {
            confid_list_.removeAll(sameConfid_list);
        }
        //保存剩下的会议历史记录
        SharedPreferences.Editor editor = userPf.edit();
        if (confid_list_ != null && confid_list_.size() > 0) {
            editor.putString(Configure.Meet.CONFID_LIST, CommonUtils.SceneList2String(confid_list_));
        } else {
            editor.putString(Configure.Meet.CONFID_LIST, "");
        }
        editor.commit();
    }
}
