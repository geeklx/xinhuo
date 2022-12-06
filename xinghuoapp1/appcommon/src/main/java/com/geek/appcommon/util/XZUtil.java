package com.geek.appcommon.util;

import java.util.regex.Pattern;

public class XZUtil {

    public static boolean validatePassword(String password){
        //大小写、数字匹配
        String x = "^(?=.*?[A-Z])(?=.*?[0-9])[a-zA-Z0-9]{8,18}$";
//        x = "^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_]+$)(?![a-z0-9]+$)(?![a-z\\W_]+$)(?![0-9\\W_]+$)[a-zA-Z0-9\\W_]{8,16}$";//4选三
        return Pattern.matches(x, password);
    }
}
