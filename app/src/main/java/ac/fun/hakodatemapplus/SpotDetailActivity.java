package ac.fun.hakodatemapplus;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

public class SpotDetailActivity extends Activity {

    private JSONArray bindings = new JSONArray();

    private String spot_title;

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

        setContentView(R.layout.activity_spot_detail);

        // SPARQLのクエリを実行して取得したデータを反映する
        SparqlGetThread st = new SparqlGetThread(spot_title);
        st.start();
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

    // WebブラウザのActivityを開く
    private void intentSpotWebBrowser(String url, String title) {
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
                bindings = json_results.getJSONArray("bindings");
                final JSONObject binding = bindings.getJSONObject(0);     // bindingはbindingsのゼロ番目であることに注意
//                System.out.println(binding);


                // 映画ロケ地である場合はその情報をパース
                List<String> film_spots = new ArrayList<String>();
                Collection<String> film_spots_distinct = new LinkedHashSet<String>();

                if (!binding.getJSONObject("filmName").isNull("value")) {
                    // 映画名リスト
                    for (int i = 0; i < bindings.length(); i++) {
                        String film_spot = bindings.getJSONObject(i).getJSONObject("filmName").getString("value");
                        film_spots.add(film_spot);
                    }
                    // 重複を排除する
                    film_spots_distinct.addAll(film_spots);
                }


                // 土木遺産である場合はその情報をパース。名前を取得する
                List<String> doboku_spots = new ArrayList<String>();
                Collection<String> doboku_spots_distinct = new LinkedHashSet<String>();

                if (!binding.getJSONObject("dobokuname").isNull("value")) {
                    // 土木遺産名リスト
                    for (int i = 0; i < bindings.length(); i++) {
                        String doboku_spot = bindings.getJSONObject(i).getJSONObject("dobokuname").getString("value");
                        doboku_spots.add(doboku_spot);
                    }
                    // 重複を排除する
                    doboku_spots_distinct.addAll(doboku_spots);
                }

                final Collection<String> film_collection = film_spots_distinct;
                final Collection<String> doboku_collection = doboku_spots_distinct;

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

                        // スポット画像。はこぶら取得したデータを反映する
                        ImageGetThread hakoburaimg_thread = new ImageGetThread(image_str);
                        hakoburaimg_thread.start();

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
                        Log.d("FILM_COUNT", String.format("%d", film_collection.size()));
                        Log.d("DOBOKU_COUNT", String.format("%d", doboku_collection.size()));

                        // URL
                        findViewById(R.id.hakoburalink_row).setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                intentSpotWebBrowser(url_str, spot_title);
                            }
                        });

                        // powered by はこぶら
                        findViewById(R.id.powered_hakobura).setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                intentSpotWebBrowser("http://www.hakobura.jp/", "はこぶら");
                            }
                        });


                        // 映画ロケ地
                        if (film_collection.size() == 0) {
                            findViewById(R.id.film_table).setVisibility(View.GONE);
                            findViewById(R.id.powered_hakodatefilm).setVisibility(View.GONE);
                        } else {
                            // TableLayoutのグループを取得
                            ViewGroup film_vg = (ViewGroup) findViewById(R.id.film_table);
                            int i = 1;
                            for (String tmp : film_collection) {
                                getLayoutInflater().inflate(R.layout.film_doboku_row, film_vg);
                                TableRow tr = (TableRow) film_vg.getChildAt(i);
                                ((TextView) (tr.getChildAt(0))).setText(tmp);

                                final String tmp2 = tmp;
                                tr.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        moveDetailFilmDoboku(tmp2, 0, binding);
                                    }
                                });
                                i++;
                            }
                        }

                        // powered by はこだてフィルムコミッション
                        findViewById(R.id.powered_hakodatefilm).setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                intentSpotWebBrowser("http://www.hakodate-fc.com/", "はこだてフィルムコミッション");
                            }
                        });


                        // 土木遺産
                        if (doboku_collection.size() == 0) {
                            findViewById(R.id.doboku_table).setVisibility(View.GONE);
                            findViewById(R.id.powered_doboku).setVisibility(View.GONE);
                        } else {
                            // TableLayoutのグループを取得
                            ViewGroup film_vg = (ViewGroup) findViewById(R.id.doboku_table);
                            int i = 1;
                            for (String tmp : doboku_collection) {
                                getLayoutInflater().inflate(R.layout.film_doboku_row, film_vg);
                                TableRow tr = (TableRow) film_vg.getChildAt(i);
                                ((TextView) (tr.getChildAt(0))).setText(tmp);

                                final String tmp2 = tmp;
                                tr.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        moveDetailFilmDoboku(tmp2, 1, binding);
                                    }
                                });
                                i++;
                            }
                        }

                        // powered by 函館近代化遺産ポータルサイト
                        findViewById(R.id.powered_doboku).setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                intentSpotWebBrowser("http://hnct-pbl.jimdo.com/", "函館近代化遺産ポータルサイト");
                            }
                        });
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    private void moveDetailFilmDoboku(String target_title, Integer target_type, JSONObject binding) {
        String description_str = "";
        String first_str = "";
        String second_str = "";
        String url = "";
        String image_url = "";
        Boolean is_found = false;

        try {
            // 映画ロケ地である場合はその情報をパース。名前を取得する
            if (target_type == 0) {
                if (!binding.getJSONObject("filmName").isNull("value")) {
                    // 映画名リスト
                    for (int i = 0; i < bindings.length(); i++) {
                        // 求めるタイトルと検索して見つかったら終了
                        String film_spot = bindings.getJSONObject(i).getJSONObject("filmName").getString("value");
                        if (film_spot.equals(target_title)) {
                            description_str = bindings.getJSONObject(i).getJSONObject("filmdescription").getString("value");
                            first_str = bindings.getJSONObject(i).getJSONObject("director").getString("value");
                            second_str = bindings.getJSONObject(i).getJSONObject("actor").getString("value");
                            url = bindings.getJSONObject(i).getJSONObject("filmurl").getString("value");
                            image_url = bindings.getJSONObject(i).getJSONObject("image").getString("value");
                            is_found = true;
                            break;
                        }
                    }
                }

                // 土木遺産である場合はその情報をパース。名前を取得する
            } else if (target_type == 1) {

                List<String> doboku_spots = new ArrayList<String>();
                Collection<String> doboku_spots_distinct = new LinkedHashSet<String>();

                if (!binding.getJSONObject("dobokuname").isNull("value")) {
                    // 土木遺産名リスト
                    for (int i = 0; i < bindings.length(); i++) {
                        // 求めるタイトルと検索して見つかったら終了
                        String doboku_spot = bindings.getJSONObject(i).getJSONObject("dobokuname").getString("value");
                        if (doboku_spot.equals(target_title)) {
                            description_str = bindings.getJSONObject(i).getJSONObject("dobokudescription").getString("value");
                            first_str = bindings.getJSONObject(i).getJSONObject("bornDate").getString("value");
                            second_str = bindings.getJSONObject(i).getJSONObject("creator").getString("value");
                            url = bindings.getJSONObject(i).getJSONObject("dobokuurl").getString("value");
                            image_url = bindings.getJSONObject(i).getJSONObject("image").getString("value");
                            is_found = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (is_found == true) {
            Intent intent = new Intent(this, FilmDoboku.class);
            intent.putExtra("title_str", target_title);
            intent.putExtra("target_type", target_type);
            intent.putExtra("description_str", description_str);
            intent.putExtra("first_str", first_str);
            intent.putExtra("second_str", second_str);
            intent.putExtra("url", url);
            intent.putExtra("image_url", image_url);
            startActivityForResult(intent, 0);
        } else {
            Log.d("ERROR", "指定されたターゲットは見つかりませんでした");
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