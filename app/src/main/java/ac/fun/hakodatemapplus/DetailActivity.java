package ac.fun.hakodatemapplus;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.view.View.OnClickListener;
 
public class DetailActivity extends Activity implements OnClickListener {
	
    private Button button_main;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
 
        button_main=(Button)findViewById(R.id.button_main);
        button_main.setOnClickListener(this);
    }
 
    public void onClick(View v) {
 
        if(v==button_main){
            Intent intent = new Intent(this, MainActivity.class);
            startActivityForResult(intent, 0);
        }
    }
}