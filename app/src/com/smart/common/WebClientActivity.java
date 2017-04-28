package com.smart.common;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.app.src.CoreMaster;
import com.smartprime.R;

public class WebClientActivity extends CoreMaster {

    private WebView webViewClient;

    private ProgressBar progress;

    private String IN_LINK;
    private String IN_CONTENT;
    private String IN_HEADER_TITLE;

    @Override
    public int getLayoutID() {
        return R.layout.webview_client;
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public void initComponents() {
        getIntentData();

        webViewClient = (WebView) findViewById(R.id.webViewClient);

        progress = (ProgressBar) findViewById(R.id.progress);

        WebSettings settings = webViewClient.getSettings();
        // Enable Javascript
        settings.setJavaScriptEnabled(true);

//        // Use WideViewport and Zoom out if there is no viewport defined
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);
            settings.setAllowFileAccessFromFileURLs(true);
        }

        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadsImagesAutomatically(true);

        webViewClient.addJavascriptInterface(new JSInterface(), "parent");
        webViewClient.setWebChromeClient(new WebChromeClient());
        webViewClient.setWebViewClient(new CheckoutWebClient());

        if (!TextUtils.isEmpty(IN_LINK)) {

            webViewClient.loadUrl(IN_LINK);
        } else if (!TextUtils.isEmpty(IN_CONTENT)) {
            IN_CONTENT=IN_CONTENT.replaceAll("\n","<br>");
            webViewClient.loadData(IN_CONTENT, "text/html; charset=UTF-8", null);
        }
    }


    @Override
    public void prepareViews() {

    }

    @Override
    public void setActionListeners() {

    }

    private void getIntentData() {

        IN_LINK = getIntent().getStringExtra("IN_LINK") == null ? "" : getIntent().getStringExtra("IN_LINK");
        IN_CONTENT = getIntent().getStringExtra("IN_CONTENT") == null ? "" : getIntent().getStringExtra("IN_CONTENT");
        IN_HEADER_TITLE = getIntent().getStringExtra("IN_HEADER_TITLE") == null ?
                getString(R.string.app_name) : getIntent().getStringExtra("IN_HEADER_TITLE");
    }

    @Override
    public void onBackPressed() {
        if (webViewClient.canGoBack()) {

            webViewClient.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private class CheckoutWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            ShowProgress();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            Log.v("@@@@URL LOADED::", url);

            view.loadUrl(url);

            return false;
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            AlertDialog.Builder builder = new AlertDialog.Builder(WebClientActivity.this);
            builder.setTitle(R.string.invalid_untrusted_certificate);
            builder.setMessage(R.string.you_re_accessing_a_page_with_an_untrusted_or_invalid_certificate_do_you_want_to_continue);

            builder.setNegativeButton(R.string.cancel_ssl, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            builder.setPositiveButton(R.string.continue_ssl, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideProgress();

            String cookies = CookieManager.getInstance().getCookie(url);
        }
    }

    public class JSInterface {

        @JavascriptInterface
        public void postMessage(String arg1, String arg2) {
            Log.i("TEST1", arg1);
            Log.i("TEST2", arg2);
        }
    }

    private void ShowProgress() {

        progress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {

        progress.setVisibility(View.GONE);
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(IN_HEADER_TITLE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });
    }
}
