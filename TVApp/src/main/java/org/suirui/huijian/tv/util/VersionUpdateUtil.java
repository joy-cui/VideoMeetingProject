package org.suirui.huijian.tv.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;
import com.srpaas.version.contant.VersionConfigure;
import com.srpaas.version.entry.VersionInfo;
import com.srpaas.version.event.DownloadEvent;
import com.srpaas.version.service.VSRClient;
import com.srpaas.version.util.InstallUtil;
import com.srpaas.version.view.IVersionView;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.common.permission.AndPermission;
import com.suirui.srpaas.common.permission.Rationale;
import com.suirui.srpaas.common.permission.RationaleListener;
import com.suirui.srpaas.video.model.impl.ConfigureModelImpl;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.util.StringUtil;
import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.TVSrHuiJianProperties;
import org.suirui.huijian.tv.widget.CustomDialog;
import org.suirui.huijian.tv.widget.VersionUpdateDialog;
import org.suirui.huijian.tv.widget.WaitingDialog;
import org.suirui.huijian.tv.widget.WarningDialog;
import org.suirui.srpaas.contant.SRPaaSdkConfigure;

import java.util.Observable;
import java.util.Observer;

/**
 * 版本更新工具类
 *http://192.168.61.39/huijian4/v1/version/checkversion?versionNum=4.0.0.71227&clientType=android_tv
 * @author dingna
 */
public class VersionUpdateUtil implements IVersionView,Observer {
    private static final String TAG = VersionUpdateUtil.class.getName();
    private static final SRLog log = new SRLog(TAG, TVAppConfigure.LOG_LEVE);
    private static VersionUpdateUtil instance;
    private CheckVersionListener versionListener;
    private Context mContext;
    private boolean isExit = false;//是否存在版本更新弹框
    private VersionUpdateDialog mWaitingDialog;
    private android.os.Handler mHandler = new android.os.Handler();

    public VersionUpdateUtil(Context context) {
        DownloadEvent.getInstance().addObserver(this);
        VSRClient.init(context);
        this.mContext = context;
    }

    public synchronized static VersionUpdateUtil getInstance(Context context) {
        if (instance == null) {
            instance = new VersionUpdateUtil(context);

        }
        return instance;
    }

    public void onDestory(){
        if (instance != null) {
            instance = null;
        }
    }

    public void addCheckPermissionListener(CheckVersionListener listener) {
        this.versionListener = listener;
    }

    public void removeCheckPermissionListener() {
        versionListener = null;
    }

    public void doDownloadVersion(String authorities,boolean isShowNotification) {
        log.E("doDownloadVersion():"+(VSRClient.getVersionService() == null));
        showWaitingDialog();
        if(VSRClient.getVersionService() == null){
            VSRClient.init(mContext);
        }
        VSRClient.getVersionService().downloadVersion(mContext,authorities,isShowNotification);
    }

    /**
     * 检查版本更新
     *
     * @param
     * @return
     */
    public void checkVersion() {
        VersionInfo versionInfo = new VersionInfo();
        String num = AppUtil.getInstance().getAppVersionName(mContext);
        versionInfo.setLocalVersion(num);//versionNum
        if(AppUtil.getInstance().isShowRootUI()){
            versionInfo.setClientType(TVAppConfigure.CLIETN_TYPE_SYSTEM);//clientType
        }else {
            versionInfo.setClientType(TVAppConfigure.CLIETN_TYPE);//clientType
        }
        setDoMain(ThirdApi.getIntance(mContext).getSharePreferDoMain(TVAppConfigure.DO_MAIN));
        versionInfo.setHttpUrl(getDoMain() + TVAppConfigure.VERSION_URL);
        log.E("检查版本更新...doMain:" + getDoMain() + TVAppConfigure.VERSION_URL);
        VSRClient.getVersionService().reqVersionUpdate(versionInfo, this);
    }

    private String getDoMain() {
        SharedPreferences sp = mContext.getSharedPreferences(ConfigureModelImpl.SPData.SPDATA, Context.MODE_MULTI_PROCESS);
        String doMain = sp.getString(ConfigureModelImpl.SPData.DO_MAIN, "");
        log.E("getDoMain...doMain:" + doMain);
        return doMain;
    }

