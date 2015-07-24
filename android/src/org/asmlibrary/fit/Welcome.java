package org.asmlibrary.fit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;

public class Welcome extends Activity {
	
//	private int					takePhoto = 1;
    Context mContext = null;    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_welcome);
        mContext = this;
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Log.i("entry","1");

        Button button0 = (Button)findViewById(R.id.buttonview0);
        button0.setTextColor(Color.BLUE);
        button0.setTextSize(30);
        
        Button button1 = (Button)findViewById(R.id.buttonview1);
        button1.setTextColor(Color.BLUE);
        button1.setTextSize(30);
        
        Button button2 = (Button)findViewById(R.id.buttonview2);
        button2.setTextColor(Color.BLUE);
        button2.setTextSize(30);

        button0.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
            	Log.i("startclick","1");
                Toast.makeText(Welcome.this, "Please place your face in the middle and do not make any emotion on your face", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
//                intent.putExtra("takePhoto", takePhoto);
                intent.setClass(Welcome.this, TakePhoto.class);
                startActivity(intent);
            }
        });
        
        button1.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
            	Log.i("select","1");
//            	takePhoto = 0;
                Intent intent = new Intent();
//                intent.putExtra("takePhoto", takePhoto);
                intent.setClass(Welcome.this, ChoosePic.class);
                startActivity(intent);
            }
        });
        
        button2.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View view) {
                
            	 Dialog alertDialog = new AlertDialog.Builder(view.getContext()). 
                         setTitle("User Instructions"). 
                         setMessage("Welcome to Face Classification. When you use this app to classify your looks please follow "). 
                         setIcon(R.drawable.ic_launcher). 
                         setPositiveButton("O", new DialogInterface.OnClickListener() { 
                              
                             @Override 
                             public void onClick(DialogInterface dialog, int which) { 

                             } 
                         }). 
  
                         create(); 
                 alertDialog.show(); 
             } 
        });
        super.onCreate(savedInstanceState);
    }
    
}
