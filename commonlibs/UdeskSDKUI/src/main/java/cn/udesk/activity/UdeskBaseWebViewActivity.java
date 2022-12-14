package cn.udesk.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import cn.udesk.R;
import cn.udesk.widget.UdeskTitleBar;


public class UdeskBaseWebViewActivity extends UdeskBaseActivity {

    protected WebView mwebView;
    protected LinearLayout linearLayout;
    protected UdeskTitleBar mTitlebar;
    protected UdeskWebChromeClient udeskWebChromeClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.udesk_base_webview);
        initView();
    }

    private void initView() {
        try {
            udeskWebChromeClient = new UdeskWebChromeClient(this, new UdeskWebChromeClient.ICloseWindow() {
                @Override
                public void closeActivty() {
                    finish();
                }
            });
            linearLayout = (LinearLayout) findViewById(R.id.udesk_webview_root);
            mTitlebar = (UdeskTitleBar) findViewById(R.id.udesktitlebar);
            mwebView = new WebView(this);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.
                    LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
            mwebView.setLayoutParams(param);
            linearLayout.addView(mwebView);
            settingWebView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setH5TitleListener(UdeskWebChromeClient.GetH5Title h5TitleListener) {
        try {
            udeskWebChromeClient.setH5TitleListener(h5TitleListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    private void settingWebView() {

        try {
            //????????????????????????????????????????????????????????????
            mwebView.requestFocusFromTouch();
            mwebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            mwebView.setScrollbarFadingEnabled(false);

            final WebSettings settings = mwebView.getSettings();
            settings.setJavaScriptEnabled(true);  //??????js
            mwebView.removeJavascriptInterface("searchBoxJavaBridge_");
            mwebView.removeJavascriptInterface("accessibilityTraversal");
            mwebView.removeJavascriptInterface("accessibility");
            //  ????????????????????????????????????
            settings.setUseWideViewPort(true); //????????????????????????webview?????????
            settings.setLoadWithOverviewMode(true); // ????????????????????????
            settings.supportMultipleWindows();  //?????????
            settings.setAllowFileAccess(true);  //????????????????????????
            settings.setNeedInitialFocus(true); //???webview??????requestFocus??????webview????????????

            settings.setJavaScriptCanOpenWindowsAutomatically(true); //????????????JS???????????????
            settings.setSavePassword(false);
            //??????????????????
            settings.setDefaultTextEncodingName("UTF-8");
            // ??????????????????
            settings.setDisplayZoomControls(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

            }
            settings.setLoadsImagesAutomatically(true);  //????????????????????????

            settings.setDomStorageEnabled(true); //??????DOM Storage

            mwebView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    // ???????????????????????????????????????????????????????????????????????????????????????????????????
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    finish();
                }
            });

            mwebView.setWebChromeClient(udeskWebChromeClient);
            mwebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                    handler.proceed();
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Uri uri = Uri.parse(url);
                    if (url.contains("tel:")) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                        startActivity(intent);
                    } else if (url.contains("show_transfer")) {
                        showTransfer();
                        return true;
                    } else if (url.contains("go_chat")) {
                        goChat();
                    } else if (url.contains("auto_transfer")){
                        autoTransfer();
                    }else if (!(url.startsWith("http://")||url.startsWith("https://"))){
                        return super.shouldOverrideUrlLoading(view,url);
                    }else {
                        view.loadUrl(url);
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showTransfer() {

    }

    protected void goChat() {

    }

    protected void autoTransfer() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            udeskWebChromeClient.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (mwebView != null) {
                ViewParent parent = mwebView.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(mwebView);
                }
                mwebView.removeAllViews();
                mwebView.destroy();
                mwebView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
