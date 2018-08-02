package org.suirui.huijian.tv.util;


import android.text.format.Time;
import android.util.Log;

import com.suirui.srpaas.video.util.StringUtil;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by hh on 2018/4/11.
 */

public class DateUtil {
    /**
     * 获取当前的日期，2011.11.11
     *
     * @return
     */
    public static String getCurrentData() {
        // final Calendar c = Calendar.getInstance();
        // c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        // String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        // String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        // String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));//
        // 获取当前月份的日期号码
        // return mYear + "." + mMonth + "." + mDay;

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String data = sDateFormat.format(new java.util.Date());
        return data;
    }

    /**
     * 获取当前的是星期几
     *
     * @return
     */
    public static String getCurrentWeek() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
        return "星期" + mWay;
    }

    /**
     * 获取系统的当前时间
     *
     * @return
     */
    public static String getCurrentSysTime() {
        // SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm");
        // String time = sDateFormat.format(new java.util.Date());

        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
        t.setToNow(); // 取得系统时间。
        int hour = t.hour; // 0-23
        int minute = t.minute;
        String hours = "";
        String minutes = "";
        if (minute < 10)
            minutes = "0" + minute;
        else
            minutes = "" + minute;
        if (hour < 10)
            hours = "0" + hour;
        else
            hours = "" + hour;

        return hours + ":" + minutes;
    }

    public static String getCurrentTimeRange() {
        // SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm");
        // String time = sDateFormat.format(new java.util.Date());

        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
        t.setToNow(); // 取得系统时间。
        int hour = t.hour; // 0-23
        int minute = t.minute;
        if(hour <= 12){
            return "上午";
        }else{
            return "下午";
        }

    }

    /**
     * 日期转化成星期几
     *
     * @param
     *
     * @return
     */
    public static String getWeek(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date newDate = format.parse(time, pos);
        Calendar c = Calendar.getInstance();
        c.setTime(newDate);
        return new SimpleDateFormat("EEEE").format(c.getTime());

    }

    public static String getDuration(long endTime, long startTime) {
         Log.e("", "timeDValue()----time1==" + endTime + "   time2=="
         + startTime + getCurrentSysTime());
        try {
            // 默认为毫秒，除以1000是为了转换成秒
            long interval = Math.abs((endTime - startTime)) / 1000;// 秒
            long day = interval / (24 * 3600);// 天
            long hour = interval % (24 * 3600) / 3600;// 小时
            long minute = interval % 3600 / 60;// 分钟
            long second = interval % 60;// 秒
            String duration = "";
            if (day > 0) {
                duration = day + "天";
            }
            if (hour > 0) {
                duration += hour + "小时";
            }
            if (minute > 0) {
                duration += minute + "分钟";
            }
            Log.e("","两个时间相差：" + day + "天" + hour + "小时" + minute
                    + "分" + second + "秒");
            return day + "天" + hour + "小时" + minute
                    + "分" + second + "秒";
//            return interval / 60;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取会议时长(结束时间-开始时间)
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static double getTimeLonger(String startTime, String endTime) {
        double Longertime = 0;
        try {
            if (!StringUtil.isEmpty(startTime) && !StringUtil.isEmpty(endTime)) {
                long currentStart = getSecond(startTime);
                long currentEnd = getSecond(endTime);
                long currentLonger = Math.abs(currentEnd - currentStart);
                double longer = new Double(currentLonger / 3600);
                double yu = new Double(currentLonger % 3600);
                if (yu == 0) {
                    Longertime = longer;
                } else {
                    Longertime = longer + 0.5;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Longertime;
    }

    /**
     * 往后推时间 计算结束时间(开始时间加时长)
     *
     * @param time
     */
    public static String DateToSecond(String time, String meetTimer) {
        try {
            if (!StringUtil.isEmpty(time) && !StringUtil.isEmpty(meetTimer)) {
                long currentSencond = getSecond(time);// 将开始时间转化成秒数
                Double secondTime = Double.parseDouble(meetTimer);
                Double timerd = secondTime * 3600;
                long second = new Double(timerd).longValue();// 将会议时长转化成秒
                long addSecond = Math.abs(currentSencond + second);
                String newEndTime = getStringTime(addSecond);
                return newEndTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 会议时长 将double类型的数据转化成String类型
     *
     * @return
     */
    public static String getlongerTT(double num) {
        String number = "1";

        try {
            if (num != 0) {
                if (num % 1.0 == 0) {
                    number = String.valueOf((long) num);
                } else {
                    number = String.valueOf(num);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return number;
    }

    /**
     * 将long类型转化成时间格式
     */

    public static String getStringTime(long current) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date currentTime = new Date(current);
            Date dateOld = new Date(currentTime.getTime() * 1000);
            String sDateTime = format.format(dateOld);
            return sDateTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * 得到秒 retun 秒
     */
    public static long getSecond(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date times1 = sdf.parse(time);
            // 默认为毫秒，除以1000是为了转换成秒
            long interval = (times1.getTime()) / 1000;// 秒
            return interval;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     *
     * @param time
     * @return yy/MM/dd 星期一
     */
    public static String getDateShort(String time) {
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy/MM/dd E");
        // SimpleDateFormat formatter3 = new SimpleDateFormat("HH:mm");
        String dateStr1 = "";
        String dateStr2 = "";
        try {
            dateStr1 = formatter2.format(formatter1.parse(time));
            // dateStr2 = formatter3.format(formatter1.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // return dateStr1.replace("星期", "周") + " " + dateStr2;
        return dateStr1.replace("周", " 星期");
    }

    /**
     *
     * @param time
     *            yyyy-MM-dd HH:mm转 yyyy/MM/dd HH:mm
     * @return
     */
    public static String getDateToFormat(String time) {
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String dateStr1 = "";
        if (time != null && !time.equals("")) {
            try {
                dateStr1 = formatter2.format(formatter1.parse(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dateStr1;
    }

    /**
     * 获取日期中的时间
     *
     * @param time
     * @return HH:mm
     */
    public static String getDateTime(String time) {
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatter3 = new SimpleDateFormat("HH:mm");
        String dateStr1 = "";
        try {
            dateStr1 = formatter3.format(formatter1.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr1;
    }
}
