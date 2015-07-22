package ac.fun.hakodatemapplus;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
 


import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CourseActivity extends Activity implements OnItemClickListener{
	 
    private List<ListItem> dataList = new ArrayList<ListItem>();
  
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //アクションバーの編集
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setLogo(R.drawable.mapplus_icon);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle("コース選択");

        setContentView(R.layout.activity_course);


        //基礎のレイアウトを作成
        LinearLayout oLayout = new LinearLayout(getApplicationContext());
        //今回の主役リストビューの生成
        ListView oListView   = new ListView(getApplicationContext());
        //リストビューに入れるアイテムのAdapterを生成
        ListAdapter     oAdp = new ListAdapter(getApplicationContext());
 
        //リストビューのレイアウトを指定
        oListView.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        //リストビューにアイテムadapterを設定
        oListView.setAdapter(oAdp);
        //リストビューをクリックした時の処理を設定
        oListView.setOnItemClickListener(this);
 
        
	    //配列にタイトルと画像を格納
	    dataList.add(new ListItem(String.valueOf("これぞ王道！函館の魅力凝縮コース"), R.drawable.matiaruki01));
	    dataList.add(new ListItem(String.valueOf("てくてく坂道　大三坂・八幡坂編"), R.drawable.matiaruki02));
	    dataList.add(new ListItem(String.valueOf("きらめきのライトアップ　教会編"), R.drawable.matiaruki03));
	    dataList.add(new ListItem(String.valueOf("きらめきのライトアップ　海辺編"), R.drawable.matiaruki04));
	    dataList.add(new ListItem(String.valueOf("湯の川あったか散策道"), R.drawable.matiaruki05));
	    dataList.add(new ListItem(String.valueOf("幕末の志士達が駆け抜けた箱館①"), R.drawable.matiaruki06));
	    dataList.add(new ListItem(String.valueOf("箱館はじめて物語"), R.drawable.matiaruki07));
	    dataList.add(new ListItem(String.valueOf("函館寺社巡り"), R.drawable.matiaruki08));
	    dataList.add(new ListItem(String.valueOf("北の豪商 高田屋嘉兵衛物語"), R.drawable.matiaruki09));
	    dataList.add(new ListItem(String.valueOf("ぺリーが見たＨＡＫＯＤＡＤＩ"), R.drawable.matiaruki10));
	    dataList.add(new ListItem(String.valueOf("きらめきのライトアップ　古き佳き函館編"), R.drawable.matiaruki11));
	    dataList.add(new ListItem(String.valueOf("函館まちなか美術館　五稜郭編"), R.drawable.matiaruki12));
	    dataList.add(new ListItem(String.valueOf("絵になる函館ロケ地巡り"), R.drawable.matiaruki13));
	    dataList.add(new ListItem(String.valueOf("てくてく坂道　姿見坂・弥生坂編"), R.drawable.matiaruki14));
	    //dataList.add(new ListItem(String.valueOf("番号:" + 1), R.drawable.matiaruki15));
	    dataList.add(new ListItem(String.valueOf("幕末の志士達が駆け抜けた箱館②"), R.drawable.matiaruki16));
	    dataList.add(new ListItem(String.valueOf("歌人石川啄木が魅せられた函館"), R.drawable.matiaruki17));
	    dataList.add(new ListItem(String.valueOf("新島襄ヒストリート"), R.drawable.matiaruki18));
	    //dataList.add(new ListItem(String.valueOf("番号:" + 1), R.drawable.matiaruki19));
	    //dataList.add(new ListItem(String.valueOf("番号:" + 1), R.drawable.matiaruki20));
	    dataList.add(new ListItem(String.valueOf("しあわせの隠れ場所を探しに"), R.drawable.matiaruki21));
	    dataList.add(new ListItem(String.valueOf("再生した「蔵」めぐり"), R.drawable.matiaruki22));
	    dataList.add(new ListItem(String.valueOf("はこだて小路めぐり"), R.drawable.matiaruki23));
	    
        //Adapteの中身を更新
        oAdp.notifyDataSetChanged();
 
        //基礎のレイアウトにリストビューを追加
        oLayout.addView(oListView);
        //基礎のレイアウトを画面に設定
        setContentView(oLayout);



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

    public class ListAdapter extends BaseAdapter{
 
        private Context mContext;
   
        public ListAdapter(Context context){
            //生成時のコンストラクターでコンテクストを取得
            mContext = context;
        }
   
        @Override
        public int getCount() {
            //リストの行数
            return dataList.size();
        }
 
        @Override
        public Object getItem(int posion) {
            //配列の中身を返す
            return dataList.get(posion);
        }
 
        @Override
        public long getItemId(int posion) {
            //現在の配列の場所
            return posion;
        }
 
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            TextView title;
            ImageView imag;
            View v = view;
            if(v==null){
         
                //グリットビューの1マス内のレイアウトを作成
                LinearLayout oListLayout = new LinearLayout(mContext);
                //左から右にアイテムを追加
                oListLayout.setOrientation(LinearLayout.HORIZONTAL);
                //イメージビューを生成
                ImageView oImage = new ImageView(mContext);
                //テキストビューを生成
                TextView oText = new TextView(mContext);
                //判別用にタグをつける
                oImage.setTag("CellImage");
                oText.setTag("CellTitle");
                //グリットビュー用のレイアウトに追加
                oListLayout.addView(oImage);
                oListLayout.addView(oText);
                v = oListLayout;
            }
            //配列から、アイテムを取得
            ListItem oList = (ListItem)getItem(position);
            if( dataList != null){
            //タグからテキストビューを取得
            title = (TextView) v.findViewWithTag("CellTitle");
            //取得したテキストビューに文字列を設定
            title.setText(oList.Get_Text());
            title.setTextColor(Color.BLACK);
            title.setTextSize(14);
            //title.setTextAlignment(1);
            //タグからイメージビューを取得
            imag = (ImageView) v.findViewWithTag("CellImage");
            //イメージビューに画像を設定
            imag.setBackgroundResource( oList.Get_Res() );
        }
        return v;
    }
}
 
    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        //押された時のパラメーターを表示
    	startActivity(new Intent(this, DetailActivity.class));
    }

}




