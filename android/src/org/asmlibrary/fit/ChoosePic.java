package org.asmlibrary.fit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class ChoosePic extends Activity{ 
	
	private static final String     TAG                 = "ChoosePic";
    private static final String		location            = "storage/emulated/0/image.jpg";
//	private Button 					galleryEntry;
	private ImageView 				mImage;
	private Button					galleryEntry;
	private Uri						mPath               = null;
	private MenuItem               	mFaceDetection;
    private MenuItem               	mGoback;
    private MenuItem               	mExit;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_pic);
		mImage = (ImageView) findViewById(R.id.imageViewId);
		Log.i(TAG, "choosepic");
//		Intent intent = new Intent();
//		intent.setAction(Intent.ACTION_PICK);
//		intent.setType("image/*");
//		startActivityForResult(intent, 0);
		Log.i(TAG, "startactivity");
		galleryEntry = (Button) findViewById(R.id.galleryEntry);
		mImage = (ImageView) findViewById(R.id.imageViewId);
		galleryEntry.setOnClickListener(getImage);
	}
	
	private View.OnClickListener getImage = new OnClickListener() {
		@Override
		public void onClick(View v) {

			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, 0);
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//if success the result code should be -1.
		Log.i(TAG, "resultCode:" + resultCode);
		if (requestCode == 0 && resultCode == -1) {
			
			//acess the uri of the choose picture
			mPath= data.getData();
			Log.i(TAG,"path"+mPath.toString());
//			mPath = mPath.toString();
			mImage.setImageURI(mPath);
			
//			BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
//    		BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
//    		myBitmap = BitmapFactory.decodeResource(getResources(),
//    		R.raw.florencecolgate, BitmapFactoryOptionsbfo);
//    		Bitmap myBitmap = BitmapFactory.decodeFile(mPath.toString(), BitmapFactoryOptionsbfo);
//    		FileOutputStream out = null;
//    		try {
//    		       out = new FileOutputStream(location);
//    		       myBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
//    		} catch (Exception e) {
//    		    e.printStackTrace();
//    		} finally {
//    		       try{
//    		           out.close();
//    		       } catch(Throwable ignore) {}
//    		}
//			
//			try {
//			    File copy = Environment.getExternalStorageDirectory();
//			    File pic = Environment.getDataDirectory();
//			    if (copy.canWrite()) {
//			        String sourceImagePath= mPath.toString();
//			        String destinationImagePath= "storage/emulated/0/image.jpg";
//			        File source= new File(pic, sourceImagePath);
//			        File destination= new File(copy, destinationImagePath);
//			        if (source.exists()) {
//			            FileChannel src = new FileInputStream(source).getChannel();
//			            FileChannel dst = new FileOutputStream(destination).getChannel();
//			            dst.transferFrom(src, 0, src.size());
//			            src.close();
//			            dst.close();
//			        }
//			    }
//			} catch (Exception e) {}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu"+mFaceDetection);
        mFaceDetection = menu.add("Face Detection");
//        mGoback = menu.add("Go Back");
        mExit = menu.add("Exit");
        return true;
    }
    
    
	 
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	
	        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

		        if (item == mFaceDetection){
		        	Intent intent = new Intent(mImage.getContext(), ASMLibraryActivity.class);
		        	intent.putExtra("path", mPath );
		        	intent.putExtra("path", "Gallery");
		            Log.i(TAG,"call show result activity");
		            startService(intent); 
		        }
//		        else if(item == mGoback){        	
//		        	goGallery();
//		        }
		        else if (item == mExit){
		        	System.exit(0);
		        }
		        
				return false;
	 }

//	public void goGallery(){
//			
//		Intent intent = new Intent();
//		intent.setAction(Intent.ACTION_PICK);
//		intent.setType("image/*");
//		startActivityForResult(intent, 0);
//		Log.i(TAG, "startactivity");
//		
//	}
}
