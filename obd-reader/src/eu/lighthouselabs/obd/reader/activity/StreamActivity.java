package eu.lighthouselabs.obd.reader.activity;

import eu.lighthouselabs.obd.reader.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class StreamActivity extends Activity {
	
	// Sample WebViewClient in case it was needed...
    // See continueWhenLoaded() sample function for the best place to set it on our webView
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate (Bundle savedInstanceState) {
			super.onCreate (savedInstanceState);
			setContentView (R.layout.stream);

			WebView web = (WebView) findViewById(R.id.myWebView);
			web.getSettings().setJavaScriptEnabled(true);

			if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) 
				  web.getSettings().setAllowUniversalAccessFromFileURLs(true);

			web.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					view.setEnabled(false);
					
					return true;
				}
			});
			
			// web.loadUrl("http://www.techrepubilc.com");
			// web.loadUrl("https://drive.google.com/file/d/0B_Ll_Vw4Gui8UFhzOFluRk1YZkU");
			web.loadUrl("file:///android_asset/chart/testOutput.html");
			web.loadUrl("https://drive.google.com/file/d/0B_Ll_Vw4Gui8VE53aWs2QTJoRHM/edit?usp=sharing");
	}
}