/*
public class CourseActivity extends Activity implements OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_course);

	    // set ListView
	    ListView listView = new ListView(this);
	    setContentView(listView);

        //add data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        adapter.add("これぞ王道！函館の魅力凝縮コース");
        adapter.add("てくてく坂道 大三坂・八幡坂編");
        adapter.add("きらめきのライトアップ 協会編");
        adapter.add("きらめきのライトアップ 海辺編");
        adapter.add("湯の川あったか散策道");
        adapter.add("幕末の志士達が駆け抜けた箱館①");
        adapter.add("箱館はじめて物語");
        adapter.add("函館寺社巡り");
        adapter.add("北の豪商高田屋嘉兵衛物語");
        adapter.add("ペリーが見たHAKODADI");
        adapter.add("きらめきのライトアップ古き佳き函館編");
        adapter.add("函館まちなか美術館五稜郭編");
        adapter.add("絵になる函館ロケ地巡り");
        adapter.add("てくてく坂道姿見坂・弥生坂編");
        adapter.add("幕末の志士達が駆け抜けた箱館②");
        adapter.add("歌人石川啄木が魅せられた函館");
        adapter.add("新島襄ヒストリート");
        adapter.add("しあわせの隠れ場所を探しに");
        adapter.add("再生した「蔵」めぐり～現在に息づく古き佳きもの～");
        adapter.add("はこだて小路めぐり大門・朝市編～大門界隈今昔物語～");

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((OnItemClickListener) this);
    }
    
    @Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		startActivity(new Intent(this, DetailActivity.class));	
    }
}
*/

