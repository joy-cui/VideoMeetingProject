package org.suirui.huijian.tv.util;


import org.suirui.huijian.tv.TVAppApplication;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.TVSrHuiJianProperties;
import org.suirui.huijian.tv.widget.WarningDialog;
import org.suirui.transfer.util.PreferenceUtil;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.input.InputManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.provider.Settings;
import android.view.InputDevice;

import com.suirui.srpaas.base.util.log.SRLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.System.in;

public class AppUtil {
    private static final SRLog log = new SRLog(AppUtil.class.getName(), TVAppConfigure.LOG_LEVE);
    private static AppUtil instance = null;
    private Handler mHandler = new Handler();

    private AppUtil() {
    }

    public static synchronized AppUtil getInstance() {
        if (instance == null) {
            instance = new AppUtil();
        }

        return instance;
    }

    public void init() {
        PreferenceUtil.initialize(TVAppApplication.getInstance());
    }

    public void testFocusView(final Activity context){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                context.getCurrentFocus().setBackgroundColor(Color.RED);
            }
        },1*1000);
    }
    public void testFocusView(final Dialog context){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                context.getCurrentFocus().setBackgroundColor(Color.RED);
            }
        },1*1000);
    }


    public boolean isShowRootUI(){
        return AndroidUtils.getInstance().hasRootPerssion() && TVSrHuiJianProperties.getIsShowRootUI();
    }

    public void showWaringDialog(final Context context, final String msg) {
        WarningDialog dialog = new WarningDialog(context);
        dialog.setWarningMsg(msg);
        dialog.show();

    }

    public void showWaringDialog(final Context context, final int msg) {
        WarningDialog dialog = new WarningDialog(context);
        dialog.setWarningMsg(msg);
        dialog.show();
    }


    public String getAppVersionName(Context context){
        PackageInfo pi = null;
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi.versionName;
    }

    public void skipSet(Context context){
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }





    private void  usbdev(){
        //获得root权限

        String command = "chmod -R 777 /dev";
        try {
            Process process = Runtime.getRuntime().exec(new String[] {"su", "-c", command});
            process.waitFor();

            String fileName = "/dev/usb/sound1-1.1";
            String res="";
            FileInputStream fin;
            fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            fin.close();
            log.E("usb--action xxxxx: " + res);
            String filepath = "/dev/usb/";
            readAllFile(filepath);



        } catch(IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    ArrayList<String> listname = new ArrayList<String>();
    public void readAllFile(String filepath) {
        File file= new File(filepath);
        if(!file.isDirectory()){
            listname.add(file.getName());
        }else if(file.isDirectory()){
            log.E("usb--action --文件");
            String[] filelist=file.list();
            for(int i = 0;i<filelist.length;i++){
                File readfile = new File(filepath);
                if (!readfile.isDirectory()) {
                    listname.add(readfile.getName());
                } else if (readfile.isDirectory()) {
                    readAllFile(filepath + "\\" + filelist[i]);//递归
                }
            }
        }
        for(int i = 0;i<listname.size();i++){
            log.E("usb--action file--"+listname.get(i));
        }
    }

    IntentFilter intentFilter;
    UsbManager usbManager;
    BroadcastReceiver receiver;
    public void usb(Context context){
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                log.E("usb--action : " + action);

                if(intent.hasExtra(UsbManager.EXTRA_PERMISSION_GRANTED)) {
                    boolean permissionGranted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                    log.E("usb--permissionGranted : " + permissionGranted);
                }
                switch(action) {
                    case UsbManager.ACTION_USB_ACCESSORY_ATTACHED:
                    case UsbManager.ACTION_USB_ACCESSORY_DETACHED:
                        //Name of extra for ACTION_USB_ACCESSORY_ATTACHED and ACTION_USB_ACCESSORY_DETACHED broadcasts containing the UsbAccessory object for the accessory.
                        UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                        log.E("usb--"+accessory.toString());
                        break;
                    case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    case UsbManager.ACTION_USB_DEVICE_DETACHED:
                        //Name of extra for ACTION_USB_DEVICE_ATTACHED and ACTION_USB_DEVICE_DETACHED broadcasts containing the UsbDevice object for the device.
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        log.E("usb--device:"+device.toString());
                        break;
                    case MyUsbManager.ACTION_USB_STATE:
                    /*
                     * <li> {@link #USB_CONNECTED} boolean indicating whether USB is connected or disconnected.
                     * <li> {@link #USB_CONFIGURED} boolean indicating whether USB is configured.
                     * currently zero if not configured, one for configured.
                     * <li> {@link #USB_FUNCTION_ADB} boolean extra indicating whether the
                     * adb function is enabled
                     * <li> {@link #USB_FUNCTION_RNDIS} boolean extra indicating whether the
                     * RNDIS ethernet function is enabled
                     * <li> {@link #USB_FUNCTION_MTP} boolean extra indicating whether the
                     * MTP function is enabled
                     * <li> {@link #USB_FUNCTION_PTP} boolean extra indicating whether the
                     * PTP function is enabled
                     * <li> {@link #USB_FUNCTION_PTP} boolean extra indicating whether the
                     * accessory function is enabled
                     * <li> {@link #USB_FUNCTION_AUDIO_SOURCE} boolean extra indicating whether the
                     * audio source function is enabled
                     * <li> {@link #USB_FUNCTION_MIDI} boolean extra indicating whether the
                     * MIDI function is enabled
                     * </ul>
                     */
                        boolean connected = intent.getBooleanExtra(MyUsbManager.USB_CONNECTED, false);
                        log.E("usb--connected : " + connected);
                        boolean configured = intent.getBooleanExtra(MyUsbManager.USB_CONFIGURED, false);
                        log.E("usb--configured : " + configured);
                        boolean function_adb = intent.getBooleanExtra(MyUsbManager.USB_FUNCTION_ADB, false);
                        log.E("usb--function_adb : " + function_adb);
                        boolean function_rndis = intent.getBooleanExtra(MyUsbManager.USB_FUNCTION_RNDIS, false);
                        log.E("usb--function_rndis : " + function_rndis);
                        boolean function_mtp = intent.getBooleanExtra(MyUsbManager.USB_FUNCTION_MTP, false);
                        log.E("usb--function_mtp : " + function_mtp);
                        boolean function_ptp = intent.getBooleanExtra(MyUsbManager.USB_FUNCTION_PTP, false);
                        log.E("usb--usb_function_ptp : " + function_ptp);
                        boolean function_audio_source = intent.getBooleanExtra(MyUsbManager.USB_FUNCTION_AUDIO_SOURCE, false);
                        log.E("usb--function_audio_source : " + function_audio_source);
                        boolean function_midi = intent.getBooleanExtra(MyUsbManager.USB_FUNCTION_MIDI, false);
                        log.E("usb--function_midi : " + function_midi);
                        break;
                }
            }
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intentFilter.addAction(MyUsbManager.ACTION_USB_STATE);
        context.registerReceiver(receiver, intentFilter);

        boolean hasUsbHost = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_HOST);
        boolean hasUsbAccessory = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_ACCESSORY);
        log.E("usb--hasUsbHost : " + hasUsbHost);
        log.E("usb--hasUsbAccessory : " + hasUsbAccessory);

