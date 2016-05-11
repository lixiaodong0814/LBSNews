package com.lbsnews.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.lbsnews.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.util.Base64;
import android.widget.ArrayAdapter;

public class MediaUtils {
	private String TAG = "*************MediaUtils*****************";

	private String path;
	private Context context;
	public static final int PIC_LOCAL = 1;
	public static final int PIC_TAKE = 2;
	public static final int PIC_ZOOM_DONE = 3;
	public static final int AUDIO_LOCAL = 4;
	public static final int AUDIO_TAKE = 5;
	public static final int VIDEO_LOCAL = 6;
	public static final int VIDEO_TAKE = 7;
	public static final String JPG_SUFFIX = ".jpg";
	public static final String AUDIO_SUFFIX = ".mp3";
	public static final String VIDEO_SUFFIX = ".mp4";
	public static final String CACHE_PAHT = "/.cache";

	public MediaUtils(Context context) {
		path = FileUtils.getSdcardPath() + "/"
				+ context.getString(R.string.app_name);
		this.context = context;
		LogUtils.i(TAG, "path: " + path);
	}

	/**
	 * 插入图片对话框
	 * @param onClickListener
	 */
	public void insertPic(OnClickListener onClickListener) {
		new AlertDialog.Builder(context)
		.setTitle(R.string.media_utils_pic_insert_title)
		.setAdapter(new ArrayAdapter<String>(
				context, 
				android.R.layout.select_dialog_item,
				new String[] {
						context.getString(R.string.media_utils_pic_take),
						context.getString(R.string.media_utils_pic_local),
						context.getString(R.string.media_utils_cancel)}), 
						onClickListener).show();
	}

	/**
	 * 插入音频对话框
	 * @param onClickListener
	 */
	public void insertAudio(OnClickListener onClickListener) {
		new AlertDialog.Builder(context)
		.setTitle(R.string.media_utils_audio_insert_title)
		.setAdapter(new ArrayAdapter<String>(
				context, 
				android.R.layout.select_dialog_item,
				new String[] {
						context.getString(R.string.media_utils_audio_take),
						context.getString(R.string.media_utils_audio_local),
						context.getString(R.string.media_utils_cancel)}), 
						onClickListener).show();
	}

	/**
	 * 插入视频对话框
	 * @param onClickListener
	 */
	public void insertVideo(OnClickListener onClickListener) {
		new AlertDialog.Builder(context)
		.setTitle(R.string.media_utils_video_insert_title)
		.setAdapter(new ArrayAdapter<String>(
				context, 
				android.R.layout.select_dialog_item,
				new String[] {
						context.getString(R.string.media_utils_video_take),
						context.getString(R.string.media_utils_video_local),
						context.getString(R.string.media_utils_cancel)}), 
						onClickListener).show();
	}

