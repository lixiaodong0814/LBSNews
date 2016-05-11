package com.lbsnews.ui.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbsnews.R;
import com.lbsnews.base.BaseActivity;
import com.lbsnews.bean.User;
import com.lbsnews.db.DBHelper;
import com.lbsnews.utils.DBUtils;
import com.lbsnews.utils.LogUtils;
import com.lbsnews.utils.MediaUtils;

public class UserInfoActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "******UserInfoActivity*****";
	
	private Button editBut;
	private ImageView headPicImageView;
	private TextView nickNameTextView;
	private TextView sexTextView;
	private TextView telephoneTextView;
	private TextView accountTextView;
	
	private User user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info);
		initView();

	}


	private void initView() {
		editBut = (Button)findViewById(R.id.editBut);
		editBut.setOnClickListener(this);

		headPicImageView = (ImageView)findViewById(R.id.headPicImageView);
		nickNameTextView = (TextView)findViewById(R.id.nickNameTextView);
		sexTextView = (TextView)findViewById(R.id.sexTextView);
		telephoneTextView = (TextView)findViewById(R.id.telephoneTextView);
		accountTextView = (TextView)findViewById(R.id.emailTextView);
		initUser();
	}
	
	private void initUser() {
		user = getLocalUser();
		
	/*	DBHelper dbHelper = new DBHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
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
				
				LogUtils.d(TAG, "初始化用户： "
						+ "\nid: " + id
						+ "\nnickName: " + nickName
						+ "\nsex: " + sex
						+ "\ntelephone: " + telephone
						+ "\npicName: " + picName
						+ "\npicPath: " + picPath
						+ "\nheadPic");
			} while (cursor.moveToNext());
		}
		cursor.close();*/
		
	/*	if (user.getHeadPic() != null) {
			Bitmap headBitmap = MediaUtils.bytesToBitmap(user.getHeadPic());
			showPic(headBitmap);
		}*/
		if (user.getHeadPicStr() != null) {
			Bitmap headBitmap = MediaUtils.stringToBitmap(user.getHeadPicStr());
			showPic(headBitmap);
		}
		if (user.getNickName() != null) {
			nickNameTextView.setText(MediaUtils.decodeString(user.getNickName()));
		}
		if (user.getSex() != null) {
			if (user.getSex().equals("male")) {
				sexTextView.setText("男");
			} else {
				sexTextView.setText("女");
			}
		}
		if (user.getTelephone() != null) {
			telephoneTextView.setText(user.getTelephone());
		}
		if (user.getAccount() != null) {
			accountTextView.setText(user.getAccount());
		}
		
	}
	
	private void showPic(Bitmap headPicBitmap) {
		headPicImageView.setImageBitmap(headPicBitmap);
		Drawable headPicDrawable = MediaUtils.BitmapToDrawble(headPicBitmap);
		headPicImageView.setImageDrawable(headPicDrawable);
	}


	@Override
	public void onClick(View view) {
		Intent intent;
		switch (view.getId()) {
		case R.id.editBut:
			intent = new Intent(UserInfoActivity.this, UpdateUserActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}

	}

}
