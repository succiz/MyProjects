package org.asmlibrary.fit;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;    
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.R.string;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;    
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;    
import android.hardware.Camera;    
import android.hardware.Camera.PictureCallback;    
import android.hardware.Camera.ShutterCallback;    
import android.hardware.Camera.Size;
import android.media.FaceDetector;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;    
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;    
import android.view.SurfaceView;    
import android.view.WindowManager;
import android.widget.LinearLayout;
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{    

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private String mPath;
 
    private PictureCallback jpeg = new PictureCallback() {    
        
        @Override    
        public void onPictureTaken(byte[] data, Camera camera) {    
            // TODO Auto-generated method stub    
            try    
            {    
                Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length); 
                ContentResolver cr = getContext().getContentResolver();
                mPath = MediaStore.Images.Media.insertImage(cr, bm, "myPhoto", "this is a Photo");
                
                Log.i("surface", "mPath"+mPath);
//             	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
//             	String currentDateandTime = sdf.format(new Date());
                //stored bitmap will be replaced by the next time to use app
          		String tempPath = Environment.getExternalStorageDirectory().getPath() +
                      "/image" + ".jpg";
          		Log.i("ASMLibrary",""+tempPath);
             	FileOutputStream out = new FileOutputStream(tempPath);
             	bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
             	bm.recycle();
                
            }catch(Exception e)    
            {    
                e.printStackTrace();    
            }    
        }    
    };    
    
    
    
    private ShutterCallback shutter = new ShutterCallback() {    
            
        @Override    
        public void onShutter() {    
            // TODO Auto-generated method stub    
            Log.d("ddd", "shutter");    
                
        }    
    };    
    private PictureCallback raw = new PictureCallback() {    
            
        @Override    
        public void onPictureTaken(byte[] data, Camera camera) {    
            // TODO Auto-generated method stub    
            Log.d("ddd", "raw");    
                
        }    
    };      
    public MySurfaceView(Context context)    
    {    
        super(context);    
        mHolder = getHolder();
        mHolder.addCallback(this);    
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //?????   
    }    
    public void tackPicture()    
    {    
        mCamera.takePicture(null,null,jpeg);    
    }    
    public void voerTack()    
    {    
        mCamera.startPreview();    
    }    
    @Override    
    public void surfaceChanged(SurfaceHolder holder, int format, int width,    
            int height) {    
    	Camera.Parameters params = mCamera.getParameters();    
        params.setPictureFormat(PixelFormat.JPEG); 
      //This has to strict follow requirements, the width and height of setPreviewSize must be 
      //exactly satisfy requirements, like 640x480, 1280x700. However, Nexus 7 returns width=1280 and
      //height=703, which cannot set to the previewSize and causes some preview stretch problems.
        if(height == 703)
        	height=height-3;
        params.setPreviewSize(width,height);    
        //mCamera.setParameters(params);    
        mCamera.startPreview(); 

//        Size s = params.getPictureSize();
//        double w = s.width;
//        double h = s.height;
//
//        if( width>height )
//        {
//         this.setLayoutParams(new LinearLayout.LayoutParams( (int)(height*(w/h)), height) );
//        }
//        else
//        {
//         this.setLayoutParams(new LinearLayout.LayoutParams( width, (int)(width*(h/w)) ) );
//        }
//    	
//    	List<Camera.Size> sizeList = params.getSupportedPreviewSizes();
//        Camera.Size previewSize = (Camera.Size)sizeList.get(1);
//        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE); // 获取当前屏幕管理器对象  
//        Display display = wm.getDefaultDisplay();             
//
//    	List<Camera.Size> mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
//    	Log.i("width",""+width);
//        Log.i("height",""+height);
//        if (mSupportedPreviewSizes != null) {
//           previewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
//        }
//        params.setPreviewSize(previewSize.width, previewSize.height);
//        Log.i("setpreviewwidth",""+previewSize.width);
//        Log.i("setpreviewheight",""+previewSize.height);
//        //params.setPictureFormat(PixelFormat.JPEG);
//        Log.i("setformat","11");
//        
//        mCamera.setParameters(params);
//        Log.i("setparameters","12");
//        
//        mCamera.startPreview();
//        Log.i("startpreview","13");
    }    
    @Override    
    public void surfaceCreated(SurfaceHolder holder) {    
        // TODO Auto-generated method stub    
        if(mCamera == null)    
        {    
            mCamera = getCamera();   
            try {    
                mCamera.setPreviewDisplay(holder);    
            } catch (IOException e) {    
                // TODO Auto-generated catch block    
                e.printStackTrace();    
            }    
        }           
    }    
    @Override    
    public void surfaceDestroyed(SurfaceHolder holder) {    
        // TODO Auto-generated method stub    
        mCamera.stopPreview();
        mCamera.release(); 
        mCamera = null;    
                
    }  
//    public void onAutoFocus(boolean success, Camera camera) {    
//        // TODO Auto-generated method stub    
//        if(success)    
//        {     
//            Camera.Parameters params = mCamera.getParameters();    
//            params.setPictureFormat(PixelFormat.JPEG);    
//            params.setPreviewSize(640,480);    
//            mCamera.setParameters(params);    
//            mCamera.takePicture(null, null, jpeg);    
//        }  
//    }
    
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.i("optimal","1");
        return optimalSize;
    }
    
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public Camera getCamera()
    {

        for(int i = 0; i < Camera.getNumberOfCameras(); i++)
            return Camera.open(i);

        return null;
    }
    
    public String getPath(){
    	Log.i("surface","getpath:"+mPath);
		return mPath;
    }
}
