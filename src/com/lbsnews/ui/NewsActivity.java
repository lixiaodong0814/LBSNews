package com.lbsnews.ui;

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

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiConfiguration.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbsnews.R;
import com.lbsnews.adapter.NewsAdapter;
import com.lbsnews.base.BaseActivity;
import com.lbsnews.base.DynamicListView;
import com.lbsnews.base.DynamicListView.DynamicListViewListener;
import com.lbsnews.base.MyTabListener;
import com.lbsnews.bean.News;
import com.lbsnews.bean.User;
import com.lbsnews.ui.SendNewsActivity.INewsCallBack;
import com.lbsnews.utils.ConstantUtils;
import com.lbsnews.utils.HttpUtils;
import com.lbsnews.utils.LogUtils;



public class NewsActivity extends BaseActivity implements OnClickListener, DynamicListViewListener {
	private static final String TAG = "NewsActivity";

	//action导航栏
	private static final int ALL_TAB = 0X0001;
	private static final int TEXT_TAB = 0x0002;
	private static final int PIC_TAB = 0x0003;
	private static final int VIDEO_TAB = 0X004;

	private static int allPageNo = 1;
	private static int textPageNo = 1;
	private static int picPageNo = 1;
	private static int videoPageNo = 1;

	private static boolean initStatus = true;
	private static String type = "all";

	private DynamicListView contentListView;
	private NewsAdapter newsAdapter;
	private List<News> newsData;
	private Handler newsHandler = null;
	private List<News> newsList = null;
	private News insertNews = null;
	private User user;


	// 用于刷新控件状态
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				newsAdapter.notifyDataSetChanged();
				contentListView.doneRefresh();
			} else if (msg.what == 1) {
				newsAdapter.notifyDataSetChanged();
				contentListView.doneMore();
			} else {
				super.handleMessage(msg);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_main);
		initNewsHandler();
		initView();

	}


	private void initView() {
		initTitle();
		titleText.setText(R.string.news_activity);

		titleRightBtn.setText(R.string.news_send);
		titleRightBtn.setOnClickListener(this);

		titleLeftBtn.setText(R.string.news_refresh);
		titleLeftBtn.setOnClickListener(this);

		contentListView = (DynamicListView)findViewById(R.id.contentListView);
		contentListView.setDoMoreWhenBottom(false);	// 滚动到低端的时候不自己加载更多
		contentListView.setOnRefreshListener(this);
		contentListView.setOnMoreListener(this);

		user = getLocalUser();
		initData();

		SendNewsActivity.getInertNewsCallBack(new INewsCallBack() {

			@Override
			public void callback(News news) {
				NewsActivity.this.insertNews = news;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (insertNews != null) {
			synchronized (newsData) {
				newsData.add(0,insertNews);
			}
			Message message = new Message();
			message.what = 0;
			handler.sendMessage(message);
		}
	}


	private void initNewsHandler() {

		newsHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					try {
						String response = msg.getData().getString("res");
						JSONObject result = new JSONObject(response);
						int sucess = Integer.parseInt(result.getString("success"));
						if (sucess == 0) {
							LogUtils.d(TAG, "newsList: " + result.getString("newsList"));
							Gson gson = new Gson();
							newsList = gson.fromJson(result.getString("newsList"), new TypeToken<List<News>>(){}.getType());
							if (newsList != null) {
								showToast(Toast.LENGTH_SHORT, R.string.news_get_news_success);
								synchronized (newsData) {
									newsData.addAll(newsList);
								}
							} else {
								showToast(Toast.LENGTH_SHORT, R.string.news_get_news_no_more_content);
							}

							if (initStatus) {
								newsAdapter = new NewsAdapter(NewsActivity.this, newsData);
								contentListView.setAdapter(newsAdapter);
								dismissDialog();
								initStatus = false;
							} else {
								//加载更多
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
							}

						} else {
							if (initStatus) {
								dismissDialog();
								initStatus = false;
							}

							showToast(Toast.LENGTH_SHORT, R.string.news_get_news_fail);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				default :
					showToast(Toast.LENGTH_SHORT, R.string.news_get_news_fail);
					break;
				}
			}

		};
	}

	private void initData() {
		if (initStatus) {
			newsData = new ArrayList<News>();
			getData();

		}

	}

	private void getDataTask(final String type, final int pageNo) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject json = new JSONObject();
					json.put("type", type);
					json.put("pageNo", pageNo);
					json.put("likeAccount", user.getAccount());
					LogUtils.d(TAG, "getDataTask-->send json: " + json.toString());
					HttpUtils.httpPostMethod(ConstantUtils.GET_NEWS_IP, json.toString(), newsHandler);

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
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_right_btn:
			sendNews();
			break;
		case R.id.title_left_btn:
			//	getData();
			break;
		default :
			break;
		}
	}

	private void getData() {
		if (this.checkNet()) {
			/*	if (initStatus == ALL_TAB) {
					type = "all";
					getDataTask(type, allPageNo++);
				} else if (initStatus == TEXT_TAB) {
					type="text";
					getDataTask(type, textPageNo++);
				} else if (initStatus == PIC_TAB) {
					type = "pic";
					getDataTask(type, picPageNo++);
				}*/
			if (initStatus) {
				getDataTask(type, allPageNo);
				showProgressDialog(R.string.news_get_news_ing);
			}
		} else {
			showToast(Toast.LENGTH_SHORT, R.string.net_error);
		}

	}

	private void sendNews() {
		Intent intent = new Intent(NewsActivity.this, SendNewsActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onRefreshOrMore(DynamicListView dynamicListView,
			boolean isRefresh) {
		if (isRefresh) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// 刷新
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
					getData();
				}
			}).start();
		}
		return false;
	}

}
