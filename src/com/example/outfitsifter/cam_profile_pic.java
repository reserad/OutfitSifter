package com.example.outfitsifter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class cam_profile_pic extends Activity {
	private SurfaceView preview=null;
	private SurfaceHolder previewHolder=null;
	private Camera camera=null;
	@SuppressWarnings("unused")
	private boolean inPreview=false;
	private boolean cameraConfigured=false;
	private List<Camera.Size> mSupportedPreviewSizes;
	private Camera.Size mPreviewSize;
	String photoPathName = "";
	String UserName = "";

	Context context = this;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.profile_pic);

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		UserName = sharedPrefs.getString(UserName+"CurrentUser", null);
		
		final Button capture = (Button)findViewById(R.id.btnCapture);

		preview=(SurfaceView)findViewById(R.id.surfaceview);
		previewHolder=preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		previewHolder.setFormat(PixelFormat.UNKNOWN);

		capture.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View arg0) {
				capture.setEnabled(false);
				camera.takePicture(null, null, myPictureCallback_JPG);
				capture.setEnabled(true);
				
				capture.postDelayed(new Runnable() {
					@Override
					public void run() {
						File FilePathOfImage = new File(photoPathName);
						crop_profile_pic c = new crop_profile_pic();
						Bitmap cropped = c.onLoad(FilePathOfImage);
						try
						{
							Toast.makeText(context, photoPathName, Toast.LENGTH_LONG).show();
							FileOutputStream  out = new FileOutputStream(FilePathOfImage);
							cropped.compress(Bitmap.CompressFormat.PNG, 60, out);
							camera.stopPreview();
							camera.setPreviewCallback(null);
							camera.release();
							camera = null;
							Toast.makeText(context, "Profile Picture Saved", Toast.LENGTH_LONG).show();
							Intent intent = new Intent(context, settings.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);	
						}
						catch(Exception e)
						{
							
						}

					}
				}, 2500);
				
				
			}});
	}


	PictureCallback myPictureCallback_JPG = new PictureCallback(){

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub

			Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data .length);

			Matrix matrix = new Matrix();
			matrix.postRotate(270);
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			resizedBitmap.compress(Bitmap.CompressFormat.PNG, 60, stream);
			byte[] array = stream.toByteArray();

			new SavePhotoTask().execute(array);
			camera.startPreview();
			inPreview=true;
		}};

		class SavePhotoTask extends AsyncTask<byte[], String, String> {
			@Override
			protected String doInBackground(byte[]... png) {
				File d = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/");
				String name = "Profile_Picture";
				if(!d.exists()){
					d.mkdir();}
				File photo=
						new File(d, name + ".png");
				photoPathName = photo.getPath();
				try {
					FileOutputStream fos=new FileOutputStream(photo.getPath());

					fos.write(png[0]);

					fos.close();

				}

				catch (java.io.IOException e) {
					Log.e("PictureDemo", "Exception in photoCallback", e);
				}

				return(null);
			}
			
		}

		@Override
		public void onResume() {
			super.onResume();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				Camera.CameraInfo info=new Camera.CameraInfo();

				for (int i=0; i < Camera.getNumberOfCameras(); i++) {
					Camera.getCameraInfo(i, info);

					if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
						camera=Camera.open(i);
						camera.setDisplayOrientation(90);						
					}
				}
			}

			if (camera == null) {
				camera=Camera.open();
				camera.unlock();
				startPreview();				
			}
		}

		AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

			@Override
			public void onAutoFocus(boolean arg0, Camera arg1) {
				// TODO Auto-generated method stub
			}};

			private Camera.Size getBestPreviewSize(int width, int height,
					Camera.Parameters parameters) {
				Camera.Size result=null;

				for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
					if (size.width <= width && size.height <= height) {
						if (result == null) {
							result=size;
						}
						else {
							int resultArea=result.width * result.height;
							int newArea=size.width * size.height;

							if (newArea > resultArea) {
								result=size;

							}
						}
					}
				}

				return(result);
			}

			private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
				Camera.Size result=null;


				for (Camera.Size size : parameters.getSupportedPictureSizes()) {
					if (result == null) {
						result=size;
					}
					else {
						int resultArea=result.width * result.height;
						int newArea=size.width * size.height;

						if (newArea < resultArea) {
							result=size;
						}
					}
				}

				return(result);
			}

			private void initPreview(int width, int height) {
				if (camera != null && previewHolder.getSurface() != null) {
					try {
						camera.setPreviewDisplay(previewHolder);
					}
					catch (Throwable t) {
						Log.e("PreviewDemo-surfaceCallback",
								"Exception in setPreviewDisplay()", t);
						Toast.makeText(cam_profile_pic.this, t.getMessage(),
								Toast.LENGTH_LONG).show();
					}

					if (!cameraConfigured) {
						Camera.Parameters parameters=camera.getParameters();
						Camera.Size size=getBestPreviewSize(width, height, parameters);
						Camera.Size pictureSize=getSmallestPictureSize(parameters);

						if (size != null && pictureSize != null) {

							mSupportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
							if (mSupportedPreviewSizes != null) {
								mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
							}

							parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
							parameters.setPictureSize(pictureSize.width,
									pictureSize.height);
							parameters.setPictureFormat(ImageFormat.JPEG);
							camera.setParameters(parameters);
							cameraConfigured=true;
						}
					}
				}
			}

			private void startPreview() {
				if (cameraConfigured && camera != null) {
					camera.startPreview();
					inPreview=true;
				}
			}

			SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
				public void surfaceCreated(SurfaceHolder holder) {
					if (camera == null) {
						camera = Camera.open();
						try {
							camera.setPreviewDisplay(holder);

							// TODO test how much setPreviewCallbackWithBuffer is faster
							camera.setPreviewCallback(null);
						} catch (IOException e) {
							camera.release();
							camera = null;
						}
					}
				}

				public void surfaceChanged(SurfaceHolder holder, int format,
						int width, int height) {
					initPreview(width, height);
					startPreview();
				}

				public void surfaceDestroyed(SurfaceHolder holder) {
					if (camera != null) {
						camera.stopPreview();
						camera.setPreviewCallback(null);
						camera.release();
						camera = null;
					}
				}
			};

			Camera.PictureCallback photoCallback=new Camera.PictureCallback() {
				public void onPictureTaken(byte[] data, Camera camera) {
					new SavePhotoTask().execute(data);
					camera.startPreview();
					inPreview=true;
				}
			};
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					camera.stopPreview();
					camera.setPreviewCallback(null);
					camera.release();
					camera = null;
					Intent intent = new Intent(context, settings.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent); 
					return true;
				}
				return super.onKeyDown(keyCode, event);
			}

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
				return optimalSize;
			}
}