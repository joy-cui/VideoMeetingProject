package org.suirui.huijian.tv.util;

import android.os.Environment;
import android.util.Log;

import com.suirui.srpaas.base.util.BaseUtil;

import org.suirui.srpaas.util.StringUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by hh on 2018/6/1.
 */

public class WriteFileUtil {


//    private final String filePaht = "";

    public void WriteFileUtil(){

    }

    public void writeToFile(String filePath, String msg,boolean append) {
        if(null != filePath) {
//           File file = new File(filePath);
//            if(!file.exists()) {
//                boolean var5 = file.mkdirs();
//            }
            Log.e("", "wxh+++----msg:" + msg);
            FileOutputStream fos = null;
            BufferedWriter bw = null;

            try {
                fos = new FileOutputStream(filePath,append);
                bw = new BufferedWriter(new OutputStreamWriter(fos));
                bw.write(msg);
            } catch (FileNotFoundException var18) {
                var18.printStackTrace();
            } catch (IOException var19) {
                var19.printStackTrace();
            } finally {
                try {
                    if(bw != null) {
                        bw.close();
                    }
                } catch (IOException var17) {
                    var17.printStackTrace();
                }

            }

        }
    }

    public void writeToFile(String dirName,String fileName, String msg,boolean append) {
        String filePath = saveFile( dirName, fileName);
        if(null != filePath) {
            Log.e("", "wxh+++----msg:" + msg);
            FileOutputStream fos = null;
            BufferedWriter bw = null;
            try {
                fos = new FileOutputStream(filePath,append);
                bw = new BufferedWriter(new OutputStreamWriter(fos));
                bw.write(msg);
            } catch (FileNotFoundException var18) {
                var18.printStackTrace();
            } catch (IOException var19) {
                var19.printStackTrace();
            } finally {
                try {
                    if(bw != null) {
                        bw.close();
                    }
                } catch (IOException var17) {
                    var17.printStackTrace();
                }
            }

        }
    }

    public String readFromFile(String filePath){

        StringBuilder result = new StringBuilder();
        try{
            if(StringUtil.isEmptyOrNull(filePath)){
                return "";
            }
            File file = new File(filePath);
            if(!file.exists()){
                return "";
            }
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        Log.e("","wxh++++readUnique()："+result.toString());
        return result.toString();
    }

    public String saveFile(String dirName,String fileName) {
        try {
            if (BaseUtil.checkSDCard()) {
                long available = BaseUtil.getAvailableExternalMemorySize();
                if (available < 1024 * 1024) {
//                    Toast.makeText(mContext, "没有SD卡", Toast.LENGTH_SHORT).show();
                    return null;
                }
                String dirPath = Environment.getExternalStorageDirectory()
                        .getPath() + dirName;
                if (dirPath == null) {
                    return null;
                }
                File file = new File(dirPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (fileName == null || fileName.equals("")) {
                    return null;
                }
                String filePath = file.getAbsolutePath() + "/" + fileName;
                Log.e("", "wxh+++----filePath:" + filePath);
                return filePath;
            } else {
//                Toast.makeText(mContext, "空间不足", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
