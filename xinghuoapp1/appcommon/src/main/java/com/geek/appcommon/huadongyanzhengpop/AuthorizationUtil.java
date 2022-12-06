package com.geek.appcommon.huadongyanzhengpop;

import android.util.Base64;

public class AuthorizationUtil {

    public static String getAuthStr() {
        String auth = "dtdjzx-app" + ":" + "kqUaCLWM&6DyC$ho";
        String strBase64 = Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);
        return "Basic " + strBase64;
    }

}
