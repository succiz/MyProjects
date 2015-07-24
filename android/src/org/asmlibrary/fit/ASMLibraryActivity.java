package org.asmlibrary.fit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.core.Scalar;
import org.opencv.core.Point;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.asmlibrary.fit.R;
import org.asmlibrary.fit.ASMFit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class ASMLibraryActivity extends Service{
    
	private static final String    	TAG                 = "ASMLibraryDemo";
    private static final String		location            = "storage/emulated/0/image";
    
    private Mat                    	mRgba;
    private Mat                    	mRgba2;
    private Mat                    	mGray;
    private Mat                    	mGray2;
    private File                   	mCascadeFile;
    private File                   	mFastCascadeFile;
    private File                   	mModelFile;
    private ASMFit      		   	mASMFit;
    private ASMFit      		   	mASMFit2;
    private int						takePhoto;
    private boolean					mFlag = true;
    private boolean					mFastDetect = false;
    private boolean                 isClicked = false;
    private Mat						mShape;
    private Mat						mShape2;
    private static final Scalar 	mColor = new Scalar(255, 0, 0);
    private static final Scalar 	mColor2 = new Scalar(0, 0, 255);
    private MenuItem               	mFaceDetection;
    private MenuItem               	mGoback;
    private MenuItem               	mExit;
    private Bitmap 					myBitmap;
    private MySurfaceView           mSurface;
//    private ImageView				mImageView;
    private ArrayList<Double>		width;
    private ArrayList<Double>		height;
    private ArrayList<Double>		width2;
    private ArrayList<Double>		height2;
    private ProgressDialog          progressBar;
	private int 					progressBarStatus = 0;
	private Handler 				progressBarHandler = new Handler();
	private long 					fileSize = 0;
	private String					mPath;
	private ImageView				mView;
	private String					mAction;
    
    public ASMLibraryActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }
    
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
    	private File getSourceFile(int id, String name, String folder){
    		File file = null;
    		try {
	    		InputStream is = getResources().openRawResource(id);
	            File cascadeDir = getDir(folder, Context.MODE_PRIVATE);
	            file = new File(cascadeDir, name);
	            FileOutputStream os = new FileOutputStream(file);
	            
	            byte[] buffer = new byte[4096];
	            int bytesRead;
	            while ((bytesRead = is.read(buffer)) != -1) {
	                os.write(buffer, 0, bytesRead);
	            }
	            is.close();
	            os.close();
    		}catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to load file " + name + ". Exception thrown: " + e);
            }
	            
            return file;
    		
    	}
    	
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //mRgba = Highgui.imread("storage/emulated/0/image.jpg");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("asmlibrary");
                    System.loadLibrary("jni-asmlibrary");
                    
                    mASMFit = new ASMFit();

                    mModelFile = getSourceFile(R.raw.my68_1d, "my68_1d.amf", "model");
                    if(mModelFile != null)
                    	mASMFit.nativeReadModel(mModelFile.getAbsolutePath());
                    
                    
                    mCascadeFile = getSourceFile(R.raw.haarcascade_frontalface_alt2, 
                    		"haarcascade_frontalface_alt2.xml", "cascade");
                    if(mCascadeFile != null)
                    	mASMFit.nativeInitCascadeDetector(mCascadeFile.getAbsolutePath());

                    mFastCascadeFile = getSourceFile(R.raw.lbpcascade_frontalface, 
                    		"lbpcascade_frontalface.xml", "cascade");
                    if(mFastCascadeFile != null)
                    	mASMFit.nativeInitFastCascadeDetector(mFastCascadeFile.getAbsolutePath());
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
        
    public void onCreate(Bundle savedInstanceState) {    
        super.onCreate();
//        Log.i(TAG,"just in onCreate");
//        setContentView(R.layout.asmlibrary);
//
//        mView = (ImageView) findViewById(R.id.mViewId);
//        mView.setImageURI(Uri.parse(location)); 
//        
//        String tempPath = (String) getIntent().getSerializableExtra("path");
//        
//        mPath = Uri.parse(tempPath);
//        mAction = (String) getIntent().getSerializableExtra("action");
//        Log.i(TAG, "oncreate");        
    }    
    
