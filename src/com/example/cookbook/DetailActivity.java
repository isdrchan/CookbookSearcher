package com.example.cookbook;

import java.util.ArrayList;

import com.example.cookbook.GetCookbookData.Cookbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;

public class DetailActivity extends Activity {
	
	private WebView webview;
	private String keyword;
	private final String url = "file:///android_asset/detail.html"; 
	ArrayList<Cookbook> listCookbook =  new ArrayList<Cookbook>();
	//创建Handler对象
	Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        Bundle data = msg.getData();
	        String status = data.getString("status");
	        if(status.equals("OK")) {
	        	webview.loadUrl(url);	//加载html文件到webview
	        }
	    }
	};
	//新建一个线程对象
	Runnable runnable = new Runnable() {
	    @Override
	    public void run() {
	    	//请求数据
	    	GetCookbookData getCookbookData = new GetCookbookData();
	    	listCookbook = getCookbookData.run(keyword);
	        Message msg = new Message();
	        Bundle data = new Bundle();
	        data.putString("status", "OK");
	        msg.setData(data);
	        handler.sendMessage(msg);
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		webview = (WebView) findViewById(R.id.webView);
		
		//取得搜索关键词
		Intent intent = getIntent();
		keyword = intent.getStringExtra("keyword");
		
		//在Runnable中做HTTP请求，以防阻塞UI线程抛NetworkOnMainThreadException
		new Thread(runnable).start();
		
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
		 		
		 		int count = 0;
		 		//解析arrylist调用js的方法将数据append到html中
		 		for(int i = 0; i < listCookbook.size(); i++, count++) {
		 			StringBuffer sb = new StringBuffer("<br/>");
		 			Cookbook cookbook = (Cookbook) listCookbook.get(i);
		 			for(int j = 0; j < cookbook.step.size(); j++) {
		 				sb.append(((String) (cookbook.step.get(j))) + "<br/>");
		 			}
		 			webview.loadUrl("javascript:appendDetail('" + cookbook.title + "','" + cookbook.albums + "','" + cookbook.tags + "','" + cookbook.imtro + "','" + cookbook.ingredients + "','" + cookbook.burden + "','" + sb.toString() + "')");
		 		}
		 		webview.loadUrl("javascript:resultCount('" + count + "')");
		 		
		 		//动态加载js
		 		String js = "var newscript = document.createElement(\"script\");";
		 		js += "newscript.src=\"file:///android_asset/js/amazeui.min.js\";";
		 		js += "document.body.appendChild(newscript);";
		 		webview.loadUrl("javascript:" + js);
		 	}
	 }
}


