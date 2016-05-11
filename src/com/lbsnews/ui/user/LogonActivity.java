package com.lbsnews.ui.user;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lbsnews.R;
import com.lbsnews.base.BaseActivity;
import com.lbsnews.bean.User;
import com.lbsnews.db.DBHelper;
import com.lbsnews.utils.ConstantUtils;
import com.lbsnews.utils.DBUtils;
import com.lbsnews.utils.HttpUtils;
import com.lbsnews.utils.LogUtils;

public class LogonActivity extends BaseActivity {

	private String TAG = "********LogonActivity******";
	
	private EditText accountEdtTxt;
	private EditText passEdtTxt;
	private EditText confirmPassEdtTxt;
	private Button logonBtn;

	private String mConfirmPassword;
	private String mPassword;
	private String mAccount;
	private Handler handler;
	private User logon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_logon);
		initView();
		initHandler();
	}

	private void initView() {
		accountEdtTxt = (EditText)findViewById(R.id.accountEdtTxt);
		passEdtTxt = (EditText)findViewById(R.id.passEdtTxt);
		confirmPassEdtTxt = (EditText)findViewById(R.id.confirmPassEdtTxt);
		
//		accountEdtTxt.setOnFocusChangeListener(CommonUtils.onFocusAutoClearHintListener);
//		passEdtTxt.setOnFocusChangeListener(CommonUtils.onFocusAutoClearHintListener);
//		confirmPassEdtTxt.setOnFocusChangeListener(CommonUtils.onFocusAutoClearHintListener);
		
		logonBtn = (Button)findViewById(R.id.logonBtn);
		logonBtn.setOnClickListener(new OnclickListenerImpl());
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
							dismissDialog();
					//		insertUser(mAccount, mPassword);
							showToast(Toast.LENGTH_SHORT, R.string.logon_sucess);
							updateLocalUser(logon);
							startActivity(new Intent(LogonActivity.this, UpdateUserActivity.class));
							finish();
						} else {
							dismissDialog();
							showToast(Toast.LENGTH_SHORT, R.string.logon_failed);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				default :
					showToast(Toast.LENGTH_SHORT, R.string.logon_failed);
					break;
				}
			}

		};
	}
	
	/**
	 * 将注册插入数据库
	 * @param account
	 * @param password
	 */
	private void insertUser(String account, String password) {
		DBHelper dbHelper = new DBHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("account", account);
		values.put("password", password);
		db.insert(DBUtils.TABLE_USER, null, values);
		values.clear();
		LogUtils.d(TAG, "插入注册的数据: " + account + " " + password);
	}

	private class OnclickListenerImpl implements OnClickListener {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.logonBtn:
				logon();
				break;
			default:
				break;
			}
		}

	}

	private void logon() {
		mAccount = accountEdtTxt.getText().toString();
		mPassword = passEdtTxt.getText().toString();
		mConfirmPassword = confirmPassEdtTxt.getText().toString();

		if (TextUtils.isEmpty(mAccount)) {
			showToast(Toast.LENGTH_SHORT, R.string.logon_input_account);
		} else if (!checkAccount(mAccount)) {
			showToast(Toast.LENGTH_SHORT, R.string.login_account_error);
		} else if (TextUtils.isEmpty(mPassword)) {
			showToast(Toast.LENGTH_SHORT, R.string.logon_input_pasword);
		} else if (TextUtils.isEmpty(mConfirmPassword)) {
			showToast(Toast.LENGTH_SHORT, R.string.logon_input_pasword);
		} else if (!mPassword.equals(mConfirmPassword)) {
			showToast(Toast.LENGTH_SHORT, R.string.logon_password_not_matched);
			passEdtTxt.setText("");
			confirmPassEdtTxt.setText("");
		} else {
			logon = new User();
			logon.setAccount(mAccount);
			logon.setPassword(mPassword);

			if (this.checkNet()) {
				logonTask(logon);
				showProgressDialog(R.string.logon_ing);
			} else {
				showToast(Toast.LENGTH_SHORT, R.string.net_error);
			}
		}

	}

	private void logonTask(final User user) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					//	JSONArray array = new JSONArray();
						JSONObject json = new JSONObject();
						json.put("account", user.getAccount());
						json.put("password", user.getPassword());
					//	array.put(json);
				//	Gson gson = new Gson();
				//	String json = gson.toJson(user);
					HttpUtils.httpPostMethod(ConstantUtils.LOGON_IP, json, handler);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}).start(); 
	}

}
