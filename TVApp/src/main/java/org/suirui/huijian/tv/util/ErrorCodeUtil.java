package org.suirui.huijian.tv.util;

/**
 * Created by hh on 2018/5/11.
 */

public class ErrorCodeUtil implements ErrorCode{
    public static String getCodeMsg(int code){
        switch (code) {
            case ERROR_200://  成功
                return ERROR_MSG_200;

            case ERROR_400:// 登录超时
                return ERROR_MSG_400;

            case ERROR_500:// 服务器错误
                return ERROR_MSG_500;

            case ERROR_600:// 无效的 token
                return ERROR_MSG_600;

            case ERROR_700:// 用户不存在
                return ERROR_MSG_700;

            case ERROR_701:// appid 不存在
                return ERROR_MSG_701;

            case ERROR_702:// 成员信息无效
                return ERROR_MSG_702;

            case ERROR_703:// appid 为空
                return ERROR_MSG_703;

            case ERROR_705://  secretKey 为空
                return ERROR_MSG_705;

            case ERROR_10001:// 会议不存在
                return ERROR_MSG_10001;

            case ERROR_10002://会议已结束
                return ERROR_MSG_10002;

            case ERROR_10003:// 会议已删除
                return ERROR_MSG_10003;

            case ERROR_10004:// 参会密码不正确
                return ERROR_MSG_10004;

            case ERROR_10005:// 临时账号资源紧张，请稍后再试
                return ERROR_MSG_10005;

            case ERROR_10006:// 不是自己的会议
                return ERROR_MSG_10006;

            case ERROR_10007:// serviceid 为空
                return ERROR_MSG_10007;

            case ERROR_10008:// 会议 ID 为空
                return ERROR_MSG_10008;

            case ERROR_10009:// thirdAudioId 必须是 12 位
                return ERROR_MSG_10009;

            case ERROR_10010:// 用户重复加入会议
                return ERROR_MSG_10010;

            case ERROR_10011:// id3 不存在
                return ERROR_MSG_10011;

            case ERROR_10012:// version3 不存在
                return ERROR_MSG_10012;

            case ERROR_10013:// subject 不存在
                return ERROR_MSG_10013;

            case ERROR_10014:// 不允许匿名加入会
                return ERROR_MSG_10014;

            case ERROR_10015:// 网关不存在
                return ERROR_MSG_10015;

            case ERROR_10016:// 非法设备
                return ERROR_MSG_10016;

            case ERROR_10017:// 用户没有媒体处理器集群
                return ERROR_MSG_10017;

            case ERROR_10018:// 没有可用的 relayserver
                return ERROR_MSG_10018;

            case ERROR_20001:// appid 不能为空
                return ERROR_MSG_20001;

            case ERROR_20002:// 非法的 appid
                return ERROR_MSG_20002;

            case ERROR_20003:// 手机或邮箱必填其一
                return ERROR_MSG_20003;

            case ERROR_20004:// 不合法的邮箱地址
                return ERROR_MSG_20004;

            case ERROR_20005:// 不合法的手机号码
                return ERROR_MSG_20005;

            case ERROR_20006:// 该邮箱已存在(根据 APPID 一起判断)
                return ERROR_MSG_20006;

            case ERROR_20007:// 该手机已存在（根据 APPID 一起判断）
                return ERROR_MSG_20007;

            case ERROR_20008:// 无效的 secretKey
                return ERROR_MSG_20008;

            case ERROR_20009:// UID 为空
                return ERROR_MSG_20009;

            case ERROR_20010:// nickname 为空
                return ERROR_MSG_20010;

            case ERROR_30000:// 错误的密码
                return ERROR_MSG_30000;

            case ERROR_30001:// 该账号已冻结/终端未授权
                return ERROR_MSG_30001;

            case ERROR_30002:// 用户唯一标识为空
                return ERROR_MSG_30002;

            case ERROR_30003:// 用户不存在或终端未注册
                return ERROR_MSG_30003;

            case ERROR_30004:// 修改密码时原有密码错误
                return ERROR_MSG_30004;

            case ERROR_30005:// 用户类型不匹配
                return ERROR_MSG_30005;

            case ERROR_30007:// 设备未注册
                return ERROR_MSG_30007;

            case ERROR_40000:// 开始时间为空
                return ERROR_MSG_40000;

            case ERROR_40001:// 开始时间格式有误
                return ERROR_MSG_40001;

            case ERROR_40002:// 开始时间小于当前时间
                return ERROR_MSG_40002;

            case ERROR_40003:// 结束时间为空
                return ERROR_MSG_40003;

            case ERROR_40004:// 结束时间格式有误
                return ERROR_MSG_40004;

            case ERROR_40005:// 结束时间小于等于开始时间
                return ERROR_MSG_40005;

            case ERROR_40006:// 专属会议号重复
                return ERROR_MSG_40006;

            case ERROR_60001:// 手机号为空
                return ERROR_MSG_60001;

            case ERROR_60002:// 请求类型为空
                return ERROR_MSG_60002;

            case ERROR_60003:// 手机号不存在
                return ERROR_MSG_60003;

            case ERROR_60004:// 发送过于频繁
                return ERROR_MSG_60004;

            case ERROR_60005:// 达到每天最大限度
                return ERROR_MSG_60005;

            case ERROR_60006:// 短信发送失败
                return ERROR_MSG_60006;

            case ERROR_60007:// 验证码为空
                return ERROR_MSG_60007;

            case ERROR_60008:// 验证码过期
                return ERROR_MSG_60008;

            case ERROR_60009:// 验证码不匹配
                return ERROR_MSG_60009;

            case ERROR_70001:// 通讯录 id 为空
                return ERROR_MSG_70001;

            case ERROR_70002:// 部门 id 为空
                return ERROR_MSG_70002;

            case ERROR_70003:// 查询内容为空
                return ERROR_MSG_70003;

            case ERROR_70004:// 用户不属于该通讯录
                return ERROR_MSG_70004;

            case ERROR_70005:// 用户 id 为空
                return ERROR_MSG_70005;

            case ERROR_80000:// 无法找到会议信息
                return ERROR_MSG_80000;

            case ERROR_80001:// 开始时间格式有误
                return ERROR_MSG_80001;

            case ERROR_80002:// 会议开始时间小于当前时间
                return ERROR_MSG_80002;

            case ERROR_80003:// 结束时间为空
                return ERROR_MSG_80003;

            case ERROR_80004:// 结束时间《=开始时间
                return ERROR_MSG_80004;

            case ERROR_80005:// 会议正在召开中
                return ERROR_MSG_80005;

            case ERROR_80006:// 无效的 op 参数
                return ERROR_MSG_80006;

            case ERROR_80007:// 应用无 im
                return ERROR_MSG_80007;

            case ERROR_80008:// 时间戳格式有误
                return ERROR_MSG_80008;

            case ERROR_80009:// 修改、删除的不是自己的会
                return ERROR_MSG_80009;

            case ERROR_80010:// 密码为空
                return ERROR_MSG_80010;

            case ERROR_80011:// 会议未开始
                return ERROR_MSG_80011;

            case ERROR_90000:// 呼叫参数不合法（呼叫类型为 MCU 时，呼叫参数不能为空）
                return ERROR_MSG_90000;

            case ERROR_90001:// 设备 UUID 不能为空
                return ERROR_MSG_90001;

            case ERROR_90002:// 设备信息已存在
                return ERROR_MSG_90002;

            case ERROR_90003://
                return ERROR_MSG_90003;
        }
        return "错误code:"+code;
    }


}
