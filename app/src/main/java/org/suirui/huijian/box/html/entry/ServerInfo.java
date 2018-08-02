package org.suirui.huijian.box.html.entry;

/**
 * 服务器信息
 *
 * @authordingna
 * @date2017-08-11
 **/
public class ServerInfo {

    private String resfulApiUrl;//服务器地址
    private String appId;
    private String secretKey;
    private String token;//登录用户的token
    private String suid;//suid
    private String userName;//用户的昵称
    private String account;//当前用户的账号
    private String email;//当前用户的邮箱
    private String userpassword;//当前用户登录的密码

    public void setServerInfo(String appId, String secretKey, String token, String suid, String resfulApiUrl, String userName, String account, String email, String userpassword) {
        this.appId = appId;
        this.secretKey = secretKey;
        this.token = token;
        this.suid = suid;
        this.resfulApiUrl = resfulApiUrl;
        this.userName = userName;
        this.account = account;
        this.email = email;
        this.userpassword = userpassword;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSuid() {
        return suid;
    }

    public void setSuid(String suid) {
        this.suid = suid;
    }

    public String getResfulApiUrl() {
        return resfulApiUrl;
    }

    public void setResfulApiUrl(String resfulApiUrl) {
        this.resfulApiUrl = resfulApiUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }
}
