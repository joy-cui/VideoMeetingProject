package org.suirui.huijian.box.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.srpaas.version.contant.VersionConfigure;
import com.srpaas.version.entry.VersionInfo;
import com.srpaas.version.event.DownloadEvent;
import com.srpaas.version.service.VSRClient;
import com.srpaas.version.view.IVersionView;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.common.permission.AndPermission;
import com.suirui.srpaas.common.permission.Rationale;
import com.suirui.srpaas.common.permission.RationaleListener;
import com.suirui.srpaas.video.model.impl.ConfigureModelImpl;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.util.StringUtil;
import com.suirui.srpaas.video.util.ToolsUtil;
import com.suirui.srpaas.video.util.TvToolsUtil;

import org.suirui.huijian.box.AppConfigure;
import org.suirui.huijian.box.R;
import org.suirui.huijian.box.dialog.CustomDialog;

import java.util.Observable;
import java.util.Observer;

/**
 * 版本更新工具类
 *
 * @author dingna
 */
public class VersionUpdateUtil implements IVersionView, Observer {
    private static final String TAG = VersionUpdateUtil.class.getName();
    private static final SRLog log = new SRLog(TAG, AppConfigure.LOG_LEVE);
    private static VersionUpdateUtil instance;
    private CheckVersionListener versionListener;
    private Context mContext;
    private boolean isExit = false;//是否存在版本更新弹框
    private boolean isHtml = false;//当前是否是H5的界面（显示不同的版本更新弹框）

    public VersionUpdateUtil() {
        DownloadEvent.getInstance().addObserver(this);
    }

    public synchronized static VersionUpdateUtil getInstance() {
        if (instance == null) {
            instance = new VersionUpdateUtil();
        }
        return instance;
    }

    public void addCheckPermissionListener(CheckVersionListener listener) {
        this.versionListener = listener;
    }

    public void removeCheckPermissionListener() {
        versionListener = null;
    }

    public void doDownloadVersion(Context context) {
        VSRClient.getVersionService().downloadVersion(context);
    }

    /**
     * 检查版本更新
     *
     * @param context
     * @param
     * @return
     */
    public void doAppUpdate(Context context, boolean isHtml) {
        this.mContext = context;
        this.isHtml = isHtml;
        VSRClient.init(context);
        VersionInfo versionInfo = new VersionInfo();
        String num = ToolsUtil.getCurrentVersionNum();
        versionInfo.setLocalVersion(num);//versionNum
        versionInfo.setClientType(AppConfigure.CLIETN_TYPE);//clientType
        setDoMain(ThirdApi.getIntance(context).getSharePreferDoMain(AppConfigure.DO_MAIN));
        versionInfo.setHttpUrl(getDoMain() + AppConfigure.VERSION_URL);
        log.E("检查版本更新...doMain:" + getDoMain() + AppConfigure.VERSION_URL);
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
        log.E("VersionUpdateUtil....showMsgState:" + updateState + " isHtml:" + isHtml);
        if (!isHtml) {
            switch (updateState) {
                case VersionConfigure.Update.v_force_update:
                    openCustomDowmLoad(VersionConfigure.Version.isForce);
                    break;
                case VersionConfigure.Update.v_no_force_update:
                    openCustomDowmLoad(VersionConfigure.Version.isNForce);
                    break;
                case VersionConfigure.Update.v_no_update:
                    log.E("VersionUpdateUtil....已经是最新版本");
//                    Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            if (versionListener != null) {
                VersionInfo info = new VersionInfo();
                info.setIsForce(String.valueOf(updateState));
                info.setServerVersion(VSRClient.getVersionService().getServerVersion());
                info.setLocalVersion(VSRClient.getVersionService().getLocalVersion());
                info.setUpdateLog(VSRClient.getVersionService().getUpdateLog());
                log.E("检查版本更新...info:" + new Gson().toJson(info));
                versionListener.openDialog(info);
            }
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
                                .requestCode(AppConfigure.permission.WRITE_EXTERNAL_STORAGE)
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
                        VersionUpdateUtil.getInstance().doDownloadVersion(mContext);
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

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof DownloadEvent) {
            final DownloadEvent.NotifyCmd cmd = (DownloadEvent.NotifyCmd) data;
            switch (cmd.type) {
                case start_exit_app:
                    exitApp();
                    break;
                case update_version_msgex:
                    openCustomDowmLoad(VersionConfigure.Version.isReload);
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
