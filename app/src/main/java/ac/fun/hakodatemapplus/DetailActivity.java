package ac.fun.hakodatemapplus;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class DetailActivity extends Activity implements OnClickListener {

    private Button button_main;
    private String title;
    private int image;
    private int course_id;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //CourseActivityの値を呼び出す
        Intent intent = getIntent();
        title = intent.getExtras().getString("title");
        image = intent.getExtras().getInt("image");
        course_id = intent.getExtras().getInt("course_id");
        //アクションバーの編集
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(title);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.mapplus_icon);

        setContentView(R.layout.activity_detail);
        button_main = (Button) findViewById(R.id.button_main);
        button_main.setOnClickListener(this);

        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        imageView.setImageResource(image);

        // SPARQLのクエリで使う形式に変換する(半角・全角スペース除去)
        String search_title = title.replaceAll("[ 　]", "");

        // SPARQLのクエリを実行して取得したデータを反映する
        SparqlGetThread st = new SparqlGetThread(search_title);
        st.start();
    }

    // SPARQLのクエリを実行して取得したデータを反映する
    class SparqlGetThread extends Thread {
        private String queue_title;

        SparqlGetThread(String name) {
            this.queue_title = name;
        }

        public void run() {
            // コース名をURLエンコードする
            String encoded_course = "";
            try {
                encoded_course = URLEncoder.encode(queue_title, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // SPARQLのクエリを準備する
            String queue_parts1 = "http://lod.per.c.fun.ac.jp:8000/sparql/?query=PREFIX%20rdfs%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2000%2f01%2frdf%2dschema%23%3e%0d%0aPREFIX%20dc%3a%20%3chttp%3a%2f%2fpurl%2eorg%2fdc%2felements%2f1%2e1%2f%3e%0d%0aPREFIX%20schema%3a%20%3chttp%3a%2f%2fschema%2eorg%2f%3e%0d%0aPREFIX%20dcterms%3a%20%3chttp%3a%2f%2fpurl%2eorg%2fdc%2fterms%2f%3e%0d%0a%0d%0aSELECT%20%3fcourseName%20%3fdescription%20%3fspotList%20%3fdistance%20%3ftime%20%3fcalory%0d%0a%0d%0aWHERE%20%7b%0d%0a%20GRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fmachiarukiinfo%2erdf%3e%20%7b%0d%0a%20%20%20%3curn%3a";
            String queue_parts2 = "%3e%20dc%3adescription%20%3fdescription%3b%0d%0a%20%20%20schema%3aname%20%3fspotList%2e%0d%0a%20%7d%0d%0a%0d%0a%20GRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fmachiaruki_akiba%2erdf%3e%7b%0d%0a%20%3curn%3a";
            String queue_parts3 = "%3e%20rdfs%3alabel%20%3fcourseName%3b%0d%0a%20schema%3adescription%20%3fdistance%3b%0d%0a%20dcterms%3aabstract%20%3ftime%3b%0d%0a%20dc%3adescription%20%3fcalory%2e%0d%0a%20%7d%0d%0a%7d&output=json";
            String queue_url = queue_parts1 + encoded_course + queue_parts2 + encoded_course + queue_parts3;

            // クエリを実行してデータを取得
            try {
                URL url = new URL(queue_url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                String str = InputStreamToString(con.getInputStream());

                // 受け取ったJSONをパースする
                JSONObject json = new JSONObject(str);
                JSONObject json_results = json.getJSONObject("results");
                JSONArray bindings = json_results.getJSONArray("bindings");
                JSONObject binding = bindings.getJSONObject(0);
                final String description_str = binding.getJSONObject("description").getString("value");
                final String contents_str = binding.getJSONObject("spotList").getString("value");
                final String distance_str = binding.getJSONObject("distance").getString("value");
                final String time_str = binding.getJSONObject("time").getString("value");
                final String calorie_str = binding.getJSONObject("calory").getString("value");

                // 受け取った結果をViewへ反映
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TextViewへ設定
                        // 概要
                        TextView description_text = (TextView) findViewById(R.id.description_text);
                        description_text.setText(description_str);

                        // コース内容
                        TextView contents_text = (TextView) findViewById(R.id.contents_text);
                        contents_text.setText(contents_str);

                        // 時間
                        TextView time_text = (TextView) findViewById(R.id.time_text);
                        time_text.setText(time_str);

                        // 移動距離
                        TextView distance_text = (TextView) findViewById(R.id.distance_text);
                        distance_text.setText(distance_str);

                        // 消費カロリー
                        TextView calorie_text = (TextView) findViewById(R.id.calorie_text);
                        calorie_text.setText(calorie_str);

                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
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


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        //アクションバーの戻るを押したときの処理
        else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {

        if (v == button_main) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("title", title);//第一引数呼び出すときのkey、第二引数:コース名
            intent.putExtra("course_id", course_id);
            startActivityForResult(intent, 0);
        }
    }
}