	/**
	 * 调用本机相机拍照
	 * @param activity
	 * @param requestCode
	 * @param name
	 */
	public void uploadTakingPic(Activity activity, int requestCode) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		//	intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path + "/pic/",
		//			name)));
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 从本地相册选取照片
	 * @param activity
	 * @param requestCode
	 */
	public void uploadLocalPic(Activity activity, int requestCode) {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 调用相机录制视频
	 * 
	 * @param activity
	 * @param requestCode
	 */
	public void uploadTakingVideo(Activity activity, int requestCode) {
		Intent intent;
		intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 上传本地视频
	 * 
	 * @param activity
	 * @param requestCode
	 */
	public void uploadLocalVideo(Activity activity, int requestCode) {
		Intent intent;
		intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setData(Media.EXTERNAL_CONTENT_URI);
		intent.setType("video/*");
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 调用系统录音设备进行录音
	 * 
	 * @param activity
	 * @param requestCode
	 */
	public void uploadTakingAudio(Activity activity, int requestCode) {
		Intent intent;
		intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 上传本地音频
	 * 
	 * @param activity
	 * @param requestCode
	 */
	public void uploadLocalAudio(Activity activity, int requestCode) {
		Intent intent;
		intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setData(Media.EXTERNAL_CONTENT_URI);
		intent.setType("audio/*");
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * get file path with the given uri
	 * 
	 * @param uri
	 * @return
	 */
	public String getFilePath(Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri, null, null,
				null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			String filePath = cursor.getString(1);
			cursor.close();
			return filePath;
		} else {
			return uri.getPath();
		}
	}

	/**
	 * get an image from the sdcard
	 * 
	 * @param name
	 *            image name
	 * @return
	 */
	public Bitmap getBitmapByName(String name, boolean cache) {
		if (cache) {
			return BitmapFactory.decodeFile(getCacheFilePath(name));
		} else {
			return BitmapFactory.decodeFile(getFilePath(name));
		}
	}

	/**
	 * get an image from the sdcard
	 * 
	 * @param name
	 *            image name
	 * @return
	 */
	public Drawable getDrawableByName(String name, boolean cache) {
		if (cache) {
			return Drawable.createFromPath(getCacheFilePath(name));
		} else {
			return Drawable.createFromPath(getFilePath(name));
		}
	}

	/**
	 * get an image from the sdcard
	 * 
	 * @param uri
	 *            image path
	 * @return
	 */
	public Bitmap getBitmapByUri(Uri uri) {
		return BitmapFactory.decodeFile(getFilePath(uri));
	}

	/**
	 * get an image from the sdcard
	 * 
	 * @param path
	 *            image path
	 * @return
	 */
	public Drawable getDrawableByPath(String path) {
		return Drawable.createFromPath(path);
	}

	/**
	 * get an image from the sdcard
	 * 
	 * @param path
	 *            image path
	 * @return
	 */
	public Bitmap getBitmapByPath(String path) {
		return BitmapFactory.decodeFile(path);
	}

	/**
	 * get an image from the sdcard
	 * 
	 * @param uri
	 *            image path
	 * @return
	 */
	public Drawable getDrawableByUri(Uri uri) {
		return Drawable.createFromPath(getFilePath(uri));
	}

	/**
	 * get a video thumbnail from the sdcard
	 * 
	 * @param uri
	 *            image path
	 * @return
	 */
	public Bitmap getVideoThumbnailByPath(String path) {
		return ThumbnailUtils.createVideoThumbnail(path,
				Video.Thumbnails.MICRO_KIND);
	}

	public Bitmap getVideoThumbnailByUri(Uri uri) {
		long id = ContentUris.parseId(uri);
		ContentResolver mContentResolver = context.getContentResolver();
		return Video.Thumbnails.getThumbnail(mContentResolver, id,
				Video.Thumbnails.MICRO_KIND, null);
	}

	/**
	 * save a bitmap to the sdcard
	 * 
	 * @param bitmap
	 * @param name
	 * @param global
	 *            the access permission of this file
	 * @return
	 */
	public boolean saveToSd(Bitmap bitmap, String name, boolean global) {
		try {
			File file;
			if (global) {
				file = FileUtils.createFile(createJpgFilePath(name));
			} else {
				file = FileUtils.createFile(createBinaryFilePath(name));
			}
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			if (global) {
				folderScan(path);
			}
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getFilePath(String name){
		return new StringBuilder(path).append("/").append(name).toString();
	}

	public String getCacheFilePath(String name){
		return new StringBuilder(path).append(CACHE_PAHT).append("/").append(name).toString();
	}

	/**
	 * create a video file path under the root file of the application and
	 * return
	 * 
	 * @param name
	 * @return the file path
	 */
	public String createVideoFilePath(String name) {
		return new StringBuilder(path).append("/video/").append(name).append(
				VIDEO_SUFFIX).toString();
	}

	/**
	 * create an audio file path under the root file of the application and
	 * return
	 * 
	 * @param name
	 * @return the file path
	 */
	public String createAudioFilePath(String name) {
		return new StringBuilder(path).append("/audio/").append(name).append(
				AUDIO_SUFFIX).toString();
	}

	/**
	 * create a jpg file path under the root file of the application and return
	 * 
	 * @param name
	 * @return the file path
	 */
	public String createJpgFilePath(String name) {
		return new StringBuilder(path).append("/pic/").append(name).append(
				JPG_SUFFIX).toString();
	}

	/**
	 * create a binary file path under the root file of the application and
	 * return
	 * 
	 * @param name
	 * @return the file path
	 */
	public String createBinaryFilePath(String name) {
		return new StringBuilder(path).append("/").append(name).toString();
	}

	/**
	 * save an image to sdcard
	 * 
	 * @param is
	 *            the stream of image
	 * @param name
	 *            the name of image
	 * @param global
	 *            the access permission of this file
	 * @return
	 */
	public boolean saveToSd(InputStream is, String name, boolean cache) {
		try {
			File file;
			if (cache) {
				file = FileUtils.createFile(getCacheFilePath(name));
			} else {
				file = FileUtils.createFile(getFilePath(name));
			}
			FileOutputStream out = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = is.read(buffer)) != -1) {
				out.write(buffer, 0, length);
			}
			out.flush();
			out.close();
			is.close();
			if (!cache) {
				folderScan(path);
			}
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * make the MediaScaner to scan this file
	 * 
	 * @param file
	 */
	public void fileScan(String file) {
		Uri data = Uri.parse("file://" + file);
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				data));
	}

	/**
	 * notify MediaScaner that the content of the folder has been changed
	 * 
	 * @param path
	 *            path of folder
	 */
	public void folderScan(String path) {
		System.out.println("folder scan");
		File file = new File(path);

		if (file.isDirectory()) {
			File[] array = file.listFiles();

			for (int i = 0; i < array.length; i++) {
				File f = array[i];

				if (f.isFile()) {// FILE TYPE
					String name = f.getName();

					if (name.contains(".jpg") || name.contains(".png")
							|| name.contains(".mp3")) {
						fileScan(f.getAbsolutePath());
					}
				} else {// FOLDER TYPE
					folderScan(f.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Make the MediaScaner to scan all files on SdCard, this will take about
	 * 2m. So if the method {@link #folderScan} can meet your demand, had better
	 * not invoke this method.
	 * 
	 * @deprecated
	 */
	public void allScan() {
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
				.parse("file://" + Environment.getExternalStorageDirectory())));
	}

	/**
	 * Insert an image into the directory of the camera's default workplace.
	 * 
	 * @param cr
	 *            The content resolver to use
	 * @param bitmap
	 *            The stream to use for the image
	 * @param name
	 *            The name of the image
	 * @param description
	 *            The description of the image
	 * @return The URL to the newly created image, or null if the image failed
	 *         to be stored for any reason.
	 */
	public static String saveAsCamereRes(ContentResolver cr, Bitmap bitmap,
			String name, String description) {
		return MediaStore.Images.Media.insertImage(cr, bitmap, name,
				description);
	}

	/**
	 * 返回圆形图片
	 * @param bitmap
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		//圆形图片宽高
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		//正方形的边长
		int r = 0;
		//取最短边长
		if (width > height) {
			r = height;
		} else {
			r = width;
		}
		
		//构建一个bitmap
		Bitmap backgroundBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		
		//设置边缘光滑，去掉锯齿
		paint.setAntiAlias(true);
		//宽高相等,即正方形
		RectF rect = new RectF(0, 0, r, r);
		//通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
		//且都等于r/2时，画出来的圆角矩形就是圆形
		canvas.drawRoundRect(rect, r / 2, r / 2, paint);
		//设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		//canvas将bitmap画在backgroundBmp上
		canvas.drawBitmap(bitmap, null, rect, paint);
		
		
		//返回已经绘画好的backgroundBmp
		return backgroundBmp;
	}

	/**
	 * convert bitmap to Drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable BitmapToDrawble(Bitmap bitmap) {
		return new BitmapDrawable(bitmap);
	}

	/**
	 * convert Drawable to bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		return ((BitmapDrawable) drawable).getBitmap();
	}

	/**
	 * 将字符串转换成位图
	 * @param string
	 * @return
	 */
	public static Bitmap stringToBitmap(String string) {
		Bitmap bitmap = null;
		//	bitmap = BitmapFactory.decodeFile(string);
		/*	byte[] bytes;
		try {
			bytes = string.getBytes("UTF-8");
			ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
			bitmap = BitmapFactory.decodeStream(bin);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/


		byte[] bitmapArray;
		bitmapArray = Base64.decode(string, Base64.DEFAULT);
		bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
		return bitmap;

	}

	/**
	 * 将Bitmap转换成字符串
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToString(Bitmap bitmap) {
		String string = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, baos);
		byte[] bytes = baos.toByteArray();
		//	string = new String(Base64Coder.encodeLines(bytes));
		//	string = Base64.encodeToString(bytes, Base64.DEFAULT);
		//	string = new String(bytes, "UTF-8");
		//	bytes = string.getBytes("UTF-8");
		string = Base64.encodeToString(bytes, Base64.DEFAULT);


		return string;
	}

	public static byte[] bitmapToBytes(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, baos);
		byte[] bytes = baos.toByteArray();

		return bytes;
	}

	public static Bitmap bytesToBitmap(byte[] bytes) {
		Bitmap bitmap = null;
		bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

		return bitmap;
	}

	public static String encodeString(String str) {
		return Base64Coder.encodeString(str);
	}

	public static String decodeString(String str) {
		return Base64Coder.decodeString(str);
	}
}
