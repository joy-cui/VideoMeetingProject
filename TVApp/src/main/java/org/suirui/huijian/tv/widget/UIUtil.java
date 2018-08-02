package org.suirui.huijian.tv.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.Build.VERSION;
import android.os.PowerManager.WakeLock;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import org.suirui.srpaas.util.StringUtil;

public class UIUtil {
    private static final String TAG = UIUtil.class.getSimpleName();
    private static final int MIUI_VERSION_NOT_SET = -2;
    public static final int MIUI_VERSION_UNDEFINED = -1;
    public static final int MIUI_VERSION_V2 = 0;
    public static final int MIUI_VERSION_V3 = 1;
    public static final int MIUI_VERSION_V4 = 2;
    public static final int MIUI_VERSION_V5 = 3;
    public static final int MIUI_VERSION_V6 = 4;
    private static int gMiuiVersionCode = -2;
    private static WakeLock gProxiWakeLock;

    public UIUtil() {
    }

    //定义函数动态控制listView的高度
    /**
     *
     * @param context
     * @param listView
     * @param itemH 设置几个Item高
     */
    public static void setListViewHeightBasedOnChildren(Context context,ListView listView,int itemH) {
        // 获取listview的适配器
        ListAdapter listAdapter = listView.getAdapter(); // item的高度
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            if (i > itemH) {
                break;
            }
            View listItem = listAdapter.getView(i, null, listView);

            listItem.measure(0, 0); // 计算子项View 的宽高 //统计所有子项的总高度
            int mH = listItem.getMeasuredHeight();
            int dH = listView.getDividerHeight();
            totalHeight += UIUtil.dip2px(context,mH)+ dH;
//			 Log.e("","totalHeight:"+totalHeight
//			 + "  mH: "+mH
//			 + "  dH:"+dH
//			 + " i:"+i
//			 +"  size:"+listAdapter.getCount()
//			 );
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;
        listView.setLayoutParams(params);
    }