//    public void load(View view) {  
//        Intent intent = new Intent();  
//        intent.setAction(Intent.ACTION_PICK);  
//        intent.setType("image/*");  
//        startActivityForResult(intent, 0);  
//    }  
//  
//    @Override  
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
//        if (data != null) {    
//            Uri uri = data.getData();  
//            mImageView.setImageURI(uri);  
//
//            Bitmap bitmap = data.getParcelableExtra("data"); 
//            
//            ContentResolver cr = this.getContentResolver();
//            MediaStore.Images.Media.insertImage(cr, bitmap, "myPhoto", "this is a Photo");
//      		String mPath = Environment.getExternalStorageDirectory().getPath() +
//                  "/image" + ".jpg";
//      		Log.i("ASMLibrary",""+mPath);
//         	FileOutputStream out = null;
//			try {
//				out = new FileOutputStream(mPath);
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//         	bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//         	bitmap.recycle();
//  
//        }  
//        super.onActivityResult(requestCode, resultCode, data);  
//    }  
 
	/** Called when the activity is first created. */
	
//	@Override
//    public void onPause()
//    {
//        super.onPause();
//    }
//
//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_5, this, mLoaderCallback);
//        mFlag = false;
//    }
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        Log.i(TAG, "called onCreateOptionsMenu"+mFastDetect);
//        mFaceDetection = menu.add("Face Detection");
//        mGoback = menu.add("Go Back");
//        mExit = menu.add("Exit");
//        return true;
//    }
//    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	
//        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
//
//	        if (item == mFaceDetection){
//	        	
//		        if (isClicked == true){	
//		        	
//		        	// prepare for a progress bar dialog
//					progressBar = new ProgressDialog(mSurface.getContext());
//					progressBar.setCancelable(true);
//					progressBar.setMessage("Face Detecting...");
//					progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//					progressBar.setProgress(0);
//					progressBar.setMax(100);
//					progressBar.show();
//		 
//					//reset progress bar status
//					progressBarStatus = 0;
//		 
//					//reset filesize
//					fileSize = 0;
//					new Thread(new Runnable() {
//						  public void run() {
//							while (progressBarStatus < 100) {
//			 
//							  // process some tasks
//							  progressBarStatus = imgProcess();
//							  Log.i(TAG,"flag0: "+mFlag);
//			 
//							  // your computer is too fast, sleep 1 second
//							  try {
//								Thread.sleep(1000);
//							  } catch (InterruptedException e) {
//								e.printStackTrace();
//							  }
//			 
//							  // Update the progress bar
//							  progressBarHandler.post(new Runnable() {
//								public void run() {
//								  progressBar.setProgress(progressBarStatus);
//								}
//							  });
//							}
//			 
//							// ok, file is downloaded,
//							if (progressBarStatus >= 100) {
//			 
//								// sleep 2 seconds, so that you can see the 100%
//								try {
//									Thread.sleep(2000);
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
//			 
//								// close the progress bar dialog/     
//								progressBar.dismiss();
//								Log.i(TAG,"flag01: "+mFlag);
//								 if (!mFlag){
//					        		 Log.i(TAG,"flag02: "+mFlag);
//					        		Toast.makeText(ASMLibraryActivity.this, "Please take a human face picture", Toast.LENGTH_LONG).show();
//					 				mSurface.voerTack();
//					 				isClicked = false;
//					        	 } else{
//				
//					        	 Intent intent = new Intent(mSurface.getContext(), showResult.class);
//					        	 intent.putExtra("path", mPath );
//					             intent.putExtra("width", width);
//					             intent.putExtra("height", height);
//					             intent.putExtra("width2", width2);
//					             intent.putExtra("height2", height2);
//					             Log.i(TAG,"call show result activity");
//					             startService(intent); 
//					        	 }
//								
//							}
//						  }
//					       }).start();
////		        	imgProcess();
//		        	 
////		        	 if (!mFlag){
////		        		 Log.i(TAG,"flag02: "+mFlag);
////		        		Toast.makeText(ASMLibraryActivity.this, "Please take a human face picture", Toast.LENGTH_LONG).show();
////		 				mSurface.voerTack();
////		 				isClicked = false;
////		        	 } else{
////	
////		        	 Intent intent = new Intent(this, FaceGrading.class);
////		             intent.putExtra("width", width);
////		             intent.putExtra("height", height);
////		             intent.putExtra("width2", width2);
////		             intent.putExtra("height2", height2);
////		             Log.i(TAG,"this");
////		             startService(intent); 
////		        	 }
//		        }   
//	        }
//	        else if(item == mGoback)
//	        {
//	        	mSurface.voerTack();
//	        	isClicked = false;
//	        }else if(item == mExit){
//	        	System.exit(0);
//	        }
//	        return true;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    public int imgProcess(String path){   	
    	BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
		BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
