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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mf = MapFragment.newInstance();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(android.R.id.content, mf);
		ft.commit();	
	}
	
	//add menu
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(Menu.NONE, 0, 0, "公立はこだて未来大学 FUN");
		return super.onCreateOptionsMenu(menu);
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
