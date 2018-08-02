package org.suirui.huijian.tv.util;

/**
 * Created by hh on 2018/5/11.
 */

public interface ErrorCode {
    int ERROR_200 = 200;//  成功
    String ERROR_MSG_200 = "成功";

    int ERROR_400 = 400;// 登录超时
    String ERROR_MSG_400 = "登录超时";

    int ERROR_500 = 500;// 服务器错误
    String ERROR_MSG_500 = "服务器错误";

    int ERROR_600 = 600;// 无效的 token
    String ERROR_MSG_600 = "无效的 token";

    int ERROR_700 = 700;// 用户不存在
    String ERROR_MSG_700 = "用户不存在";

    int ERROR_701 = 701;// appid 不存在
    String ERROR_MSG_701 = "appid 不存在";

    int ERROR_702 = 702;// 成员信息无效
    String ERROR_MSG_702 = "成员信息无效";

    int ERROR_703 = 703;// appid 为空
    String ERROR_MSG_703 = "appid 为空";

    int ERROR_705 = 705;//  secretKey 为空
    String ERROR_MSG_705 = "secretKey 为空";

    int ERROR_10001 = 10001;// 会议不存在
    String ERROR_MSG_10001 = "会议不存在";

    int ERROR_10002 = 10002;//会议已结束
    String ERROR_MSG_10002 = "会议已结束";

    int ERROR_10003 = 10003;// 会议已删除
    String ERROR_MSG_10003 = "会议已删除";

    int ERROR_10004 = 10004;// 参会密码不正确
    String ERROR_MSG_10004 = "参会密码不正确";

    int ERROR_10005 = 10005;// 临时账号资源紧张，请稍后再试
    String ERROR_MSG_10005 = "临时账号资源紧张，请稍后再试";

    int ERROR_10006 = 10006;// 不是自己的会议
    String ERROR_MSG_10006 = "不是自己的会议";

    int ERROR_10007 = 10007;// serviceid 为空
    String ERROR_MSG_10007 = "serviceid 为空";

    int ERROR_10008 = 10008;// 会议 ID 为空
    String ERROR_MSG_10008 = "会议 ID 为空";

    int ERROR_10009 = 10009;// thirdAudioId 必须是 12 位
    String ERROR_MSG_10009 = "thirdAudioId 必须是 12 位";

    int ERROR_10010 = 10010;// 用户重复加入会议
    String ERROR_MSG_10010 = "用户重复加入会议";

    int ERROR_10011 = 10011;// id3 不存在
    String ERROR_MSG_10011 = "id3 不存在";

    int ERROR_10012 = 10012;// version3 不存在
    String ERROR_MSG_10012 = "version3 不存在";

    int ERROR_10013 = 10013;// subject 不存在
    String ERROR_MSG_10013 = "subject 不存在";

    int ERROR_10014 = 10014;// 不允许匿名加入会
    String ERROR_MSG_10014 = "不允许匿名加入会";

    int ERROR_10015 = 10015;// 网关不存在
    String ERROR_MSG_10015 = "网关不存在";

    int ERROR_10016 = 10016;// 非法设备
    String ERROR_MSG_10016 = "非法设备";

    int ERROR_10017 = 10017;// 用户没有媒体处理器集群
    String ERROR_MSG_10017 = "用户没有媒体处理器集群";

    int ERROR_10018 = 10018;// 没有可用的 relayserver
    String ERROR_MSG_10018 = "没有可用的 relayserver";

    int ERROR_20001 = 20001;// appid 不能为空
    String ERROR_MSG_20001 = "appid 不能为空";

    int ERROR_20002 = 20002;// 非法的 appid
    String ERROR_MSG_20002 = "非法的 appid";

    int ERROR_20003 = 20003;// 手机或邮箱必填其一
    String ERROR_MSG_20003 = "手机或邮箱必填其一";

    int ERROR_20004 = 20004;// 不合法的邮箱地址
    String ERROR_MSG_20004 = "不合法的邮箱地址";

    int ERROR_20005 = 20005;// 不合法的手机号码
     String ERROR_MSG_20005 = "不合法的手机号码";

    int ERROR_20006 = 20006;// 该邮箱已存在(根据 APPID 一起判断)
    String ERROR_MSG_20006 = "该邮箱已存在";

    int ERROR_20007 = 20007;// 该手机已存在（根据 APPID 一起判断）
    String ERROR_MSG_20007 = "该手机已存在";

    int ERROR_20008 = 20008;// 无效的 secretKey
    String ERROR_MSG_20008 = "无效的 secretKey";

    int ERROR_20009 = 20009;// UID 为空
    String ERROR_MSG_20009 = "UID 为空";

    int ERROR_20010 = 20010;// nickname 为空
    String ERROR_MSG_20010 = "nickname 为空";

    int ERROR_30000 = 30000;// 错误的密码
    String ERROR_MSG_30000 = "错误的密码";

    int ERROR_30001 = 30001;// 该账号已冻结/终端未授权
    String ERROR_MSG_30001 = "该账号已冻结/终端未授权";

    int ERROR_30002 = 30002;// 用户唯一标识为空
    String ERROR_MSG_30002 = "用户唯一标识为空";

    int ERROR_30003 = 30003;// 用户不存在或终端未注册
    String ERROR_MSG_30003 = "用户不存在或终端未注册";