    public static float px2dp(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float dp2px(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float dip2px(Context context, float value) {
        if(context == null) {
            return (int)value;
        } else {
            float density = context.getResources().getDisplayMetrics().density;
            return (float)(value * density + 0.5F);
        }
    }

    public static float px2dip(Context context, int value) {
        if(context == null) {
            return (float)value;
        } else {
            float density = context.getResources().getDisplayMetrics().density;
            return (float)value / density;
        }
    }

    public static int sp2px(Context context, float value) {
        if(context == null) {
            return (int)value;
        } else {
            float density = context.getResources().getDisplayMetrics().scaledDensity;
            return (int)(value * density + 0.5F);
        }
    }

    public static float px2sp(Context context, int value) {
        if(context == null) {
            return (float)value;
        } else {
            float density = context.getResources().getDisplayMetrics().scaledDensity;
            return (float)value / density;
        }
    }

    public static float getDisplayMinWidthInDip(Context context) {
        int width = getDisplayWidth(context);
        int height = getDisplayHeight(context);
        width = Math.min(width, height);
        return px2dip(context, width);
    }

    public static void closeSoftKeyboard(Context context, View view) {
        if(context != null && view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void openSoftKeyboard(Context context, View view) {
        if(context != null && view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
        }
    }

    public static void setWindowSize(Activity activity) {
        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        Window window = activity.getWindow();
        LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的1.0
        p.width = (int) (d.getWidth() * 0.6); // 宽度设置为屏幕的0.8
//		p.alpha = 1.0f; // 设置本身透明度
//		p.dimAmount = 0.0f; // 设置黑暗度

        window.setAttributes(p); // 设置生效
        window.setGravity(Gravity.CENTER); // 设置靠右对齐
    }

    public static void setDialogActivitySize(Activity activity,View view) {
        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        Window window = activity.getWindow();
        int height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的1.0
        int width = (int) (d.getWidth() * 0.6); // 宽度设置为屏幕的0.8
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                width,
                height
        );
//		p.alpha = 1.0f; // 设置本身透明度
//		p.dimAmount = 0.0f; // 设置黑暗度

        view.setLayoutParams(p);
    }

    public static int getDisplayWidth(Context context) {
        WindowManager windowMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        if(windowMgr == null) {
            return 0;
        } else {
            Display display = windowMgr.getDefaultDisplay();
            return display.getWidth();
        }
    }

    public static float getDisplayWidthInDip(Context context) {
        int widthPixels = getDisplayWidth(context);
        return px2dip(context, widthPixels);
    }

    public static int getDisplayHeight(Context context) {
        WindowManager windowMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        if(windowMgr == null) {
            return 0;
        } else {
            Display display = windowMgr.getDefaultDisplay();
            return display.getHeight();
        }
    }

    public static  int getDensityW(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static  int getDensityH(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static float getDisplayHeightInDip(Context context) {
        int heightPixels = getDisplayHeight(context);
        return px2dip(context, heightPixels);
    }

    public static int getCurrentOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    public static boolean isPortraitMode(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == 1;
    }

    public static boolean isLandscapeMode(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == 2;
    }

    public static boolean isXLargeScreen(Context context) {
        Configuration config = context.getResources().getConfiguration();
        int size = config.screenLayout & 15;
        return size >= 4;
    }

    public static boolean isLargeScreen(Context context) {
        Configuration config = context.getResources().getConfiguration();
        int size = config.screenLayout & 15;
        return size >= 3;
    }

    public static String getScreenCategoryName(Context context) {
        Configuration config = context.getResources().getConfiguration();
        int size = config.screenLayout & 15;
        String category = null;
        if(size >= 4) {
            category = "xlarge";
        } else if(size >= 3) {
            category = "large";
        } else if(size >= 2) {
            category = "normal";
        } else {
            category = "small";
        }
        Log.d(TAG, "Screen category : " + category);
        return category;
    }

    public static  void getScreenSizeOfDevice2(Context context) {
        Point point = new Point();
        WindowManager windowMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowMgr.getDefaultDisplay().getRealSize(point);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        double x = Math.pow(point.x/ dm.xdpi, 2);
        double y = Math.pow(point.y / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        Log.d(TAG, "Screen inches : " + screenInches);
    }


    public static  void getDisplayInfomation(Context context) {
        Point point = new Point();
        WindowManager windowMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowMgr.getDefaultDisplay().getSize(point);
        Log.d(TAG,"the screen size is "+point.toString());
        windowMgr.getDefaultDisplay().getRealSize(point);
        Log.d(TAG,"the screen real size is "+point.toString());
    }

    public static Rect getAbsoluteRect(View view) {
        if(view == null) {
            return null;
        } else {
            Rect rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            ViewParent parent = view.getParent();
            if(parent instanceof ViewGroup) {
                Rect rcParent = getAbsoluteRect((ViewGroup)parent);
                rect.left += rcParent.left;
                rect.top += rcParent.top;
                rect.right += rcParent.left;
                rect.bottom += rcParent.top;
            }

            return rect;
        }
    }

    public static boolean openURL(Context context, String url) {
        if(url != null && url.length() != 0) {
            try {
                Uri e = Uri.parse(url);
                String scheme = e.getScheme();
                if(StringUtil.isEmptyOrNull(scheme)) {
                    url = "http://" + url;
                } else {
                    int intent = scheme.length();
                    url = scheme.toLowerCase() + url.substring(intent);
                }

                e = Uri.parse(url);
                Intent intent1 = new Intent();
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                intent1.setAction("android.intent.action.VIEW");
                intent1.addCategory("android.intent.category.BROWSABLE");
                intent1.setData(e);
                context.startActivity(intent1);
                return true;
            } catch (Exception var5) {
//                ZMLog.w(TAG, "cannot open URL url=%s", new Object[]{url});
                return false;
            }
        } else {
            return false;
        }
    }

    public static int getDefaultOrientation(Context context) {
        WindowManager windowMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        if(windowMgr == null) {
            return 0;
        } else {
            Display display = windowMgr.getDefaultDisplay();
            int rotation = display.getRotation();
            int width = 0;
            int height = 0;
            switch(rotation) {
                case 0:
                case 2:
                    width = display.getWidth();
                    height = display.getHeight();
                    break;
                case 1:
                case 3:
                    width = display.getHeight();
                    height = display.getWidth();
            }

            return width > height?0:1;
        }
    }

    public static synchronized void startProximityScreenOffWakeLock(Context context) {
        if(gProxiWakeLock == null || !gProxiWakeLock.isHeld()) {
            try {
                PowerManager e = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
                gProxiWakeLock = e.newWakeLock(32, UIUtil.class.getName() + ":proximitiy");
                if(gProxiWakeLock != null) {
                    gProxiWakeLock.acquire();
                }
            } catch (Exception var2) {
//                ZMLog.w(TAG, var2, "startProximityScreenOffWakeLock failure", new Object[0]);
            }

        }
    }

    public static synchronized void stopProximityScreenOffWakeLock() {
        if(gProxiWakeLock != null) {
            try {
                gProxiWakeLock.release();
            } catch (Exception var1) {
//                ZMLog.w(TAG, var1, "stopProximityScreenOffWakeLock failure", new Object[0]);
            }

            gProxiWakeLock = null;
        }

    }

    public static void buildLinkTextView(TextView view, String text, final OnClickListener listener) {
        if(!StringUtil.isEmptyOrNull(text)) {
            view.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableString spannableString = new SpannableString(text);
            URLSpan urlSpan = new URLSpan("") {
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onClick(v);
                    }

                }
            };
            spannableString.setSpan(urlSpan, 0, text.length(), 33);
            view.setText(spannableString);
        }
    }

//    public static Dialog showSimpleMessageDialog(Activity activity, String title, String message) {
//        if(activity == null) {
//            return null;
//        } else {
//            ZMAlertDialog dlg = (new Builder(activity)).setTitle(title).setMessage(message).setCancelable(true).setPositiveButton(R.string.zm_btn_ok, new android.content.DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                }
//            }).create();
//            dlg.show();
//            return dlg;
//        }

//    	return null;
//    }

//    public static Dialog showSimpleMessageDialog(Activity activity, int title, int message) {
//        if(activity == null) {
//            return null;
//        } else {
//            String sTitle = title > 0?activity.getString(title):null;
//            String sMessage = message > 0?activity.getString(message):null;
//            return showSimpleMessageDialog(activity, sTitle, sMessage);
//        }
//    }

    public static ProgressDialog showSimpleWaitingDialog(Activity activity, String message) {
        if(activity == null) {
            return null;
        } else {
            ProgressDialog dlg = new ProgressDialog(activity);
            dlg.requestWindowFeature(1);
            dlg.setMessage(message);
            dlg.setCanceledOnTouchOutside(false);
            dlg.show();
            return dlg;
        }
    }

    public static ProgressDialog showSimpleWaitingDialog(Activity activity, int message) {
        if(activity == null) {
            return null;
        } else {
            String sMessage = message > 0?activity.getString(message):"";
            return showSimpleWaitingDialog(activity, sMessage);
        }
    }

    @SuppressLint({"NewApi"})
    public static boolean setTranslucentStatus(Activity activity, boolean on) {
        if(VERSION.SDK_INT < 19) {
            return false;
        } else if(activity == null) {
            return false;
        } else {
            Window win = activity.getWindow();
            if(win == null) {
                return false;
            } else {
                win.setFlags(67108864, 67108864);
                return true;
            }
        }
    }

    public static String getSystemProperty(String key, String defValue) {
        if(key == null) {
            return defValue;
        } else {
            Properties properties = new Properties();

            try {
                properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
                return properties.getProperty(key, defValue);
            } catch (Exception var4) {
//                ZMLog.e(TAG, var4, "getSystemProperty exception, key=%s", new Object[]{key});
                return defValue;
            }
        }
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resourceId > 0?resources.getDimensionPixelSize(resourceId):(int)dip2px(context, 25.0F);
    }

    public static int getMIUIVersionCode() {
        if(gMiuiVersionCode == -2) {
            String sMiuiVersionCode = getSystemProperty("ro.miui.ui.version.code", "-1");
            gMiuiVersionCode = -1;

            try {
                gMiuiVersionCode = Integer.parseInt(sMiuiVersionCode);
            } catch (Exception var2) {
//                ZMLog.e(TAG, var2, "isMIUIV6Above, convert MIUI version code to integer failed. sMiuiVersionCode=%s", new Object[]{sMiuiVersionCode});
            }
        }

        return gMiuiVersionCode;
    }

    public static boolean isFlymeOS() {
        String fingerprint = Build.FINGERPRINT;
        return fingerprint == null?false:fingerprint.contains("Meizu");
    }

    public static boolean isEMUI() {
        String emuiVersion = getSystemProperty("ro.build.version.emui", (String)null);
        return !StringUtil.isEmptyOrNull(emuiVersion);
    }

    public static boolean isImmersedModeSupported() {
        return getMIUIVersionCode() >= 4?true:(isFlymeOS() && VERSION.SDK_INT >= 19?true:isEMUI() && VERSION.SDK_INT >= 19);
    }

    public static boolean setStatusBarDarkMode(Activity activity, boolean darkMode) {
        return activity == null?false:(getMIUIVersionCode() >= 4?setStatusBarDarkMode_MIUI(activity, darkMode):(isFlymeOS() && VERSION.SDK_INT >= 19?setStatusBarDarkMode_FlymeOS(activity, darkMode):false));
    }

    private static boolean setStatusBarDarkMode_MIUI(Activity activity, boolean darkMode) {
        Class clazz = activity.getWindow().getClass();

        try {
            boolean darkModeFlag = false;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            if(layoutParams == null) {
                return false;
            } else {
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                if(field == null) {
                    return false;
                } else {
                    int darkModeFlag1 = field.getInt((Object)null);
                    Method extraFlagField = clazz.getMethod("setExtraFlags", new Class[]{Integer.TYPE, Integer.TYPE});
                    if(extraFlagField == null) {
                        return false;
                    } else {
                        extraFlagField.invoke(activity.getWindow(), new Object[]{Integer.valueOf(darkMode?darkModeFlag1:0), Integer.valueOf(darkModeFlag1)});
                        return true;
                    }
                }
            }
        } catch (Exception var7) {
            return false;
        }
    }

    private static boolean setStatusBarDarkMode_FlymeOS(Activity activity, boolean darkMode) {
        Window window = activity.getWindow();
        boolean result = false;
        if(window != null) {
            try {
                LayoutParams lp = window.getAttributes();
                Field darkFlag = LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt((Object)null);
                int value = meizuFlags.getInt(lp);
                if(darkMode) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }

                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception var9) {
                ;
            }
        }

        return result;
    }

    public static boolean setNotificationMessageCount(Notification notification, int count) {
        try {
            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            if(miuiNotificationClass != null) {
                Object miuiNotification = miuiNotificationClass.newInstance();
                Field field = miuiNotification.getClass().getDeclaredField("messageCount");
                field.setAccessible(true);
                field.set(miuiNotification, Integer.valueOf(count));
                field = notification.getClass().getField("extraNotification");
                field.set(notification, miuiNotification);
                return true;
            }
        } catch (Exception var5) {
            ;
        }

        return false;
    }
}
