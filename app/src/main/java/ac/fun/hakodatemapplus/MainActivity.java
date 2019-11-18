package ac.fun.hakodatemapplus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        implements OnMapReadyCallback, LocationListener {

    MapFragment mf;
    ProgressDialog progressDialog;
    private String title;
    private int course_id;
    private GoogleMap gMap;
    private final int REQUESTCODE_OF_PERMISSION = 1;//requestPermission実行時のrequestCode
    private double latitude = 41.773746;//緯度　あらかじめ函館駅前の座標を入れておく
    private double longitude = 140.726399;//経度
    private boolean isOnMapReadyCalled = false; // 設定画面での海抜スイッチ操作の事情で必要。

    // スポット表示の初期設定
    private boolean is_show_taberu = true;
    private boolean is_show_miru = true;
    private boolean is_show_asobu = true;
    private boolean is_show_kaimono = true;
    private boolean is_show_onsen = true;
    private boolean is_show_event = true;
    private boolean is_show_sweets = true;

    // 避難モード
    private boolean is_escape_mode = false;
    private MenuItem escape_menu = null;    // 状態をアイコンとしてセットするためにメニューを用意

    // 海抜を表示するかどうか初期設定
    private boolean is_show_altitude = false;
    private boolean is_show_hinanjo = false;
    private boolean is_show_tsunamibuilding = false;

    // GooglePlay開発者サービスの準備ができていないときは表示設定を操作できないようにする
    private boolean isMapReady = false;
    private LocationManager mLocationManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Debug", "onCreate is called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // この時点でネットワークに接続できるかどうか調べる
        if (checkNetworkStatus()) {
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }


//        //Permissionの確認を実行するか否かを判定。
//        // API23以降(Android6.0以降)はパーミッションの確認へ
        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("Debug3", "API23以上");
            checkLocationPermitted();
        } else {
            Log.d("Debug3", "API22以下");
        }

    }

    //パーミッション許可の確認を行う。
    private void checkLocationPermitted() {
        //マップの初期位置を得るためにパーミッション許可の確認を行う
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Debug3", "Location is already permitted");
        } else {
            Log.d("Debug3", "Location is not permitted and Request permission");
            // 権限が与えられていないならば許可を申請する。
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUESTCODE_OF_PERMISSION);
        }
    }

    //マップの初期位置を得るためにrequestPermissionの結果を受け取る。
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUESTCODE_OF_PERMISSION:
                if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {  //位置情報の使用が許可された場合
                    Log.d("Debug3", "Location is just permitted");
                    locationStart();
                } else {
                    Log.d("Debug3", "Location is just refused");    //位置情報の仕様が拒否された場合
                }
                break;
            default:
                Log.d("Debug3", "Location is just refused");
                break;
        }
    }

    //パーミッションをクリアしたときにロケーションマネージャーのインスタンスを作りアップデート
    // このメソッドの機能は以下の通りである。
    // ・LocationManagerのインスタンスを取得
    // ・現在地の緯度経度をGPS→インターネットの順で取得
    // ・函館市内にいれば、緯度経度のフィールドを上記データで上書き
    // ・現在地のピンを表示
    // ・初期位置にカメラを移動
    //このメソッドは3つの場合において動作する
    // １．マップ起動時から位置情報がすでに許可されているとき
    // ２．マップ起動時のダイアログで位置情報の使用を始めて許可したとき
    // ３．表示設定画面にて位置情報の使用を許可したとき
    @SuppressLint("MissingPermission")
    private void locationStart() {
        Log.d("Debug3", "locationStart is called");
        //今まではonCreate内でmLocationManagerをインスタンス化したが、今回はパーミッションをクリアしないとインスタンス化されないようにした。
        mLocationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        // GPSでmLocateの変数に位置情報が取得されなければ。インターネットからの位置情報の取得を試す。
        // どちらかで位置情報が取得されれば現在地に緯度経度が更新。そうでなければ緯度経度初期化時の函館駅前の座標が入ったまま。
        @SuppressLint("MissingPermission") Location myLocate = mLocationManager.getLastKnownLocation("gps");
        //GPSによる取得ができなかった場合はインターネットから現在地の更新を試みる。
        if (myLocate == null) {
            myLocate = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.d("Debug", "GPS location failed and trying to acquire location from network");
        } else {
            Log.d("Debug", "Successfully acquired location from GPS");
        }

        //GPSかネットワーク、どちらかで現在地が取得できた場合は、latitudeとlongitudeの値をそれぞれ更新する。
        //函館市外にいる場合は、値の更新は行われない。
        if (myLocate != null) {
            if (myLocate.getLongitude() >= 140.6924363 &&
                    myLocate.getLongitude() <= 141.1867893 &&
                    myLocate.getLatitude() >= 41.708836 &&
                    myLocate.getLatitude() <= 42.010086) {
                Log.d("Debug", "acquired latitude is " + String.valueOf(myLocate.getLatitude()));
                Log.d("Debug", "acquired longitude is" + String.valueOf(myLocate.getLongitude()));
                latitude = myLocate.getLatitude();
                longitude = myLocate.getLongitude();
            }else{
                Log.d("Debug", "Could not acquire Original Location.");
            }
        }

        //gMapのオブジェクトが取得できていれば、現在位置を表示する。並びに、カメラを現在位置へ移動する。
        if (gMap != null) {
            gMap.setMyLocationEnabled(true);

            //位置情報がOFFの状態で現在位置に飛ぼうとした場合は通知する。
            gMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if(!(mLocationManager != null && mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
                        Toast.makeText(getApplicationContext(), "現在地を表示するには位置情報をONにしてください。", Toast.LENGTH_LONG).show();
                    }
                    return false;
                }
            });

            Log.d("Debug", "camera is moving to the following latitude: " + String.valueOf(latitude));
            Log.d("Debug", "camera is moving to the following longitude:" + String.valueOf(longitude));

            CameraPosition Hakodate = new CameraPosition
                    .Builder()
                    .target(new LatLng(latitude, longitude)) //2019/10/10白戸。変数に緯度経度の情報が入る。
                    .zoom(13)
                    .build();

            gMap.moveCamera(CameraUpdateFactory.newCameraPosition(Hakodate));    // 初期表示位置へ移動
        }
        // GPS使用可能時に表示するLog
        if (mLocationManager != null && mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("Debug", "location manager Enabled");
        }

    }



    // Google Mapが利用できるとき
    @Override
    public void onMapReady(GoogleMap map) {
        isOnMapReadyCalled = true;
        Log.d("Debug", "onMapReady is called");
        gMap = map;

        //位置情報のパーミッションがすでに許可されている場合は、locationStart()を実行する。
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationStart();
        }

        map.setTrafficEnabled(false);

        // インフォウィンドウに触ったときの処理
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker arg0) {
                String marker_title = arg0.getTitle();
                String marker_snippet = arg0.getSnippet();
                String marker_id = arg0.getTag().toString();

                Log.d("Title", marker_title);
                Log.d("Snippet", marker_snippet);
                if (!marker_title.equals("スタート") && !marker_snippet.equals("まちあるきコース")) {
                    Intent intent;

                    if (marker_snippet.indexOf("津波避難所") != -1 || marker_snippet.indexOf("津波避難ビル") != -1) {
                        intent = new Intent(MainActivity.this, ShelterDetailActivity.class);
                    } else {
                        intent = new Intent(MainActivity.this, SpotDetailActivity.class);
                    }
                    intent.putExtra("spot_title", marker_title);    // 第二引数：マーカーのタイトル
                    intent.putExtra("spot_category", marker_snippet);
                    intent.putExtra("spot_id", marker_id);
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
                String marker_snippet = marker.getSnippet();
                TextView snippet = (TextView) view.findViewById(R.id.spot_info_snippet);
                snippet.setText(marker_snippet);

                if (marker_snippet.indexOf("津波避難所") != -1 || marker_snippet.indexOf("津波避難ビル") != -1) {
                    snippet.setTextColor(Color.BLUE);
                }

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


        //すでに位置情報のパーミッションが許可されている場合はカメラ移動が二重に起こることになる。
        Log.d("Debug", "camera is moving to the following latitude: " + String.valueOf(latitude));
        Log.d("Debug", "camera is moving to the following longitude:" + String.valueOf(longitude));

        CameraPosition Hakodate = new CameraPosition
                .Builder()
                .target(new LatLng(latitude, longitude)) //変数に緯度経度の情報が入る。
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

    // 別のActivityから戻ってきた場合
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d("Debug", "onActivityResult");
        System.out.println("onActivityResult");
        int enable_spoticons = 0;   // 観光スポットが有効にされた数
        int enable_escapeitems = 0; // 避難所・避難ビル・海抜が有効にされた数

        if (requestCode == 1002) {
            // 返却結果ステータスとの比較
            if (resultCode == Activity.RESULT_OK) {
                // この時点でネットワークに接続できるかどうか調べる
                if (checkNetworkStatus()) {
                    // 表示設定画面からの値を取得
                    is_show_taberu = intent.getExtras().getBoolean("is_show_taberu");
                    is_show_miru = intent.getExtras().getBoolean("is_show_miru");
                    is_show_asobu = intent.getExtras().getBoolean("is_show_asobu");
                    is_show_kaimono = intent.getExtras().getBoolean("is_show_kaimono");
                    is_show_onsen = intent.getExtras().getBoolean("is_show_onsen");
                    is_show_event = intent.getExtras().getBoolean("is_show_event");
                    is_show_sweets = intent.getExtras().getBoolean("is_show_sweets");
                    is_show_hinanjo = intent.getExtras().getBoolean("is_show_hinanjo");
                    is_show_tsunamibuilding = intent.getExtras().getBoolean("is_show_tsunamibuilding");
                    is_show_altitude = intent.getExtras().getBoolean("is_show_altitude");

                    // 観光スポットを非表示にし、避難所・避難ビル・海抜がすべて表示されたら避難モードにする
                    if (is_show_taberu == false &&
                            is_show_miru == false &&
                            is_show_asobu == false &&
                            is_show_kaimono == false &&
                            is_show_onsen == false &&
                            is_show_event == false &&
                            is_show_sweets == false &&
                            is_show_hinanjo == true &&
                            is_show_tsunamibuilding == true &&
                            is_show_altitude == true) {
                        is_escape_mode = true;
                    } else {
                        is_escape_mode = false;
                    }

                    //パーミッションが切れた状態で海抜スイッチがONになっているという状況を防ぐため、スイッチとパーミッションを確認する。
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (is_show_altitude == true && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                            Log.d("Debug", "例外発生:Permission 海抜スイッチをオフに");
                            is_show_altitude = false;
                        }
                    }

                    //パーミッションが許可されていた場合locationStart()を実行
                    // APIレベルとパーミッションを再度確認し、許可が通っていれば、現在地の表示を行う。
                    // 海抜表示がオンになっていれば、現在地にカメラを移動する。現在地の表示も行う。
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            locationStart();

                            //ユーザーが海抜スイッチをONにした後にGPSをOFFにすることによって、スイッチはONなのに海抜は取得できないという状態を防ぐ。
                            if (is_show_altitude == true && !(mLocationManager != null && mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
                                Log.d("Debug", "例外発生:GPS 海抜スイッチをオフに");
                                is_show_altitude = false;
                            }

                        }
                    }
                    Log.d("Debug","siOnMapReadyCalled is " + isOnMapReadyCalled);
                    if(isOnMapReadyCalled) {
                        mapRepaint();
                    }
                }
            }
        }

    }


    // 観光スポットのピンを再描画する
    private void mapRepaint() {
        Log.d("Debug", "mapRepaint is called");
        // 表示するピンを反映するために地図上のOverlayを全消去
        try {
            // GoogleMapオブジェクトの取得
            //2019/10/28白戸追加。
            if (gMap != null) {
                gMap.clear();
            }
        }
        // GoogleMapが使用不可・Overlayをクリアできなかったとき
        catch (Exception e) {
            System.out.println("古いOverlayをクリアできませんでした");
            e.printStackTrace();
        }

        // 避難モードのインジケータを設定する
        if (is_escape_mode == true) {
            escape_menu.setIcon(R.drawable.escape_button_on);
        } else {
            escape_menu.setIcon(R.drawable.escape_button_off);
        }


        // 海抜を表示する設定なら取得を開始する
        LinearLayout altitude_container = (LinearLayout) findViewById(R.id.altitude_container);
        if (is_show_altitude == true) {
            altitude_container.setVisibility(View.VISIBLE);
            startGetAltitude();
        } else {
            altitude_container.setVisibility(View.INVISIBLE);
        }

        // まちあるきコースを表示して、地図の中心をコースのスタートにしない
        createMatiarukiMapWithStart(MatiarukiCourse.getMatiarukiCourse(course_id), false);

        // 更新された設定で観光スポットのピンを表示
        getSPARQLInvoke();
    }

    public void onResume() {
        System.out.println("onResume");
        Log.d("Debug","onResume is called");


        // 海抜を表示する設定なら取得を開始する
        LinearLayout altitude_container = (LinearLayout) findViewById(R.id.altitude_container);
        if (is_show_altitude == true) {
            altitude_container.setVisibility(View.VISIBLE);
            startGetAltitude();
        } else {
            altitude_container.setVisibility(View.INVISIBLE);
        }


        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // この画面が隠れるのなら、位置情報の取得は終了
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // この画面が隠れるのなら、位置情報の取得は終了
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
        super.onPause();
    }

    // メニューを読み込む
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(title);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.mapplus_icon);


        // 後でアイコンを変更するために避難モードのボタンを取得
        escape_menu = menu.findItem(R.id.menu_escape);

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
                intent.putExtra("is_show_sweets", is_show_sweets);
                intent.putExtra("is_show_hinanjo", is_show_hinanjo);
                intent.putExtra("is_show_tsunamibuilding", is_show_tsunamibuilding);
                intent.putExtra("is_show_altitude", is_show_altitude);

                // 遷移先から返却されてくる際の識別コード
                int requestCode = 1002;// 返却値を考慮したActivityの起動を行う
                startActivityForResult(intent, requestCode);

            } else {
                Toast.makeText(this, "地図が読み込まれていません。Google Play開発者サービスが更新されていない場合は更新をお願いします。", Toast.LENGTH_LONG).show();
            }
            return true;

            // 避難モードボタンを押したときの処理
        } else if (id == R.id.menu_escape) {
            if (is_escape_mode == false) {
                is_show_taberu = false;
                is_show_miru = false;
                is_show_asobu = false;
                is_show_kaimono = false;
                is_show_onsen = false;
                is_show_event = false;
                is_show_sweets = false;
                is_show_hinanjo = true;
                is_show_tsunamibuilding = true;

                is_escape_mode = true;

                //位置情報がOFFの時トーストで通知。Permissionが通ってないときはそっちも通知。
                if (Build.VERSION.SDK_INT >= 23 && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    //パーミッションが通っていないとき
                    is_show_altitude = false;
                    Log.d("Debug", "GPSがパーミットされてないときののトーストを表示");
                    Toast toastLocationNotPermitted = Toast.makeText(this, "GPSによる位置情報の使用を許可すると、現在地の海抜を表示できます。\n端末の[設定]→[アプリ]→[はこだてMap+]→[許可]→[位置情報]から設定できます。", Toast.LENGTH_LONG);
                    toastLocationNotPermitted.show();
                    // さらに、GPSがOFFの場合もトーストで通知。
                } else if (!(mLocationManager != null && mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
                    is_show_altitude = false;
                    Log.d("Debug", "GPSOFF時のトーストを表示");
                    Toast toastLocationDisabled = Toast.makeText(this, "GPSがOFFになっています。\nGPSをONにすると、現在地の海抜を表示できます。", Toast.LENGTH_LONG);
                    toastLocationDisabled.show();
                    // GPSのPermissionが通って、GPSもきちんとONの場合のみ海抜表示をONに。
                } else {
                    Log.d("Debug", "location manager Enabled");
                    is_show_altitude = true;

                }


                mapRepaint();
            } else {
                is_show_taberu = true;
                is_show_miru = true;
                is_show_asobu = true;
                is_show_kaimono = true;
                is_show_onsen = true;
                is_show_event = true;
                is_show_sweets = true;
                is_show_hinanjo = false;
                is_show_tsunamibuilding = false;
                is_show_altitude = false;
                is_escape_mode = false;

                mapRepaint();
            }
            return true;
            //アクションバーの戻るを押したときの処理
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // GPSの有効・無効をチェックしながら海抜の取得を開始する
    @SuppressLint("MissingPermission")
    private void startGetAltitude() {
        // 位置情報が取得できるかどうか確認する
        //permissionをクリアしてmLocationManagerがインスタンス化されたら以前と同じ動作
        //mLocationManagerがインスタンス化されていなければ海抜は「----」と表示。
        // ※パーミッションが通っていないとき（mLocationManagerのインスタンスがnullの時）は、海抜スイッチはOFFになるはずなので、
        // 「----」と表示される場合は、万が一位置情報が利用できないのに海抜スイッチがONになっている場合のためにある。
        if (mLocationManager != null &&  (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            final boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (mLocationManager != null && gpsEnabled) {

                // 海抜の表示をリセットする
                //GPSが有効の時、今までと同じ動作。
                if (gpsEnabled) {
                    TextView alt_val_tv = (TextView) findViewById(R.id.altitude_value);
                    alt_val_tv.setText("取得中");

                    mLocationManager.getProvider(LocationManager.GPS_PROVIDER);
                    mLocationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            2000,
                            0,
                            this);
                }
            } else {
                TextView alt_val_tv = (TextView) findViewById(R.id.altitude_value);
                alt_val_tv.setText("----");
            }
        } else {
            TextView alt_val_tv = (TextView) findViewById(R.id.altitude_value);
            alt_val_tv.setText("----");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double alt = location.getAltitude();
        TextView alt_val_tv = (TextView) findViewById(R.id.altitude_value);
        alt_val_tv.setText(String.format("%.1fm", alt));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//        switch (status) {
//            case LocationProvider.AVAILABLE:
//                Log.v("Status", "AVAILABLE");
//                break;
//            case LocationProvider.OUT_OF_SERVICE:
//                Log.v("Status", "OUT_OF_SERVICE");
//                break;
//            case LocationProvider.TEMPORARILY_UNAVAILABLE:
//                Log.v("Status", "TEMPORARILY_UNAVAILABLE");
//                break;
//        }
    }


    @Override
    public void onProviderEnabled(String provider) {
//        Log.v("Provider", "ENABLED");
    }

    @Override
    public void onProviderDisabled(String provider) {
//        Log.v("Provider", "DISABLED");
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
        // 2019/10/28白戸

//        String search_title = title.replaceAll("[ 　]", "");
        if(title != null) {
            String search_title = title.replaceAll("[ 　]", "");

            if(gMap != null) {
                if (course_id != 0) {
                    SparqlGetThread st = new SparqlGetThread(gMap, search_title);
                    st.start();
                } else {
                    SparqlGetThread st = new SparqlGetThread(gMap, "");
                    st.start();
                }
            }
        }
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
                try {
                    spot_detail.add(2, binding.getJSONObject("spotName").getString("value"));
                } catch (JSONException e) {
                    spot_detail.add(2, binding.getJSONObject("shopname").getString("value"));
                }

                try {
                    spot_detail.add(3, binding.getJSONObject("category").getString("value"));
                } catch (JSONException e) {
                    spot_detail.add(3, "函館スイーツ");
                }
                spot_detail.add(4, binding.getJSONObject("lat").getString("value"));
                spot_detail.add(5, binding.getJSONObject("long").getString("value"));

                if (spot_detail.get(3) == "函館スイーツ") {
                    spot_detail.add(6, binding.getJSONObject("id").getString("value"));
                } else {
                    spot_detail.add(6, null);
                }

                spot_list.add(spot_detail);
            }
            //System.out.println("ここだよ！！"+spot_list);
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
            builder.setMessage("GPSを有効にすると地図に自分の場所が表示できます。設定アプリを開いてGPSを有効にしますか?").setTitle("GPSが使えません")
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
    private class SparqlGetThread extends Thread {
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
                //スイーツ情報の取得
                if (encoded_course != null) {
                    System.out.println("スイーツ情報の呼び出し");
                    String que_sweets_url = "http://lod.per.c.fun.ac.jp:8080/sparql?default-graph-uri=&query=PREFIX+geo%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2003%2F01%2Fgeo%2Fwgs84_pos%23%3E%0D%0APREFIX+sweets%3A+%3Chttp%3A%2F%2Flod.fun.ac.jp%2Fhakobura%2Fterms%2Fsweet%23%3E%0D%0APREFIX+dc%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0D%0APREFIX+schema%3A+%3Chttp%3A%2F%2Fschema.org%2F%3E%0D%0A%0D%0Aselect+%3Fid+%3Fshopname+%3Flat+%3Flong+where+%7B%0D%0A%3Fs+sweets%3Aid+%3Fid.%0D%0A%3Fs+sweets%3Ashopname+%3Fshopname.%0D%0A%3Fs+geo%3Alat+%3Flat.%0D%0A%3Fs+geo%3Along+%3Flong.%0D%0A%7D&format=application%2Fsparql-results%2Bjson&timeout=0&debug=on";
                    setSparqlResultFromQueue(spot_list, que_sweets_url);
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
            BitmapDescriptor sweets = BitmapDescriptorFactory.fromResource(R.drawable.sweets);

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

            //System.out.println("ここでもあるかも"+final_list.size());

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
                            case "函館スイーツ":
                                options.snippet("まちあるきコース - 函館スイーツ");

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
                    //System.out.println("ここですよー！！！へい！"+ final_list.get(i).get(3));
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
                            // System.out.println("ここで判定！観光イベント"+is_pin_show);
                            break;
                        case "函館スイーツ":
                            options.icon(sweets);
                            options.snippet("函館スイーツ");
                            is_pin_show = is_show_sweets;
                            //System.out.println("ここで判定！"+is_pin_show);
                            break;
                        case "津波避難所":
                            options.icon(hinanjo);
                            options.snippet(String.format("津波避難所 - 海抜%sm", final_list.get(i).get(1)));
                            is_pin_show = is_show_hinanjo;
                            break;
                        case "津波避難ビル":
                            options.icon(tsunamibuilding);
                            options.snippet(String.format("津波避難ビル - 海抜%sm", final_list.get(i).get(1)));
                            is_pin_show = is_show_tsunamibuilding;
                    }
                }
                Marker marker = gm.addMarker(options);
                //println("id:"+final_list.get(i).get(6));

                String thisId = final_list.get(i).get(6);
                if (thisId == null) {
                    marker.setTag("");
                } else {
                    marker.setTag(thisId);
                }

                //System.out.println("this_marker:"+marker.getTag());
                marker.setVisible(is_pin_show);
            }

            // 読み込み中のダイアログを閉じる
            progressDialog.dismiss();
        }
    }
}