package com.nk.message;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.PermissionRequest;
import android.webkit.WebSettings;
import android.content.pm.PackageManager;

public class MainActivity extends Activity {

    private WebView webView;
    private PermissionRequest pendingRequest;

    private static final String SERVER =
        "https://nk-message.onrender.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebViewClient(new WebViewClient());

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                runOnUiThread(() -> {
                    String host = request.getOrigin().getHost();

                    if (!"nk-message.onrender.com".equals(host)) {
                        request.deny();
                        return;
                    }

                    pendingRequest = request;

                    String[] permissions = {
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.RECORD_AUDIO
                    };

                    boolean granted = true;

                    for (String permission : permissions) {
                        if (checkSelfPermission(permission)
                            != PackageManager.PERMISSION_GRANTED) {
                            granted = false;
                        }
                    }

                    if (granted) {
                        request.grant(request.getResources());
                        pendingRequest = null;
                    } else {
                        requestPermissions(permissions, 100);
                    }
                });
            }
        });

        webView.loadUrl(SERVER);
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode, String[] permissions, int[] results) {

        super.onRequestPermissionsResult(
            requestCode, permissions, results);

        if (requestCode == 100 && pendingRequest != null) {
            boolean granted = true;

            for (int result : results) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                }
            }

            if (granted) {
                pendingRequest.grant(pendingRequest.getResources());
            } else {
                pendingRequest.deny();
            }

            pendingRequest = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
