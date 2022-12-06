package com.geek.biz1.bean;

import java.io.Serializable;

public class FshengjiBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String apkPath;
    private String serverVersionCode;
    private String serverVersionName;
    private String appPackageName;
    private String updateInfoTitle;
    private String updateInfo;
    private String md5;
    private boolean isForce;
    private String applicationLable;

    public FshengjiBean() {
    }

    public FshengjiBean(String apkPath, String serverVersionCode, String serverVersionName, String appPackageName,
                        String updateInfoTitle, String updateInfo, String md5, boolean isForce, String applicationLable) {
        this.apkPath = apkPath;
        this.serverVersionCode = serverVersionCode;
        this.serverVersionName = serverVersionName;
        this.appPackageName = appPackageName;
        this.updateInfoTitle = updateInfoTitle;
        this.updateInfo = updateInfo;
        this.md5 = md5;
        this.isForce = isForce;
        this.applicationLable = applicationLable;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public String getServerVersionCode() {
        return serverVersionCode;
    }

    public void setServerVersionCode(String serverVersionCode) {
        this.serverVersionCode = serverVersionCode;
    }

    public String getServerVersionName() {
        return serverVersionName;
    }

    public void setServerVersionName(String serverVersionName) {
        this.serverVersionName = serverVersionName;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getUpdateInfoTitle() {
        return updateInfoTitle;
    }

    public void setUpdateInfoTitle(String updateInfoTitle) {
        this.updateInfoTitle = updateInfoTitle;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public boolean getIsForce() {
        return isForce;
    }

    public void setIsForce(boolean isForce) {
        this.isForce = isForce;
    }

    public String getApplicationLable() {
        return applicationLable;
    }

    public void setApplicationLable(String applicationLable) {
        this.applicationLable = applicationLable;
    }
}
