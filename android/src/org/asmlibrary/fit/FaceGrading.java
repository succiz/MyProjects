package org.asmlibrary.fit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.opencv.android.Utils;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class FaceGrading extends Service{
	private static final String    TAG                 = "FaceGrading";
	private static final double    gr                  = 1.618;
    private ArrayList<Double>				width;
    private ArrayList<Double>				height;
    private ArrayList<Double>				width2;
    private ArrayList<Double>				height2;
	private int  							symGrade   = 0;
	private int								GRGrade    = 0;
	private int 							verBluePoint;
	private int 							horGoldenPoint;
	private int 							verGreenPoint;
	private int 							verEyebrowHighPoint;
	private int 							verEyebrowLowPoint;
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
    private double 							k;
    private double 							k2;
    private double 							variance;
    private Thread							thread;
    @Override    
    public void onCreate() {    
        super.onCreate();
    }
    
    
    public void symCalculate(){
    	double x;
    	double y;
    	double x2;
    	double y2;
    	double dis;
    	double dis2;
    	double dis3;
    	double X = width.get(width.size()-1);;
    	double Y = height.get(height.size()-1);;
    	double X2 = width2.get(width2.size()-1);
    	double Y2 = height2.get(height2.size()-1);
    	Log.i(TAG,"SYM");
    	
    	for(int i = 0; i<width.size()-1; i++){
    		x = width.get(i);
    		y = height.get(i);
    		x2 = width2.get(i);
    		y2 = height2.get(i);
    		
    		dis = Math.abs(Math.sqrt(x*x + y*y) - Math.sqrt(X*X + Y*Y));
    		dis2 = Math.abs(Math.sqrt(x2*x2 + y2*y2) - Math.sqrt(X2*X2 + Y2*Y2));
    		dis3 = Math.abs(dis - dis2);
    		
    		//if the grade is larger then 50 then consider this point has a huge difference
    		if (dis3 <= 50)
    			symGrade = (int) (symGrade + dis3);
    	}
    	float tmp = symGrade;
    	tmp = (tmp/3234)*100;
    	symGrade = (int) tmp;
    	Log.i(TAG, "grade: "+symGrade);
    	
    }
    
    public void GRHorizontalCalculate(){
    	double dis;
    	double dis2;
    	double dis3;
    	
//    	Log.i(TAG,"31 "+width.get(31));
    	k = (height.get(67)-height.get(41))/(width.get(67)-width.get(41));
    	Log.i(TAG,"horizontal k: "+k); 	
    	
//    	1. Face side : Eyebrows : Face side 

    	dis = disCalculate(width.get(24),height.get(24),k,1);
    	dis2 = disCalculate(width.get(18),height.get(18),k,1);
    	dis3 = disCalculate(width.get(13),height.get(13),k,1);
    	
//    	Log.i(TAG,"1: "+width.get(1)+"  "+height.get(1));
//    	Log.i(TAG,"24: "+width.get(24)+"  "+height.get(24));
//    	Log.i(TAG,"18: "+width.get(18)+"  "+height.get(18));
//    	Log.i(TAG,"13: "+width.get(13)+"  "+height.get(13));
//    	Log.i(TAG,"dis: "+dis);
//    	Log.i(TAG,"dis2: "+dis2);
//    	Log.i(TAG,"dis3: "+dis3);
    	
    	horGoldenRatio = ratioCalculate(dis,dis2);
    	horGoldenRatio2 = ratioCalculate(dis2,dis3);
    	Log.i(TAG, "horGoldenRatio:"+horGoldenRatio);
    	Log.i(TAG, "horGoldenRatio2:"+horGoldenRatio2);
    	
//    	2.Gold – Face side : Eye inside : Face side
    	
    	dis = disCalculate(width.get(1),height.get(1),k,29);
    	dis2 = disCalculate(width.get(1),height.get(1),k,34);
    	dis3 = disCalculate(width.get(1),height.get(1),k,13);
    	
//    	Log.i(TAG,"1: "+width.get(1)+"  "+height.get(1));
//    	Log.i(TAG,"29: "+width.get(29)+"  "+height.get(29));
//    	Log.i(TAG,"34: "+width.get(34)+"  "+height.get(34));
//    	Log.i(TAG,"13: "+width.get(13)+"  "+height.get(13));
//    	Log.i(TAG,"dis: "+dis);
//    	Log.i(TAG,"dis2: "+dis2);
//    	Log.i(TAG,"dis3: "+dis3);
    	
    	horGolden2Ratio = ratioCalculate(dis,dis2);
    	horGolden2Ratio2 = ratioCalculate(dis2,dis3);
    	
    	Log.i(TAG, "horGolden2Ratio:"+horGolden2Ratio);
    	Log.i(TAG, "horGolden2Ratio2:"+horGolden2Ratio2);
    	
//    	3.Gold – Face side : Nose width : Face side
    	dis = disCalculate(width.get(1),height.get(1),k,39);
    	dis2 = disCalculate(width.get(1),height.get(1),k,43);
    	dis3 = disCalculate(width.get(1),height.get(1),k,13);
    	
    	horGolden3Ratio = ratioCalculate(dis,dis2);
    	horGolden3Ratio2 = ratioCalculate(dis2,dis3);
    	Log.i(TAG, "horGolden3Ratio:"+horGolden3Ratio);
    	Log.i(TAG, "horGolden3Ratio2:"+horGolden3Ratio2);
    	
//    	4. White – Face side : Eye outside : Nose center
    	dis = disCalculate(width.get(27),height.get(27),k,67);
    	dis2 = disCalculate(width.get(1),height.get(1),k,67);
    	horWhiteRatio = ratioCalculate(dis,dis2);
    	Log.i(TAG, "horWhiteRatio:"+horWhiteRatio);
    	
//    	5. Blue – Eye outside : Eye inside : Nose center
    	dis = disCalculate(width.get(27),height.get(27),k,29);
    	dis2 = disCalculate(width.get(27),height.get(27),k,67);
    	horBlueRatio = ratioCalculate(dis,dis2);
    	Log.i(TAG, "horBlueRatio:"+horBlueRatio);
    	
//    	6. Green – Mouth outside : Lip cupid’s bow : Mouth outside
    	dis = disCalculate(width.get(48),height.get(48),k,50);
    	dis2 = disCalculate(width.get(48),height.get(48),k,52);
    	dis3 = disCalculate(width.get(48),height.get(48),k,54);
    	
    	horGreenRatio = ratioCalculate(dis,dis2);
    	horGreenRatio2 = ratioCalculate(dis2,dis3);
    	horGoldenRatio2 = ratioCalculate(dis2,dis3);
    	Log.i(TAG, "horGreenRatio:"+horGreenRatio);
    	Log.i(TAG, "horGreenRatio2:"+horGreenRatio2);
    }
    
    public void GRVerticalCalculate(){
    	
    	//use eyepupils as bases(hard to change), calculate slope k from points 31 and 36
    	double dis;
    	double dis2;
    	double dis3;
    	double ratio;
    	double ratio2;
    	
    	k2 = ((height.get(31)-height.get(36))/(width.get(31)-width.get(36)));
    	Log.i(TAG,"k: "+k2);
    	
    	//1.Blue – Eye pupil : Nose flair : Nose bottom   compare point 40 and point 42
    	
    	dis = disCalculate(width.get(67),height.get(67),k2,31);
    	dis2 = disCalculate(width.get(40),height.get(40),k2,31);
    	ratio = ratioCalculate(dis,dis2);
    	
    	dis = disCalculate(width.get(67),height.get(67),k2,31);
    	dis2 = disCalculate(width.get(42),height.get(42),k2,31);
    	ratio2 = ratioCalculate(dis,dis2);
    	
    	if (Math.abs(ratio-gr) <= Math.abs(ratio2-gr)){
    		verBluePoint = 40;
    		verBlueRatio = ratio;
    	}else{
    		verBluePoint = 42;
    		verBlueRatio = ratio2;
    	}		
    	Log.i(TAG, "point:"+verBluePoint);
    	Log.i(TAG, "verBlueRatio"+verBlueRatio);
    	
    	//2.Green – Eye pupil : Nose bottom : Mouth  compare point 40 and 42
    	
    	dis = disCalculate(width.get(40),height.get(40),k2,31);
    	dis2 = disCalculate(width.get(66),height.get(66),k2,31);
    	ratio = ratioCalculate(dis,dis2);
    	dis2 = disCalculate(width.get(66),height.get(66),k2,31);
    	ratio2 = ratioCalculate(dis,dis2);
    	
    	if (Math.abs(ratio-gr) <= Math.abs(ratio2-gr)){
    		verGreenPoint = 40;
    		verGreenRatio = ratio;
    	}else{
    		verGreenPoint = 42;
    		verGreenRatio = ratio2;
    	}		
    	Log.i(TAG, "verGreenRatio:"+verGreenRatio);
    	
//    	3. Gold – Eyebrow top : Eyebrow bottom : Eye top : Eye bottom
    	dis = disCalculate(width.get(22),height.get(22),k2,28);
    	dis2 = disCalculate(width.get(23),height.get(23),k2,28);
    	if (dis <= dis2){
    		verEyebrowHighPoint = 23;
    		dis = dis2;
    		}
    	else	verEyebrowHighPoint = 22;
    	
    	dis2 = disCalculate(width.get(25),height.get(25),k2,28);
    	dis3 = disCalculate(width.get(24),height.get(24),k2,28);
    	
    	if (dis2 <= dis3){
    		verEyebrowLowPoint = 25;
    		dis2 = disCalculate(width.get(25),height.get(25),k2,verEyebrowHighPoint);
    	}else{
    		verEyebrowLowPoint = 24;
    		dis2 = disCalculate(width.get(24),height.get(24),k2,verEyebrowHighPoint);
    	}
    	eyeRatio = ratioCalculate(dis2,dis);
    	
    	dis3 = disCalculate(width.get(30),height.get(30),k2,verEyebrowHighPoint);
    	eyeRatio2 = ratioCalculate(dis,dis3);
    	
    	Log.i(TAG, "eyeRatio:"+eyeRatio);
    	Log.i(TAG, "eyeRatio2:"+eyeRatio2);
    	
    	eyeHeight = disCalculate(width.get(28),height.get(28),k2,30);
    	Log.i(TAG, "eyeHeight:"+eyeHeight);
    }
    
    public double disCalculate(double X, double Y, double k, int i){
    	
    	double A;
    	double B;
    	double C;
    	double distance;
    	
    	A = k;
    	B = -1;
    	C = height.get(i)-A*width.get(i);
    	distance = Math.abs(A*X + B*Y + C)/Math.sqrt(A*A+B*B);
    	
		return distance;
    }
    
    public double CCalculate(double X, double Y, double k, int i){
    	
    	double A;
    	double B;
    	double C;
    	double distance;
    	
    	A = k;
    	B = -1;
    	C = height.get(i)-A*width.get(i);    	
		return C;
    }

    public double ratioCalculate(double dis, double dis2){
    	double ratio = dis2/dis;
    	
		return ratio;
    	
    }
    
    public double varCalculate(){
    	
    	double x;
    	double average;
    	int num = 0;
    	int total = 14;
    	
    	x = Math.sqrt(((verBlueRatio - gr)*(verBlueRatio - gr) +
    					(verGreenRatio - gr)*(verGreenRatio - gr)+
    					(horGoldenRatio - gr)*(horGoldenRatio - gr)+
    					(horGoldenRatio2 - gr)*(horGoldenRatio2 - gr)+
    					(horGolden2Ratio - gr)*(horGolden2Ratio - gr)+
    					(horGolden2Ratio2 - gr)*(horGolden2Ratio2 - gr)+
    					(horGolden3Ratio - gr)*(horGolden3Ratio - gr)+
    					(horGolden3Ratio2 - gr)*(horGolden3Ratio2 - gr)+
    					(horGreenRatio - gr)*(horGreenRatio - gr)+
    					(horGreenRatio2 - gr)*(horGreenRatio2 - gr)+
    					(eyeRatio - gr)*(eyeRatio - gr)+
    					(eyeRatio2 - gr)*(eyeRatio2 - gr)+
    					(horWhiteRatio - gr)*(horWhiteRatio - gr)+
    					(horBlueRatio - gr)*(horBlueRatio - gr))/total);
    	Log.i(TAG,"VARIANCE: "+x);
//    	
//    	if (Math.abs(verBlueRatio -gr) <= x)
//    		num=num+1;
//    	if (Math.abs(verGreenRatio -gr) <= x)
//    		num=num+1;
//    	if (Math.abs(horGoldenRatio -gr) <= x)
//    		num=num+1;
//    	if (Math.abs(horGoldenRatio2 -gr) <= x)
//    		num=num+1;
//    	if (Math.abs(horGolden2Ratio -gr) <= x)
//    		num=num+1;
//    	if (Math.abs(horGolden2Ratio2 -gr) <= x)
//    		num=num+1;
//    	if (Math.abs(horGolden3Ratio -gr) <= x)
//    		num=num+1;
//    	if (Math.abs(horGolden3Ratio2 -gr) <= x)
//    		num=num+1;
//    	if (Math.abs(horGreenRatio -gr) <= x)
//    		num=num+1;
//    	if (Math.abs(horGreenRatio2 -gr) <= x)
//    		num=num+1;
//    	if (Math.abs(eyeRatio -gr) <= x)
//    		num=num+1;
//    	if (Math.abs(eyeRatio2 -gr) <= x)
//    		num=num+1;
//    	if (Math.abs(horBlueRatio -gr) <= x)
//    		num=num+1;
//    	if (Math.abs(horWhiteRatio -gr) <= x)
//    		num=num+1;
//    	Log.i(TAG,"NUM: "+num);
    	
//    	GRGrade = (int) (((double)num/(double)total)*100);
    	Log.i(TAG,"GRADE:"+GRGrade);
    	
		return x;
    	
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
    @Override  
    public void onStart(Intent intent, int startId) {  
        Log.v(TAG, "ServiceDemo onStart");  
        super.onStart(intent, startId);  
        
        //receive data from last activity
        width = (ArrayList<Double>) intent.getSerializableExtra("width");
        height = (ArrayList<Double>) intent.getSerializableExtra("height");
        width2 = (ArrayList<Double>) intent.getSerializableExtra("width2");
        height2 = (ArrayList<Double>) intent.getSerializableExtra("height2");
        Log.i(TAG,"onCreate");
        
        //calculate
        symCalculate();
        GRVerticalCalculate();
        GRHorizontalCalculate();
        variance = varCalculate();
        
        //draw standard lines on bitmap
        Bitmap bm = BitmapFactory.decodeFile("storage/emulated/0/image.jpg");
        Bitmap bitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();

        //draw horizontal
        // set drawing color
        p.setColor(Color.BLUE);
        
        double B = -1;
        double C= height.get(29)-k*width.get(29);
        float startY = (float) (height.get(verEyebrowHighPoint) - 40);
        float startX = (float) ((startY*(-B) - C)/k);
        float stopY = (float) (height.get(40) + 20);
        float stopX = (float) ((stopY*(-B) - C)/k);
        // draw a line onto the canvas
        canvas.drawLine(startX, startY, stopX, stopY, p);
        
        C= height.get(34)-k*width.get(34);
        startY = (float) (height.get(verEyebrowHighPoint) - 40);
        startX = (float) ((startY*(-B) - C)/k);
        stopY = (float) (height.get(40) + 20);
        stopX = (float) ((stopY*(-B) - C)/k);
        // draw a line onto the canvas
        canvas.drawLine(startX, startY, stopX, stopY, p);
        
        C= height.get(1)-k*width.get(1);
        startY = (float) (height.get(verEyebrowHighPoint) - 40);
        startX = (float) ((startY*(-B) - C)/k);
        stopY = (float) (height.get(40) + 20);
        stopX = (float) ((stopY*(-B) - C)/k);
        float y0 = startY;
        float x0 = startX;
        float y1 = stopY;
        float x1 = stopX;
        // draw a line onto the canvas
        canvas.drawLine(startX, startY, stopX, stopY, p);
        
        C= height.get(13)-k*width.get(13);
        startY = (float) (height.get(verEyebrowHighPoint) - 40);
        startX = (float) ((startY*(-B) - C)/k);
        stopY = (float) (height.get(40) + 20);
        stopX = (float) ((stopY*(-B) - C)/k);
        // draw a line onto the canvas
        canvas.drawLine(startX, startY, stopX, stopY, p);
        
        canvas.drawLine(x0, y0, startX, startY, p);
        canvas.drawLine(x1, y1, stopX, stopY, p);
        
        //Draw vertical eye part
        
        ContentResolver cr = getContentResolver();
        MediaStore.Images.Media.insertImage(cr, bitmap, "a photo", "this is a Photo.");
        
        String mPath = Environment.getExternalStorageDirectory().getPath() +
                "/image" + ".jpg";
    		Log.i(TAG,"path:"+mPath);
       	FileOutputStream out = null;
		try {
			out = new FileOutputStream(mPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       	bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
       	bitmap.recycle();
	   	Intent mIntent = new Intent(this, showResult.class);
	   	mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    mIntent.putExtra("symGrade", symGrade);
	    mIntent.putExtra("variance", variance);
	    mIntent.putExtra("verBlueRatio", verBlueRatio);
	    mIntent.putExtra("verGreenRatio", verGreenRatio);
	    Log.i(TAG,"this");
	    startActivity(mIntent);
    }  
      
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.v(TAG, "ServiceDemo onStartCommand");  
        return super.onStartCommand(intent, flags, startId);  
    } 
    
    

}