    private void setDoMain(String doMain) {
        if (!StringUtil.isEmpty(doMain)) {
            SharedPreferences sp = mContext.getSharedPreferences(ConfigureModelImpl.SPData.SPDATA, Context.MODE_MULTI_PROCESS);
            String doMainStr = sp.getString(ConfigureModelImpl.SPData.DO_MAIN, "");
            //by test解决没有登录模块时，初次安装app时doMainStr为空
            if (StringUtil.isEmpty(doMainStr)) {
                SharedPreferences.Editor editor = sp.edit(); //获取编辑器
                editor.putString(ConfigureModelImpl.SPData.DO_MAIN, doMain);
                log.E("setDoMain.....doMain:" + doMain + "  doMainStr:" + doMainStr);
                editor.commit();
            } else {
                if (!doMainStr.equals(doMain)) {
                    SharedPreferences.Editor editor = sp.edit(); //获取编辑器
                    editor.putString(ConfigureModelImpl.SPData.DO_MAIN, doMainStr);
                    log.E("setDoMain.....doMain:" + doMain + "  doMainStr:" + doMainStr);
                    editor.commit();
                }
            }
        }
    }

    @Override
    public void showError(int eType, String msg) {
        log.E("VersionUpdateUtil...showError...eType:" + eType + "  msg:" + msg);
        if (eType == 1) {
            Toast.makeText(mContext, "版本更新服务器字段错误", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMsgState(int updateState) {
        log.E("VersionUpdateUtil....showMsgState:" + updateState + "serverVersion"+VSRClient.getVersionService().getServerVersion());
            switch (updateState) {
                case VersionConfigure.Update.v_force_update:
//                    openCustomDowmLoad(VersionConfigure.Version.isForce);
                    break;
                case VersionConfigure.Update.v_no_force_update:
//                    openCustomDowmLoad(VersionConfigure.Version.isNForce);
                    break;
                case VersionConfigure.Update.v_no_update:
                    log.E("VersionUpdateUtil....已经是最新版本");
//                    Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_SHORT).show();
                    break;
            }

    }

    /**
     * 下载提示框
     *
     * @param isforce
     */
    private void openCustomDowmLoad(int isforce) {
        if (isExit)
            return;
        try {
            final String versionNum = VSRClient.getVersionService().getServerVersion();
            String updatelog = VSRClient.getVersionService().getUpdateLog();
            final CustomDialog forceDialog = new CustomDialog(mContext, R.style.sr_custom_dialog, isforce, versionNum, updatelog);
            forceDialog.show();
            isExit = true;
            forceDialog.setClicklistener(new CustomDialog.ClickListenerInterface() {
                @Override
                public void onCancel() {
                    forceDialog.dismiss();
                    isExit = false;
                }

                @Override
                public void onUpdate() {
                    if (!AndPermission.hasPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AndPermission.with((Activity) mContext)
                                .requestCode(TVAppConfigure.permission.WRITE_EXTERNAL_STORAGE)
                                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .rationale(
                                        new RationaleListener() {
                                            @Override
                                            public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                                // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                                                AndPermission.rationaleDialog((Activity) mContext, rationale).show();
                                            }
                                        }
                                ).send();
                    } else {
                        doDownloadVersion(mContext.getString(R.string.authoritise),true);
                    }
//                    if (versionListener != null) {
//                        versionListener.onCheckPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, AppConfigure.permission.WRITE_EXTERNAL_STORAGE);
//                    }
                }
            });
        } catch (Exception e) {
            StringUtil.Exceptionex(TAG, "***openCustomDowmLoad***Exception***", e);
        }
    }


    private void exitApp() {
        DownloadEvent.getInstance().deleteObserver(this);
        VSRClient.getRemoveVersionService();
        if (versionListener != null)
            versionListener.exitApp();
    }


    private void showWaitingDialog(){
        if(mWaitingDialog == null){
            mWaitingDialog = new VersionUpdateDialog(mContext);
        }
        mWaitingDialog.show();
        mWaitingDialog.setPercent(0);
        setGradualColor(Color.RED, Color.GREEN);
    }
    private void dismissWaitingDialog(){
        if(mWaitingDialog != null){
            mWaitingDialog.dismiss();
        }
    }

    public void showPercent(final int percent) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mWaitingDialog != null){
                    mWaitingDialog.setPercent(percent);
                }else {
                    log.E("showPercent.....mWaitingDialog is null...");
                }
            }
        });

    }

    public void setGradualColor(Object startValue, Object endValue) {
        if(mWaitingDialog != null) {
            mWaitingDialog.setGradualColor(startValue, endValue);
        }
    }

