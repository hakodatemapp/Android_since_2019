package ac.fun.hakodatemapplus;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.reflect.Field;

public class SpotWebBrowser extends Activity {


    private String browser_title;
    private String browser_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //CourseActivityの値を呼び出す
        Intent intent = getIntent();
        browser_title = intent.getExtras().getString("browser_title");
        browser_url = intent.getExtras().getString("browser_url");

        //アクションバーの編集
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(browser_title);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.mapplus_icon);

        setContentView(R.layout.activity_spot_web_browser);

        // レイアウトで指定したWebViewのIDを指定する
        WebView webview = (WebView) findViewById(R.id.webView);
        WebSettings websettings = webview.getSettings();

        // リンクをタップしたときに標準ブラウザを起動させない
        webview.setWebViewClient(new WebViewClient());

        // JavaScriptを有効にする
        websettings.setJavaScriptEnabled(true);

        // 拡大・縮小を有効にする
        websettings.setBuiltInZoomControls(true);
        websettings.setSupportZoom(true);
        websettings.setDisplayZoomControls(false);

        // ページを表示する
        webview.loadUrl(browser_url);
    }


    // メニューを読み込む
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_spot_webbrowser, menu);
        return true;
    }


    // メニューをタップしたとき
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        WebView webview = (WebView) findViewById(R.id.webView);

        switch (item.getItemId()) {
            case R.id.menu_back:
                // 前のページに戻る
                webview.goBack();
                return true;
            case R.id.menu_forward:
                // 次のページに進む
                webview.goForward();
                return true;
            case R.id.menu_reload:
                // 更新
                webview.reload();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }


    //アクションバーの戻るを押したときの処理
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
