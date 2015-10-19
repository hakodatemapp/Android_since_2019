package ac.fun.hakodatemapplus;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
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

public class SpotDetailActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_detail);


        // SPARQLのクエリを実行して取得したデータを反映する
        SparqlGetThread st = new SparqlGetThread("松原家住宅");
        st.start();
    }


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
            String queue_parts1 = "http://lod.per.c.fun.ac.jp:8000/sparql/?query=PREFIX%20rdf%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f1999%2f02%2f22%2drdf%2dsyntax%2dns%23%3e%0d%0aPREFIX%20rdfs%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2000%2f01%2frdf%2dschema%23%3e%0d%0aPREFIX%20schema%3a%20%3chttp%3a%2f%2fschema%2eorg%2f%3e%0d%0aPREFIX%20dc%3a%20%3chttp%3a%2f%2fpurl%2eorg%2fdc%2felements%2f1%2e1%2f%3e%0d%0aPREFIX%20geo%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2003%2f01%2fgeo%2fwgs84_pos%23%3e%0d%0aPREFIX%20xsd%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2001%2fXMLSchema%23%3e%0d%0aPREFIX%20dcterms%3a%20%3chttp%3a%2f%2fpurl%2eorg%2fdc%2fterms%2f%3e%0d%0aPREFIX%20foaf%3a%20%3chttp%3a%2f%2fxmlns%2ecom%2ffoaf%2f0%2e1%2f%3e%0d%0a%0d%0aSELECT%20DISTINCT%20%3fdobokuname%20%3fdescription%20%3faccess%20%3farea%20%3fimage%20%3faddress%20%3ftelephone%20%3furl%20%3ffilmName%20%3fdirector%20%3factor%20%3ffilmdescription%20%3ffilmurl%20%3fbornDate%20%3fdobokudescription%20%3fdobokuurl%20%3fcreator%0d%0a%0d%0a%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fhakobura_akiba%2erdf%3e%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2ffilm_akiba%2erdf%3e%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fdoboku_akiba%2erdf%3e%0d%0a%0d%0a%0d%0a%0d%0aWHERE%20%7b%0d%0a%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fhakobura_akiba%2erdf%3e%20%7b%0d%0a%20%3fhs%20rdfs%3alabel%20%22";
            String queue_parts2 = "%22%3b%0d%0a%20dc%3adescription%20%3fdescription%3b%0d%0a%20schema%3adescription%20%3faccess%3b%0d%0a%20schema%3acontainedIn%20%3farea%3b%0d%0a%20schema%3aimage%20%3fimage%3b%0d%0a%20schema%3aaddress%20%3faddress%3b%0d%0a%20schema%3atelephone%20%3ftelephone%3b%0d%0a%20schema%3aurl%20%3furl%2e%0d%0a%7d%0d%0a%0d%0a%0d%0a%0d%0aOPTIONAL%20%7b%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2ffilm_akiba%2erdf%3e%7b%0d%0a%3ffss%20schema%3aname%20%22";
            String queue_parts3 = "%22%3b%0d%0adcterms%3adescription%20%3ffilmdescription%2e%0d%0a%0d%0a%3ffs%20dc%3arelation%20%3ffss%3b%0d%0ardfs%3alabel%20%3ffilmName%3b%0d%0adc%3acreator%20%3fdirector%3b%0d%0afoaf%3aperson%20%3factor%3b%0d%0aschema%3aurl%20%3ffilmurl%0d%0a%7d%0d%0a%7d%0d%0a%0d%0a%0d%0aOPTIONAL%20%7b%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fdoboku_akiba%2erdf%3e%7b%0d%0a%3fds%20rdfs%3acomment%20%22";
            String queue_parts4 = "%22%3b%0d%0adc%3adescription%20%3fdobokudescription%3b%0d%0aschema%3aurl%20%3fdobokuurl%2e%0d%0a%7d%0d%0a%7d%0d%0a%0d%0aOPTIONAL%20%7b%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fdoboku_akiba%2erdf%3e%7b%0d%0a%3fds%20rdfs%3acomment%20%22";
            String queue_parts5 = "%22%3b%0d%0adc%3adate%20%3fbornDate%3b%0d%0adc%3acreator%20%3fcreator%3b%0d%0a%7d%0d%0a%7d%0d%0a%0d%0aOPTIONAL%20%7b%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fdoboku_akiba%2erdf%3e%7b%0d%0a%3fds%20rdfs%3acomment%20%22";
            String queue_parts6 = "%22%3b%0d%0ardfs%3alabel%20%3fdobokuname%3b%0d%0a%7d%0d%0a%7d%0d%0a%0d%0a%7d&output=json";
            String queue_url = queue_parts1 + encoded_spot + queue_parts2 + encoded_spot + queue_parts3 + encoded_spot + queue_parts4 + encoded_spot + queue_parts5 + encoded_spot + queue_parts6;

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
                System.out.println(binding);


                // 映画ロケ地である場合はその情報をパース
                if (!binding.getJSONObject("filmName").isNull("value")) {
                    final String director_str = binding.getJSONObject("director").getString("value");
                    final String actor_str = binding.getJSONObject("actor").getString("value");
                    final String filmdescription_str = binding.getJSONObject("filmdescription").getString("value");
                    final String filmurl_str = binding.getJSONObject("filmurl").getString("value");
                }

                // 土木遺産である場合はその情報をパース
                if (!binding.getJSONObject("dobokuurl").isNull("value")) {
                    final String borndate_str = binding.getJSONObject("bornDate").getString("value");
                    final String dobokudescription_str = binding.getJSONObject("dobokudescription").getString("value");
                    final String filmdescription_str = binding.getJSONObject("filmdescription").getString("value");
                    final String creator_str = binding.getJSONObject("creator").getString("value");
                }

                final String description_str = binding.getJSONObject("description").getString("value");
                System.out.println(description_str);
                final String access_str = binding.getJSONObject("access").getString("value");
                final String area_str = binding.getJSONObject("area").getString("value");
                final String image_str = binding.getJSONObject("image").getString("value");
                final String address_str = binding.getJSONObject("address").getString("value");
                final String telephone_str = binding.getJSONObject("telephone").getString("value");
                final String url_str = binding.getJSONObject("url").getString("value");


                // 受け取った結果をViewへ反映
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TextViewへ設定
                        System.out.println("UI Setting ...");

                        // スポット名
                        TextView name_text = (TextView) findViewById(R.id.name_text);
                        name_text.setText(queue_title);

                        // 概要
                        TextView description_text = (TextView) findViewById(R.id.description_text);
                        description_text.setText(description_str);

                        // 所在地
                        TextView address_text = (TextView) findViewById(R.id.address_text);
                        address_text.setText(address_str);

                        // 電話番号
                        TextView tel_text = (TextView) findViewById(R.id.tel_text);
                        tel_text.setText(telephone_str);

                        // アクセス
                        TextView access_text = (TextView) findViewById(R.id.access_text);
                        access_text.setText(access_str);

                        // エリア
                        TextView area_text = (TextView) findViewById(R.id.area_text);
                        area_text.setText(area_str);

//                        // URL
//                        TextView calorie_text = (TextView) findViewById(R.id.calorie_text);
//                        calorie_text.setText(calorie_str);

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


}
