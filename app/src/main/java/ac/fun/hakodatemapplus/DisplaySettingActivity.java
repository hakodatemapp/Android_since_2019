package ac.fun.hakodatemapplus;

//import android.support.v7.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class DisplaySettingActivity extends Activity {

    private boolean is_show_taberu = true;
    private boolean is_show_miru = true;
    private boolean is_show_asobu = true;
    private boolean is_show_kaimono = true;
    private boolean is_show_onsen = true;
    private boolean is_show_event = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_setting);

        // 地図画面の値を呼び出す
        Intent intent = getIntent();

        is_show_taberu = intent.getExtras().getBoolean("is_show_taberu");
        is_show_miru = intent.getExtras().getBoolean("is_show_miru");
        is_show_asobu = intent.getExtras().getBoolean("is_show_asobu");
        is_show_kaimono = intent.getExtras().getBoolean("is_show_kaimono");
        is_show_onsen = intent.getExtras().getBoolean("is_show_onsen");
        is_show_event = intent.getExtras().getBoolean("is_show_event");

        // 現在の状態をチェックボックスへ反映させる
        CheckBox taberu_checkbox = (CheckBox) findViewById(R.id.eat_checkBox);
        taberu_checkbox.setChecked(is_show_taberu);
        CheckBox miru_checkbox = (CheckBox) findViewById(R.id.look_checkBox);
        miru_checkbox.setChecked(is_show_miru);
        CheckBox asobu_checkbox = (CheckBox) findViewById(R.id.play_checkBox);
        asobu_checkbox.setChecked(is_show_asobu);
        CheckBox kaimono_checkbox = (CheckBox) findViewById(R.id.buy_checkBox);
        kaimono_checkbox.setChecked(is_show_kaimono);
        CheckBox onsen_checkbox = (CheckBox) findViewById(R.id.spa_checkBox);
        onsen_checkbox.setChecked(is_show_onsen);
        CheckBox event_checkbox = (CheckBox) findViewById(R.id.event_checkBox);
        event_checkbox.setChecked(is_show_event);

        //アクションバーの編集
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setLogo(R.drawable.mapplus_icon);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle("表示設定");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        // アクションバーの戻るを押したときの処理
        else if (id == android.R.id.home) {
            // intentの作成
            Intent intent = new Intent();

            // チェックボックスの状態をセットする
            CheckBox taberu_checkbox = (CheckBox) findViewById(R.id.eat_checkBox);
            is_show_taberu = taberu_checkbox.isChecked();
            CheckBox miru_checkbox = (CheckBox) findViewById(R.id.look_checkBox);
            is_show_miru = miru_checkbox.isChecked();
            CheckBox asobu_checkbox = (CheckBox) findViewById(R.id.play_checkBox);
            is_show_asobu = asobu_checkbox.isChecked();
            CheckBox kaimono_checkbox = (CheckBox) findViewById(R.id.buy_checkBox);
            is_show_kaimono = kaimono_checkbox.isChecked();
            CheckBox onsen_checkbox = (CheckBox) findViewById(R.id.spa_checkBox);
            is_show_onsen = onsen_checkbox.isChecked();
            CheckBox event_checkbox = (CheckBox) findViewById(R.id.event_checkBox);
            is_show_event = event_checkbox.isChecked();

            System.out.println(is_show_taberu);
            intent.putExtra("is_show_taberu", is_show_taberu);
            intent.putExtra("is_show_miru", is_show_miru);
            intent.putExtra("is_show_asobu", is_show_asobu);
            intent.putExtra("is_show_kaimono", is_show_kaimono);
            intent.putExtra("is_show_onsen", is_show_onsen);
            intent.putExtra("is_show_event", is_show_event);

            // 返却したい結果ステータスをセットする
            setResult(Activity.RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
