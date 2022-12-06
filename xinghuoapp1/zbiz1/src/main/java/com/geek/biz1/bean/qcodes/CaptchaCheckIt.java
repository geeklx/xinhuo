package com.geek.biz1.bean.qcodes;

import java.io.Serializable;

/**
 * Date:2020/5/18
 * author:wuyan
 */
public class CaptchaCheckIt implements Serializable {
    private boolean success;
    private String repCode;
    private String repMsg;
    private DataBean repData;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getRepCode() {
        return repCode;
    }

    public void setRepCode(String repCode) {
        this.repCode = repCode;
    }

    public String getRepMsg() {
        return repMsg;
    }

    public void setRepMsg(String repMsg) {
        this.repMsg = repMsg;
    }

    public DataBean getRepData() {
        return repData;
    }

    public void setRepData(DataBean repData) {
        this.repData = repData;
    }

    public static class DataBean implements Serializable {

        private String captchaType;
        private String token;
        private boolean result;
        private boolean opAdmin;

        public String getCaptchaType() {
            return captchaType;
        }

        public void setCaptchaType(String captchaType) {
            this.captchaType = captchaType;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public boolean isOpAdmin() {
            return opAdmin;
        }

        public void setOpAdmin(boolean opAdmin) {
            this.opAdmin = opAdmin;
        }
    }
}
