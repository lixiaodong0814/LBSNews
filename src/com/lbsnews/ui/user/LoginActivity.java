package com.lbsnews.ui.user;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint.Join;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.lbsnews.R;
import com.lbsnews.base.BaseActivity;
import com.lbsnews.bean.User;
import com.lbsnews.ui.MainTabActivity;
import com.lbsnews.utils.ConstantUtils;
import com.lbsnews.utils.HttpUtils;
import com.lbsnews.utils.LogUtils;
import com.lbsnews.utils.MediaUtils;

public class LoginActivity extends BaseActivity {
	
	private String TAG = "*******LoginActivity********";
	
	private EditText accountEdtTxt;
	private EditText passEdtTxt;
	private Button loginBtn;
	private Button logonBtn;
	private String mAccount;
	private String mPassword;
	private Handler handler;
	private User user;
	private LocationClient mLocationClient;
	private MyLocationListener mMyLocationListener;
	User login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_login);
		initView();
		initMyLocation();
		initHandler();
	}
	
	private void initMyLocation() {
		mLocationClient = new LocationClient(this);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (this.checkNet()) {
			if (!mLocationClient.isStarted())
				mLocationClient.start();
		}
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (this.checkNet()) {
			mLocationClient.stop();
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
						JSONObject result = new JSONObject(response);
						int sucess = Integer.parseInt(result.getString("sucess"));
						if (sucess == 0) {
							Gson gson = new Gson();
							user = new User();
							user = gson.fromJson(result.getString("user"), User.class);
							LogUtils.i(TAG, "·µ»Ø±¨ÎÄ:" + user);
							
							updateLocalUser(user);
							
							dismissDialog();
							showToast(Toast.LENGTH_SHORT, R.string.login_sucess);
							directToMain();
						} else {
							dismissDialog();
							showToast(Toast.LENGTH_SHORT, R.string.login_fail);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				default :
					showToast(Toast.LENGTH_SHORT, R.string.login_fail);
					break;
				}
			}

		};
	}

	private void initView() {
		accountEdtTxt = (EditText)findViewById(R.id.accountEdtTxt);
		passEdtTxt = (EditText)findViewById(R.id.passEdtTxt);
		
//		accountEdtTxt.setOnFocusChangeListener(CommonUtils.onFocusAutoClearHintListener);
//		passEdtTxt.setOnFocusChangeListener(CommonUtils.onFocusAutoClearHintListener);
		
		loginBtn = (Button)findViewById(R.id.loginBtn);
		logonBtn = (Button)findViewById(R.id.logonBtn);
		loginBtn.setOnClickListener(new OnclickListenerImpl());
		logonBtn.setOnClickListener(new OnclickListenerImpl());
		login = new User();
	}



	private class OnclickListenerImpl implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.loginBtn:
				login();
				break;
			case R.id.logonBtn:
				Intent intent = new Intent(LoginActivity.this, LogonActivity.class);
				startActivity(intent);
				break;
			}
		}

	}
	
	private void directToMain() {
		Intent intent = new Intent(LoginActivity.this, MainTabActivity.class);
		startActivity(intent);
		finish();
	}

	private void login() {
		mAccount = accountEdtTxt.getText().toString();
		mPassword = passEdtTxt.getText().toString();
		if (TextUtils.isEmpty(mAccount)) {
			showToast(Toast.LENGTH_SHORT, R.string.login_input_account);
		} else if (TextUtils.isEmpty(mPassword)) {
			showToast(Toast.LENGTH_SHORT, R.string.login_input_password);
		} else if (!checkAccount(mAccount)) {
			showToast(Toast.LENGTH_SHORT, R.string.login_account_error);
		} else {
			
			login.setAccount(mAccount);
			login.setPassword(mPassword);

			if (this.checkNet()) {
				loginTask(login);
				Log.d(TAG, "login()");
				showProgressDialog(R.string.login_ing);
			} else {
				showToast(Toast.LENGTH_SHORT, R.string.net_error);
			}
		}

	}

	private void loginTask(final User login) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject json = new JSONObject();
					Gson gson = new Gson();
					json.put("login", gson.toJson(login));
					Log.d(TAG, "loginTask: user " + json);
					HttpUtils.httpPostMethod(ConstantUtils.LOGIN_IP, json, handler);
				} catch (JSONException e) {
					e.printStackTrace();
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
	
	private class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			showMyLocation(location);
		}
		
	}
	
	private void showMyLocation(BDLocation location) {
		String address = location.getAddrStr();
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		login.setAddress(MediaUtils.encodeString(address));
		login.setLatitude(latitude);
		login.setLongitude(longitude);
	}
}
