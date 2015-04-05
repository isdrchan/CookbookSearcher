package com.example.cookbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;

public class DetailActivity extends Activity {
	
	private WebView webview;
	private String keyword;
	private final String url = "file:///android_asset/detail.html"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		webview = (WebView) findViewById(R.id.webView);
		
		//取得搜索关键词
		Intent intent = getIntent();
		keyword = intent.getStringExtra("keyword");
		
		//设置webview的参数和加载本地页面
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);	//可使滚动条不占位
		webview.getSettings().setBuiltInZoomControls(false);	//隐藏左下角缩放按钮
		webview.getSettings().setSupportZoom(false);	//不允许html缩放
		webview.getSettings().setJavaScriptEnabled(true);	//必须！使webview中的html支持javascript，能够与安卓进行交互
		webview.getSettings().setUseWideViewPort(true);	//使自适应分辨率
		webview.getSettings().setLoadWithOverviewMode(true);	//使自适应分辨率
		webview.setWebViewClient(new webViewClient()); ////为WebView设置WebViewClient处理某些操作	
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);// 禁止由于内容过宽，导致横屏滚动。
//		webview.addJavascriptInterface(this, "android");	//注意！使用这条语句，需在本类或onCreate方法添加注解@SuppressLint("JavascriptInterface")，和导入android.annotation.SuppressLint包，不然会报错。并且要用@JavascriptInterface注解的公有方法才能在webview中被调用
		webview.loadUrl(url);	//加载当前项目的assets目录下的welcome.html文件到webview
	}
	
	/**
	 * 关键就是为WebView设置WebViewClient，然后重写shouldOverrideUrlLoading方法即可。其中WebViewClient为WebView的一个辅助类，主要处理各种通知、请求事件。
	 * @author Dr.Chan
	 *
	 */
	 class webViewClient extends WebViewClient{ 
		 	/**
		 	 * 重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。 
		 	 */
		 	@Override 
		    public boolean shouldOverrideUrlLoading(WebView view, String url) { 
		        view.loadUrl(url); 
		        //如果不需要其他对点击链接事件的处理返回true，否则返回false 
		        return true; 
		    }
		 	
		 	/**
		 	 * 页面载入完成后调用
		 	 */
		 	@Override
		 	public void onPageFinished(WebView view, String url) {
		 		super.onPageFinished(view, url);
//		 		webview.loadUrl("javascript:changeH1Value('" + getRomTotalSize() + "')");  
		 	}
	 }
}
