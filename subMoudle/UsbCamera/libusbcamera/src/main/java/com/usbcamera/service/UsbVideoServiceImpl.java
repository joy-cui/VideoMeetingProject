package com.usbcamera.service;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.view.Surface;

import com.serenegiant.usb.widget.CameraViewInterface;
import com.srpaas.capture.constant.CameraEntry;
import com.suirui.srpaas.base.util.log.SRLog;
import com.usbcamera.capture.UsbVideoCapture;
import com.usbcamera.contant.UsbCameraEntry;
import com.usbcamera.event.UsbCameraEvent;
import com.usbcamera.listener.UsbCameraCaptureListener;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by cui on 2018/6/4.
 */

public class UsbVideoServiceImpl implements IUsbVideoServeice , UsbCameraCaptureListener.UsbCameraVideoListener,Observer{
static SRLog log=new SRLog("UsbVideoServiceImpl",1);
    private UsbVideoServiceListener mListener;
    private UsbVideoCapture videoCapture;
    private static UsbVideoServiceImpl instance=null;

    public synchronized static UsbVideoServiceImpl getInstance(Context context, CameraViewInterface cameraViewInterface) {
        if (instance == null) {
            log.E("usbCamera..UsbVideoServiceImpl初始化");
            instance = new UsbVideoServiceImpl(context,cameraViewInterface);
        }
        return instance;
    }


    public UsbVideoServiceImpl(Context mContext, CameraViewInterface cameraViewInterface) {
        if (videoCapture == null) {
            log.E("usbCamera..UsbVideoServiceImpl...初始化.cameraViewInterface..");
            videoCapture = new UsbVideoCapture(mContext,cameraViewInterface);
        }
        UsbCameraEvent.getInstance().addObserver(this);
        UsbCameraCaptureListener.getInstance().addCameraVideoListener(this);

    }


    @Override
    public void addVideoServiceListener(UsbVideoServiceListener listener) {
        this.mListener=listener;
    }

    @Override
    public void removeVideoServiceListener() {
        this.mListener=null;
    }



    @Override
    public UsbDevice getDeviceType() {
        if(videoCapture!=null) {
            return videoCapture.getSelectUsbDevice();
        }
        return null;
    }

    @Override
    public boolean startCapture() {
        if(videoCapture!=null) {
            return videoCapture.startCapture();
        }
        return false;
    }

    @Override
    public void stopCapture() {
        if(videoCapture!=null) {
            videoCapture.stopCapture();
        }
    }

    @Override
    public void switchCamera() {
        if(videoCapture!=null) {
            videoCapture.switchCamera();
        }
    }

    @Override
    public void setCaptureSize(int width, int height) {
        UsbCameraEntry.CaptureSize.width=width;
        UsbCameraEntry.CaptureSize.height=height;
        log.E("setCaptureSize...."+UsbCameraEntry.CaptureSize.width);
    }

    @Override
    public void setCaptureFps(int mFps) {
        UsbCameraEntry.CaptureParam.mFps=mFps;
    }

    @Override
    public List<UsbDevice> getUsbDeviceList() {
        return videoCapture.getUsbDeviceList();
    }

    @Override
    public int getUsbUsbDeviceCount() {
        if(videoCapture!=null) {
            List<UsbDevice> usbDeviceList = videoCapture.getUsbDeviceList();
            if (usbDeviceList != null) {
                return usbDeviceList.size();
            }
        }
        return 0;
    }

    @Override
    public void addSurface(int surfaceId, Surface surface, boolean isRecordable) {
        if(videoCapture!=null) {
            videoCapture.addSurface(surfaceId, surface, isRecordable);
        }
    }

    @Override
    public void removeSurface(int surfaceId) {
        if(videoCapture!=null) {
            videoCapture.removeSurface(surfaceId);
        }
    }

    @Override
    public void clearData() {
        if(videoCapture!=null) {
            videoCapture.clearData();
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof UsbCameraEvent) {
            final UsbCameraEvent.NotifyCmd cmd = (UsbCameraEvent.NotifyCmd) data;
            switch (cmd.type) {
                case USB_CAMERA_ADDSURFACE:
                    log.E("...usbCamera ....addSurface");
                   Surface surface= (Surface) cmd.data;
                    addSurface(surface.hashCode(),surface,false);
                    break;
                case USB_CAMERA_REMOVESURFACE:
                    log.E("...usbCamera ....removeSurface");
                    Surface removeSurface= (Surface) cmd.data;
                   removeSurface(removeSurface.hashCode());
                    break;
            }
        }
    }


    @Override
    public void onPreviewCallback(byte[] data, int width, int height) {
        if (mListener != null)
            mListener.onPreviewCallback(data,width,height);
    }

    @Override
    public void onPreviewCallback(ByteBuffer data, int width, int height) {
//        log.E("onPreviewCallback....ByteBuffer");
        if (mListener != null)
            mListener.onPreviewCallback(data,width,height);
    }

    @Override
    public void onUsbCameraCaptureListener(boolean isOpen, int code) {
        if (mListener != null)
            mListener.onUsbCameraCaptureListener(isOpen,code);
    }

    @Override
    public void onPreviewCallback(ByteBuffer y, ByteBuffer u, ByteBuffer v, int width, int height) {
        if (mListener != null)
            mListener.onPreviewCallback(y,u,v,width,height);
    }
}
