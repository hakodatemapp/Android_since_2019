package ac.fun.hakodatemapplus;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mimimi on 2017/11/09.
 */

public class FeaturedDetailActivity extends Activity {

    private String featuredProductName;
    private String featuredProductPrice;
    private String featuredProductDescription;
    private String featuredProductImage;
    private String shop_title;
    private String url_str;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Intent intent =getIntent();
        featuredProductName=intent.getStringExtra("name");
        featuredProductDescription=intent.getStringExtra("description");
        featuredProductImage=intent.getStringExtra("img_url");
        featuredProductPrice=intent.getStringExtra("price");
        shop_title=intent.getStringExtra("shop_title");
        url_str=intent.getStringExtra("url_str");

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(featuredProductName);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.mapplus_icon);
        setContentView(R.layout.activity_featured_detail);


        ImageGetThread featuredImg_thread = new ImageGetThread(featuredProductImage);
        featuredImg_thread.start();
        Log.d("price",featuredProductPrice);
        System.out.println("site"+url_str);
//        TextView featured_name=(TextView) findViewById(R.id.featuredName_text);
//        featured_name.setText(featuredProductName);
        TextView featured_description = (TextView) findViewById(R.id.featuredDescription_text);
        featured_description.setText(featuredProductDescription);

        TextView featured_price = (TextView) findViewById(R.id.featuredPrice_text);
        featured_price.setText(featuredProductPrice);

        if( (! url_str.equals("")&&(!url_str.equals(" ")))) {
            TextView officialWebSite=(TextView)findViewById(R.id.officialWeb_text);
            officialWebSite.setText(url_str);
            findViewById(R.id.officialWeb_text).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Uri uri = Uri.parse(url_str);
                    Intent i = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(i);
                }
            });
            findViewById(R.id.spot_officialWeb).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Uri uri = Uri.parse(url_str);
                    Intent i = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(i);
                }
            });
        }

        findViewById(R.id.powered_hakobura).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intentSpotWebBrowser("http://hnct-pbl.jimdo.com/", "函館近代化遺産ポータルサイト");
            }
        });
    }

    // WebブラウザのActivityを開く
    private void intentSpotWebBrowser(String url, String title) {
        // この時点でネットワークに接続できるかどうか調べる
            Intent intent = new Intent(this, SpotWebBrowser.class);
            intent.putExtra("browser_url", url);
            intent.putExtra("browser_title", title);
            startActivityForResult(intent, 0);
    }

    // スポットの画像を取得して設定
    class ImageGetThread extends Thread {
        private String imageurl_str;

        ImageGetThread(String instr) {
            this.imageurl_str = instr;
        }

        public void run() {
            URL image_url = null;
            InputStream istream = null;
            //画像のURLを直うち
            try {
                image_url = new URL(imageurl_str);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap oBmp = null;

            //インプットストリームで画像を読み込む
            try {
                istream = image_url.openStream();
                //読み込んだファイルをビットマップに変換
                oBmp = BitmapFactory.decodeStream(istream);
                //インプットストリームを閉じる
                istream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            final Bitmap addBmp = oBmp;

            // 受け取った結果をViewへ反映
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //ビットマップをImageViewに設定
                    ImageView image_view = (ImageView) findViewById(R.id.featuredImage_view);
                    image_view.setImageBitmap(addBmp);
                }
            });
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // intentの作成
            Intent intent = new Intent();

            // 返却したい結果ステータスをセットする
            setResult(Activity.RESULT_OK, intent);

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
