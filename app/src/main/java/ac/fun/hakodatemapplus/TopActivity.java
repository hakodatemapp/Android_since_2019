package ac.fun.hakodatemapplus;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.view.View.OnClickListener;
 
public class TopActivity extends Activity implements OnClickListener {
	
    private Button button_main;
    private Button button_course;
    private Button button_info;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
 
        button_main=(Button)findViewById(R.id.button_main);
        button_course=(Button)findViewById(R.id.button_course);
        button_info=(Button)findViewById(R.id.button_info);
        
        button_main.setOnClickListener(this);
        button_course.setOnClickListener(this);
        button_info.setOnClickListener(this);
    }
 
    //ボタンクリック時の関数
    public void onClick(View v) {
 
        if(v==button_main){
            Intent intent = new Intent(this, MainActivity.class);
            startActivityForResult(intent, 0);
        }
        if(v==button_course){
            Intent intent = new Intent(this, CourseActivity.class);
            startActivityForResult(intent, 0);
        }
        if(v==button_info){
            Intent intent = new Intent(this, InfoActivity.class);
            startActivityForResult(intent, 0);
        }
 
    }
 
}