    int ERROR_30004 = 30004;// 修改密码时原有密码错误
    String ERROR_MSG_30004 = "修改密码时原有密码错误";

    int ERROR_30005 = 30005;// 用户类型不匹配
    String ERROR_MSG_30005 = "用户类型不匹配";

    int ERROR_30007 = 30007;// 设备未注册
    String ERROR_MSG_30007 = "设备未注册";

    int ERROR_40000 = 40000;// 开始时间为空
    String ERROR_MSG_40000 = "开始时间为空";

    int ERROR_40001 = 40001;// 开始时间格式有误
    String ERROR_MSG_40001 = "开始时间格式有误";

    int ERROR_40002 = 40002;// 开始时间小于当前时间
    String ERROR_MSG_40002 = "开始时间小于当前时间";

    int ERROR_40003 = 40003;// 结束时间为空
    String ERROR_MSG_40003 = "结束时间为空";

    int ERROR_40004 = 40004;// 结束时间格式有误
    String ERROR_MSG_40004 = "结束时间格式有误";

    int ERROR_40005 = 40005;// 结束时间小于等于开始时间
    String ERROR_MSG_40005 = "结束时间小于等于开始时间";

    int ERROR_40006 = 40006;// 专属会议号重复
    String ERROR_MSG_40006 = "专属会议号重复";

    int ERROR_60001 = 60001;// 手机号为空
    String ERROR_MSG_60001 = "手机号为空";

    int ERROR_60002 = 60002;// 请求类型为空
    String ERROR_MSG_60002 = "请求类型为空";

    int ERROR_60003 = 60003;// 手机号不存在
    String ERROR_MSG_60003 = "手机号不存在";

    int ERROR_60004 = 60004;// 发送过于频繁
    String ERROR_MSG_60004 = "发送过于频繁";

    int ERROR_60005 = 60005;// 达到每天最大限度
    String ERROR_MSG_60005 = "达到每天最大限度";

    int ERROR_60006 = 60006;// 短信发送失败
    String ERROR_MSG_60006 = "短信发送失败";

    int ERROR_60007 = 60007;// 验证码为空
    String ERROR_MSG_60007 = "验证码为空";

    int ERROR_60008 = 60008;// 验证码过期
    String ERROR_MSG_60008 = "验证码过期";

    int ERROR_60009 = 60009;// 验证码不匹配
    String ERROR_MSG_60009 = "验证码不匹配";

    int ERROR_70001 = 70001;// 通讯录 id 为空
    String ERROR_MSG_70001 = "通讯录 id 为空";

    int ERROR_70002 = 70002;// 部门 id 为空
    String ERROR_MSG_70002 = "部门 id 为空";

    int ERROR_70003 = 70003;// 查询内容为空
    String ERROR_MSG_70003 = "查询内容为空";

    int ERROR_70004 = 70004;// 用户不属于该通讯录
    String ERROR_MSG_70004 = "用户不属于该通讯录";

    int ERROR_70005 = 70005;// 用户 id 为空
    String ERROR_MSG_70005 = "用户 id 为空";

    int ERROR_80000 = 80000;// 无法找到会议信息
    String ERROR_MSG_80000 = "无法找到会议信息";

    int ERROR_80001 = 80001;// 开始时间格式有误
    String ERROR_MSG_80001 = "开始时间格式有误";

    int ERROR_80002 = 80002;// 会议开始时间小于当前时间
    String ERROR_MSG_80002 = "会议开始时间小于当前时间";

    int ERROR_80003 = 80003;// 结束时间为空
    String ERROR_MSG_80003 = "结束时间为空";

    int ERROR_80004 = 80004;// 结束时间《=开始时间
    String ERROR_MSG_80004 = "结束时间《=开始时间";

    int ERROR_80005 = 80005;// 会议正在召开中
    String ERROR_MSG_80005 = "会议正在召开中";

    int ERROR_80006 = 80006;// 无效的 op 参数
    String ERROR_MSG_80006 = "无效的 op 参数";

    int ERROR_80007 = 80007;// 应用无 im
    String ERROR_MSG_80007 = "应用无 im";

    int ERROR_80008 = 80008;// 时间戳格式有误
    String ERROR_MSG_80008 = "时间戳格式有误";

    int ERROR_80009 = 80009;// 修改、删除的不是自己的会
    String ERROR_MSG_80009 = "修改、删除的不是自己的会";

    int ERROR_80010 = 80010;// 密码为空
    String ERROR_MSG_80010 = " 密码为空";

    int ERROR_80011 = 80011;// 会议未开始
    String ERROR_MSG_80011 = "会议未开始";

    int ERROR_90000 = 90000;// 呼叫参数不合法（呼叫类型为 MCU 时，呼叫参数不能为空）
    String ERROR_MSG_90000  = "呼叫参数不合法";

    int ERROR_90001 = 90001;// 设备 UUID 不能为空
    String ERROR_MSG_90001 = "设备 UUID 不能为空";

    int ERROR_90002 = 90002;// 设备信息已存在
    String ERROR_MSG_90002 = "设备信息已存在";

    int ERROR_90003 = 90003;// 设备名称为空
    String ERROR_MSG_90003 ="设备名称为空";
}