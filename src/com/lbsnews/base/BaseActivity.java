package com.lbsnews.base;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lbsnews.R;
import com.lbsnews.bean.User;
import com.lbsnews.utils.LogUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity {
	private static final String TAG = "***********";
	
	private String regex;
	private Pattern pattern;
	private Matcher matcher;

	ProgressDialog progressDialog;
	int dialogTextResultId;
	protected TextView titleText;

	protected ImageButton titleLeftImg, titleRightImg;
	protected Button titleRightBtn, titleLeftBtn;
	protected static List<Activity> sActivityList = new ArrayList<Activity>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sActivityList.add(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		sActivityList.remove(this);
	}

	protected void initTitle() {
	//	titleLeftImg = (ImageButton) findViewById(R.id.title_left_img);
	//	titleRightImg = (ImageButton) findViewById(R.id.title_right_img);
		
		titleText = (TextView)findViewById(R.id.title_text);
		
		titleRightBtn = (Button)findViewById(R.id.title_right_btn);
		titleRightBtn.setVisibility(View.VISIBLE);
		
		titleLeftBtn = (Button)findViewById(R.id.title_left_btn);
		titleLeftBtn.setVisibility(View.VISIBLE);
		
//		titleLeftImg.setVisibility(View.GONE);
//		titleRightImg.setVisibility(View.GONE);
		
	}

	/**
	 * 将更新的数据同步更新到本地数据库中
	 * @param user
	 */
	protected void updateLocalUser(User user) {
		//将账号信息存到data里面
		SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
		editor.putInt("id", user.getId());
		editor.putString("account", user.getAccount());
		editor.putString("password", user.getPassword());
		editor.putString("latitude", String.valueOf(user.getLatitude()));
		editor.putString("longitude", String.valueOf(user.getLongitude()));
		if (user.getNickName() != null) {
			editor.putString("flag", "true");
			editor.putString("nickName", user.getNickName());
		} else {
			editor.putString("flag", "flase");
		}
		if (user.getSex() != null) {
			editor.putString("sex", user.getSex());
		}
		if (user.getTelephone() != null) {
			editor.putString("telephone", user.getTelephone());
		}
		if (user.getPicName() != null) {
			editor.putString("picName", user.getPicName());
			editor.putString("picPath", user.getPicPath());
			//	editor.putString("headPic", new String(user.getHeadPic()));
			editor.putString("headPicStr", user.getHeadPicStr());
		}
		editor.commit();
		LogUtils.d(TAG, "将信息存到sharedpreferences中了:");
	}
	
	protected User getLocalUser() {
		User user = new User();
		SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
		user.setId(pref.getInt("id", 001));
		user.setAccount(pref.getString("account", "1234@163.com"));
		user.setPassword(pref.getString("password", "password"));
		if (pref.getString("flag", "f").equals("true")) {
			user.setNickName(pref.getString("nickName", "我的昵称"));
			user.setSex(pref.getString("sex", "male"));
			user.setTelephone(pref.getString("telephone", "110120119"));
		//	user.setHeadPic(pref.getString("headPic", null).getBytes());
			user.setHeadPicStr(pref.getString("headPicStr", null));
			user.setPicName(pref.getString("picName", null));
			user.setPicPath(pref.getString("picPath", null));
			user.setLatitude(Double.parseDouble(pref.getString("latitude", "0.0")));
			user.setLongitude(Double.parseDouble(pref.getString("longitude", "0.0")));
		}
		
		return user;
	}

	protected final boolean checkAccount(String email) {

		if (regex == null) {
			regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
			pattern = Pattern.compile(regex);
		}

		matcher = pattern.matcher(email);
		return matcher.find();
	}

	protected final void showProgressDialog(int resId) {
		dialogTextResultId = resId;
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage(getString(dialogTextResultId));
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	protected final void dismissDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	protected final void showToast(int duration, int resId) {
		Toast.makeText(this, resId, duration).show();
	}

	@SuppressLint("ShowToast") 
	protected final void showToast(int duration, String text) {
		Toast.makeText(this, text, duration).show();
	}

	public void finishActivities() {
		for (Activity activity : sActivityList) {
			if (activity != null)
				activity.finish();
		}
	}

	/**
	 * 检查网络连接状态
	 * @return 网络连接状态
	 */
	protected final boolean checkNet() {
		boolean connected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			if (info.getState() == NetworkInfo.State.CONNECTED) {
				connected = true;
			}
		}

		return connected;
	}

}
