package org.suirui.huijian.tv.event;

/**
 * Created by cui on 2018/4/19.
 */

public class UpdateEvent{
    private int type;
    private Object data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    public UpdateEvent(){

    }

    public UpdateEvent(int type, Object data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public String toString() {
        return "UpdateEvent{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }

    public static class TypeEvent{
        public static final int UPDATE_MEETINGLIST=100;
        public static final int GET_MEETINGLIST=200;
        public static final int Invite_MEETINGLIST=201;

    }
}
