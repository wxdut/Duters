package com.chillax.softwareyard.activity;

import android.os.Build;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.chillax.softwareyard.R;
import com.chillax.softwareyard.customview.ActionBar;
import com.chillax.swipebacklayout.app.SwipeBackActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.webview_layout)
public class WebViewShower extends SwipeBackActivity implements ActionBar.onTopBarClickedListener{
	@ViewById(R.id.webView)
	WebView mWebView;
	@ViewById(R.id.pgb)
	ProgressBar mPgb;
	@ViewById(R.id.topBar)
	ActionBar mActionBar;
	String mUrlStr;
	WebSettings mSettings;

	@AfterViews
	void inits() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			// create our manager instance after the content view is set
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			// enable status bar tint
			tintManager.setStatusBarTintEnabled(true);
			// enable navigation bar tint
			tintManager.setNavigationBarTintEnabled(true);
			// set a custom tint color for all system bars
			tintManager.setTintColor(getResources().getColor(R.color.actionbar_bg));
		}
		mActionBar.initTopBar("WebViewShower");
		mActionBar.setOnTopBarClickedListener(this);
		mSettings = mWebView.getSettings();
		mSettings.setUseWideViewPort(true);//支持双击缩放
		mSettings.setLoadWithOverviewMode(true);//全屏显示
		mSettings.setJavaScriptEnabled(true);//支持js
		mSettings.setBuiltInZoomControls(true);//支持放大缩小
		mSettings.setTextSize(WebSettings.TextSize.NORMAL);//设置字体大小，兼容到API8
		mUrlStr = getIntent().getStringExtra("URL");
		mWebView.loadUrl(mUrlStr);
		mWebView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				mPgb.setProgress(newProgress);
				if (newProgress==100){
					mPgb.setVisibility(View.GONE);
				}
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				mActionBar.setTitleText(title);
			}
		});
	}

	@Override
	public void onLogoClicked() {
		finish();
		overridePendingTransition(R.anim.slide_clam,R.anim.slide_out_right);
	}

	@Override
	public void onSpinnerItemClicked(int position) {

	}

	@Override
	public void onTitleClicked() {

	}

	@Override
	public void onMoreClicked(View view) {

	}

}
