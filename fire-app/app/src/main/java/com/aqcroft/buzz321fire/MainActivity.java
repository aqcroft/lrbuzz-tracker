package com.aqcroft.buzz321fire;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private static final String START_URL = "https://aqcroft.github.io/lrbuzz-tracker/";
    private WebView webView;
    private View loadingOverlay;
    private ProgressBar loadingProgress;
    private TextView loadingText;
    private final Handler loadingHandler = new Handler(Looper.getMainLooper());
    private int loadingDotCount = 0;

    private final Runnable loadingTextAnimator = new Runnable() {
        @Override
        public void run() {
            if (loadingOverlay == null || loadingOverlay.getVisibility() != View.VISIBLE) return;
            loadingDotCount = (loadingDotCount + 1) % 4;
            String dots = "";
            for (int i = 0; i < loadingDotCount; i++) dots += ".";
            loadingText.setText("Loading" + dots);
            loadingHandler.postDelayed(this, 450);
        }
    };

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        enterImmersiveMode();

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(0xFF111827);

        webView = new WebView(this);
        webView.setBackgroundColor(0xFF111827);
        root.addView(webView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        loadingOverlay = createLoadingOverlay();
        root.addView(loadingOverlay, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        setContentView(root);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(false);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(false);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoadingOverlay();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoadingOverlay();
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                loadingProgress.setIndeterminate(newProgress < 10);
                loadingProgress.setProgress(newProgress);
                if (newProgress >= 100) hideLoadingOverlay();
            }
        });

        if (savedInstanceState == null) {
            showLoadingOverlay();
            webView.loadUrl(START_URL);
        } else {
            webView.restoreState(savedInstanceState);
            hideLoadingOverlay();
        }
    }

    private View createLoadingOverlay() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        container.setPadding(dp(36), dp(36), dp(36), dp(36));
        container.setBackgroundColor(0xFF111827);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.buzz_321_icon);
        logo.setAdjustViewBounds(true);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(dp(128), dp(128));
        logoParams.setMargins(0, 0, 0, dp(28));
        container.addView(logo, logoParams);

        TextView title = new TextView(this);
        title.setText("Buzz 321");
        title.setTextColor(Color.WHITE);
        title.setTextSize(30);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(0, 0, 0, dp(10));
        container.addView(title, titleParams);

        loadingText = new TextView(this);
        loadingText.setText("Loading");
        loadingText.setTextColor(0xFFE5E7EB);
        loadingText.setTextSize(18);
        loadingText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textParams.setMargins(0, 0, 0, dp(22));
        container.addView(loadingText, textParams);

        loadingProgress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        loadingProgress.setIndeterminate(true);
        loadingProgress.setMax(100);
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(dp(220), dp(8));
        container.addView(loadingProgress, progressParams);

        return container;
    }

    private void showLoadingOverlay() {
        loadingOverlay.setAlpha(1f);
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingHandler.removeCallbacks(loadingTextAnimator);
        loadingHandler.post(loadingTextAnimator);
    }

    private void hideLoadingOverlay() {
        if (loadingOverlay == null || loadingOverlay.getVisibility() != View.VISIBLE) return;
        loadingHandler.removeCallbacks(loadingTextAnimator);
        loadingOverlay.animate()
                .alpha(0f)
                .setDuration(250)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        loadingOverlay.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void enterImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) enterImmersiveMode();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        loadingHandler.removeCallbacks(loadingTextAnimator);
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
        }
        super.onDestroy();
    }
}
