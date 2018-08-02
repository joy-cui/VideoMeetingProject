package org.suirui.huijian.tv.bean;

import java.io.Serializable;

/**
 * Created by hh on 2018/4/19.
 */

public class LocationBean implements Serializable{
    static final long serialVersionUID =1L;
    private String name;
    private boolean isSelectd;
    private Object object;
    public void setName(String camName){
        name = camName;
    }
    public String getName(){
        return name;
    }

    public void setObject(Object object){
        this.object = object;
    }
    public Object getObject(){
        return this.object;
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
