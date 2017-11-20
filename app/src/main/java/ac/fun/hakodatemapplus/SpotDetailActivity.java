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
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import org.w3c.dom.Text;

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

import static ac.fun.hakodatemapplus.R.id.address_text;
import static ac.fun.hakodatemapplus.R.id.area_text;
import static ac.fun.hakodatemapplus.R.id.description_text;
import static ac.fun.hakodatemapplus.R.id.image_view;
import static ac.fun.hakodatemapplus.R.id.postcode_text;
import static ac.fun.hakodatemapplus.R.id.spot_tel;
import static ac.fun.hakodatemapplus.R.id.tel_text;
import static android.R.attr.category;
import static android.R.attr.id;
import static android.R.attr.name;
import static android.view.View.GONE;

public class SpotDetailActivity extends Activity {

    private JSONArray bindings = new JSONArray();

    private String spot_title;
    private String spot_category;
    private String spot_id;
    ProgressDialog progressDialog;
    private final int TOPIMAGE_MODE = 0;
    private final int FEATURE_MODE = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //CourseActivityの値を呼び出す
        Intent intent = getIntent();
        spot_title = intent.getExtras().getString("spot_title");
        spot_category = intent.getExtras().getString("spot_category");
        spot_id= intent.getExtras().getString("spot_id");
        //アクションバーの編集
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(spot_title);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.mapplus_icon);

        setContentView(R.layout.activity_spot_detail);

        // この時点でネットワークに接続できるかどうか調べる
        if (checkNetworkStatus(true)) {
            // 観光スポット読み込み中のダイアログを表示する
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("観光スポットを読み込んでいます…");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // SPARQLのクエリを実行して取得したデータを反映する
            SparqlGetThread st = new SparqlGetThread(spot_title,spot_category,spot_id);
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
                            SpotDetailActivity calling_activity = (SpotDetailActivity) getActivity();
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
                            SpotDetailActivity calling_activity = (SpotDetailActivity) getActivity();
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

    // WebブラウザのActivityを開く
    private void intentSpotWebBrowser(String url, String title) {
        // この時点でネットワークに接続できるかどうか調べる
        if (checkNetworkStatus(false)) {
            Intent intent = new Intent(this, SpotWebBrowser.class);
            intent.putExtra("browser_url", url);
            intent.putExtra("browser_title", title);
            startActivityForResult(intent, 0);
        }
    }

    // スポットの画像を取得して設定
    class ImageGetThread extends Thread {
        private String imageurl_str;
        private int systemMode;

        ImageGetThread(String instr,int mode) {
            this.imageurl_str = instr;
            this.systemMode=mode;
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
                    //System.out.println("Setting spot Image");
                    if(systemMode==TOPIMAGE_MODE) {
                        ImageView image_view= (ImageView) findViewById(R.id.image_view);
                        image_view.setImageBitmap(addBmp);
                    }else if(systemMode==FEATURE_MODE){
                        ImageView image_view = (ImageView) findViewById(R.id.featuredImage_view);
                        image_view.setImageBitmap(addBmp);
                    }else{
                        System.out.println("errorあああ");
                    }

                }
            });
        }
    }




    // SPARQLのクエリを実行して取得したデータを反映する
    class SparqlGetThread extends Thread {
        private String queue_title;
        private String queue_category;
        private String queue_id;

        SparqlGetThread(String name,String category,String id) {
            this.queue_title = name;
            this.queue_category = category;
            this.queue_id=id;
        }

        private String getDataFromJSON(JSONObject binding,String fromNormal,String fromSweets){
            if(binding.has(fromNormal)){
                return binding.optJSONObject(fromNormal).optString("value");
            }else if(binding.has(fromSweets)){
                return binding.optJSONObject(fromSweets).optString("value");
            }
            return "";
        }

        public void run() {
            // コース名をURLエンコードする
            String encoded_spot = "";
            try {
                encoded_spot = queue_category.equals("函館スイーツ")?URLEncoder.encode(queue_id,"UTF-8"):URLEncoder.encode(queue_title, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String queue_url=null;
            // SPARQLのクエリを準備する
            if(queue_category.equals("函館スイーツ")) {
                String queue_parts1= "http://lod.per.c.fun.ac.jp:8080/sparql?default-graph-uri=http%3A%2F%2Flocalhost%3A8080%2FDAV%2Fhakodate_sweets&query=PREFIX+geo%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2003%2F01%2Fgeo%2Fwgs84_pos%23%3E%0D%0APREFIX+dc%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0D%0APREFIX+schema%3A+%3Chttp%3A%2F%2Fschema.org%2F%3E%0D%0APREFIX+sweet%3A+%3Chttp%3A%2F%2Flod.fun.ac.jp%2Fhakobura%2Fterms%2Fsweet%23%3E%0D%0A%0D%0ASELECT+%3Fshopname+%3Fshopimage+%3Fshopdescription+%3Farea+%3Fmodified+%3Fpostcode+%3Faddress+%3Ftelephone+%3FfaxNumber+%3FopeningHoursSpecification+%3Fclosed+%3Fparking+%3Featin+%3Fid+%3Femail+%3Furl+%3Flat+%3Flong+%3Ffeaturedproductname+%3Ffeaturedproductprice+%3Ffeaturedproductimage+%3Ffeaturedproductdescription+%0D%0AWHERE+%7B%0D%0A%3Curn%3A";
                String queue_parts2= "%3E+sweet%3Ashopname+%3Fshopname.%0D%0A%3Curn%3A";
                String queue_parts3= "%3E+sweet%3Ashopimage+%3Fshopimage.%0D%0A%3Curn%3A";
                String queue_parts4= "%3E+sweet%3Ashopdescription+%3Fshopdescription.%0D%0A%3Curn%3A";
                String queue_parts5= "%3E+sweet%3Aarea+%3Farea.%0D%0A%3Curn%3A";
                String queue_parts6= "%3E+dc%3Amodified+%3Fmodified.%0D%0A%3Curn%3A";
                String queue_parts7= "%3E+sweet%3Apostcode+%3Fpostcode.%0D%0A%3Curn%3A";
                String queue_parts8= "%3E+schema%3Aaddress+%3Faddress.%0D%0A%3Curn%3A";
                String queue_parts9= "%3E+schema%3Atelephone+%3Ftelephone.%0D%0A%3Curn%3A";
                String queue_parts10="%3E+schema%3AfaxNumber+%3FfaxNumber.%0D%0A%3Curn%3A";
                String queue_parts11="%3E+schema%3AopeningHoursSpecification+%3FopeningHoursSpecification.%0D%0A%3Curn%3A";
                String queue_parts12="%3E+sweet%3Aclosed+%3Fclosed.%0D%0A%3Curn%3A";
                String queue_parts13="%3E+sweet%3Aparking+%3Fparking.%0D%0A%3Curn%3A";
                String queue_parts14="%3E+sweet%3Aeatin+%3Featin.%0D%0A%3Curn%3A";
                String queue_parts15="%3E+sweet%3Aid+%3Fid.%0D%0A%3Curn%3A";
                String queue_parts16="%3E+schema%3Aemail+%3Femail.%0D%0A%3Curn%3A";
                String queue_parts17="%3E+schema%3Aurl+%3Furl.%0D%0A%3Curn%3A";
                String queue_parts18="%3E+geo%3Alat+%3Flat.%0D%0A%3Curn%3A";
                String queue_parts19="%3E+geo%3Along+%3Flong.%0D%0A%3Curn%3A";
                String queue_parts20="%3E+sweet%3Afeaturedproductname+%3Ffeaturedproductname.%0D%0A%3Curn%3A";
                String queue_parts21="%3E+sweet%3Afeaturedproductprice+%3Ffeaturedproductprice.%0D%0A%3Curn%3A";
                String queue_parts22="%3E+sweet%3Afeaturedproductimage+%3Ffeaturedproductimage.%0D%0A%3Curn%3A";
                String queue_parts23="%3E+sweet%3Afeaturedproductdescription+%3Ffeaturedproductdescription.%0D%0AFILTER+%28lang%28%3Fshopname%29+%3D+%27ja%27%29%0D%0A%7D&format=application%2Fsparql-results%2Bjson&timeout=0&debug=on";
                queue_url = queue_parts1 +encoded_spot + queue_parts2 + encoded_spot + queue_parts3 + encoded_spot + queue_parts4 + encoded_spot + queue_parts5 + encoded_spot + queue_parts6 + encoded_spot + queue_parts7 + encoded_spot + queue_parts8 + encoded_spot + queue_parts9 + encoded_spot + queue_parts10 + encoded_spot + queue_parts11 + encoded_spot + queue_parts12 + encoded_spot + queue_parts13 + encoded_spot + queue_parts14 + encoded_spot + queue_parts15 + encoded_spot + queue_parts16 + encoded_spot + queue_parts17 + encoded_spot + queue_parts18 + encoded_spot + queue_parts19 + encoded_spot + queue_parts20 + encoded_spot + queue_parts21 + encoded_spot + queue_parts22 + encoded_spot + queue_parts23;
                System.out.println("encoded_spot"+encoded_spot);
            }else{
                String queue_parts1 = "http://lod.per.c.fun.ac.jp:8000/sparql/?query=PREFIX%20rdf%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f1999%2f02%2f22%2drdf%2dsyntax%2dns%23%3e%0d%0aPREFIX%20rdfs%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2000%2f01%2frdf%2dschema%23%3e%0d%0aPREFIX%20schema%3a%20%3chttp%3a%2f%2fschema%2eorg%2f%3e%0d%0aPREFIX%20dc%3a%20%3chttp%3a%2f%2fpurl%2eorg%2fdc%2felements%2f1%2e1%2f%3e%0d%0aPREFIX%20geo%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2003%2f01%2fgeo%2fwgs84_pos%23%3e%0d%0aPREFIX%20xsd%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2001%2fXMLSchema%23%3e%0d%0aPREFIX%20dcterms%3a%20%3chttp%3a%2f%2fpurl%2eorg%2fdc%2fterms%2f%3e%0d%0aPREFIX%20foaf%3a%20%3chttp%3a%2f%2fxmlns%2ecom%2ffoaf%2f0%2e1%2f%3e%0d%0a%0d%0aSELECT%20DISTINCT%20%3fdobokuname%20%3fdescription%20%3faccess%20%3farea%20%3fimage%20%3faddress%20%3ftelephone%20%3furl%20%3ffilmName%20%3fdirector%20%3factor%20%3ffilmdescription%20%3ffilmurl%20%3fbornDate%20%3fdobokudescription%20%3fdobokuurl%20%3fcreator%0d%0a%0d%0a%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fhakobura_akiba%2erdf%3e%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2ffilm_akiba%2erdf%3e%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fdoboku_akiba%2erdf%3e%0d%0a%0d%0a%0d%0a%0d%0aWHERE%20%7b%0d%0a%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fhakobura_akiba%2erdf%3e%20%7b%0d%0a%20%3fhs%20rdfs%3alabel%20%22";
                String queue_parts2 = "%22%3b%0d%0a%20dc%3adescription%20%3fdescription%3b%0d%0a%20schema%3adescription%20%3faccess%3b%0d%0a%20schema%3acontainedIn%20%3farea%3b%0d%0a%20schema%3aimage%20%3fimage%3b%0d%0a%20schema%3aaddress%20%3faddress%3b%0d%0a%20schema%3atelephone%20%3ftelephone%3b%0d%0a%20schema%3aurl%20%3furl%2e%0d%0a%7d%0d%0a%0d%0a%0d%0a%0d%0aOPTIONAL%20%7b%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2ffilm_akiba%2erdf%3e%7b%0d%0a%3ffss%20schema%3aname%20%22";
                String queue_parts3 = "%22%3b%0d%0adcterms%3adescription%20%3ffilmdescription%2e%0d%0a%0d%0a%3ffs%20dc%3arelation%20%3ffss%3b%0d%0ardfs%3alabel%20%3ffilmName%3b%0d%0adc%3acreator%20%3fdirector%3b%0d%0afoaf%3aperson%20%3factor%3b%0d%0aschema%3aurl%20%3ffilmurl%0d%0a%7d%0d%0a%7d%0d%0a%0d%0a%0d%0aOPTIONAL%20%7b%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fdoboku_akiba%2erdf%3e%7b%0d%0a%3fds%20rdfs%3acomment%20%22";
                String queue_parts4 = "%22%3b%0d%0adc%3adescription%20%3fdobokudescription%3b%0d%0aschema%3aurl%20%3fdobokuurl%2e%0d%0a%7d%0d%0a%7d%0d%0a%0d%0aOPTIONAL%20%7b%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fdoboku_akiba%2erdf%3e%7b%0d%0a%3fds%20rdfs%3acomment%20%22";
                String queue_parts5 = "%22%3b%0d%0adc%3adate%20%3fbornDate%3b%0d%0adc%3acreator%20%3fcreator%3b%0d%0a%7d%0d%0a%7d%0d%0a%0d%0aOPTIONAL%20%7b%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fdoboku_akiba%2erdf%3e%7b%0d%0a%3fds%20rdfs%3acomment%20%22";
                String queue_parts6 = "%22%3b%0d%0ardfs%3alabel%20%3fdobokuname%3b%0d%0a%7d%0d%0a%7d%0d%0a%0d%0a%7d&output=json";
                queue_url = queue_parts1 + encoded_spot + queue_parts2 + encoded_spot + queue_parts3 + encoded_spot + queue_parts4 + encoded_spot + queue_parts5 + encoded_spot + queue_parts6;
                System.out.println("encoded_spot"+encoded_spot);
            }

            Log.d("URLはこれだぁぁぁ",queue_url);


            // クエリを実行してデータを取得
            try {
                URL url = new URL(queue_url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                String str = InputStreamToString(con.getInputStream());
                System.out.println("ここまでおｋ0");
                System.out.println("str"+str);

                // 受け取ったJSONをパースする
                JSONObject json = new JSONObject(str);
                System.out.println("ここまでおｋ１");
                System.out.println("json"+json);
                JSONObject json_results = json.getJSONObject("results");
                System.out.println("ここまでおｋ２");
                System.out.println("json_result"+json_results);
                bindings = json_results.getJSONArray("bindings");
                System.out.println("ここまでおｋ３");
                System.out.println(bindings);
                //ここで死亡
                final JSONObject binding = bindings.getJSONObject(0);     // bindingはbindingsのゼロ番目であることに注意
                System.out.println("ここまでおｋ４");

                // 映画ロケ地である場合はその情報をパース
                List<String> film_spots = new ArrayList<String>();
                Collection<String> film_spots_distinct = new LinkedHashSet<String>();

                try {
                    if (!binding.getJSONObject("filmName").isNull("value")) {
                        // 映画名リスト
                        for (int i = 0; i < bindings.length(); i++) {
                            String film_spot = bindings.getJSONObject(i).getJSONObject("filmName").getString("value");
                            film_spots.add(film_spot);
                        }
                        // 重複を排除する
                        film_spots_distinct.addAll(film_spots);
                    }
                }catch (JSONException e){
                    System.out.println("映画とか関係ないお");
                }


                // 土木遺産である場合はその情報をパース。名前を取得する
                List<String> doboku_spots = new ArrayList<String>();
                Collection<String> doboku_spots_distinct = new LinkedHashSet<String>();
                try{
                    if (!binding.getJSONObject("dobokuname").isNull("value")) {
                        // 土木遺産名リスト
                        for (int i = 0; i < bindings.length(); i++) {
                            String doboku_spot = bindings.getJSONObject(i).getJSONObject("dobokuname").getString("value");
                            doboku_spots.add(doboku_spot);
                        }
                        // 重複を排除する
                        doboku_spots_distinct.addAll(doboku_spots);
                    }
                }catch(JSONException e){
                    System.out.println("遺産とか関係ないお");
                }

                final Collection<String> film_collection = film_spots_distinct;
                final Collection<String> doboku_collection = doboku_spots_distinct;

                final String description_str = getDataFromJSON(binding,"description","shopdescription");


                final String featuredProductName= getDataFromJSON(binding,"","featuredproductname");
                final String featuredProductPrice= getDataFromJSON(binding,"","featuredproductprice");
                final String featuredProductImage= getDataFromJSON(binding,"","featuredproductimage");
                final String featuredProductDescription= getDataFromJSON(binding,"","featuredproductdescription");



                final String postcode_str = getDataFromJSON(binding,"postcode","postcode");
                final String access_str = getDataFromJSON(binding,"access","");
                final String area_str = getDataFromJSON(binding,"area","");
                final String image_str = getDataFromJSON(binding,"image","shopimage");
                final String address_str = getDataFromJSON(binding,"address","address");
                final String telephone_str = getDataFromJSON(binding,"telephone","telephone");
                final String fax_str = getDataFromJSON(binding,"fax","faxNumber");
                final String openTime_str = getDataFromJSON(binding,"","openingHoursSpecification");
                final String horiday_str= getDataFromJSON(binding,"","closed");
                final String parking_str= getDataFromJSON(binding,"","parking");
                final String eatIn_str= getDataFromJSON(binding,"","eatin");
                final String eMail_str= getDataFromJSON(binding,"","email");
                final String url_str = getDataFromJSON(binding,"url","url");

                // 受け取った結果をViewへ反映
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TextViewへ設定

                        // スポット画像。はこぶら取得したデータを反映する
                        ImageGetThread hakoburaimg_thread = new ImageGetThread(image_str,TOPIMAGE_MODE);
                        hakoburaimg_thread.start();

                        // スポット名
                        TextView name_text = (TextView) findViewById(R.id.name_text);
                        name_text.setText(queue_title);

                        // 概要
                        TextView description_text = (TextView) findViewById(R.id.description_text);
                        description_text.setText(description_str);

                        //おすすめ商品
                        TextView featured_text=(TextView) findViewById(R.id.featured_text);
                        ImageView featured_img = (ImageView) findViewById(R.id.featuredImage_view);
                        TextView featured_name=(TextView) findViewById(R.id.featuredName_text);
                        TextView nextButton_text=(TextView) findViewById(R.id.nextButton_text);
                        if(featuredProductName.equals("")){
                            featured_text.setVisibility(GONE);
                            featured_img.setVisibility(GONE);
                            featured_name.setVisibility(GONE);
                            nextButton_text.setVisibility(GONE);
                        }else{
                            ImageGetThread featuredImg_thread = new ImageGetThread(featuredProductImage,FEATURE_MODE);
                            featuredImg_thread.start();
                            featured_name.setText(featuredProductName);
                            featured_name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getApplication(), FeaturedDetailActivity.class);
                                    intent.putExtra("name",featuredProductName);
                                    intent.putExtra("description",featuredProductDescription);
                                    intent.putExtra("img_url",featuredProductImage);
                                    intent.putExtra("price",featuredProductPrice);
                                    intent.putExtra("url_str",url_str);
                                    startActivity(intent);
                                }
                            });
                            featured_img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getApplication(), FeaturedDetailActivity.class);
                                    intent.putExtra("name",featuredProductName);
                                    intent.putExtra("description",featuredProductDescription);
                                    intent.putExtra("img_url",featuredProductImage);
                                    intent.putExtra("price",featuredProductPrice);
                                    intent.putExtra("url_str",url_str);
                                    startActivity(intent);
                                }
                            });
                            findViewById(R.id.nextButton_text).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getApplication(), FeaturedDetailActivity.class);
                                    intent.putExtra("name",featuredProductName);
                                    intent.putExtra("description",featuredProductDescription);
                                    intent.putExtra("img_url",featuredProductImage);
                                    intent.putExtra("price",featuredProductPrice);
                                    intent.putExtra("url_str",url_str);
                                    startActivity(intent);
                                }
                            });
                        }

                        //基本情報

                        //郵便番号
                        TextView postcode_text = (TextView) findViewById(R.id.postcode_text);
                        if(postcode_str.equals("")){
                            findViewById(R.id.spot_postcode).setVisibility(GONE);
                            postcode_text.setVisibility(GONE);
                        }else{
                            postcode_text.setText(postcode_str);
                        }

                        // 所在地
                        TextView address_text = (TextView) findViewById(R.id.address_text);
                        if(address_str.equals("")){
                            findViewById(R.id.spot_address).setVisibility(GONE);
                            address_text.setVisibility(GONE);
                        }else{
                            address_text.setText(address_str);
                        }

                        // 電話番号
                        TextView tel_text = (TextView) findViewById(R.id.tel_text);
                        tel_text.setText(telephone_str);
                        if(telephone_str.equals("")){
                            findViewById(R.id.spot_tel).setVisibility(GONE);
                            address_text.setVisibility(GONE);
                        }

                        //Fax番号
                        TextView fax_text = (TextView) findViewById(R.id.fax_text);
                        fax_text.setText(fax_str);
                        if(fax_str.equals("")){
                            findViewById(R.id.spot_fax).setVisibility(GONE);
                            fax_text.setVisibility(GONE);
                        }

                        //営業時間
                        TextView openTime_text = (TextView) findViewById(R.id.openTime_text);
                        openTime_text.setText(openTime_str);
                        if(openTime_str.equals("")){
                            findViewById(R.id.spot_openTime).setVisibility(GONE);
                            openTime_text.setVisibility(GONE);
                        }

                        //定休日
                        TextView horiday_text = (TextView) findViewById(R.id.horiday_text);
                        horiday_text.setText(horiday_str);
                        if(horiday_str.equals("")){
                            findViewById(R.id.spot_horiday).setVisibility(GONE);
                            horiday_text.setVisibility(GONE);
                        }

                        // アクセス
                        TextView access_text = (TextView) findViewById(R.id.access_text);
                        access_text.setText(access_str);
                        if(access_str.equals("")){
                            findViewById(R.id.spot_access).setVisibility(GONE);
                            access_text.setVisibility(GONE);
                        }

                        // エリア
                        TextView area_text = (TextView) findViewById(R.id.area_text);
                        area_text.setText(area_str);
                        Log.d("FILM_COUNT", String.format("%d", film_collection.size()));
                        Log.d("DOBOKU_COUNT", String.format("%d", doboku_collection.size()));
                        if(area_str.equals("")){
                            findViewById(R.id.spot_area).setVisibility(GONE);
                            area_text.setVisibility(GONE);
                        }

                        // 駐車場
                        TextView parking_text = (TextView) findViewById(R.id.parking_text);
                        parking_text.setText(parking_str);
                        if(parking_str.equals("")){
                            findViewById(R.id.spot_parking).setVisibility(GONE);
                            parking_text.setVisibility(GONE);
                        }

                        // イートイン
                        TextView eatIn_text = (TextView) findViewById(R.id.eatIn_text);
                        eatIn_text.setText(eatIn_str);
                        if(eatIn_str.equals("")){
                            findViewById(R.id.spot_eatIn).setVisibility(GONE);
                            eatIn_text.setVisibility(GONE);
                        }

                        // メール
                        TextView eMail_text = (TextView) findViewById(R.id.eMail_text);
                        eMail_text.setText(eMail_str);
                        if(eMail_str.equals("")){
                            findViewById(R.id.spot_eMail).setVisibility(GONE);
                            eMail_text.setVisibility(GONE);
                        }

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
                            findViewById(R.id.film_table).setVisibility(GONE);
                            findViewById(R.id.powered_hakodatefilm).setVisibility(GONE);
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
                            findViewById(R.id.doboku_table).setVisibility(GONE);
                            findViewById(R.id.powered_doboku).setVisibility(GONE);
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
