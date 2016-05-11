package com.lbsnews.ui;


import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbsnews.R;
import com.lbsnews.adapter.MyNewsAdapter;
import com.lbsnews.adapter.NearPeopleAdapter;
import com.lbsnews.base.BaseActivity;
import com.lbsnews.base.DynamicListView;
import com.lbsnews.base.DynamicListView.DynamicListViewListener;
import com.lbsnews.bean.MyNews;
import com.lbsnews.bean.NearPeople;
import com.lbsnews.bean.User;
import com.lbsnews.ui.user.MyNewsActivity;
import com.lbsnews.utils.ConstantUtils;
import com.lbsnews.utils.HttpUtils;
import com.lbsnews.utils.LogUtils;


public class NearPeopleActivity extends BaseActivity implements DynamicListViewListener {
	private static final String TAG = "*****NearPeopleActivity*******";
	private DynamicListView nearPeopleListView;
	private NearPeopleAdapter nearPeopleAdapter;
	private static int pageNo = 1;
	private List<NearPeople> nearPeopleData;
	private List<NearPeople> nearPeopleList;
	private User user;

	// 用于刷新控件状态
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				nearPeopleAdapter.notifyDataSetChanged();
				nearPeopleListView.doneRefresh();
			} else if (msg.what == 1) {
				nearPeopleAdapter.notifyDataSetChanged();
				nearPeopleListView.doneMore();
			} else {
				super.handleMessage(msg);
			}
		}
	};

	Handler getNearPeopleHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				try {
					String response = msg.getData().getString("res");
					JSONObject result = new JSONObject(response);
					int sucess = Integer.parseInt(result.getString("success"));
					if (sucess == 0) {
						nearPeopleList = new ArrayList<NearPeople>();
						Gson gson = new Gson();
						nearPeopleList = gson.fromJson(result.getString("nearPeople"), new TypeToken<List<NearPeople>>(){}.getType());

						if (nearPeopleList != null) {

							showToast(Toast.LENGTH_SHORT, R.string.near_people_get_success);
							synchronized (nearPeopleData) {
								nearPeopleData.addAll(nearPeopleList);
							}
						} else {
							showToast(Toast.LENGTH_SHORT, R.string.near_people_get_no_more_content);
						}
						Message message = new Message();
						message.what = 0;
						handler.sendMessage(message);
						dismissDialog();

					} else {
						dismissDialog();
						showToast(Toast.LENGTH_SHORT, R.string.near_poeple_get_fail);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			default :
				showToast(Toast.LENGTH_SHORT, R.string.near_poeple_get_fail);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearpeople_main);
		initView();
	}

	private void initView() {
		initTitle();
		titleLeftBtn.setText(R.string.near_people_load);
		titleText.setText(R.string.near_people_activity);
		titleRightBtn.setText(R.string.near_people_convert);

		titleLeftBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getNearPeople();
			}
		});

		titleRightBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				convertToMapActivity();
			}
		});

		nearPeopleListView = (DynamicListView)findViewById(R.id.nearPeopleListView);
		nearPeopleListView.setDoMoreWhenBottom(false);	// 滚动到低端的时候不自己加载更多
		nearPeopleListView.setOnRefreshListener(this);
		nearPeopleListView.setOnMoreListener(this);

		user = getLocalUser();
		initData();
	}

	private void initData() {
		nearPeopleData = new ArrayList<NearPeople>();
		nearPeopleAdapter = new NearPeopleAdapter(this, nearPeopleData);
		nearPeopleListView.setAdapter(nearPeopleAdapter);
	}

	private void convertToMapActivity() {
		Intent intent = new Intent(NearPeopleActivity.this, NearPeopleMapActivity.class);
		if (nearPeopleData != null) {
		/*	Bundle bundle = new Bundle();
			bundle.putInt("count", nearPeopleData.size());
			for (int i=0; i<nearPeopleData.size(); i++) {
				bundle.putSerializable("np" + i, nearPeopleData.get(i));
			}*/
			intent.putExtra("nearPeopleList", (Serializable)nearPeopleData);
			startActivity(intent);
		}
		
	}

	private void getNearPeople() {
		if (checkNet()) {
			getNearPeopleTask();
			showProgressDialog(R.string.near_people_get_ing);
		} else {
			showToast(Toast.LENGTH_SHORT, R.string.net_error);
		}
	}

	private void getNearPeopleTask() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject json = new JSONObject();
					json.put("pageNo", pageNo++);
					json.put("account", user.getAccount());
					LogUtils.d(TAG, "getDataTask-->send json: " + json.toString());
					HttpUtils.httpPostMethod(ConstantUtils.GET_NEAR_PEOPLE_IP, json.toString(), getNearPeopleHandler);

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

	@Override
	public boolean onRefreshOrMore(DynamicListView dynamicListView,
			boolean isRefresh) {
		if (isRefresh) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Message message = new Message();
					message.what = 0;
					handler.sendMessage(message);
				}
			}).start();
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// 加载更多
					getNearPeople();
				}
			}).start();
		}
		return false;
	}

}
