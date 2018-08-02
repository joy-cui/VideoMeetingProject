package org.suirui.huijian.tv.bean;

/**
 * Created by hh on 2018/4/19.
 */

public class CamDevice {

    private String name;
    private boolean isSelectd;

    public void setName(String camName){
        name = camName;
    }
    public String getName(){
        return name;
    }

    public void setSelectd(boolean isSelectd){
        this.isSelectd = isSelectd;
    }
    public boolean isSelectd(){
        return isSelectd;
    }

    public String toString() {
        return "name:" + name + ",isSelectd:" + isSelectd;
    }
}
