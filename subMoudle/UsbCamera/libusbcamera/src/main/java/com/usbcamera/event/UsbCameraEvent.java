package com.usbcamera.event;

import android.view.Surface;

import java.util.Observable;

/**
 * Created by cui.li on 2016/11/2.
 */

public class UsbCameraEvent extends Observable {
    private volatile static UsbCameraEvent instance;
    private NotifyCmd notifyCmd;

    private UsbCameraEvent() {
        notifyCmd = new NotifyCmd();
    }
    public static UsbCameraEvent getInstance() {
        if (instance == null) {
            synchronized (UsbCameraEvent.class) {
                if (instance == null) {
                    instance = new UsbCameraEvent();
                }
            }
        }
        return instance;
    }




    public void addUsbSurface(Surface surface){
        setChanged();
        notifyObservers(getNotifyCmd(NotifyType.USB_CAMERA_ADDSURFACE, surface));
    }

    public void removeUsbSurface(Surface surface){
        setChanged();
        notifyObservers(getNotifyCmd(NotifyType.USB_CAMERA_REMOVESURFACE, surface));
    }

    private synchronized NotifyCmd getNotifyCmd(NotifyType type, Object data) {
        if (notifyCmd == null)
            notifyCmd = new NotifyCmd();
        notifyCmd.setType(type);
        notifyCmd.setData(data);
        return notifyCmd;
    }

    public enum NotifyType {
        USB_CAMERA_ADDSURFACE,
        USB_CAMERA_REMOVESURFACE,
    }

    /**
     * 通知上层用的数据
     */
    public class NotifyCmd {
        public NotifyType type;
        public Object data;

        public void setType(NotifyType type) {
            this.type = type;
        }

        public void setData(Object data) {
            this.data = data;
        }
//        NotifyCmd(NotifyType type, Object data) {
//            this.type = type;
//            this.data = data;
//        }

    }
}
