package com.geek.appcommon.util;

import android.content.Context;

import com.haier.cellarette.baselibrary.toasts3.MProgressDialog;
import com.haier.cellarette.baselibrary.toasts3.config.MDialogConfig;

public class MProgressDialogUtils {

    public static void showMprogressDialog(Context context, String message) {
        MDialogConfig.Builder builder = new MDialogConfig.Builder();
        builder.isCancelable(true);
        MProgressDialog.showProgress(context, message, builder.build());
    }

    public static void onDesMprogressDialog() {
        MProgressDialog.dismissProgress();
    }

}
