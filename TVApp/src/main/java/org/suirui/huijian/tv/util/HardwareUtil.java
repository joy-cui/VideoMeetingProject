package org.suirui.huijian.tv.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HardwareUtil {
    public static final int FLAG_CUR_FREQUENCY = 0;
    public static final int FLAG_MIN_FREQUENCY = 1;
    public static final int FLAG_MAX_FREQUENCY = 2;

    public HardwareUtil() {
    }


    /**
     * 获取CPU序列号
     *
     * @return CPU序列号(16位)
     * 读取失败为"0000000000000000"
     */

    public static String getCPUSerial() {

        String str = "",
                strCPU = "",
                cpuAddress = "0000000000000000";

        try {
            //读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat/proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            //查找CPU序列号
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    //查找到序列号所在行
                    if (str.indexOf("Serial")> -1) {
                        //提取序列号
                        strCPU = str.substring(str.indexOf(":") + 1, str.length());
                        //去空格
                        cpuAddress = strCPU.trim();
                        break;
                    }

                } else {
                    //文件结尾
                    break;
                }
            }

        } catch (IOException ex) {
            //赋予默认值
            ex.printStackTrace();
        }
        return cpuAddress;

    }

    public static int getCPUKernalNumbers() {
        File dir = new File("/sys/devices/system/cpu");

        try {
            if (!dir.isDirectory()) {
                return 1;
            } else {
                String[] e = dir.list(new FilenameFilter() {
                    public boolean accept(File dir, String filename) {
                        Pattern pattern = Pattern.compile("cpu(\\d+)");
                        Matcher matcher = pattern.matcher(filename);
                        return matcher.matches();
                    }
                });
                return e == null ? 1 : e.length;
            }
        } catch (Exception var2) {
            return 1;
        }
    }

    public static int getCPUKernelFrequency(int kernelIndex, int flag) {
        String fileName = "/sys/devices/system/cpu/cpu" + kernelIndex;
        if (flag == 0) {
            fileName = fileName + "/cpufreq/scaling_cur_freq";
        } else if (flag == 1) {
            fileName = fileName + "/cpufreq/cpuinfo_min_freq";
        } else {
            if (flag != 2) {
                return 0;
            }

            fileName = fileName + "/cpufreq/cpuinfo_max_freq";
        }

        File f = new File(fileName);
        FileReader fReader = null;
        BufferedReader reader = null;
        int freq = 0;

        try {
            fReader = new FileReader(f);
            reader = new BufferedReader(fReader);
            String line = reader.readLine();
            if (line != null) {
                freq = Integer.parseInt(line);
            }
        } catch (Exception var16) {
            ;
        } finally {
            try {
                reader.close();
                fReader.close();
            } catch (Exception var15) {
                ;
            }

        }

        return freq;
    }
}