//		myBitmap = BitmapFactory.decodeResource(getResources(),
//		R.raw.florencecolgate, BitmapFactoryOptionsbfo);
		Log.i(TAG,"imgProcess:"+path);
		myBitmap = BitmapFactory.decodeFile(mPath, BitmapFactoryOptionsbfo);
		mRgba = new Mat(myBitmap.getWidth(),myBitmap.getHeight(), CvType.CV_8UC1);
		Utils.bitmapToMat(myBitmap, mRgba);
        mGray = new Mat(myBitmap.getWidth(),myBitmap.getHeight(), CvType.CV_8UC1);
		Imgproc.cvtColor(mRgba,mGray,Imgproc.COLOR_RGB2GRAY);
		Imgproc.equalizeHist(mGray,mGray);
		
//		mirror the picture
		mRgba2 = new Mat(myBitmap.getWidth(),myBitmap.getHeight(), CvType.CV_8UC1);
		Core.flip(mRgba, mRgba2, 1);
		mGray2 = new Mat(myBitmap.getWidth(),myBitmap.getHeight(), CvType.CV_8UC1);
		Imgproc.cvtColor(mRgba2,mGray2,Imgproc.COLOR_RGB2GRAY);
		Imgproc.equalizeHist(mGray2,mGray2);
		
		width = new ArrayList<Double>();
		height = new ArrayList<Double>();
		width2 = new ArrayList<Double>();
		height2 = new ArrayList<Double>();
		
//		if(mFlag == false)
//		{
		Mat detShape = new Mat();
		Mat detShape2 = new Mat();
		if(mFastDetect){
			mFlag = mASMFit.fastDetectAll(mGray, detShape);
				Log.i(TAG,"face detect");
			mASMFit2.fastDetectAll(mGray2, detShape2);
				Log.i(TAG,"second face detect");
				Log.i(TAG,"flag1: "+mFlag);
				}
		else{
			mFlag = mASMFit.detectAll(mGray, detShape);
			mASMFit.detectAll(mGray2, detShape2);
				Log.i(TAG,"flag2: "+mFlag);
		}
		if(mFlag){
			mShape = detShape.row(0);
			mShape2 = detShape2.row(0);
		}	
//		}
	
		if(mFlag) 
		{//
			 mASMFit.fitting(mGray, mShape);
			 mASMFit.fitting(mGray2, mShape2);
		}
			
		if(mFlag)
		{
			int nPoints = mShape.row(0).cols()/2;
				
			for(int i = 0; i < nPoints; i++)
			{ 		
					
				double x = mShape.get(0, 2*i)[0];
				double y = mShape.get(0, 2*i+1)[0];
				double x2 = mShape2.get(0, 2*i)[0];
				double y2 = mShape2.get(0, 2*i+1)[0];
					
				width.add(i,x);
				height.add(i,y);
				width2.add(i,x2);
				height2.add(i,y2);
					
				Point pt = new Point(x, y);	
				Core.circle(mRgba, pt, 3, mColor);

			}
		}
		
        Log.i(TAG,"this");
		Bitmap resultBitmap = Bitmap.createBitmap(mRgba.cols(),  mRgba.rows(),Bitmap.Config.RGB_565);
        Utils.matToBitmap(mRgba, resultBitmap);
        ContentResolver cr = getContentResolver();
        MediaStore.Images.Media.insertImage(cr, resultBitmap, "a photo", "this is a Photo.");
//		mPath = Environment.getExternalStorageDirectory().getPath() +
//                "/image" + ".jpg";
//    		Log.i(TAG,"path:"+mPath);
//    		Log.i(TAG,"flag3: "+mFlag);
//       	FileOutputStream out = null;
//       	
//		try {
//			out = new FileOutputStream(mPath);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//       	resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
       	resultBitmap.recycle();
		return 100;
    }//end of imgProcess

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
    @Override  
    public void onStart(Intent intent, int startId) {  
        Log.v(TAG, "ServiceDemo onStart");  
        super.onStart(intent, startId);  
        
        mPath = (String) intent.getSerializableExtra("path");
        mAction = (String) intent.getSerializableExtra("action");
        Log.i(TAG, "onStart");
        Log.i(TAG,"onStart"+mPath);
        imgProcess(mPath);
    }  
      
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.v(TAG, "ServiceDemo onStartCommand");  
        return super.onStartCommand(intent, flags, startId);  
    } 

}