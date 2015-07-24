package org.asmlibrary.fit;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class showResult extends Activity {
	
	private static final String    TAG                 = "ASMLibraryDemo";
	
	private double 							verBlueRatio;
	private double 							verGreenRatio;
	private double 							horGoldenRatio;
	private double 							horGoldenRatio2;
	private double 							horGolden2Ratio;
	private double 							horGolden2Ratio2;
	private double 							horGolden3Ratio;
	private double 							horGolden3Ratio2;
	private double 							horWhiteRatio;
	private double 							horBlueRatio;
	private double 							horGreenRatio;
	private double 							horGreenRatio2;
    private double 							eyeRatio;
    private double 							eyeRatio2;
    private double							eyeHeight;   
    private int								symGrade;
    private double							variance;
    private ImageView 						imgView;
    private MenuItem						analysis;
    private MenuItem						exit;
    private String							mPath;
    
    public void onCreate(Bundle savedInstanceState) {    
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showresult);

        imgView = (ImageView) findViewById(R.id.imageViewId);
        imgView.setImageBitmap(BitmapFactory.decodeFile("storage/emulated/0/image.jpg"));
        
        //receive data
        mPath = (String) getIntent().getSerializableExtra("path");
//        verBlueRatio = (Double) getIntent().getSerializableExtra("verBlueRatio");
//        verGreenRatio = (Double) getIntent().getSerializableExtra("verGreenRatio");
//        symGrade = (Integer) getIntent().getSerializableExtra("symGrade");
//        variance = (Double) getIntent().getSerializableExtra("variance");
        
//        imgView = (ImageView) findViewById(R.id.imageViewId);
//        imgView.setImageBitmap(BitmapFactory.decodeFile(mPath));
        
        Log.i(TAG, "showResult: "+mPath);
        }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        analysis = menu.add("Analysis");
        exit = menu.add("Exit");
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
	        if (item == analysis){
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);   
	            builder.setIcon(R.drawable.icon);
	            builder.setTitle("Analysis");
	            builder.setMessage("The variance of your facial Golden Ratio is:"+variance+
	            					". The grade of your facial symmetry is: "+symGrade+
	            					". This is a centesimal grade.The lower symmetry grade you got, the more symetric you are."
	            					+ " However if you have very high score that maight you didn't put you head straight."
	            					+ "Your Golden Ratio of (Eye pupil : Nose flair : Nose bottom) is:"+verBlueRatio
	            					+ ". Your Golden Ratio of (Eye pupil : Nose bottom : Mouth) is" + verGreenRatio +". "    					
	            					);
	            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {

	                }
	            });
	            builder.create().show();


	        }
	        else if(item == exit)
	        {

	        }
	        return true;
    }

}
