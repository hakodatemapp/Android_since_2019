package ac.fun.hakodatemapplus;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class ShelterDetailActivity extends Activity {

    private JSONArray bindings = new JSONArray();

    private String spot_title;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //CourseActivityの値を呼び出す
        Intent intent = getIntent();
        spot_title = intent.getExtras().getString("spot_title");
        //アクションバーの編集
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(spot_title);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.mapplus_icon);

        setContentView(R.layout.activity_shelter_detail);

        // この時点でネットワークに接続できるかどうか調べる
        if (checkNetworkStatus(true)) {
            // 観光スポット読み込み中のダイアログを表示する
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("避難所を読み込んでいます…");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // SPARQLのクエリを実行して取得したデータを反映する
            SparqlGetThread st = new SparqlGetThread(spot_title);
            st.start();
        }
    }

    public boolean checkNetworkStatus(boolean back_needed) {
        boolean result = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            result = true;
        }

        if(!result) {
            DialogFragment dialog;
            if(back_needed) {
                dialog = new NoConnectionDialogFragment();
            } else {
                dialog = new NoConnectionDialogFragmentWithoutBack();
            }
            dialog.show(getFragmentManager(), null);
        }

        return result;
    }

    // ネットワーク接続がないときのダイアログ
    public static class NoConnectionDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("ネットワーク接続がないため、データを取得できません。").setTitle("ネットワークオフライン")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ShelterDetailActivity calling_activity = (ShelterDetailActivity) getActivity();
                            calling_activity.finish();
                        }
                    });
            this.setCancelable(false);
            return builder.create();
        }
    }

    // ネットワーク接続がないときのダイアログ
    public static class NoConnectionDialogFragmentWithoutBack extends DialogFragment {
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

    // 接続できなかったときのダイアログ
    public static class ConnectionErrorDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("データを取得できませんでした。しばらく待ってから再度試して下さい。").setTitle("ネットワークオフライン")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ShelterDetailActivity calling_activity = (ShelterDetailActivity) getActivity();
                            calling_activity.finish();
                        }
                    });
            this.setCancelable(false);
            return builder.create();
        }
    }

    // アクションバーの戻るを押したときの処理
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

    // SPARQLのクエリを実行して取得したデータを反映する
    class SparqlGetThread extends Thread {
        private String queue_title;

        SparqlGetThread(String name) {
            this.queue_title = name;
        }

        public void run() {
            // コース名をURLエンコードする
            String encoded_spot = "";
            try {
                encoded_spot = URLEncoder.encode(queue_title, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // SPARQLのクエリを準備する
            String queue_parts1 = "http://lod.per.c.fun.ac.jp:8000/sparql/?query=PREFIX%20rdfs%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2000%2f01%2frdf%2dschema%23%3e%0d%0aPREFIX%20schema%3a%20%3chttp%3a%2f%2fschema%2eorg%2f%3e%0d%0aPREFIX%20shelter%3a%20%3chttp%3a%2f%2flod%2eper%2ec%2efun%2eac%2ejp%2fbosai%2fterms%2fshelter%23%3e%0d%0aPREFIX%20evcx%3a%20%3chttp%3a%2f%2fsmartercity%2ejp%2fevacuation%23%3e%0d%0aSELECT%20DISTINCT%20%3fcategory%20%3fcapa%20%3faddress%20%3fphone%0d%0a%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fshelter%2erdf%3e%0d%0a%0d%0aWHERE%20%7b%0d%0a%20%20%20%20%3fs%20rdfs%3alabel%20%22";
            String queue_parts2 = "%22%40ja%3b%0d%0a%20%20%20%20%20%20%20%20shelter%3atypeOfshelter%20%3fcategory%3b%0d%0a%20%20%20%20%20%20%20%20evcx%3acapacityOfEvacuationFacility%20%3fcapa%3b%0d%0a%20%20%20%20%20%20%20%20schema%3aaddress%20%3faddress%3b%0d%0a%20%20%20%20%20%20%20%20schema%3atelephone%20%3fphone%2e%0d%0a%7d&output=json";
            String queue_url = queue_parts1 + encoded_spot + queue_parts2;
            // クエリを実行してデータを取得
            try {
                URL url = new URL(queue_url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                String str = InputStreamToString(con.getInputStream());

                // 受け取ったJSONをパースする
                JSONObject json = new JSONObject(str);
                JSONObject json_results = json.getJSONObject("results");
                bindings = json_results.getJSONArray("bindings");
                final JSONObject binding = bindings.getJSONObject(0);     // bindingはbindingsのゼロ番目であることに注意

                final String category_str = binding.getJSONObject("category").getString("value");
                final String capa_str = binding.getJSONObject("capa").getString("value");
                final String address_str = binding.getJSONObject("address").getString("value");
                final String phone_str = binding.getJSONObject("phone").getString("value");

                // 受け取った結果をViewへ反映
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TextViewへ設定

                        // スポット名
                        TextView name_text = (TextView) findViewById(R.id.name_text);
                        name_text.setText(queue_title);

                        // 種類
                        TextView category_text = (TextView) findViewById(R.id.category_text);
                        category_text.setText(category_str);

                        // 収容人数
                        if (!capa_str.equals("")){
                            TextView capa_text = (TextView) findViewById(R.id.capa_text);
                            capa_text.setText(capa_str + "人");
                        }

                        // 所在地
                        TextView address_text = (TextView) findViewById(R.id.address_text);
                        address_text.setText(address_str);

                        // 電話番号
                        TextView phone_text = (TextView) findViewById(R.id.phone_text);
                        phone_text.setText(phone_str);

                        // 読み込み中のダイアログを閉じる
                        progressDialog.dismiss();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                DialogFragment dialog = new ConnectionErrorDialogFragment();
                dialog.show(getFragmentManager(), null);
            }
        }
    }

    // InputStream -> String
    static String InputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}
