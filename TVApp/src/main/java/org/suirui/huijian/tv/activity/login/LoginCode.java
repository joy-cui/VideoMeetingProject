package org.suirui.huijian.tv.activity.login;

/**
 * Created by hh on 2018/4/27.
 */

public class LoginCode {
    public static final int PASSWORD_ERROR =  30000;  //错误的密码
    public static final int ACCOUNT_FREEZE_OR_NO_AUTHORITY = 30001;  //该账号已冻结/终端未授权
    public static final int USER_IDENTIFICATION_NULL = 30002;  //用户唯一标识为空
    public static final int NO_REGISTE_OR_EXIST = 30003;  //用户不存在或终端未注册
    public static final int OLD_PWD_ERROR = 30004;  //修改密码时原有密码错误
    public static final int USER_TYPE_MISMATCHING = 30005;  //用户类型不匹配
    public static final int NO_REGISTE = 30007;  //设备未注册
   //90000  呼叫参数不合法（呼叫类型为 MCU 时，呼叫参数不能为空）
    public static final int REGISTE_UUID_NULL = 90001;  //设备 UUID 不能为空
    public static final int REGISTE_INFO_ALREADY_EXIST = 90002;  //设备信息已存在
    public static final int REGISTE_NAME_NULL = 90003;//  设备名称为空
}
