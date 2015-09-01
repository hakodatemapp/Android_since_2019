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
                course_list.add(new LatLng(41.76653550555116,140.71242928504944) );
                course_list.add(new LatLng(41.7665084979006,140.7124038040638) );
                course_list.add(new LatLng(41.767150676734815,140.71136444807053) );
                course_list.add(new LatLng(41.76521386783585,140.70920661091805) );
                course_list.add(new LatLng(41.76482874973684,140.70976920425892) );
                course_list.add(new LatLng(41.76470596159717,140.70978127419949) );
                course_list.add(new LatLng(41.76373515177974,140.71160919964314) );
                course_list.add(new LatLng(41.76338103511448,140.7122502475977) );
                course_list.add(new LatLng(41.76287336445208,140.7129107415676) );
                course_list.add(new LatLng(41.7630614281412,140.71315482258797) );
                course_list.add(new LatLng(41.76246372381709,140.71400307118893) );
                course_list.add(new LatLng(41.762715310499836, 140.71430817246437));
                course_list.add(new LatLng(41.76285435798475, 140.71411237120628));
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
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.miru_1));

                // add Marker
                gm.addMarker(options);

                // written in a single line
                gm.addMarker(new MarkerOptions().position(new LatLng(41.84183489999999, 140.7669978)).title("公立はこだて未来大学"));
        }


	}
}
