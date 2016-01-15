package ac.fun.hakodatemapplus;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.*;


import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static ac.fun.hakodatemapplus.DetailActivity.InputStreamToString;

//
// はこだてMap+ Android
//
// コース地図画面・地図画面用
//

public class MainActivity extends FragmentActivity
        implements OnMapReadyCallback {

    MapFragment mf;
    ProgressDialog progressDialog;
    private String title;
    private int course_id;
    private GoogleMap gMap;
    // スポット表示の初期設定
    private boolean is_show_taberu = true;
    private boolean is_show_miru = true;
    private boolean is_show_asobu = true;
    private boolean is_show_kaimono = true;
    private boolean is_show_onsen = true;
    private boolean is_show_event = true;
    private boolean is_show_hinanjo = false;
    private boolean is_show_tsunamibuilding = false;
    // GooglePlay開発者サービスの準備ができていないときは表示設定を操作できないようにする
    private boolean isMapReady = false;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // この時点でネットワークに接続できるかどうか調べる
        if(checkNetworkStatus()) {
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    // Google Mapが利用できるとき
    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;

        // 位置情報が取得できるかどうか確認する
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            DialogFragment dialog = new NoLocationDialogFragment();
            dialog.show(getFragmentManager(), null);
        }

        mLocationManager = (LocationManager) this.getSystemService(Service.LOCATION_SERVICE);
        Location myLocate = mLocationManager.getLastKnownLocation("gps");

        map.setTrafficEnabled(false);
        map.setMyLocationEnabled(true);

        // インフォウィンドウに触ったときの処理
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker arg0) {
                String marker_title = arg0.getTitle();
                String marker_snippet = arg0.getSnippet();
                Log.d("MARKER", marker_title);
                if (!marker_title.equals("スタート") && !marker_snippet.equals("まちあるきコース")) {
                    Intent intent;

                    if(marker_snippet.equals("津波避難所") || marker_snippet.equals("津波避難ビル") ){
                        intent = new Intent(MainActivity.this, ShelterDetailActivity.class);
                    } else {
                        intent = new Intent(MainActivity.this, SpotDetailActivity.class);
                    }
                    intent.putExtra("spot_title", marker_title);    // 第二引数：マーカーのタイトル
                    // 遷移先から返却されてくる際の識別コード
                    int requestCode = 1001;// 返却値を考慮したActivityの起動を行う
                    startActivityForResult(intent, requestCode);
                }
            }
        });

        // インフォウィンドウの中身を設定する
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.spot_info_window, null);
                // タイトルを設定
                TextView title = (TextView) view.findViewById(R.id.spot_info_title);
                title.setText(marker.getTitle());

                // スニペットを設定
                TextView snippet = (TextView) view.findViewById(R.id.spot_info_snippet);
                snippet.setText(marker.getSnippet());

                // 画像を設定
                if (!marker.getSnippet().equals("まちあるきコース")) {
                    ImageView img = (ImageView) view.findViewById(R.id.spot_info_icon);
                    img.setImageResource(R.drawable.infomark);
                }
                return view;
            }

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }
        });


        // 地図の初期表示位置を設定する
        CameraPosition Hakodate = new CameraPosition
                .Builder()
                .target(new LatLng(41.773746, 140.726399))
                .zoom(13)
                .build();

        map.moveCamera(CameraUpdateFactory.newCameraPosition(Hakodate));    // 初期表示位置へ移動

        //DetailActivityの値を呼び出す
        Intent intent = getIntent();

        if (intent.getExtras() != null) {  //取得した値がnullじゃなかったら
            title = intent.getStringExtra("title");
            course_id = intent.getExtras().getInt("course_id");
        } else {
            title = "周辺の地図";
            course_id = 0;
        }

        // まちあるきコースを表示して、地図の中心をコースのスタートにする
        if (course_id != 0) {
            createMatiarukiMapWithStart(MatiarukiCourse.getMatiarukiCourse(course_id), true);
        }

        // 観光スポットのピンを表示
        getSPARQLInvoke();
        isMapReady = true;
    }

    // 読み込み中のダイアログを出しながら観光スポットのデータを問い合わせる
    public void getSPARQLInvoke() {
        // 地図読み込み中のダイアログを表示する
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("地図を読み込んでいます…");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // SPARQLのクエリで使う形式に変換する(半角・全角スペース除去)
        String search_title = title.replaceAll("[ 　]", "");

        if (course_id != 0) {
            SparqlGetThread st = new SparqlGetThread(gMap, search_title);
            st.start();
        } else {
            SparqlGetThread st = new SparqlGetThread(gMap, "");
            st.start();
        }
    }

    // 別のActivityから戻ってきた場合
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        System.out.println("onActivityResult");

        if (requestCode == 1002) {
            // 返却結果ステータスとの比較
            if (resultCode == Activity.RESULT_OK) {
                // この時点でネットワークに接続できるかどうか調べる
                if(checkNetworkStatus()) {
                    // 表示設定画面からの値を取得
                    is_show_taberu = intent.getExtras().getBoolean("is_show_taberu");
                    is_show_miru = intent.getExtras().getBoolean("is_show_miru");
                    is_show_asobu = intent.getExtras().getBoolean("is_show_asobu");
                    is_show_kaimono = intent.getExtras().getBoolean("is_show_kaimono");
                    is_show_onsen = intent.getExtras().getBoolean("is_show_onsen");
                    is_show_event = intent.getExtras().getBoolean("is_show_event");
                    is_show_hinanjo = intent.getExtras().getBoolean("is_show_hinanjo");
                    is_show_tsunamibuilding = intent.getExtras().getBoolean("is_show_tsunamibuilding");
                    System.out.println(is_show_taberu);

                    // 表示するピンを反映するために地図上のOverlayを全消去
                    try {
                        // GoogleMapオブジェクトの取得
                        gMap.clear();
                    }
                    // GoogleMapが使用不可のときのためにtry catchで囲っています。
                    catch (Exception e) {
                        System.out.println("古いOverlayをクリアできませんでした");
                        e.printStackTrace();
                    }

                    // まちあるきコースを表示して、地図の中心をコースのスタートにしない
                    createMatiarukiMapWithStart(MatiarukiCourse.getMatiarukiCourse(course_id), false);

                    // 更新された設定で観光スポットのピンを表示
                    getSPARQLInvoke();
                }
            }
        }

    }

    public void onResume() {
        System.out.println("onResume");
        super.onResume();
    }

    // メニューを読み込む
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
        actionBar.setLogo(R.drawable.mapplus_icon);

        actionBar.setDisplayHomeAsUpEnabled(true);

        return super.onCreateOptionsMenu(menu);
    }

    // 画面左上の戻るボタン・表示設定が押されたとき
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_displaysetting) {
            if (isMapReady == true) {
                Intent intent = new Intent(MainActivity.this, DisplaySettingActivity.class);

                // 現在の地図画面の状態をセットする
                intent.putExtra("is_show_taberu", is_show_taberu);
                intent.putExtra("is_show_miru", is_show_miru);
                intent.putExtra("is_show_asobu", is_show_asobu);
                intent.putExtra("is_show_kaimono", is_show_kaimono);
                intent.putExtra("is_show_onsen", is_show_onsen);
                intent.putExtra("is_show_event", is_show_event);
                intent.putExtra("is_show_hinanjo", is_show_hinanjo);
                intent.putExtra("is_show_tsunamibuilding", is_show_tsunamibuilding);

                // 遷移先から返却されてくる際の識別コード
                int requestCode = 1002;// 返却値を考慮したActivityの起動を行う
                startActivityForResult(intent, requestCode);

            } else {
                Toast.makeText(this, "地図が読み込まれていません。Google Play開発者サービスが更新されていない場合は更新をお願いします。", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        //アクションバーの戻るを押したときの処理
        else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    // まちあるきコースをスタートと共に描画
    public void createMatiarukiMapWithStart(List<LatLng> course_list, boolean isDoReset) {
        if (course_list != null) {
            LatLng start_position = course_list.get(0);    //スタート地点の緯度経度

            MarkerOptions options = new MarkerOptions();
            options.position(start_position);
            options.title("スタート");
            options.snippet("まちあるきコース");

            gMap.addMarker(options); //スタート地点にピンをたてる
            if (isDoReset)
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start_position, 16));    //スタート地点へカメラを調整

            //コースの線を描く
            for (int i = 0; i < course_list.size() - 1; i++) {
                // 直線
                PolylineOptions straight = new PolylineOptions()
                        .add(course_list.get(i), course_list.get(i + 1))
                        .geodesic(false)    // 直線
                        .color(Color.RED)
                        .width(6);
                gMap.addPolyline(straight);
            }
        }
    }

    // SPARQLのクエリを実行して結果をArrayList形式で取得する。成功すると1を、失敗すると0を返す
    public boolean setSparqlResultFromQueue(ArrayList<ArrayList<String>> spot_list, String queue_url) {
        try {
            URL url = new URL(queue_url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String str = InputStreamToString(con.getInputStream());

            // 受け取ったJSONをパースする
            JSONObject json = new JSONObject(str);
            JSONObject json_results = json.getJSONObject("results");
            JSONArray bindings = json_results.getJSONArray("bindings");

            for (int i = 0; i < bindings.length(); i++) {
                JSONObject binding = bindings.getJSONObject(i);
                ArrayList spot_detail = new ArrayList<>();

                try {
                    spot_detail.add(0, binding.getJSONObject("courseName").getString("value"));
                } catch (JSONException e) {
                    spot_detail.add(0, null);
                }
                try {
                    spot_detail.add(1, binding.getJSONObject("rootNum").getString("value"));
                } catch (JSONException e) {
                    spot_detail.add(1, null);
                }
                spot_detail.add(2, binding.getJSONObject("spotName").getString("value"));
                try {
                    spot_detail.add(3, binding.getJSONObject("category").getString("value"));
                } catch (JSONException e) {
                    spot_detail.add(3, null);
                }
                spot_detail.add(4, binding.getJSONObject("lat").getString("value"));
                spot_detail.add(5, binding.getJSONObject("long").getString("value"));

                spot_list.add(spot_detail);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;

    }

    // 位置情報が無効になっている場合のダイアログ
    public static class NoLocationDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("位置情報を有効にすると地図に自分の場所が表示できます。設定アプリを開いて位置情報を有効にしますか?").setTitle("位置情報が使えません")
                    .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }).setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            this.setCancelable(false);
            return builder.create();
        }
    }

    // ネットワーク接続がないときのダイアログ
    public static class NoConnectionDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("ネットワーク接続がないため、データを取得できません。").setTitle("ネットワークオフライン")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity calling_activity = (MainActivity) getActivity();
                            calling_activity.finish();
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
                            MainActivity calling_activity = (MainActivity) getActivity();
                            calling_activity.finish();
                        }
                    });
            this.setCancelable(false);
            return builder.create();
        }
    }

    // SPARQLのクエリを実行して取得したデータを反映する
    class SparqlGetThread extends Thread {
        GoogleMap gm;
        String queue_title;

        SparqlGetThread(GoogleMap gm, String name) {
            this.gm = gm;
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
            String queue_parts1 = "http://lod.per.c.fun.ac.jp:8000/sparql/?query=PREFIX%20rdf%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f1999%2f02%2f22%2drdf%2dsyntax%2dns%23%3e%0d%0aPREFIX%20rdfs%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2000%2f01%2frdf%2dschema%23%3e%0d%0aPREFIX%20schema%3a%20%3chttp%3a%2f%2fschema%2eorg%2f%3e%0d%0aPREFIX%20dc%3a%20%3chttp%3a%2f%2fpurl%2eorg%2fdc%2felements%2f1%2e1%2f%3e%0d%0aPREFIX%20geo%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2003%2f01%2fgeo%2fwgs84_pos%23%3e%0d%0aPREFIX%20xsd%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2001%2fXMLSchema%23%3e%0d%0aPREFIX%20dcterms%3a%20%3chttp%3a%2f%2fpurl%2eorg%2fdc%2fterms%2f%3e%0d%0aPREFIX%20foaf%3a%20%3chttp%3a%2f%2fxmlns%2ecom%2ffoaf%2f0%2e1%2f%3e%0d%0a%0d%0aSELECT%20DISTINCT%20%3fcourseName%20%3frootNum%20%3fspotName%20%3fcategory%20%3flat%20%3flong%20%3furl%20%0d%0a%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fmachiaruki_akiba%2erdf%3e%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fhakobura_akiba%2erdf%3e%0d%0a%0d%0a%0d%0aWHERE%20%7b%0d%0a%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fhakobura_akiba%2erdf%3e%20%7b%0d%0a%20%3fhs%20rdfs%3alabel%20%3fspotName%3b%0d%0a%20rdfs%3acomment%20%3fcategory%3b%0d%0a%20geo%3alat%20%3flat%3b%0d%0a%20geo%3along%20%3flong%3b%0d%0a%7d%0d%0a%0d%0aOPTIONAL%20%7b%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fmachiaruki_akiba%2erdf%3e%20%7b%0d%0a%3fmss%20schema%3aname%20%3fspotName%3b%0d%0adc%3asubject%20%3frootNum%2e%0d%0a%3fms%20dc%3arelation%20%3fmss%3b%0d%0ardfs%3alabel%20%3fcourseName%2e%0d%0a%7d%0d%0aFILTER%28%3fms%20%3d%20%3curn%3a";
            String queue_parts2 = "%3e%29%0d%0a%7d%0d%0a%0d%0a%7d&output=json";
            String queue_url = queue_parts1 + encoded_course + queue_parts2;


            //観光スポットの取得
            ArrayList<ArrayList<String>> spot_list = new ArrayList<ArrayList<String>>();
            if (!setSparqlResultFromQueue(spot_list, queue_url)) {
                DialogFragment dialog = new ConnectionErrorDialogFragment();
                dialog.show(getFragmentManager(), null);
            } else {
                final ArrayList<ArrayList<String>> final_list = spot_list;

                //まちあるきマップにしかないスポットの取得
                if (encoded_course != null) {
                    String queue_machi1 = "http://lod.per.c.fun.ac.jp:8000/sparql/?query=PREFIX%20rdfs%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2000%2f01%2frdf%2dschema%23%3e%0d%0aPREFIX%20schema%3a%20%3chttp%3a%2f%2fschema%2eorg%2f%3e%0d%0aPREFIX%20dc%3a%20%3chttp%3a%2f%2fpurl%2eorg%2fdc%2felements%2f1%2e1%2f%3e%0d%0aPREFIX%20geo%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2003%2f01%2fgeo%2fwgs84_pos%23%3e%0d%0a%0d%0aSELECT%20DISTINCT%20%3fcourseName%20%3frootNum%20%3fspotName%20%3fcategory%20%3flat%20%3flong%0d%0a%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fmachiaruki_akiba%2erdf%3e%0d%0a%0d%0aWHERE%20%7b%0d%0a%7b%0d%0a%20%20%20%20%3curn%3a";
                    String queue_machi2 = "%3e%20rdfs%3alabel%20%3fcourseName%3b%0d%0a%20%20%20%20dc%3arelation%20%3fmspotURI%2e%0d%0a%20%20%20%20%3fmspotURI%20dc%3asubject%20%3frootNum%3b%0d%0a%20%20%20%20schema%3aname%20%3fspotName%3b%0d%0a%20%20%20%20geo%3alat%20%3flat%3b%0d%0a%20%20%20%20geo%3along%20%3flong%2e%0d%0a%7d%0d%0a%7d&output=json";
                    String queue_machi_url = queue_machi1 + encoded_course + queue_machi2;
                    System.out.println("2回目の呼び出し");
                    setSparqlResultFromQueue(spot_list, queue_machi_url);
                }

                //避難所の取得
                String queue_shelter_url = "http://lod.per.c.fun.ac.jp:8000/sparql/?query=PREFIX%20rdf%3a%20%3Chttp%3a%2f%2fwww%2ew3%2eorg%2f1999%2f02%2f22-rdf-syntax-ns%23%3E%0d%0aPREFIX%20rdfs%3a%20%3Chttp%3a%2f%2fwww%2ew3%2eorg%2f2000%2f01%2frdf-schema%23%3E%0d%0aPREFIX%20geo%3a%20%3Chttp%3a%2f%2fwww%2ew3%2eorg%2f2003%2f01%2fgeo%2fwgs84_pos%23%3E%0d%0aPREFIX%20schema%3a%20%3Chttp%3a%2f%2fschema%2eorg%2f%3E%0d%0aPREFIX%20shelter%3a%20%3Chttp%3a%2f%2flod%2eper%2ec%2efun%2eac%2ejp%2fbosai%2fterms%2fshelter%23%3E%0d%0aPREFIX%20evcx%3a%20%3Chttp%3a%2f%2fsmartercity%2ejp%2fevacuation%23%3E%0d%0a%0d%0aSELECT%20DISTINCT%20%3fspotName%20%3frootNum%20%3fcategory%20%3flat%20%3flong%0d%0a%0d%0aFROM%20%3Cfile%3a%2f%2f%2fvar%2flib%2f4store%2fshelter%2erdf%3E%0d%0a%0d%0aWHERE%20%7b%0d%0a%20%20%3fs%20rdfs%3alabel%20%3fspotName%3b%0d%0a%20%20%20%20geo%3aalt%20%3frootNum%3b%0d%0a%20%20%20%20shelter%3atypeOfshelter%20%3fcategory%3b%0d%0a%20%20%20%20geo%3alat%20%3flat%3b%0d%0a%20%20%20%20geo%3along%20%3flong%3b%0d%0a%7d&output=json";
                System.out.println("3回目の呼び出し");
                setSparqlResultFromQueue(spot_list, queue_shelter_url);

                // 受け取った結果を地図へ反映
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSpotPinFromFinalList(final_list);
                    }
                });
            }
        }

        public void setSpotPinFromFinalList(ArrayList<ArrayList<String>> final_list) {
            BitmapDescriptor taberu = BitmapDescriptorFactory.fromResource(R.drawable.taberu);
            BitmapDescriptor miru = BitmapDescriptorFactory.fromResource(R.drawable.miru);
            BitmapDescriptor asobu = BitmapDescriptorFactory.fromResource(R.drawable.asobu);
            BitmapDescriptor kaimono = BitmapDescriptorFactory.fromResource(R.drawable.kaimono);
            BitmapDescriptor onsen = BitmapDescriptorFactory.fromResource(R.drawable.onsen);
            BitmapDescriptor event = BitmapDescriptorFactory.fromResource(R.drawable.event);

            BitmapDescriptor pin01 = BitmapDescriptorFactory.fromResource(R.drawable.pin01);
            BitmapDescriptor pin02 = BitmapDescriptorFactory.fromResource(R.drawable.pin02);
            BitmapDescriptor pin03 = BitmapDescriptorFactory.fromResource(R.drawable.pin03);
            BitmapDescriptor pin04 = BitmapDescriptorFactory.fromResource(R.drawable.pin04);
            BitmapDescriptor pin05 = BitmapDescriptorFactory.fromResource(R.drawable.pin05);
            BitmapDescriptor pin06 = BitmapDescriptorFactory.fromResource(R.drawable.pin06);
            BitmapDescriptor pin07 = BitmapDescriptorFactory.fromResource(R.drawable.pin07);
            BitmapDescriptor pin08 = BitmapDescriptorFactory.fromResource(R.drawable.pin08);
            BitmapDescriptor pin09 = BitmapDescriptorFactory.fromResource(R.drawable.pin09);
            BitmapDescriptor pin10 = BitmapDescriptorFactory.fromResource(R.drawable.pin10);
            BitmapDescriptor pin11 = BitmapDescriptorFactory.fromResource(R.drawable.pin11);
            BitmapDescriptor pin12 = BitmapDescriptorFactory.fromResource(R.drawable.pin12);
            BitmapDescriptor pin13 = BitmapDescriptorFactory.fromResource(R.drawable.pin13);
            BitmapDescriptor pin14 = BitmapDescriptorFactory.fromResource(R.drawable.pin14);
            BitmapDescriptor pin15 = BitmapDescriptorFactory.fromResource(R.drawable.pin15);
            BitmapDescriptor pin16 = BitmapDescriptorFactory.fromResource(R.drawable.pin16);
            BitmapDescriptor pin17 = BitmapDescriptorFactory.fromResource(R.drawable.pin17);
            BitmapDescriptor pin18 = BitmapDescriptorFactory.fromResource(R.drawable.pin18);
            BitmapDescriptor pin19 = BitmapDescriptorFactory.fromResource(R.drawable.pin19);
            BitmapDescriptor pin20 = BitmapDescriptorFactory.fromResource(R.drawable.pin20);

            BitmapDescriptor hinanjo = BitmapDescriptorFactory.fromResource(R.drawable.hinanjo);
            BitmapDescriptor tsunamibuilding = BitmapDescriptorFactory.fromResource(R.drawable.tsunamibuilding);

            // スポットのピンを地図上に表示
            for (int i = 0; i < final_list.size(); i++) {
                LatLng location = new LatLng(Double.parseDouble(final_list.get(i).get(4)),
                        Double.parseDouble(final_list.get(i).get(5)));

                // マーカー(観光スポットのピン)の設定
                MarkerOptions options = new MarkerOptions();

                // ピンの位置を緯度経度で設定
                options.position(location);

                // 観光スポット名を設定
                options.title(final_list.get(i).get(2));

                boolean is_pin_show = true;

                // ピンにcourseNameがセットされていればまちあるきコース
                if (final_list.get(i).get(0) != null) {
                    // カテゴリが設定されていなければはこぶらにない(詳細が出せない)
                    if (final_list.get(i).get(3) == null) {
                        options.snippet("まちあるきコース");
                    } else {
                        switch (final_list.get(i).get(3)) {
                            case "食べる":
                                options.snippet("まちあるきコース - 食べる");
                                break;
                            case "見る":
                                options.snippet("まちあるきコース - 見る");
                                break;
                            case "遊ぶ":
                                options.snippet("まちあるきコース - 遊ぶ");
                                break;
                            case "買う":
                                options.snippet("まちあるきコース - 買う");
                                break;
                            case "温泉":
                                options.snippet("まちあるきコース - 温泉");
                                break;
                            case "観光カレンダー":
                                options.snippet("まちあるきコース - 観光イベント");
                                break;
                        }
                    }
                    switch (final_list.get(i).get(1)) {
                        case "1":
                            options.icon(pin01);
                            break;
                        case "2":
                            options.icon(pin02);
                            break;
                        case "3":
                            options.icon(pin03);
                            break;
                        case "4":
                            options.icon(pin04);
                            break;
                        case "5":
                            options.icon(pin05);
                            break;
                        case "6":
                            options.icon(pin06);
                            break;
                        case "7":
                            options.icon(pin07);
                            break;
                        case "8":
                            options.icon(pin08);
                            break;
                        case "9":
                            options.icon(pin09);
                            break;
                        case "10":
                            options.icon(pin10);
                            break;
                        case "11":
                            options.icon(pin11);
                            break;
                        case "12":
                            options.icon(pin12);
                            break;
                        case "13":
                            options.icon(pin13);
                            break;
                        case "14":
                            options.icon(pin14);
                            break;
                        case "15":
                            options.icon(pin15);
                            break;
                        case "16":
                            options.icon(pin16);
                            break;
                        case "17":
                            options.icon(pin17);
                            break;
                        case "18":
                            options.icon(pin18);
                            break;
                        case "19":
                            options.icon(pin19);
                            break;
                        case "20":
                            options.icon(pin20);
                            break;
                    }

                    // courseNameが設定されていないピンは通常の観光スポットとして扱う
                } else {
                    // カテゴリを設定する
                    options.snippet(final_list.get(i).get(3));
                    switch (final_list.get(i).get(3)) {
                        case "食べる":
                            options.icon(taberu);
                            is_pin_show = is_show_taberu;
                            break;
                        case "見る":
                            options.icon(miru);
                            is_pin_show = is_show_miru;
                            break;
                        case "遊ぶ":
                            options.icon(asobu);
                            is_pin_show = is_show_asobu;
                            break;
                        case "買う":
                            options.icon(kaimono);
                            is_pin_show = is_show_kaimono;
                            break;
                        case "温泉":
                            options.icon(onsen);
                            is_pin_show = is_show_onsen;
                            break;
                        case "観光カレンダー":
                            options.icon(event);
                            options.snippet("観光イベント");
                            is_pin_show = is_show_event;
                            break;
                        case "津波避難所":
                            options.icon(hinanjo);
                            is_pin_show = is_show_hinanjo;
                            break;
                        case "津波避難ビル":
                            options.icon(tsunamibuilding);
                            is_pin_show = is_show_tsunamibuilding;
                    }
                }
                Marker marker = gm.addMarker(options);
                marker.setVisible(is_pin_show);
            }

            // 読み込み中のダイアログを閉じる
            progressDialog.dismiss();
        }
    }
}