package ac.fun.hakodatemapplus;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View.OnClickListener;
 
public class DetailActivity extends Activity implements OnClickListener {
	
    private Button button_main;
    private String title;
    private int image;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //CourseActivityの値を呼び出す
        Intent intent = getIntent();
        title = intent.getExtras().getString("title");
        image = intent.getExtras().getInt("image");
        //アクションバーの編集
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(title);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.mapplus_icon);

        setContentView(R.layout.activity_detail);
        button_main=(Button)findViewById(R.id.button_main);
        button_main.setOnClickListener(this);

        ImageView imageView = (ImageView)findViewById(R.id.image_view);
        imageView.setImageResource(image);
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

    public void onClick(View v) {
 
        if(v==button_main){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("title", title);//第一引数呼び出すときのkey、第二引数:コース名
            startActivityForResult(intent, 0);
        }
    }
}