package com.lbsnews.ui.user;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lbsnews.R;
import com.lbsnews.base.BaseActivity;
import com.lbsnews.bean.User;
import com.lbsnews.db.DBHelper;
import com.lbsnews.ui.MainTabActivity;
import com.lbsnews.utils.ConstantUtils;
import com.lbsnews.utils.DBUtils;
import com.lbsnews.utils.FileUtils;
import com.lbsnews.utils.HttpUtils;
import com.lbsnews.utils.LogUtils;
import com.lbsnews.utils.MediaUtils;

public class UpdateUserActivity extends BaseActivity implements DialogInterface.OnClickListener {
	private static final String TAG = "*************UpdateUserActivity***********";

	private MediaUtils mediaUtils;
	private ImageView headPicImgView;
	private Button uploadHeadPicBut;
	private EditText nickNameEdtTxt;
	private EditText telephoneEdtTxt;
	private RadioButton maleRadioBut;
	private RadioButton femaleRadioBut;
	private Handler handler;
	private Bitmap headPicBitmap;
	private Drawable headPicDrawable;

	private User user;
	private String headPicName;
	private String headPicPath;
	private byte[] headPic;
	private String nickName;
	private String telephone;
	private String sex; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_update);

		initView();
		initUser();
		initHandler();
	}

	private void initView() {
		initTitle();
		titleRightBtn = (Button) findViewById(R.id.title_right_btn);
		titleRightBtn.setVisibility(View.VISIBLE);
		titleRightBtn.setText(R.string.update_user_save);

		titleText = (TextView)findViewById(R.id.title_text);
		titleText.setText(R.string.update_user_complete_info);

		titleLeftBtn = (Button) findViewById(R.id.title_left_btn);
		titleLeftBtn.setVisibility(View.VISIBLE);
		titleLeftBtn.setText(R.string.update_user_skip);

		titleLeftBtn.setOnClickListener(new OnclickListenerImpl());
		titleRightBtn.setOnClickListener(new OnclickListenerImpl());

		headPicImgView = (ImageView)findViewById(R.id.headpic);
		uploadHeadPicBut = (Button)findViewById(R.id.upload_headpic);
		nickNameEdtTxt = (EditText)findViewById(R.id.nick_name);
		telephoneEdtTxt = (EditText)findViewById(R.id.telephone);
		maleRadioBut = (RadioButton)findViewById(R.id.male);
		femaleRadioBut = (RadioButton)findViewById(R.id.female);

		uploadHeadPicBut.setOnClickListener(new OnclickListenerImpl());

	}

	private void initUser() {
		user = getLocalUser();
		/*	DBHelper dbHelper = new DBHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(DBUtils.TABLE_USER, new String[]{"account"}, "account=?", new String[]{user.getAccount()}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				user.setId(id);
				String nickName = cursor.getString(cursor.getColumnIndex("nickName"));
				user.setNickName(nickName);
				String sex = cursor.getString(cursor.getColumnIndex("sex"));
				user.setSex(sex);
				String telephone = cursor.getString(cursor.getColumnIndex("telephone"));
				user.setTelephone(telephone);
				String picName = cursor.getString(cursor.getColumnIndex("picName"));
				user.setPicName(picName);
				String picPath = cursor.getString(cursor.getColumnIndex("picPath"));
				user.setPicPath(picPath);
				byte[] bytes = cursor.getBlob(cursor.getColumnIndex("headPic"));
				user.setHeadPic(bytes);

			} while (cursor.moveToNext());
		}
		cursor.close();*/

		if (user.getHeadPicStr() != null) {
			Bitmap headBitmap = MediaUtils.stringToBitmap(user.getHeadPicStr());
			showPic(headBitmap);
		}
		/*if (user.getHeadPic() != null) {
			Bitmap headBitmap = MediaUtils.bytesToBitmap(user.getHeadPic());
			showPic(headBitmap);
		}*/
		if (user.getNickName() != null) {
			nickNameEdtTxt.setText(MediaUtils.decodeString(user.getNickName()));
		}
		if (user.getSex() != null) {
			if (user.getSex().equals("male")) {
				maleRadioBut.setChecked(true);
			} else if (user.getSex().equals("female")) {
				femaleRadioBut.setChecked(true);
			} 
		}
		if (user.getTelephone() != null) {
			telephoneEdtTxt.setText(user.getTelephone());
		}

	}

	private void initHandler() {
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					try {
						String response = msg.getData().getString("res");
						Gson gson = new Gson();
						user = new User();
						user = gson.fromJson(response, User.class);
						LogUtils.i(TAG, "返回报文:" + user);
						updateLocalUser(user);

						dismissDialog();
						showToast(Toast.LENGTH_SHORT, R.string.update_user_success);
						startActivity(new Intent(UpdateUserActivity.this, MainTabActivity.class));
						finish();

					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default :
					showToast(Toast.LENGTH_SHORT, R.string.update_user_failed);
					break;
				}
			}

		};
	}

	private class OnclickListenerImpl implements OnClickListener {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			//跳过完善个人信息
			case R.id.title_left_btn:
				directToMain();
				break;
				//拍照或选择图片
			case R.id.upload_headpic:
				showPicSelectDialog();
				break;
				//更新用户信息
			case R.id.title_right_btn:
				LogUtils.i(TAG, "点击更新按钮了!");
				updateUser();
				break;

			}
		}

	}

	private void updateUser() {
		nickName = nickNameEdtTxt.getText().toString().trim();
		telephone = telephoneEdtTxt.getText().toString().trim();
		if (maleRadioBut.isChecked()) {
			sex = "male";
		} else if (femaleRadioBut.isChecked()) {
			sex = "female";
		}

		//		user = new User();
		if (headPicBitmap != null) {
			user.setHeadPicStr(MediaUtils.bitmapToString(headPicBitmap));
			user.setPicPath(headPicPath);
			user.setPicName(headPicName);
		}
		if (nickName != null && nickName.length() > 0) {
			user.setNickName(MediaUtils.encodeString(nickName));
		}
		if (sex != null && sex.length()> 0) {
			user.setSex(sex);
		}
		if (telephone != null && telephone.length() > 0) {
			user.setTelephone(telephone);
		}

		LogUtils.i(TAG, "user info: " + user);

		if (this.checkNet()) {
			updateUserTask(user);
			showProgressDialog(R.string.update_user_ing);
		} else {
			showToast(Toast.LENGTH_SHORT, R.string.net_error);
		}

	}

	private void updateUserTask(final User user) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Gson gson = new Gson();
					String json = gson.toJson(user);
					LogUtils.d(TAG, "updateUserTask:!!!");
					HttpUtils.httpPostMethod(ConstantUtils.UPDATE_USER_IP, json, handler);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start(); 
	}

	private void showPicSelectDialog() {
		mediaUtils = new MediaUtils(this);
		mediaUtils.insertPic(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//结果码不等于取消的时候
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case MediaUtils.PIC_LOCAL:
				/*	if(data != null){
					setPicToView(data, requestCode);
				}*/
				startPhotoZoom(data.getData());
				break;
			case MediaUtils.PIC_TAKE:
				if(data != null){
					setPicToView(data, requestCode);
				}
				/*		String picPath = mediaUtils.createJpgFilePath(cameraPicName);
				File tempFile = new File(picPath);
				if (!(tempFile.getParentFile().exists() && tempFile.getParentFile().isDirectory())) {
					tempFile.mkdirs();
				} else {
					startPhotoZoom(Uri.fromFile(tempFile));
				}*/

				break;
			case MediaUtils.PIC_ZOOM_DONE:
				if(data != null){
					setPicToView(data, MediaUtils.PIC_LOCAL);
				}
				break;
			default:
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 图片裁剪
	 * @param uri
	 */
	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 340);
		intent.putExtra("outputY", 340);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, MediaUtils.PIC_ZOOM_DONE);
	}

	private void directToMain() {
		Intent intent = new Intent(UpdateUserActivity.this, MainTabActivity.class);
		startActivity(intent);
		finish();
	}

	private void setPicToView(Intent data, int requestCode) {

	/*	if (headPicDrawable != null || headPicBitmap != null) {
			headPicDrawable = null;
			headPicBitmap.recycle();
		}*/

		if (requestCode == MediaUtils.PIC_LOCAL) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				headPicBitmap = extras.getParcelable("data");
			}

		} else if (requestCode == MediaUtils.PIC_TAKE) {
			headPicBitmap = (Bitmap) data.getExtras().get("data");
		}

		headPicName = Long.toString(System.currentTimeMillis());
		mediaUtils.saveToSd(headPicBitmap, headPicName, true);

		headPicPath = mediaUtils.createJpgFilePath(headPicName);
		showPic(headPicBitmap);

		LogUtils.i(TAG, "picName: " + headPicName);
		LogUtils.i(TAG, "picPath: " + headPicPath);

	}

	private void showPic(Bitmap headPicBitmap) {
		headPicDrawable = MediaUtils.BitmapToDrawble(headPicBitmap);
		headPicImgView.setImageDrawable(headPicDrawable);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case 0:
			mediaUtils.uploadTakingPic(this, MediaUtils.PIC_TAKE);
			break;
		case 1:
			mediaUtils.uploadLocalPic(this, MediaUtils.PIC_LOCAL);
			break;
		default :
			break;
		}
	}

}
