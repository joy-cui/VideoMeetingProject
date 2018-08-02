package org.suirui.huijian.box.bean;

import java.io.Serializable;

/**
 * @authordingna
 * @date2017-06-12
 **/
public class HistoryBean implements Serializable, Comparable<HistoryBean> {
    private String confId;//会议号
    private String createTime;//会议创建的时间
    private String nickName;//昵称
    private String passWord;//密码
    private String suid;//账号

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

    @Override
    public int compareTo(HistoryBean history) {
        return this.getCreateTime().compareTo(history.getCreateTime());
    }
}