//    public void showCenterImg(final int resId) {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                if(mWaitingDialog != null){
//                    mWaitingDialog.setCenterImg(resId);
//                }
//            }
//        });
//
//    }

    public void setWaitingDialogTips(final int tipsId){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mWaitingDialog != null){
                    mWaitingDialog.setTips(tipsId);
                }else {
                    log.E("setWaitingDialogTips.....mWaitingDialog is null...");
                }
            }
        });

    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof DownloadEvent) {
            final DownloadEvent.NotifyCmd cmd = (DownloadEvent.NotifyCmd) data;
            log.E("Versionupdate......cmd.type..." + cmd.type);
            switch (cmd.type) {
                case start_exit_app:
//                    exitApp();
//                      android.os.Process.killProcess(android.os.Process.myPid());
//                      System.exit(0);
                    break;
                case update_version_msgex:
//                    openCustomDowmLoad(VersionConfigure.Version.isReload);
                    break;
                case show_download_app:

                    break;

                case apk_download_complete:
                    if(InstallUtil.hasRootPerssion()){
                        if(TVSrHuiJianProperties.getIsShowRootUI()){
                            setWaitingDialogTips(R.string.m_loading_copy);
                            showPercent(0);
                            setGradualColor(Color.GREEN, Color.BLUE);
                        }else{
                            showPercent(100);
                            log.E("Versionupdate......(int) apk_download_complete...111m_installing" );
                            setWaitingDialogTips(R.string.m_installing);
                            showPercent(0);
                            setGradualColor(Color.GREEN, Color.BLUE);
                        }
                    }else{
                        dismissWaitingDialog();
                    }

                    break;
                case unZipError:
//                    dismissWaitingDialog();
//                    AppUtil.getInstance().showWaringDialog(mContext,R.string.m_un_zip_error);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            final WarningDialog dialog = new WarningDialog(mContext);
                            dialog.setWarningMsg(R.string.m_un_zip_error);
                            dialog.show();
                            dialog.setButton2(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dismissWaitingDialog();
                                    dialog.dismiss();
                                }
                            });
                        }
                    });

                    break;
                case unZiping:
                    int unZipPer = (int) cmd.data;
                    log.E("Versionupdate......(int) unZipPer..." + unZipPer);
                    showPercent(unZipPer);
                    break;
                case unZipComplete:
                    setGradualColor(Color.BLUE, Color.RED);
                    break;
                case installing:
                    int installPer = (int) cmd.data;
                    log.E("Versionupdate......(int) installPer..." + installPer);
                    if(InstallUtil.hasRootPerssion()){
                        if(TVSrHuiJianProperties.getIsShowRootUI()){
                            if(installPer ==100){
                                showPercent(installPer);
                                setWaitingDialogTips(R.string.m_install_sys_complete);
                            }else {
                                setWaitingDialogTips(R.string.m_installing);
                                showPercent(installPer);
                            }
                        }else{
                            if(installPer ==100){
                                showPercent(installPer);
                                setWaitingDialogTips(R.string.m_install_complete);
                            }else {
                                setWaitingDialogTips(R.string.m_installing);
                                showPercent(installPer);
                            }

                        }
                    }

                    break;
                case installComplete:
                    boolean isSuccess = (boolean) cmd.data;
                    if(isSuccess){
                        showPercent(100);
//                        showCenterImg(R.drawable.m_upgrade_ok);
                        setWaitingDialogTips(R.string.m_install_complete);
                    }else {
//                        dismissWaitingDialog();
//                        AppUtil.getInstance().showWaringDialog(mContext,R.string.m_install_error);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                final WarningDialog dialog = new WarningDialog(mContext);
                                dialog.setWarningMsg(R.string.m_install_error);
                                dialog.show();
                                dialog.setButton2(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dismissWaitingDialog();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                    }

                    break;
                case update_notification_precent:
                    int percent = (int) cmd.data;
                    log.E("Versionupdate......(int) percent..." + percent);
                    showPercent(percent);

//                    NativeCallJsHandler.callJsHander(webView, ToJson.getInstance().NativeJsBaseJson(percent, Configure.CallJSMethod.appDownloadPercent, ""));
//                    setUpdateState(VersionConfigure.Update.v_downloading_update);
//                    setDownloadPrecent((int) cmd.data);
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            log.E("VersionPresenterImpl.....updateNotification...DownloadEvent...");
//                            NotificationUtil.getInstance().updateNotification();
//                        }
//                    });
                    break;

                case e_no_sdcard:
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, mContext.getResources().getString(com.srpaas.version.R.string.sr_no_sdcard), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case e_no_sdcard_space:
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, mContext.getResources().getString(com.srpaas.version.R.string.sr_no_space_sdcard), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
        }
    }


    public interface CheckVersionListener {
        /**
         * 检查获取写文件的权限
         */
//        void onCheckPermission(String permission, int requestCode);

        /**
         * 更新下载完成，退出app，走安装流程
         */
        void exitApp();

        /**
         * 版本更新的弹窗
         *
         * @param info
         */
        void openDialog(VersionInfo info);
    }
}
