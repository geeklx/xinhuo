package com.geek.appcommon.web.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.geek.appcommon.SlbBase;
import com.geek.appcommon.web.fragment.H5WebFragment;
import com.geek.common.R;
import com.geek.libutils.app.FragmentHelper;

/**
 * @author fosung
 */
public class H5WebAct extends SlbBase {

    private H5WebFragment mFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gztfragmentshouye;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);

        Bundle bundle = new Bundle();
        String url = getIntent().getStringExtra("url");
        boolean isSHowToolBar = getIntent().getBooleanExtra(H5WebFragment.IS_SHOW_TOOLBAR, false);
        url = url.replace("===", "#");
        bundle.putString("url", url);
        bundle.putBoolean(H5WebFragment.IS_SHOW_TOOLBAR, isSHowToolBar);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_framelayout, mFragment = FragmentHelper.newFragment(H5WebFragment.class, bundle))
                .show(mFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (mFragment == null || !mFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    //      Intent appLinkIntent = getIntent();
//      String url = appLinkIntent.getStringExtra("url");
//      String url = "http://10.254.119.22:7102//#/serviceMap";
//        if (appLinkIntent != null) {
//            String appLinkAction = appLinkIntent.getAction();
//            if (appLinkAction != null) {
//                Uri appLinkData = appLinkIntent.getData();
//                if (appLinkData != null) {
//                  String url = appLinkData.getQueryParameter("url");
//                  String bbbb = appLinkData.getQueryParameter("query2");
//                }
//            }
//        }
}