//        detectUsbDeviceWithInputManager( context);
        detectUsbDeviceWithUsbManager( context);
//        listUsbDevices(context,true);

        usbdev();
    }
    private void detectUsbDeviceWithInputManager(Context context) {
        InputManager im = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
        int[] devices = im.getInputDeviceIds();
        for (int id : devices) {
            InputDevice device = im.getInputDevice(id);
            log.E("UsbDeviceWithInputManager: " + device.getName()+">>>"+ device.toString());
            //do something
        }
    }

    private void detectUsbDeviceWithUsbManager(Context context) {
        HashMap<String, UsbDevice> deviceHashMap = ((UsbManager) context.getSystemService(Context.USB_SERVICE)).getDeviceList();
        for (Map.Entry entry : deviceHashMap.entrySet()) {
            log.E("UsbDeviceWithUsb: " + entry.getKey() + ", "+"    " + entry.toString());
        }

        Iterator<UsbDevice> iterator = deviceHashMap.values().iterator();
        while (iterator.hasNext()) {
            String returnValue = "";
            UsbDevice device = iterator.next();
            returnValue = "$$$$$$$$$$  device name: "+device.getDeviceName()+"\ndevice product name:"
                    +device.getProductName()+"\nvendor id:"+device.getVendorId()+
                    "\ndevice serial: "+device.getSerialNumber()
                    +"\ngetDeviceClass: "+device.getDeviceClass()
                    +"\ngetDeviceSubclass: "+device.getDeviceSubclass()
                    + "\ndescribeContents"+device.describeContents();

            if(device.getDeviceClass() == 0){
                for (int i = 0; i < device.getConfigurationCount(); i++) {
                    if (i >= 1) {
                        continue;
                    }
                    returnValue = returnValue+"\n-----config--------------\n"+ "    i:"+i+"\ndevice getConfiguration: "+device.getConfiguration(i)
                            +"\n@@@@@@@-----------";;

                }
            }else {



                for (int index = 0; index < device.getInterfaceCount(); index++) {
                    if (index >= 1) {
                        continue;
                    }
                    UsbInterface mUsbInterface = device.getInterface(index);
                    returnValue = returnValue + "--- interface-----------\r\n";
                    returnValue = returnValue + "  Interface index: " + index + "\r\n";
                    returnValue = returnValue + "  Interface ID: " + mUsbInterface.getId() + "\r\n";
                    returnValue = returnValue + "  Inteface class: " + mUsbInterface.getInterfaceClass() + "\r\n";
                    returnValue = returnValue + "  Interface protocol: " + mUsbInterface.getInterfaceProtocol() + "\r\n";
                    returnValue = returnValue + "  Endpoint count: " + mUsbInterface.getEndpointCount() + "\r\n"
                    +"\n@@@@@@@-----------";
                }

            }
            log.E(returnValue);
//            sb.append("DeviceName="+usbDevice.getDeviceName()+"\n");
//            sb.append("DeviceId="+usbDevice.getDeviceId()+"\n");
//            sb.append("VendorId="+usbDevice.getVendorId()+"\n");
//            sb.append("ProductId="+usbDevice.getProductId()+"\n");
//            sb.append("DeviceClass="+usbDevice.getDeviceClass()+"\n");
//            int deviceClass = device.getDeviceClass();
//                if(deviceClass==0) {
//                    UsbInterface anInterface = device.getInterface(0);
//                    int interfaceClass = anInterface.getInterfaceClass();
//
//                    sb.append("device Class 为0-------------\n");
//                    sb.append("Interface.describeContents()="+anInterface.describeContents()+"\n");
//                    sb.append("Interface.getEndpointCount()="+anInterface.getEndpointCount()+"\n");
//                    sb.append("Interface.getId()="+anInterface.getId()+"\n");
//                    //http://blog.csdn.net/u013686019/article/details/50409421
//                    //http://www.usb.org/developers/defined_class/#BaseClassFFh
//                    //通过下面的InterfaceClass 来判断到底是哪一种的，例如7就是打印机，8就是usb的U盘
//                    sb.append("Interface.getInterfaceClass()="+anInterface.getInterfaceClass()+"\n");
//                    if(anInterface.getInterfaceClass()==7){
//                        sb.append("此设备是打印机\n");
//                    }else if(anInterface.getInterfaceClass()==8){
//                        sb.append("此设备是U盘\n");
//                    }
//                    sb.append("anInterface.getInterfaceProtocol()="+anInterface.getInterfaceProtocol()+"\n");
//                    sb.append("anInterface.getInterfaceSubclass()="+anInterface.getInterfaceSubclass()+"\n");
//                    sb.append("device Class 为0------end-------\n");
            }
    }

    private String listUsbDevices(Context context,boolean all)
    {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap deviceList = usbManager.getDeviceList();


        if (deviceList.size() == 0)
        {
            return "no usb devices found";
        }


        Iterator deviceIterator = deviceList.values().iterator();
        String returnValue = "";

        while (deviceIterator.hasNext())
        {
            UsbDevice device = (UsbDevice)deviceIterator.next();

                returnValue = returnValue + "Model: " + device.getDeviceName() + "\r\n";
                returnValue = returnValue + "ID: " + device.getDeviceId() + "\r\n";
                returnValue = returnValue + "Class: " + device.getDeviceClass() + "\r\n";
                returnValue = returnValue + "Protocol: " + device.getDeviceProtocol() + "\r\n";
                returnValue = returnValue + "Vendor ID " + device.getVendorId() + "\r\n";
                returnValue = returnValue + "Product ID: " + device.getProductId() + "\r\n";
                returnValue = returnValue + "Interface count: " + device.getInterfaceCount() + "\r\n";
                returnValue = returnValue + "---------------------------------------\r\n";


                for (int index = 0; index < device.getInterfaceCount(); index++)
                {
                    if(index >=1){
                        continue;
                    }
                    UsbInterface mUsbInterface = device.getInterface(index);
                    returnValue = returnValue + "  *****     *****\r\n";
                    returnValue = returnValue + "  Interface index: " + index + "\r\n";
                    returnValue = returnValue + "  Interface ID: " + mUsbInterface.getId() + "\r\n";
                    returnValue = returnValue + "  Inteface class: " + mUsbInterface.getInterfaceClass() + "\r\n";
                    returnValue = returnValue + "  Interface protocol: " + mUsbInterface.getInterfaceProtocol() + "\r\n";
                    returnValue = returnValue + "  Endpoint count: " + mUsbInterface.getEndpointCount() + "\r\n";


//                    for (int epi = 0; epi < mUsbInterface.getEndpointCount(); epi++)
//                    {
//                        UsbEndpoint mEndpoint = mUsbInterface.getEndpoint(epi);
//                        returnValue = returnValue + "    ++++   ++++   ++++\r\n";
//                        returnValue = returnValue + "    Endpoint index: " + epi + "\r\n";
//                        returnValue = returnValue + "    Attributes: " + mEndpoint.getAttributes() + "\r\n";
//                        returnValue = returnValue + "    Direction: " + mEndpoint.getDirection() + "\r\n";
//                        returnValue = returnValue + "    Number: " + mEndpoint.getEndpointNumber() + "\r\n";
//                        returnValue = returnValue + "    Interval: " + mEndpoint.getInterval() + "\r\n";
//                        returnValue = returnValue + "    Packet size: " + mEndpoint.getMaxPacketSize() + "\r\n";
//                        returnValue = returnValue + "    Type: " + mEndpoint.getType() + "\r\n";
//                    }
                }

            log.E(returnValue);
        }

        return returnValue;
    }

    public class MyUsbManager {

        /**
         * Broadcast Action: A sticky broadcast for USB state change events when in device mode.
         * This is a sticky broadcast for clients that includes USB connected/disconnected state,
         * <ul>
         * <li> {@link #USB_CONNECTED} boolean indicating whether USB is connected or disconnected.
         * <li> {@link #USB_CONFIGURED} boolean indicating whether USB is configured. currently zero if not configured, one for configured.
         * <li> {@link #USB_FUNCTION_ADB} boolean extra indicating whether the adb function is enabled
         * <li> {@link #USB_FUNCTION_RNDIS} boolean extra indicating whether the RNDIS ethernet function is enabled
         * <li> {@link #USB_FUNCTION_MTP} boolean extra indicating whether the MTP function is enabled
         * <li> {@link #USB_FUNCTION_PTP} boolean extra indicating whether the PTP function is enabled
         * <li> {@link #USB_FUNCTION_PTP} boolean extra indicating whether the accessory function is enabled
         * <li> {@link #USB_FUNCTION_AUDIO_SOURCE} boolean extra indicating whether the audio source function is enabled
         * <li> {@link #USB_FUNCTION_MIDI} boolean extra indicating whether the MIDI function is enabled
         * </ul>
         */

        public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";

        /**
         * Boolean extra indicating whether USB is connected or disconnected.
         * Used in extras for the {@link #ACTION_USB_STATE} broadcast.
         */
        public static final String USB_CONNECTED = "connected";

        /**
         * Boolean extra indicating whether USB is configured.
         * Used in extras for the {@link #ACTION_USB_STATE} broadcast.
         */
        public static final String USB_CONFIGURED = "configured";

        /**
         * Boolean extra indicating whether confidential user data, such as photos, should be
         * made available on the USB connection. This variable will only be set when the user
         * has explicitly asked for this data to be unlocked.
         * Used in extras for the {@link #ACTION_USB_STATE} broadcast.
         */
        public static final String USB_DATA_UNLOCKED = "unlocked";

        /**
         * A placeholder indicating that no USB function is being specified.
         * Used to distinguish between selecting no function vs. the default function
         */
        public static final String USB_FUNCTION_NONE = "none";

        /**
         * Name of the adb USB function.
         * Used in extras for the {@link #ACTION_USB_STATE} broadcast
         */
        public static final String USB_FUNCTION_ADB = "adb";

        /**
         * Name of the RNDIS ethernet USB function.
         * Used in extras for the {@link #ACTION_USB_STATE} broadcast
         */
        public static final String USB_FUNCTION_RNDIS = "rndis";

        /**
         * Name of the MTP USB function.
         * Used in extras for the {@link #ACTION_USB_STATE} broadcast
         */
        public static final String USB_FUNCTION_MTP = "mtp";

        /**
         * Name of the PTP USB function.
         * Used in extras for the {@link #ACTION_USB_STATE} broadcast
         */
        public static final String USB_FUNCTION_PTP = "ptp";

        /**
         * Name of the audio source USB function.
         * Used in extras for the {@link #ACTION_USB_STATE} broadcast
         */
        public static final String USB_FUNCTION_AUDIO_SOURCE = "audio_source";

        /**
         * Name of the MIDI USB function.
         * Used in extras for the {@link #ACTION_USB_STATE} broadcast
         */
        public static final String USB_FUNCTION_MIDI = "midi";
    }

    FileInputStream in=null;
    public boolean isSupportMediaCodecHardDecoder(){
        boolean is_exist = false;
        try {
            // 判断4.1版本以上是否有硬解功能
            Class<?> aClass = Class.forName("android.media.MediaCodecList");

            is_exist = true;
        } catch (ClassNotFoundException e) {
           e.printStackTrace();
        }
        return is_exist;
    }
}
