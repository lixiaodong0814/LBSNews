package com.lbsnews.ui.user;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbsnews.R;
import com.lbsnews.adapter.MyNewsAdapter;
import com.lbsnews.base.BaseActivity;
import com.lbsnews.base.DynamicListView;
import com.lbsnews.base.DynamicListView.DynamicListViewListener;
import com.lbsnews.bean.MyNews;
import com.lbsnews.bean.User;
import com.lbsnews.ui.MainTabActivity;
import com.lbsnews.utils.ConstantUtils;
import com.lbsnews.utils.HttpUtils;
import com.lbsnews.utils.LogUtils;
import com.lbsnews.utils.MediaUtils;
import com.lbsnews.utils.TimeUtils;

public class MyNewsActivity extends BaseActivity implements DynamicListViewListener {
	private static final String TAG = "*****MyNewsActivity********";

	private DynamicListView myNewsListView;
	private MyNewsAdapter myNewsAdapter;
	private List<MyNews> myNewsdata;
	private Handler getMyNewsHandler;
	private List<MyNews> myNewsList;
	private User user;
	private static int pageNo = 1;
	private static boolean initStatus = true;

	// 用于刷新控件状态
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				myNewsAdapter.notifyDataSetChanged();
				myNewsListView.doneRefresh();
			} else if (msg.what == 1) {
				myNewsAdapter.notifyDataSetChanged();
				myNewsListView.doneMore();
			} else {
				super.handleMessage(msg);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mynews_main);
		initGetMyNewsHandler();
		initView();
	}

	private void initGetMyNewsHandler() {
		getMyNewsHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					try {
						String response = msg.getData().getString("res");
						JSONObject result = new JSONObject(response);
						int sucess = Integer.parseInt(result.getString("success"));
						if (sucess == 0) {
							myNewsList = new ArrayList<MyNews>();
							Gson gson = new Gson();
							myNewsList = gson.fromJson(result.getString("myNews"), new TypeToken<List<MyNews>>(){}.getType());

							if (myNewsList != null) {
								showToast(Toast.LENGTH_SHORT, R.string.mynews_get_success);
								synchronized (myNewsdata) {
									myNewsdata.addAll(myNewsList);
								}
							} else {
								showToast(Toast.LENGTH_SHORT, R.string.mynews_get_no_more_content);
							}

							if (initStatus) {
								//初始化
								myNewsAdapter = new MyNewsAdapter(MyNewsActivity.this, myNewsdata);
								myNewsListView.setAdapter(myNewsAdapter);
								dismissDialog();
								initStatus = false;
							} else {
								//加载
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
							}


						} else {
							if (initStatus) {
								dismissDialog();
								initStatus = false;
							}
							showToast(Toast.LENGTH_SHORT, R.string.mynews_get_fail);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				default :
					showToast(Toast.LENGTH_SHORT, R.string.mynews_get_fail);
					break;
				}
			}
		};
	}

	private void initView() {
		initTitle();
		titleLeftBtn.setText(R.string.mynews_back);
		//	titleRightBtn.setVisibility(View.INVISIBLE);
		titleRightBtn.setText(R.string.mynews_load);
		titleText.setText(R.string.mynews_activity);
		titleLeftBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MyNewsActivity.this, MainTabActivity.class);
				startActivity(intent);
			//	finish();
			}
		});
		titleRightBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
		//		getMyNews();
			}
		});
		myNewsListView = (DynamicListView)findViewById(R.id.myNewsListView);
		myNewsListView.setDoMoreWhenBottom(false);	// 滚动到低端的时候不自己加载更多
		myNewsListView.setOnRefreshListener(this);
		myNewsListView.setOnMoreListener(this);
		user = getLocalUser();
		initData();
	}

	private void initData() {
		if (initStatus) {
			myNewsdata = new ArrayList<MyNews>();
			getMyNews();
		}

	}

	private void getMyNews() {
		if (checkNet()) {
			getMyNewsTask();
			if (initStatus) {
				showProgressDialog(R.string.mynews_get_ing);
			}
		} else {
			showToast(Toast.LENGTH_SHORT, R.string.net_error);
		}

	}

	private void getMyNewsTask() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject json = new JSONObject();
					json.put("type", "self");
					json.put("account", user.getAccount());
					json.put("pageNo", pageNo++);
					LogUtils.d(TAG, "getDataTask-->send json: " + json.toString());
					HttpUtils.httpPostMethod(ConstantUtils.GET_MY_NEWS_IP, json.toString(), getMyNewsHandler);

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
					getMyNews();

				}
			}).start();
		}
		return false;
	}

}
