package ac.fun.hakodatemapplus;

//import android.support.v7.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

public class DisplaySettingActivity extends Activity {

    private boolean is_show_taberu = true;
    private boolean is_show_miru = true;
    private boolean is_show_asobu = true;
    private boolean is_show_kaimono = true;
    private boolean is_show_onsen = true;
    private boolean is_show_event = true;
    private boolean is_show_sweets = true;
    private boolean is_show_hinanjo = true;
    private boolean is_show_tsunamibuilding = true;
    private boolean is_show_altitude = true;

    private CheckBox taberu_checkbox;
    private CheckBox miru_checkbox;
    private CheckBox asobu_checkbox;
    private CheckBox kaimono_checkbox;
    private CheckBox onsen_checkbox;
    private CheckBox event_checkbox;
    private CheckBox sweets_checkbox;
    private CheckBox hinanjo_checkbox;
    private CheckBox tsunamibuilding_checkbox;
    private Switch altitude_switch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_setting);

        // 現在の状態をチェックボックスへ反映させる
        setCheckBoxFromIntent();

        // TabelRowをタップしたときもチェックボックスを操作できるようにする
        setTableRowListener();

        //アクションバーの編集
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setLogo(R.drawable.mapplus_icon);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle("表示設定");
    }

    // デバイスの物理キーが押されたとき
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 戻るボタン
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // ダイアログを表示する
            DialogFragment newFragment = new ConfirmDialogFragment();
            newFragment.show(getFragmentManager(), "confirm");
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    // 現在の状態をチェックボックスへ反映させる
    private void setCheckBoxFromIntent() {
        // 地図画面の値を呼び出す
        Intent intent = getIntent();

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

        // 現在の状態をチェックボックスへ反映させる
        taberu_checkbox = (CheckBox) findViewById(R.id.eat_checkBox);
        taberu_checkbox.setChecked(is_show_taberu);
        miru_checkbox = (CheckBox) findViewById(R.id.look_checkBox);
        miru_checkbox.setChecked(is_show_miru);
        asobu_checkbox = (CheckBox) findViewById(R.id.play_checkBox);
        asobu_checkbox.setChecked(is_show_asobu);
        kaimono_checkbox = (CheckBox) findViewById(R.id.buy_checkBox);
        kaimono_checkbox.setChecked(is_show_kaimono);
        onsen_checkbox = (CheckBox) findViewById(R.id.spa_checkBox);
        onsen_checkbox.setChecked(is_show_onsen);
        event_checkbox = (CheckBox) findViewById(R.id.event_checkBox);
        event_checkbox.setChecked(is_show_event);
        sweets_checkbox = (CheckBox) findViewById(R.id.sweets_checkBox);
        sweets_checkbox.setChecked(is_show_sweets);

        hinanjo_checkbox = (CheckBox) findViewById(R.id.hinanjo_checkbox);
        hinanjo_checkbox.setChecked(is_show_hinanjo);
        tsunamibuilding_checkbox = (CheckBox) findViewById(R.id.tsunamibuilding_checkbox);
        tsunamibuilding_checkbox.setChecked(is_show_tsunamibuilding);
        altitude_switch = (Switch) findViewById(R.id.altitude_switch);
        altitude_switch.setChecked(is_show_altitude);
    }

    private void setTableRowListener() {
        // TabelRowをタップしたときもチェックボックスを操作できるようにする
        TableRow taberu_row = (TableRow) findViewById(R.id.eat_row);
        taberu_row.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //押したときの動作
                    optionsTouchListener(R.id.eat_row, R.id.eat_checkBox);
                }
                return true;
            }
        });

        TableRow miru_row = (TableRow) findViewById(R.id.look_row);
        miru_row.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //押したときの動作
                    optionsTouchListener(R.id.look_row, R.id.look_checkBox);
                } 
                return true;
            }
        });

        TableRow asobu_row = (TableRow) findViewById(R.id.play_row);
        asobu_row.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //押したときの動作
                    optionsTouchListener(R.id.play_row, R.id.play_checkBox);
                }
                return true;
            }
        });

        TableRow kaimono_row = (TableRow) findViewById(R.id.buy_row);
        kaimono_row.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //押したときの動作
                    optionsTouchListener(R.id.buy_row, R.id.buy_checkBox);
                }
                return true;
            }
        });

        TableRow onsen_row = (TableRow) findViewById(R.id.spa_row);
        onsen_row.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //押したときの動作
                    optionsTouchListener(R.id.spa_row, R.id.spa_checkBox);
                }
                return true;
            }
        });

        TableRow event_row = (TableRow) findViewById(R.id.event_row);
        event_row.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //押したときの動作
                    optionsTouchListener(R.id.event_row, R.id.event_checkBox);
                }
                return true;
            }
        });

        TableRow sweets_row = (TableRow) findViewById(R.id.sweets_row);
        sweets_row.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //押したときの動作
                    optionsTouchListener(R.id.sweets_row, R.id.sweets_checkBox);
                }
                return true;
            }
        });

        TableRow hinanjo_row = (TableRow) findViewById(R.id.hinanjo_row);
        hinanjo_row.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //押したときの動作
                    optionsTouchListener(R.id.hinanjo_row, R.id.hinanjo_checkbox);
                }
                return true;
            }
        });

        TableRow tsunamibuilding_row = (TableRow) findViewById(R.id.tsunamibuilding_row);
        tsunamibuilding_row.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //押したときの動作
                    optionsTouchListener(R.id.tsunamibuilding_row, R.id.tsunamibuilding_checkbox);
                }
                return true;
            }
        });


    }

    // 表示設定のRowに指を付けたときの処理
    private void optionsTouchListener(int targetTableRowId, int targetCheckBoxId) {
        TableRow targetTableRow = (TableRow) findViewById(targetTableRowId);
        CheckBox targetCheckBox = (CheckBox) findViewById(targetCheckBoxId);
        targetCheckBox.setChecked(!targetCheckBox.isChecked());

        // さわったところが分かりやすいよう背景色を変える
        // targetTableRow.setBackgroundResource(android.R.drawable.list_selector_background);
    }

    // アクションバーのメニューを押したときの処理
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        // アクションバーの戻るを押したときの処理
        else if (id == android.R.id.home) {
            setAndFinishDisplaySettings(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 表示設定を保存して前の画面に戻る
    public void setAndFinishDisplaySettings(boolean set_settings) {
        // intentの作成
        Intent intent = new Intent();

        // 設定を反映させる場合
        if (set_settings) {
            // チェックボックスの状態をセットする
            is_show_taberu = taberu_checkbox.isChecked();
            is_show_miru = miru_checkbox.isChecked();
            is_show_asobu = asobu_checkbox.isChecked();
            is_show_kaimono = kaimono_checkbox.isChecked();
            is_show_onsen = onsen_checkbox.isChecked();
            is_show_event = event_checkbox.isChecked();
            is_show_sweets = sweets_checkbox.isChecked();
            is_show_hinanjo = hinanjo_checkbox.isChecked();
            is_show_tsunamibuilding = tsunamibuilding_checkbox.isChecked();
            is_show_altitude = altitude_switch.isChecked();


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

            // 返却したい結果ステータスをセットする
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            // 返却したい結果ステータスをセットする
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        }

    }

    // デバイスの戻るボタンを押したときのダイアログ
    public static class ConfirmDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("地図へ戻ろうとしています。\nここで設定した観光スポットの表示設定を反映させますか？").setTitle("表示設定")
                    .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DisplaySettingActivity calling_activity = (DisplaySettingActivity) getActivity();
                            calling_activity.setAndFinishDisplaySettings(true);
                        }
                    })
                    .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                    .setNeutralButton("いいえ", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DisplaySettingActivity calling_activity = (DisplaySettingActivity) getActivity();
                            calling_activity.setAndFinishDisplaySettings(false);
                        }
                    });
            return builder.create();
        }
    }

}
