package com.yhl.higo.ec.source;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/6/26/026.
 */

public class SourceDelegate extends HigoDelegate {

    @BindView(R2.id.web_view)
    WebView mWebView = null;
    private String mSource = null;

    public static SourceDelegate create(String source){
        final Bundle args = new Bundle();
        args.putString("source", source);
        final SourceDelegate delegate = new SourceDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            mSource = args.getString("source");

        }

    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_source;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(mSource);
    }
}
