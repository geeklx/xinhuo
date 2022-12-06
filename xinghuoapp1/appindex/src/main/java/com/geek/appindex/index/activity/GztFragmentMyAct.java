package com.geek.appindex.index.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.geek.appcommon.SlbBase;
import com.geek.appindex.R;
import com.geek.appindex.index.fragment.ShouyeF6;

public class GztFragmentMyAct extends SlbBase {
    private Fragment mFragment = null;

    private final static String ACTION = "GztFragmentMyAct";

//    public class MessageReceiverIndex extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            try {
//                if ("ACTION".equals(intent.getAction())) {
//                    //点击item
//                    if (mFragment != null) {
//                        ShouyeF6 f = (ShouyeF6) mFragment;
//
//                    }
//                }
//            } catch (Exception e) {
//            }
//        }
//    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gztfragmentshouye;
    }

//    registerReceiver(mReceiver, new IntentFilter(ACTION));

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_framelayout, mFragment = ShouyeF6.getInstance(new Bundle()), ShouyeF6.class.getName())
                .show(mFragment)
                .commit();
    }
}
