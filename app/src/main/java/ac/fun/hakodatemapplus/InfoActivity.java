package ac.fun.hakodatemapplus;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

public class InfoActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		//アクションバーの編集
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.mapplus_icon);
		actionBar.setTitle("Information");

        setContentView(R.layout.activity_info);

        TextView textview = (TextView) findViewById(R.id.textview);
        
        //LinkMovementMethodインスタンスを取得
        MovementMethod movementmethod = LinkMovementMethod.getInstance();
        
        //LinkMovementMethodを登録
        textview.setMovementMethod(movementmethod);
        
        textview.setText(Html.fromHtml(
        		
        		"はこだてMap+<br>"
        		+ "Copyright(c) Future University Hakodate. All Rights Reserved.<br>"
        		+ "<br>"
        		+ "はこだてMap+に掲載されている情報は、下記のWebサイトよりご提供いただいたものです。<br>"
        		+ "・函館市公式観光情報サイト「はこぶら」：<br>"
        		+ "<a href=\"http://www.hakobura.jp/\">http://www.hakobura.jp</a><br>"
        		+ "<br>"
        		+ "・函館まちあるきマップ：<br>"
        		+ "<a href=\"http://hakodate-machiaruki.com//\">http://hakodate-machiaruki.com/</a><br>"
        		+ "<br>"
        		+ "・はこだてフィルムコミッション：<br>"
        		+ "<a href=\"http://www.hakodate-fc.com/\">http://www.hakodate-fc.com/</a><br>"
        		+ "<br>"
        		+ "・函館近代化遺産ポータルサイト：<br>"
        		+ "<a href=\"http://hnct-pbl.jimdo.com/\">http://hnct-pbl.jimdo.com/</a><br>"
        		+ "<br>"
        		+ "はこだてMap+はLOD（リンクト・オープン・データ）を活用して作成されております。"
        		+ "コンテンツの拡充にご協力いただける方は、Facebookページよりご連絡ください。<br>"
        		+ "<br>"
        		+ "「公立はこだて未来大学 高度ICT演習 観光系プロジェクト Facebookページ」<br>"
        		+ "<a href=\"https://www.facebook.com/FUNTourismProject/\">https://www.facebook.com/FUNTourismProject</a><br>"
        		+ "<br>"
        		+ "<br>"
        		+ "ーーーーーーーーーーー<br>"
        		+ "ヘルプ<br>"
        		+ "<br>"
        		+ "■はこだてMap+とは<br>"
        		+ "はこだてMap+(プラス)は、北海道函館市のおすすめ観光コースを歩きながら、"
        		+ "観光スポットにまつわる+α情報を知ることができる地図アプリです。"
        		+ "函館観光をガイド付きで楽しみたいなら、はこだてMap+を持って街を歩いてみましょう。<br>"
        		+ "<br>"
        		+ "はこだてMap+はLOD(リンクト・オープン・データ)を活用しています。"
        		+ "観光コースや観光スポット情報を表示する際には、必ずインターネットに接続された状態でご利用ください。<br>"
        		+ "<br>"
        		+ "<br>"
        		+ "■アプリの使い方<br>"
        		+ "1. コースを選んでまちあるきをする<br>"
        		+ "<br>"
        		+ "トップ画面<br>"
        		+ "・「コース一覧を見る」を選択します。<br>"
        		+ "<br>"
        		+ "まちあるきコース一覧画面<br>"
        		+ "・まちあるきコースの一覧が表示されます。"
        		+ "一覧から好きなコースを選択すると、コースについての詳細情報を見ることができます。<br>"
        		+ "<br>"
        		+ "まちあるきコース詳細画面<br>"
        		+ "・歩くコースが決まったら、「このコースを選ぶ」を選択します。<br>"
        		+ "<br>"
        		+ "マップ画面<br>"
        		+ "・マップ上に、選んだコースと周辺の観光スポットが表示されます。"
        		+ "また、自分の現在地が表示されます。マップを見ながら、"
        		+ "コースに従ってまちあるきをすることができます。<br>"
        		+ "<br>"
        		+ "青い丸：自分の現在地　※ 端末の位置情報サービスがオンの場合に表示されます。<br>"
        		+ "赤いピン：まちあるきコース対象の観光スポット<br>"
        		+ "アイコンのピン：各カテゴリ（食べる・見る・遊ぶ・買う・温泉・観光イベント）の観光スポット<br>"
        		+ "<br>"
        		+ "各カテゴリに応じたアイコンはカテゴリ一覧画面に書いてあります。<br>"
        		+ "<br>"
        		+ "<br>"
        		+ "2. コースを選んでまちあるきをする<br>"
        		+ "トップ画面<br>"
        		+ "・「周辺の地図を見る」を選択します。<br>"
        		+ "<br>"
        		+ "マップ画面<br>"
        		+ "・マップ上に、周辺の観光スポットが表示されます。"
        		+ "また、自分の現在地が表示されます。マップを見ながら、自由にまちあるきをすることができます。<br>"
        		+ "<br>"
        		+ "<br>"
        		+ "3. 観光スポットの詳しい情報を見る<br>"
        		+ "<br>"
        		+ "<br>"
        		+ "マップ画面<br>"
        		+ "・マップ画面で詳しい情報を知りたい観光スポットを選択すると、観光スポットについての詳細情報を見ることができます。<br>"
        		+ "<br>"
        		+ "観光スポット詳細画面<br>"
        		+ "・観光スポットの住所や電話番号などの情報が表示されます。「もっと見る」を選択すると、函館市公式観光情報サイトはこぶらのWebページでさらに詳しい情報を見ることができます。<br>"
        		+ "・観光スポットで撮影された映画のタイトルが表示されます。映画のタイトルを選択すると、観光スポットの映画ロケ地としての情報が表示されます。<br>"
        		+ "・土木遺産の「概要」を選択すると、観光スポットの土木遺産としての情報が表示されます。<br>"
        		+ "<br>"
        		+ "映画ロケ地情報画面<br>"
        		+ "・観光スポットでの映画のシーンの説明や、映画の監督、主演の情報が表示されます。<br>"
        		+ "・「もっと見る」を選択すると、はこだてフィルムコミッションのWebページでさらに詳しい情報を見ることができます。<br>"
        		+ "<br>"
        		+ "土木遺産情報画面<br>"
        		+ "・観光スポットの歴史的な説明や竣工年、関連人物の情報が表示されます。<br>"
        		+ "・「もっと見る」を選択すると、函館近代化遺産ポータルのWebページでさらに詳しい情報を見ることができます。<br>"
        		+ "<br>"
        		+ "<br>"
        		+ "4. カテゴリで観光スポットを絞り込む<br>"
        		+ "<br>"
        		+ "マップ画面<br>"
        		+ "・「カテゴリ」を選択すると、カテゴリの一覧が表示されます。<br>"
        		+ "<br>"
        		+ "カテゴリ一覧画面<br>"
        		+ "・マップに表示させたい観光スポットのカテゴリにチェックを入れ、「戻る」を選択してマップ画面に戻ります。<br>"
        		+ "<br>"
        		+ "マップ画面<br>"
        		+ "・選択したカテゴリの観光スポットのみがマップに表示されます。<br>"
        		+ "・飲食店を探したい時など、目的に応じてカテゴリを設定することで観光スポットが調べやすくなります。<br>"
        		+ "<br>"
        		+ "※ まちあるきのコースが表示されている場合、コース上の観光スポットはカテゴリに関わらず全て表示されます。<br>"

        		));
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

}
