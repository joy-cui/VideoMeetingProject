package org.suirui.huijian.tv.util;

import android.util.Log;

import org.suirui.srpaas.util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by hh on 2018/6/6.
 */

public class WirteUnique extends WriteFileUtil{

    private final String dirName = "/org.suirui.huijian/unique/";
//    private final String dirName = "/org.suirui.version/image/";
    private final String fileName = "unique.txt";

    private static WirteUnique instance = null;

    public static synchronized WirteUnique getInstance() {
        if (instance == null) {
            instance = new WirteUnique();
        }

        return instance;
    }
    public void wirteUnique(String unique){
        String filePath = saveFile(dirName,fileName);
        writeToFile(filePath,unique,false);
    }

    public String readUnique(){
        String filePath = saveFile(dirName,fileName);
        String result = readFromFile(filePath);
        return result;
    }

    public String readUniqueMac(){
        String unique = readUnique();
        if(StringUtil.isEmptyOrNull(unique)){
            return "";
        }else{
            int uniqueLen = unique.length();

            String [] macArray = unique.split("-");
            if(macArray != null && macArray.length >0){
                return macArray[0];
            }else{
                return unique;
            }
        }
    }

}
