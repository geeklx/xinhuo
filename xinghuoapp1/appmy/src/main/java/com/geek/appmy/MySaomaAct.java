package com.geek.appmy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.libutils.data.MmkvUtils;
import com.geek.zxinglibs3.Saoma3CommonScanActivity2;
import com.google.zxing.Result;


public class MySaomaAct extends Saoma3CommonScanActivity2 {

    public static final String TAG_people = "com.geek.appindex.index.activity.GztFragmentPeopleAct?condition=login";

    @Override
    protected void setup() {
    }

    @Override
    protected void setOnScanResult(Result rawResult, Bundle bundle) {
        String imtType = MmkvUtils.getInstance().get_common("choose_im");
        Intent intent = new Intent();
        if (imtType.equals(TAG_people)) {
            intent.setAction("com.tencent.qcloud.tuikit.tuicontact.addmoreactivity");
//            intent.putExtra(TUIContactConstants.GroupType.GROUP, false);
//            intent.putExtra(TUIContactConstants.GroupType.USERID, rawResult.getText());
        } else {
            intent.setAction(AppUtils.getAppPackageName() + ".hs.act.slbapp.DTRXEmployeeSearchActivity");
            intent.putExtra("keyword", rawResult.getText());
        }
        startActivity(intent);
        finish();
    }
}
