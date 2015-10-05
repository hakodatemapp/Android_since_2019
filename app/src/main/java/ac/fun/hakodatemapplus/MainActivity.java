package ac.fun.hakodatemapplus;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.*;


import android.app.*;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//
// はこだてMap+ Android
//
// コース地図画面・地図画面用
//

public class MainActivity extends FragmentActivity {

	MapFragment mf;
	private String title;
    private int course_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mf = MapFragment.newInstance();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(android.R.id.content, mf);
		ft.commit();

		//DetailActivityの値を呼び出す
		Intent intent = getIntent();

		if (intent.getExtras() != null) {  //取得した値がnullじゃなかったら
			title = intent.getStringExtra("title");
            course_id = intent.getExtras().getInt("course_id");
        }
		else {
			title = "周辺の地図";
		}


        Toast toast = Toast.makeText(this, "selected course: " + course_id, Toast.LENGTH_SHORT);
        toast.show();
	}

	//add menu
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, 0, "公立はこだて未来大学 FUN");

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(title);
		actionBar.setLogo(R.drawable.mapplus_icon);

		actionBar.setDisplayHomeAsUpEnabled(true);

		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		//アクションバーの戻るを押したときの処理
		else if(id==android.R.id.home){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    public void createMatiarukiMapWithStart(GoogleMap gm, List<LatLng> course_list) {
        gm.addMarker(new MarkerOptions().position(course_list.get(0)).title("スタート"));
        for(int i=0; i<course_list.size() - 1; i++) {
            // 直線
            PolylineOptions straight = new PolylineOptions()
                    .add(course_list.get(i), course_list.get(i+1))
                    .geodesic(false)    // 直線
                    .color(Color.RED)
                    .width(6);
            gm.addPolyline(straight);
        }
    }

	public void onResume(){
		super.onResume();

		GoogleMap gm;
		gm = mf.getMap();
		gm.setTrafficEnabled(true);
		gm.setMyLocationEnabled(true);
		gm.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
			@Override
			public void onInfoWindowClick(Marker arg0) {
				Intent intent = new Intent(MainActivity.this, PingActivity.class);
				MainActivity.this.startActivity(intent);
			}});

		// 地図の初期表示位置を設定する
		CameraPosition Hakodate = new CameraPosition
				.Builder()
		.target(new LatLng(41.773746,140.726399))
		.zoom(13)
		.build();

		gm.moveCamera(CameraUpdateFactory.newCameraPosition(Hakodate));	// 初期表示位置へ移動

        // 選択したまちあるきコースに応じて表示内容を決定する
        List<LatLng> course_list = new ArrayList<LatLng>();

        switch(course_id) {
            // 1. これぞ王道！函館の魅力凝縮コース
            case 1:
                course_list.add(new LatLng(41.76653550555116,140.71242928504944));
                course_list.add(new LatLng(41.7665084979006,140.7124038040638));
                course_list.add(new LatLng(41.767150676734815,140.71136444807053));
                course_list.add(new LatLng(41.76521386783585,140.70920661091805));
                course_list.add(new LatLng(41.76482874973684,140.70976920425892));
                course_list.add(new LatLng(41.76470596159717,140.70978127419949));
                course_list.add(new LatLng(41.76373515177974,140.71160919964314));
                course_list.add(new LatLng(41.76338103511448,140.7122502475977));
                course_list.add(new LatLng(41.76287336445208,140.7129107415676));
                course_list.add(new LatLng(41.7630614281412,140.71315482258797));
                course_list.add(new LatLng(41.76246372381709,140.71400307118893));
                course_list.add(new LatLng(41.762715310499836, 140.71430817246437));
                course_list.add(new LatLng(41.76285435798475, 140.71411237120628));
                createMatiarukiMapWithStart(gm, course_list);
                break;

			//2. てくてく坂道大三坂八幡坂編
			case 2:
				course_list.add(new LatLng(41.76653550555116,140.71242928504944));
				course_list.add(new LatLng(41.76663153266101,140.712531208992));
				course_list.add(new LatLng(41.76654500824031,140.71266800165176));
				course_list.add(new LatLng(41.76719018752795,140.7133962213993));
				course_list.add(new LatLng(41.76640946975133,140.7146018743515));
				course_list.add(new LatLng(41.76565800116302,140.71377038955688));
				course_list.add(new LatLng(41.76480174136783,140.71513697504997));
				course_list.add(new LatLng(41.763689386812196,140.71388237178326));
				course_list.add(new LatLng(41.76324223885451,140.71487344801426));
				course_list.add(new LatLng(41.762495484814536,140.71400575339794));
				course_list.add(new LatLng(41.763082685305434,140.71318231523037));
				course_list.add(new LatLng(41.76287961657827,140.71292147040367));
				course_list.add(new LatLng(41.763387037108146,140.71224823594093));
				course_list.add(new LatLng(41.76373715243334, 140.71162194013596));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//3. きらめきのライトアップ教会編
			case 3:
				course_list.add(new LatLng(41.76653550555116,140.71242928504944));
				course_list.add(new LatLng(41.7665084979006,140.7124038040638));
				course_list.add(new LatLng(41.767150676734815,140.71136444807053));
				course_list.add(new LatLng( 41.76521386783585,140.70920661091805));
				course_list.add(new LatLng(41.76482874973684,140.70976920425892));
				course_list.add(new LatLng(41.76470596159717,140.70978127419949));
				course_list.add(new LatLng(41.76373515177974,140.71160919964314));
				course_list.add(new LatLng(41.76338103511448,140.7122502475977));
				course_list.add(new LatLng(41.76287336445208,140.7129107415676));
				course_list.add(new LatLng(41.764192799659796,140.71447849273682));
				course_list.add(new LatLng(41.76474047234042,140.71360409259796));
				course_list.add(new LatLng(41.76466844989868,140.71353033185005));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//4. きらめきのライトアップ海辺編
			case 4:
				course_list.add(new LatLng(41.76653550555116,140.71242928504944));
				course_list.add(new LatLng(41.7665084979006,140.7124038040638));
				course_list.add(new LatLng(41.76715117687159,140.71136847138405));
				course_list.add(new LatLng(41.765862311464055,140.70993416011333));
				course_list.add(new LatLng(41.76588731880015,140.70989191532135));
				course_list.add(new LatLng(41.76718368575352,140.71132823824883));
				course_list.add(new LatLng(41.76734773032188,140.7114214450121));
				course_list.add(new LatLng(41.7679323857531064,140.71206852793694));
				course_list.add(new LatLng(41.7680189083027,140.71183785796165));
				course_list.add(new LatLng(41.76809942918337,140.71173928678036));
				course_list.add(new LatLng(41.769307730385556,140.71089439094067));
				course_list.add(new LatLng(41.770479500749694,140.70990800857544));
				course_list.add(new LatLng(41.771296176527734,140.70902824401855));
				course_list.add(new LatLng(41.77171676299783,140.7082698494196));
				course_list.add(new LatLng(41.771652750015065,140.70820480585098));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//5. 湯の川あったか散歩道
			case 5:
				course_list.add(new LatLng(41.77503759890953,140.7835467159748));
				course_list.add(new LatLng(41.77476055660197,140.78532300889492));
				course_list.add(new LatLng(41.77760893449195,140.78536793589592));
				course_list.add(new LatLng(41.77855353188113,140.78579507768154));
				course_list.add(new LatLng(41.779653877786146,140.78542292118073));
				course_list.add(new LatLng(41.77975238545125,140.78542962670326));
				course_list.add(new LatLng(41.77977738737266,140.786102861166));
				course_list.add(new LatLng(41.78049943865646,140.78824192285538));
				course_list.add(new LatLng(41.780721452790914,140.7888025045395));
				course_list.add(new LatLng(41.78234403275567,140.78790932893753));
				course_list.add(new LatLng(41.78346706405908,140.78681766986847));
				course_list.add(new LatLng(41.7841775737053,140.78886151313782));
				course_list.add(new LatLng(41.7840203222488,140.79007387161255));
				course_list.add(new LatLng(41.78483282562265,140.7902368158102));
				course_list.add(new LatLng(41.78501457496813,140.79036086797714));
				course_list.add(new LatLng(41.78421607400313,140.79241812229156));
				course_list.add(new LatLng(41.782790547557305,140.792043954134));
				course_list.add(new LatLng(41.782300031128756,140.7920292019844));
				course_list.add(new LatLng(41.781477995183785,140.79234570264816));
				course_list.add(new LatLng(41.78080295778703,140.79250931739807));
				course_list.add(new LatLng(41.780792957179585,140.79315036535263));
				course_list.add(new LatLng(41.78074795442677,140.7931463420391));
				course_list.add(new LatLng(41.7807569549799,140.792498588562));
				course_list.add(new LatLng(41.78123498253959,140.7912701368332));
				course_list.add(new LatLng(41.78124698318562,140.79092144966125));
				course_list.add(new LatLng(41.78140299137952,140.79066932201385));
				course_list.add(new LatLng(41.78169500569671,140.79046815633774));
				course_list.add(new LatLng(41.78106497314633, 140.7904440164566));
				course_list.add(new LatLng(41.78110897562075,140.79135864973068));
				course_list.add(new LatLng(41.7798968964222,140.79124331474304));
				course_list.add(new LatLng(41.77962087518429,140.7912728190422));
				course_list.add(new LatLng(41.77849377613241,140.7917918264866));
				course_list.add(new LatLng(41.77807073384213,140.79178914427757));
				course_list.add(new LatLng(41.77778070323639,140.79163625836372));
				course_list.add(new LatLng(41.77738465932531,140.7913452386856));
				course_list.add(new LatLng(41.77705562098359,140.79096972942352));
				course_list.add(new LatLng(41.77673258169887,140.79056471586227));
				course_list.add(new LatLng(41.77611700233441,140.7893630862236));
				course_list.add(new LatLng(41.77569044387465,140.78981570899487));
				course_list.add(new LatLng(41.77523887889689,140.78918807208538));
				course_list.add(new LatLng(41.775045850151244,140.78905060887337));
				course_list.add(new LatLng(41.77491533038522,140.79054057598114));
				course_list.add(new LatLng(41.77439524898628,140.79043060541153));
				course_list.add(new LatLng(41.774411251553744,140.79031258821487));
				course_list.add(new LatLng(41.77433823980726,140.7902978360653));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//6. 幕末の志士達が駆け抜けた箱館1
			case 6:
				course_list.add(new LatLng(41.77012442108145,140.7092186808586));
				course_list.add(new LatLng(41.770138674317266,140.7092347741127));
				course_list.add(new LatLng(41.77003765131438,140.70937223732471));
				course_list.add(new LatLng(41.770495254238725,140.7098637521267));
				course_list.add(new LatLng(41.770462747034884,140.7098999619484));
				course_list.add(new LatLng(41.76893488987201,140.7082638144493));
				course_list.add(new LatLng(41.770888,140.704458));
				course_list.add(new LatLng(41.772152,140.705756));
				course_list.add(new LatLng(41.773288,140.704039));
				course_list.add(new LatLng(41.77332,140.701078));
				course_list.add(new LatLng(41.772104,140.701067));
				course_list.add(new LatLng(41.77210108960229,140.70240788161755));
				course_list.add(new LatLng(41.77177002375129,140.70277601480484));
				course_list.add(new LatLng(41.77029570975835,140.70084147155285));
				course_list.add(new LatLng(41.769941129187764,140.70145837962627));
				course_list.add(new LatLng(41.76991112227945,140.70143423974514));
				course_list.add(new LatLng(41.77027620537734,140.70081934332848));
				course_list.add(new LatLng(41.769220,140.699536));
				course_list.add(new LatLng(41.769104,140.699699));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//7. 箱館はじめて物語
			case 7:
				course_list.add(new LatLng(41.764134,140.718717));
				course_list.add(new LatLng(41.764068,140.718744));
				course_list.add(new LatLng(41.763965,140.718245));
				course_list.add(new LatLng(41.763892,140.717718));
				course_list.add(new LatLng(41.763896,140.717212));
				course_list.add(new LatLng(41.764044,140.716574));
				course_list.add(new LatLng(41.764276,140.716024));
				course_list.add(new LatLng(41.762460,140.713985));
				course_list.add(new LatLng(41.762754,140.713564));
				course_list.add(new LatLng(41.761874,140.712416));
				course_list.add(new LatLng(41.761740,140.712612));
				course_list.add(new LatLng(41.761661,140.712865));
				course_list.add(new LatLng(41.761563,140.712996));
				course_list.add(new LatLng(41.761522,140.713163));
				course_list.add(new LatLng(41.761062,140.713587));
				course_list.add(new LatLng(41.760991,140.713848));
				course_list.add(new LatLng(41.761695,140.713351));
				course_list.add(new LatLng(41.762445,140.714009));
				course_list.add(new LatLng(41.764262,140.716056));
				course_list.add(new LatLng(41.764824,140.716708));
				course_list.add(new LatLng(41.765064,140.716938));
				course_list.add(new LatLng(41.765312,140.717499));
				course_list.add(new LatLng(41.765589,140.718032));
				course_list.add(new LatLng(41.765782,140.717887));
				course_list.add(new LatLng(41.766823,140.716930));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//8. 函館寺社巡り
			case 8:
				course_list.add(new LatLng(41.753245,140.71634));
				course_list.add(new LatLng(41.752973,140.716249));
				course_list.add(new LatLng(41.753582,140.713214));
				course_list.add(new LatLng(41.753931,140.711580));
				course_list.add(new LatLng(41.754203,140.711627));
				course_list.add(new LatLng(41.754391,140.711798));
				course_list.add(new LatLng(41.754927,140.712739));
				course_list.add(new LatLng(41.755110,140.713142));
				course_list.add(new LatLng(41.755426,140.713604));
				course_list.add(new LatLng(41.755981,140.714111));
				course_list.add(new LatLng(41.756589,140.714835));
				course_list.add(new LatLng(41.757309,140.715394));
				course_list.add(new LatLng(41.757664,140.715601));
				course_list.add(new LatLng(41.757733,140.715495));
				course_list.add(new LatLng(41.759036,140.714618));
				course_list.add(new LatLng(41.759228,140.715148));
				course_list.add(new LatLng(41.761700,140.713332));
				course_list.add(new LatLng(41.762342,140.714142));
				course_list.add(new LatLng(41.763050,140.713168));
				course_list.add(new LatLng(41.762874,140.712937));
				course_list.add(new LatLng(41.763552,140.711999));
				course_list.add(new LatLng(41.764259,140.710575));
				course_list.add(new LatLng(41.763988,140.710401));
				course_list.add(new LatLng(41.764001,140.710384));
				course_list.add(new LatLng(41.764263,140.710551));
				course_list.add(new LatLng(41.764688,140.709783));
				course_list.add(new LatLng(41.764791,140.709782));
				course_list.add(new LatLng(41.765572,140.708601));
				course_list.add(new LatLng(41.765821,140.708398));
				course_list.add(new LatLng(41.766504,140.707578));
				course_list.add(new LatLng(41.767124,140.706479));
				course_list.add(new LatLng(41.768780,140.703485));
				course_list.add(new LatLng(41.768390,140.703081));
				course_list.add(new LatLng(41.767737,140.702445));
				course_list.add(new LatLng(41.767266,140.701994));
				course_list.add(new LatLng(41.766826,140.701540));
				course_list.add(new LatLng(41.766836,140.701517));
				course_list.add(new LatLng(41.767271,140.701978));
				course_list.add(new LatLng(41.767743,140.702416));
				course_list.add(new LatLng(41.768408,140.703044));
				course_list.add(new LatLng(41.768803,140.703445));
				course_list.add(new LatLng(41.770275,140.700845));
				course_list.add(new LatLng(41.769403,140.699803));
				course_list.add(new LatLng(41.769081,140.699388));
				course_list.add(new LatLng(41.768975,140.699496));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//9. 北の豪商高田屋嘉兵衛物語
			case 9:
				course_list.add(new LatLng(41.764134,140.718717));
				course_list.add(new LatLng(41.764068,140.718744));
				course_list.add(new LatLng(41.763965,140.718245));
				course_list.add(new LatLng(41.763892,140.717718));
				course_list.add(new LatLng(41.763896,140.717212));
				course_list.add(new LatLng(41.764044,140.716574));
				course_list.add(new LatLng(41.764276,140.716024));
				course_list.add(new LatLng(41.764816,140.715169));
				course_list.add(new LatLng(41.764895,140.715274));
				course_list.add(new LatLng(41.764380,140.716172));
				course_list.add(new LatLng(41.765041,140.716951));
				course_list.add(new LatLng(41.765121,140.717081));
				course_list.add(new LatLng(41.765300,140.717486));
				course_list.add(new LatLng(41.765545,140.717986));
				course_list.add(new LatLng(41.765595,140.717998));
				course_list.add(new LatLng(41.765750,140.717889));
				course_list.add(new LatLng(41.765761,140.717922));
				course_list.add(new LatLng(41.765615,140.718031));
				course_list.add(new LatLng(41.765528,140.718071));
				course_list.add(new LatLng(41.765277,140.718238));
				course_list.add(new LatLng(41.764221,140.719017));
				course_list.add(new LatLng(41.764175,140.719130));
				course_list.add(new LatLng(41.763523,140.719492));
				course_list.add(new LatLng(41.761912,140.720619));
				course_list.add(new LatLng(41.761531,140.719708));
				course_list.add(new LatLng(41.761372,140.719239));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//10. ペリーが見たHAKODADI
			case 10:
				course_list.add(new LatLng(41.770052,140.709239));
				course_list.add(new LatLng(41.770085,140.709290));
				course_list.add(new LatLng(41.770014,140.709378));
				course_list.add(new LatLng(41.770481,140.709898));
				course_list.add(new LatLng(41.771279,140.709051));
				course_list.add(new LatLng(41.771816,140.708138));
				course_list.add(new LatLng(41.77116,140.70744));
				course_list.add(new LatLng(41.769967,140.709361));
				course_list.add(new LatLng(41.767287,140.711292));
				course_list.add(new LatLng(41.764878,140.71524));
				course_list.add(new LatLng(41.762901,140.712944));
				course_list.add(new LatLng(41.763742,140.711646));
				course_list.add(new LatLng(41.764742,140.709779));
				course_list.add(new LatLng(41.765286,140.710312));
				course_list.add(new LatLng(41.765466,140.710359));
				course_list.add(new LatLng(41.765622,140.710280));
				course_list.add(new LatLng(41.765848,140.709911));
				course_list.add(new LatLng(41.765896,140.709987));
				course_list.add(new LatLng(41.766553,140.710684));
				course_list.add(new LatLng(41.768919,140.708299));
				course_list.add(new LatLng(41.770488,140.70523));
				course_list.add(new LatLng(41.770512,140.705209));
				course_list.add(new LatLng(41.768791,140.703492));
				course_list.add(new LatLng(41.770292,140.700838));
				course_list.add(new LatLng(41.769408,140.699823));
				course_list.add(new LatLng(41.768944,140.699216));
				course_list.add(new LatLng(41.768518,140.698540));
				course_list.add(new LatLng(41.768366,140.697696));
				course_list.add(new LatLng(41.768026,140.697026));
				course_list.add(new LatLng(41.767918,140.696869));
				course_list.add(new LatLng(41.767776,140.696534));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//11. きらめきのライトアップ 古き佳き函館編
			case 11:
				course_list.add(new LatLng(41.766670,140.712251));
				course_list.add(new LatLng(41.766733,140.712337));
				course_list.add(new LatLng(41.767226,140.711517));
				course_list.add(new LatLng(41.767776,140.712109));
				course_list.add(new LatLng(41.767889,140.712140));
				course_list.add(new LatLng(41.767853,140.712286));
				course_list.add(new LatLng(41.767180,140.713403));
				course_list.add(new LatLng(41.766520,140.712659));
				course_list.add(new LatLng(41.765760,140.713858));
				course_list.add(new LatLng(41.766422,140.714564));
				course_list.add(new LatLng(41.766036,140.714807));
				course_list.add(new LatLng(41.766991,140.716796));
				course_list.add(new LatLng(41.765769,140.717918));
				course_list.add(new LatLng(41.765622,140.718040));
				course_list.add(new LatLng(41.766366,140.719156));
				course_list.add(new LatLng(41.766339,140.719200));
				course_list.add(new LatLng(41.765591,140.718080));
				course_list.add(new LatLng(41.765529,140.717980));
				course_list.add(new LatLng(41.765304,140.717515));
				course_list.add(new LatLng(41.765130,140.717120));
				course_list.add(new LatLng(41.765043,140.716957));
				course_list.add(new LatLng(41.764270,140.716066));
				course_list.add(new LatLng(41.764107,140.716440));
				course_list.add(new LatLng(41.764010,140.716740));
				course_list.add(new LatLng(41.763906,140.717200));
				course_list.add(new LatLng(41.763832,140.717160));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//12. 函館まちなか美術館 五稜郭編
			case 12:
				course_list.add(new LatLng(41.789226,140.752231));
				course_list.add(new LatLng(41.789292,140.752242));
				course_list.add(new LatLng(41.789324,140.751796));
				course_list.add(new LatLng(41.789310,140.751622));
				course_list.add(new LatLng(41.789785,140.751686));
				course_list.add(new LatLng(41.790077,140.751718));
				course_list.add(new LatLng(41.791095,140.751813));
				course_list.add(new LatLng(41.791540,140.751836));
				course_list.add(new LatLng(41.792521,140.751829));
				course_list.add(new LatLng(41.792635,140.752055));
				course_list.add(new LatLng(41.793161,140.752438));
				course_list.add(new LatLng(41.795640,140.754204));
				course_list.add(new LatLng(41.795811,140.753636));
				course_list.add(new LatLng(41.795916,140.753533));
				course_list.add(new LatLng(41.796144,140.753527));
				course_list.add(new LatLng(41.796976,140.754151));
				course_list.add(new LatLng(41.797066,140.753945));
				course_list.add(new LatLng(41.797192,140.754046));
				course_list.add(new LatLng(41.797520,140.753381));
				course_list.add(new LatLng(41.797861,140.753630));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//13. 絵になる函館ロケ地巡り
			case 13:
				course_list.add(new LatLng(41.764158,140.718706));
				course_list.add(new LatLng(41.764206,140.718663));
				course_list.add(new LatLng(41.764294,140.718963));
				course_list.add(new LatLng(41.765266,140.718225));
				course_list.add(new LatLng(41.765740,140.717925));
				course_list.add(new LatLng(41.766979,140.716784));
				course_list.add(new LatLng(41.766039,140.714826));
				course_list.add(new LatLng(41.766404,140.714575));
				course_list.add(new LatLng(41.766519,140.714438));
				course_list.add(new LatLng(41.767173,140.713418));
				course_list.add(new LatLng(41.767855,140.712297));
				course_list.add(new LatLng(41.767926,140.712106));
				course_list.add(new LatLng(41.768019,140.711877));
				course_list.add(new LatLng(41.768110,140.711741));
				course_list.add(new LatLng(41.768753,140.711303));
				course_list.add(new LatLng(41.769308,140.710925));
				course_list.add(new LatLng(41.769662,140.711821));
				course_list.add(new LatLng(41.769696,140.711791));
				course_list.add(new LatLng(41.769319,140.710852));
				course_list.add(new LatLng(41.768753,140.711248));
				course_list.add(new LatLng(41.768112,140.711691));
				course_list.add(new LatLng(41.768008,140.711843));
				course_list.add(new LatLng(41.767914,140.712070));
				course_list.add(new LatLng(41.767329,140.711413));
				course_list.add(new LatLng(41.767225,140.711280));
				course_list.add(new LatLng(41.767174,140.711344));
				course_list.add(new LatLng(41.766593,140.710686));
				course_list.add(new LatLng(41.765854,140.709876));
				course_list.add(new LatLng(41.765651,140.710278));
				course_list.add(new LatLng(41.765565,140.710335));
				course_list.add(new LatLng(41.765458,140.710364));
				course_list.add(new LatLng(41.765362,140.710335));
				course_list.add(new LatLng(41.765309,140.710306));
				course_list.add(new LatLng(41.764404,140.712296));
				course_list.add(new LatLng(41.763757,140.711601));
				course_list.add(new LatLng(41.763552,140.712013));
				course_list.add(new LatLng(41.762894,140.712922));
				course_list.add(new LatLng(41.763066,140.713154));
				course_list.add(new LatLng(41.762486,140.713985));
				course_list.add(new LatLng(41.762329,140.714142));
				course_list.add(new LatLng(41.761716,140.713338));
				course_list.add(new LatLng(41.760914,140.713930));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//14. てくてく坂道 姿見坂・弥生坂編
			case 14:
				course_list.add(new LatLng(41.770260,140.708919));
				course_list.add(new LatLng(41.770296,140.708967));
				course_list.add(new LatLng(41.770011,140.709385));
				course_list.add(new LatLng(41.770491,140.709907));
				course_list.add(new LatLng(41.770563,140.710053));
				course_list.add(new LatLng(41.771128,140.710907));
				course_list.add(new LatLng(41.771627,140.710330));
				course_list.add(new LatLng(41.771987,140.709943));
				course_list.add(new LatLng(41.772108,140.709729));
				course_list.add(new LatLng(41.771281,140.709041));
				course_list.add(new LatLng(41.771795,140.708115));
				course_list.add(new LatLng(41.770587,140.706811));
				course_list.add(new LatLng(41.771907,140.704321));
				course_list.add(new LatLng(41.770854,140.703080));
				course_list.add(new LatLng(41.770387,140.703893));
				course_list.add(new LatLng(41.769230,140.702700));
				course_list.add(new LatLng(41.768819,140.703463));
				course_list.add(new LatLng(41.768518,140.703159));
				course_list.add(new LatLng(41.768173,140.702802));
				course_list.add(new LatLng(41.767876,140.702508));
				course_list.add(new LatLng(41.767774,140.702434));
				course_list.add(new LatLng(41.767460,140.702137));
				course_list.add(new LatLng(41.767450,140.702169));
				course_list.add(new LatLng(41.767771,140.702476));
				course_list.add(new LatLng(41.767862,140.702555));
				course_list.add(new LatLng(41.768171,140.702863));
				course_list.add(new LatLng(41.768515,140.703206));
				course_list.add(new LatLng(41.770493,140.705212));
				course_list.add(new LatLng(41.768923,140.708270));
				course_list.add(new LatLng(41.768691,140.708049));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//16. 幕末の志士達が駆け抜けた箱館②
			case 15:
				course_list.add(new LatLng(41.789249,140.752223));
				course_list.add(new LatLng(41.789297,140.752223));
				course_list.add(new LatLng(41.789321,140.751793));
				course_list.add(new LatLng(41.789297,140.751579));
				course_list.add(new LatLng(41.790729,140.751739));
				course_list.add(new LatLng(41.791521,140.751793));
				course_list.add(new LatLng(41.792489,140.751793));
				course_list.add(new LatLng(41.792628,140.752065));
				course_list.add(new LatLng(41.793600,140.752753));
				course_list.add(new LatLng(41.795492,140.754100));
				course_list.add(new LatLng(41.795649,140.754780));
				course_list.add(new LatLng(41.795607,140.754786));
				course_list.add(new LatLng(41.795476,140.754178));
				course_list.add(new LatLng(41.793579,140.752816));
				course_list.add(new LatLng(41.792612,140.752129));
				course_list.add(new LatLng(41.792554,140.752062));
				course_list.add(new LatLng(41.792377,140.751708));
				course_list.add(new LatLng(41.790919,140.749495));
				course_list.add(new LatLng(41.790070,140.748201));
				course_list.add(new LatLng(41.788609,140.746043));
				course_list.add(new LatLng(41.787308,140.744081));
				course_list.add(new LatLng(41.786355,140.742629));
				course_list.add(new LatLng(41.785531,140.741370));
				course_list.add(new LatLng(41.784523,140.739866));
				course_list.add(new LatLng(41.784046,140.739497));
				course_list.add(new LatLng(41.784102,140.738992));
				course_list.add(new LatLng(41.783987,140.738955));
				course_list.add(new LatLng(41.782730,140.737069));
				course_list.add(new LatLng(41.781814,140.735662));
				course_list.add(new LatLng(41.781480,140.735146));
				course_list.add(new LatLng(41.781454,140.735052));
				course_list.add(new LatLng(41.781430,140.734765));
				course_list.add(new LatLng(41.780975,140.734073));
				course_list.add(new LatLng(41.780716,140.733827));
				course_list.add(new LatLng(41.780441,140.733646));
				course_list.add(new LatLng(41.777162,140.731696));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//17. 歌人石川啄木が魅せられた函館
			case 16:
				course_list.add(new LatLng(41.760930,140.719977));
				course_list.add(new LatLng(41.760940,140.720003));
				course_list.add(new LatLng(41.761333,140.719749));
				course_list.add(new LatLng(41.761006,140.718724));
				course_list.add(new LatLng(41.761026,140.718438));
				course_list.add(new LatLng(41.760649,140.717216));
				course_list.add(new LatLng(41.759617,140.717172));
				course_list.add(new LatLng(41.756967,140.716841));
				course_list.add(new LatLng(41.756455,140.718164));
				course_list.add(new LatLng(41.756200,140.718432));
				course_list.add(new LatLng(41.755840,140.718888));
				course_list.add(new LatLng(41.756329,140.720051));
				course_list.add(new LatLng(41.756071,140.720233));
				course_list.add(new LatLng(41.755813,140.720269));
				course_list.add(new LatLng(41.755603,140.720160));
				course_list.add(new LatLng(41.755155,140.719796));
				course_list.add(new LatLng(41.754052,140.718748));
				course_list.add(new LatLng(41.753492,140.718464));
				course_list.add(new LatLng(41.753872,140.716587));
				course_list.add(new LatLng(41.750826,140.715444));
				course_list.add(new LatLng(41.750312,140.716420));
				course_list.add(new LatLng(41.749899,140.717759));
				course_list.add(new LatLng(41.748761,140.718415));
				course_list.add(new LatLng(41.747551,140.719332));
				course_list.add(new LatLng(41.746741,140.720148));
				course_list.add(new LatLng(41.746454,140.720327));
				course_list.add(new LatLng(41.746024,140.720558));
				course_list.add(new LatLng(41.745584,140.720743));
				course_list.add(new LatLng(41.745530,140.720829));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//18. 新島襄ヒストリート
			case 17:
				course_list.add(new LatLng(41.766717,140.712248));
				course_list.add(new LatLng(41.766770,140.712312));
				course_list.add(new LatLng(41.765812,140.713842));
				course_list.add(new LatLng(41.766448,140.714536));
				course_list.add(new LatLng(41.766533,140.714440));
				course_list.add(new LatLng(41.767797,140.712409));
				course_list.add(new LatLng(41.767921,140.712121));
				course_list.add(new LatLng(41.767971,140.711931));
				course_list.add(new LatLng(41.768052,140.711771));
				course_list.add(new LatLng(41.768750,140.711276));
				course_list.add(new LatLng(41.768961,140.711864));
				course_list.add(new LatLng(41.768996,140.711830));
				course_list.add(new LatLng(41.768780,140.711215));
				course_list.add(new LatLng(41.768513,140.710279));
				course_list.add(new LatLng(41.769917,140.709290));
				course_list.add(new LatLng(41.770469,140.709901));
				course_list.add(new LatLng(41.771011,140.709357));
				course_list.add(new LatLng(41.771770,140.710595));
				course_list.add(new LatLng(41.772381,140.709925));
				course_list.add(new LatLng(41.771300,140.709033));
				course_list.add(new LatLng(41.771222,140.708983));
				course_list.add(new LatLng(41.770543,140.708319));
				course_list.add(new LatLng(41.772966,140.704447));
				course_list.add(new LatLng(41.772110,140.703253));
				course_list.add(new LatLng(41.770932,140.701697));
				course_list.add(new LatLng(41.770271,140.700854));
				course_list.add(new LatLng(41.769236,140.702704));
				course_list.add(new LatLng(41.769984,140.703465));
				course_list.add(new LatLng(41.769572,140.704219));
				course_list.add(new LatLng(41.768732,140.703365));
				course_list.add(new LatLng(41.767797,140.702427));
				course_list.add(new LatLng(41.767727,140.702398));
				course_list.add(new LatLng(41.767332,140.702017));
				course_list.add(new LatLng(41.767108,140.701792));
				course_list.add(new LatLng(41.767102,140.701817));
				course_list.add(new LatLng(41.767332,140.702042));
				course_list.add(new LatLng(41.767727,140.702419));
				course_list.add(new LatLng(41.767783,140.702484));
				course_list.add(new LatLng(41.768387,140.703076));
				course_list.add(new LatLng(41.768781,140.703490));
				course_list.add(new LatLng(41.767684,140.705457));
				course_list.add(new LatLng(41.766529,140.707536));
				course_list.add(new LatLng(41.765869,140.708329));
				course_list.add(new LatLng(41.765634,140.708555));
				course_list.add(new LatLng(41.765577,140.708618));
				course_list.add(new LatLng(41.764780,140.709789));
				course_list.add(new LatLng(41.764709,140.709788));
				course_list.add(new LatLng(41.764277,140.710590));
				course_list.add(new LatLng(41.763744,140.711628));
				course_list.add(new LatLng(41.763527,140.712051));
				course_list.add(new LatLng(41.763412,140.712196));
				course_list.add(new LatLng(41.763100,140.711824));
				course_list.add(new LatLng(41.763023,140.711935));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//21. しあわせの隠れ場所を探しに
			case 18:
				course_list.add(new LatLng(41.764229,140.718837));
				course_list.add(new LatLng(41.764051,140.718191));
				course_list.add(new LatLng(41.763997,140.717295));
				course_list.add(new LatLng(41.764143,140.716691));
				course_list.add(new LatLng(41.764374,140.716152));
				course_list.add(new LatLng(41.765051,140.716934));
				course_list.add(new LatLng(41.765313,140.717524));
				course_list.add(new LatLng(41.765541,140.717988));
				course_list.add(new LatLng(41.765610,140.718002));
				course_list.add(new LatLng(41.766093,140.717607));
				course_list.add(new LatLng(41.766988,140.716800));
				course_list.add(new LatLng(41.766041,140.714793));
				course_list.add(new LatLng(41.766407,140.714555));
				course_list.add(new LatLng(41.765743,140.713793));
				course_list.add(new LatLng(41.767217,140.711421));
				course_list.add(new LatLng(41.765191,140.709175));
				course_list.add(new LatLng(41.764775,140.709817));
				course_list.add(new LatLng(41.764707,140.709763));
				course_list.add(new LatLng(41.763736,140.711626));
				course_list.add(new LatLng(41.763541,140.712011));
				course_list.add(new LatLng(41.762885,140.712933));
				course_list.add(new LatLng(41.763064,140.713165));
				course_list.add(new LatLng(41.762474,140.714009));
				course_list.add(new LatLng(41.762615,140.714189));
				course_list.add(new LatLng(41.760767,140.716205));
				course_list.add(new LatLng(41.760420,140.716478));
				course_list.add(new LatLng(41.759862,140.714695));
				course_list.add(new LatLng(41.760868,140.713963));
				course_list.add(new LatLng(41.759244,140.704862));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//22. 再生した「蔵」めぐり〜現在に息づく古き佳きもの〜
			case 19:
				course_list.add(new LatLng(41.772582,140.705100));
				course_list.add(new LatLng(41.770861,140.703086));
				course_list.add(new LatLng(41.770393,140.703900));
				course_list.add(new LatLng(41.770881,140.704427));
				course_list.add(new LatLng(41.768904,140.708292));
				course_list.add(new LatLng(41.766565,140.710666));
				course_list.add(new LatLng(41.765854,140.709891));
				course_list.add(new LatLng(41.765617,140.710297));
				course_list.add(new LatLng(41.765465,140.710377));
				course_list.add(new LatLng(41.765305,140.710315));
				course_list.add(new LatLng(41.765005,140.710973));
				course_list.add(new LatLng(41.766475,140.712574));
				course_list.add(new LatLng(41.765746,140.713800));
				course_list.add(new LatLng(41.766408,140.714574));
				course_list.add(new LatLng(41.766029,140.714833));
				course_list.add(new LatLng(41.766986,140.716790));
				course_list.add(new LatLng(41.765583,140.718035));
				course_list.add(new LatLng(41.766611,140.719597));
				course_list.add(new LatLng(41.766285,140.719925));
				course_list.add(new LatLng(41.765231,140.721059));
				course_list.add(new LatLng(41.764761,140.720266));
				course_list.add(new LatLng(41.763623,140.721240));
				course_list.add(new LatLng(41.763188,140.720669));
				course_list.add(new LatLng(41.762202,140.721297));
				course_list.add(new LatLng(41.761510,140.719617));
				course_list.add(new LatLng(41.761329,140.719718));
				course_list.add(new LatLng(41.760995,140.718752));
				course_list.add(new LatLng(41.760937,140.718780));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//23. はこだて小路めぐり　大門・朝市編～大門界隈今昔物語～
			case 20:
				course_list.add(new LatLng(41.773238,140.726385));
				course_list.add(new LatLng(41.772607,140.725926));
				course_list.add(new LatLng(41.772585,140.725964));
				course_list.add(new LatLng(41.773087,140.726346));
				course_list.add(new LatLng(41.772799,140.727106));
				course_list.add(new LatLng(41.772643,140.727635));
				course_list.add(new LatLng(41.772612,140.727885));
				course_list.add(new LatLng(41.772557,140.728031));
				course_list.add(new LatLng(41.772622,140.728088));
				course_list.add(new LatLng(41.771073,140.732648));
				course_list.add(new LatLng(41.771169,140.732729));
				course_list.add(new LatLng(41.771390,140.732123));
				course_list.add(new LatLng(41.771590,140.732307));
				course_list.add(new LatLng(41.771614,140.732267));
				course_list.add(new LatLng(41.771408,140.732077));
				course_list.add(new LatLng(41.771522,140.731738));
				course_list.add(new LatLng(41.771927,140.732090));
				course_list.add(new LatLng(41.772082,140.731828));
				course_list.add(new LatLng(41.772459,140.732177));
				course_list.add(new LatLng(41.773076,140.731021));
				course_list.add(new LatLng(41.772412,140.730475));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//24. 近代の函館を想う
			case 21:
				course_list.add(new LatLng(41.753247,140.716316));
				course_list.add(new LatLng(41.751960,140.715856));
				course_list.add(new LatLng(41.751379,140.715636));
				course_list.add(new LatLng(41.750906,140.715437));
				course_list.add(new LatLng(41.750825,140.715384));
				course_list.add(new LatLng(41.751346,140.714386));
				course_list.add(new LatLng(41.751610,140.713651));
				course_list.add(new LatLng(41.751393,140.713526));
				course_list.add(new LatLng(41.751355,140.712862));
				course_list.add(new LatLng(41.751394,140.712444));
				course_list.add(new LatLng(41.751327,140.712383));
				course_list.add(new LatLng(41.751305,140.712281));
				course_list.add(new LatLng(41.751316,140.712171));
				course_list.add(new LatLng(41.751650,140.711162));
				course_list.add(new LatLng(41.751264,140.710964));
				course_list.add(new LatLng(41.751248,140.710718));
				course_list.add(new LatLng(41.751318,140.710516));
				course_list.add(new LatLng(41.751456,140.709734));
				course_list.add(new LatLng(41.751652,140.709732));
				course_list.add(new LatLng(41.751809,140.709745));
				course_list.add(new LatLng(41.752186,140.709840));
				course_list.add(new LatLng(41.752603,140.709944));
				course_list.add(new LatLng(41.753082,140.710019));
				course_list.add(new LatLng(41.753115,140.710122));
				course_list.add(new LatLng(41.752557,140.710125));
				course_list.add(new LatLng(41.752408,140.710151));
				course_list.add(new LatLng(41.752281,140.710191));
				course_list.add(new LatLng(41.752122,140.710278));
				course_list.add(new LatLng(41.751920,140.711188));
				course_list.add(new LatLng(41.752876,140.711541));
				course_list.add(new LatLng(41.752587,140.712783));
				course_list.add(new LatLng(41.752506,140.713244));
				course_list.add(new LatLng(41.752346,140.713973));
				course_list.add(new LatLng(41.755027,140.714949));
				course_list.add(new LatLng(41.756008,140.716637));
				course_list.add(new LatLng(41.756054,140.717001));
				course_list.add(new LatLng(41.756099,140.717169));
				course_list.add(new LatLng(41.756274,140.717474));
				course_list.add(new LatLng(41.756620,140.717829));
				course_list.add(new LatLng(41.756472,140.718185));
				course_list.add(new LatLng(41.756400,140.718287));
				course_list.add(new LatLng(41.756518,140.718427));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			//25. 防火・防災のまち十字街
			case 22:
				course_list.add(new LatLng(41.760865,140.720063));
				course_list.add(new LatLng(41.761553,140.719650));
				course_list.add(new LatLng(41.761960,140.720601));
				course_list.add(new LatLng(41.763548,140.719490));
				course_list.add(new LatLng(41.764182,140.719133));
				course_list.add(new LatLng(41.764079,140.718851));
				course_list.add(new LatLng(41.763939,140.718353));
				course_list.add(new LatLng(41.763919,140.718216));
				course_list.add(new LatLng(41.764673,140.717813));
				course_list.add(new LatLng(41.764635,140.717632));
				course_list.add(new LatLng(41.764626,140.717410));
				course_list.add(new LatLng(41.764641,140.717238));
				course_list.add(new LatLng(41.764810,140.716715));
				course_list.add(new LatLng(41.764848,140.716649));
				course_list.add(new LatLng(41.765068,140.716905));
				course_list.add(new LatLng(41.765338,140.717477));
				course_list.add(new LatLng(41.765613,140.718013));
				course_list.add(new LatLng(41.766977,140.716789));
				course_list.add(new LatLng(41.766046,140.714816));
				course_list.add(new LatLng(41.766434,140.714570));
				course_list.add(new LatLng(41.766861,140.713930));
				createMatiarukiMapWithStart(gm, course_list);
				break;

			default:
                // 五稜郭公園の位置
                LatLng location = new LatLng(41.797595, 140.755849);

                // Option of Marker
                MarkerOptions options = new MarkerOptions()
                        .position(location)
                        .title("五稜郭公園")
                        .snippet(location.toString())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.miru));

                // add Marker
                gm.addMarker(options);

                // written in a single line
                gm.addMarker(new MarkerOptions().position(new LatLng(41.84183489999999, 140.7669978)).title("公立はこだて未来大学"));
        }


	}
}
