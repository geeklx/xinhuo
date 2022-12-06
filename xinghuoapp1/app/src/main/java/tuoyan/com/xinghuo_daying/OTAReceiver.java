package tuoyan.com.xinghuo_daying;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.AppUtils;

public class OTAReceiver extends BroadcastReceiver {
    public OTAReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent i = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WelComeActivity");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}