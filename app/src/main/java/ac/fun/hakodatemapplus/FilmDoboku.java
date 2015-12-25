package ac.fun.hakodatemapplus;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class FilmDoboku extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_doboku);

        // SpotDetailActivityの値を呼び出す
        Intent intent = getIntent();
        final String title_str = intent.getExtras().getString("title_str");
        Integer target_type = intent.getExtras().getInt("target_type");
        String description_str = intent.getExtras().getString("description_str");
        String first_str = intent.getExtras().getString("first_str");
        String second_str = intent.getExtras().getString("second_str");
        final String url = intent.getExtras().getString("url");
        final String image_url = intent.getExtras().getString("image_url");

        // 画面の設定
        ImageGetThread hakoburaimg_thread = new ImageGetThread(image_url);
        hakoburaimg_thread.start();

        // ターゲット名
        TextView name_text = (TextView) findViewById(R.id.name_text);
        name_text.setText(title_str);

        // 概要
        TextView description_text = (TextView) findViewById(R.id.description_text);
        description_text.setText(description_str);

        if (target_type == 0) {
            // 1番目の内容
            TextView first_title = (TextView) findViewById(R.id.first_title);
            first_title.setText("監督");
            TextView first_text = (TextView) findViewById(R.id.first_text);
            first_text.setText(first_str);

            // 2番目の内容
            TextView second_title = (TextView) findViewById(R.id.second_title);
            second_title.setText("主演");
            TextView second_text = (TextView) findViewById(R.id.second_text);
            second_text.setText(second_str);

        } else if (target_type == 1) {
            // 1番目の内容
            TextView first_title = (TextView) findViewById(R.id.first_title);
            first_title.setText("竣工年");
            TextView address_text = (TextView) findViewById(R.id.first_text);
            address_text.setText(first_str);

            // 2番目の内容
            TextView second_title = (TextView) findViewById(R.id.second_title);
            second_title.setText("関連人物");
            TextView tel_text = (TextView) findViewById(R.id.second_text);
            tel_text.setText(second_str);
        }

        // URL
        findViewById(R.id.filmdobokulink_row).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intentSpotWebBrowser(url, title_str);
            }
        });

        // powered by …
        if (target_type == 0) {
            TextView powered_text = (TextView) findViewById(R.id.powered_filmdoboku);
            powered_text.setText("powered by はこだてフィルムコミッション");
            findViewById(R.id.powered_filmdoboku).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    intentSpotWebBrowser("http://www.hakodate-fc.com/", "はこだてフィルムコミッション");
                }
            });
        } else if (target_type == 1) {
            TextView powered_text = (TextView) findViewById(R.id.powered_filmdoboku);
            powered_text.setText("powered by 函館近代化遺産ポータルサイト");
            findViewById(R.id.powered_filmdoboku).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    intentSpotWebBrowser("http://hnct-pbl.jimdo.com/", "函館近代化遺産ポータルサイト");
                }
            });
        }


        //アクションバーの編集
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(title_str);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.mapplus_icon);

    }


    public boolean checkNetworkStatus() {
        boolean result = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            DialogFragment dialog = new NoConnectionDialogFragment();
            dialog.show(getFragmentManager(), null);
        } else if (!ni.isConnected()) {
            DialogFragment dialog = new NoConnectionDialogFragment();
            dialog.show(getFragmentManager(), null);
        } else {
            result = true;
        }

        return result;
    }

    // ネットワーク接続がないときのダイアログ
    public static class NoConnectionDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("ネットワーク接続がないため、Webサイトを表示できません。").setTitle("ネットワークオフライン")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            this.setCancelable(false);
            return builder.create();
        }
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
                    System.out.println("Setting spot Image");
                    ImageView image_view = (ImageView) findViewById(R.id.image_view);
                    image_view.setImageBitmap(addBmp);
                }
            });
        }
    }

    // WebブラウザのActivityを開く
    public void intentSpotWebBrowser(String url, String title) {
        // この時点でネットワークに接続できるかどうか調べる
        if (checkNetworkStatus()) {
            Intent intent = new Intent(this, SpotWebBrowser.class);
            intent.putExtra("browser_url", url);
            intent.putExtra("browser_title", title);
            startActivityForResult(intent, 0);
        }
    }


    // アクションバーの戻るを押したときの処理
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
