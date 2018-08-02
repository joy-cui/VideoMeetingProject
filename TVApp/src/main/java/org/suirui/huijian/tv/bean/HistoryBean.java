package org.suirui.huijian.tv.bean;

import java.io.Serializable;

/**
 * @authordingna
 * @date2017-06-12
 **/
public class HistoryBean implements Serializable, Comparable<HistoryBean> {
    static final long serialVersionUID =7860068824122245051L;
    private String confId;//会议号
    private String createTime;//会议创建的时间
    private String nickName;//昵称
    private String passWord;//密码
    private String suid;//账号
    private String meetName;//会议名称
    private String duration;//持续时间

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }


    public String getSuid() {
        return suid;
    }

    public void setSuid(String suid) {
        this.suid = suid;
    }

    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
    }

    public String getMeetName() {
        return meetName;
    }

    public void setMeetName(String meetName) {
        this.meetName = meetName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public int compareTo(HistoryBean history) {
        return this.getCreateTime().compareTo(history.getCreateTime());
    }

    public String getToString(){
        return " confId:"+confId
                + " createTime:"+createTime
                + " nickName:"+nickName
                + " passWord:"+passWord
                + " suid:"+suid
                + " meetName:"+meetName
                + " duration:"+duration;
    }
}
