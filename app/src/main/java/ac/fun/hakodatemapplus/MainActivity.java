package ac.fun.hakodatemapplus;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.*;


import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.*;

public class MainActivity extends FragmentActivity {

	MapFragment mf;
	private String title;

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
		}
		else {
			title = "周辺の地図";
		}
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

		CameraPosition Hakodate = new CameraPosition
				.Builder()
		.target(new LatLng(41.773746,140.726399))
		.zoom(13)
		.build();

		gm.moveCamera(CameraUpdateFactory.newCameraPosition(Hakodate));

		// latitude and longitude
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
