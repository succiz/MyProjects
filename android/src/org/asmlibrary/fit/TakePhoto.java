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
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class TakePhoto extends Activity implements OnClickListener{
	
	private static final String     TAG                 = "TakePhoto";
    
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
	 @Override    
	    public void onCreate(Bundle savedInstanceState) {    
	        super.onCreate(savedInstanceState);
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);           
	        mSurface = new MySurfaceView(this);    
	        setContentView(mSurface); 
	        mSurface.setOnClickListener(this);
	        Log.i(TAG, "oncreate");
	        
	    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 if(!isClicked)    
	        {    
		        mSurface.tackPicture(); 
//		        mPath = mSurface.getPath();
		        isClicked = true;            
	        }         
	}    
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        Log.i(TAG, "called onCreateOptionsMenu"+mFaceDetection);
	        mFaceDetection = menu.add("Face Detection");
	        mExit = menu.add("Exit");
	        return true;
	    }
	    
	    
		 
		 @Override
		    public boolean onOptionsItemSelected(MenuItem item) {
		    	
		        Log.i(TAG, "selected item: " + item);

			        if (item == mFaceDetection){
			        	mPath = mSurface.getPath();
				        Log.i(TAG, "Mpath:" + mPath);
				        mPath = getPath(Uri.parse(mPath));
			        	Intent intent = new Intent(this, ASMLibraryActivity.class);
			        	intent.putExtra("path", mPath );
			        	intent.putExtra("action", "Gallery");
			            Log.i(TAG,"call ASM service");
			            startService(intent); 
			        }else if (item == mExit){
			        	System.exit(0);
			        }
			        
					return false;
		 }
		 
		 public String getPath(Uri uri) {
			 String[] projection = { MediaStore.Images.Media.DATA };
			 CursorLoader loader = new CursorLoader(this, uri, projection, null, null, null);
			 Cursor cursor = loader.loadInBackground();
			 startManagingCursor(cursor);
			 int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			 cursor.moveToFirst();
			 return cursor.getString(column_index);
			 }
}
