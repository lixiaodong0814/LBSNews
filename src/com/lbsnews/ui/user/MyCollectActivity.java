package com.lbsnews.ui.user;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbsnews.R;
import com.lbsnews.adapter.MyCollectAdapter;
import com.lbsnews.adapter.MyNewsAdapter;
import com.lbsnews.base.BaseActivity;
import com.lbsnews.base.DynamicListView;
import com.lbsnews.base.DynamicListView.DynamicListViewListener;
import com.lbsnews.bean.MyCollect;
import com.lbsnews.bean.MyNews;
import com.lbsnews.bean.User;
import com.lbsnews.utils.ConstantUtils;
import com.lbsnews.utils.HttpUtils;
import com.lbsnews.utils.LogUtils;

public class MyCollectActivity extends BaseActivity implements
DynamicListViewListener {
	private static final String TAG = "****MyCollectActivity****";

	private DynamicListView myCollectListView;
	private MyCollectAdapter myCollectAdapter;
	private List<MyCollect> myCollectdata;
	private List<MyCollect> myCollectList;
	private User user;
	private static int pageNo = 1;
	private static boolean initStatus = true;
	
	private Handler getMyCollectHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				try {
					String response = msg.getData().getString("res");
					JSONObject result = new JSONObject(response);
					int sucess = Integer.parseInt(result.getString("success"));
					if (sucess == 0) {
						myCollectList = new ArrayList<MyCollect>();
						Gson gson = new Gson();
						myCollectList = gson.fromJson(result.getString("myCollect"), new TypeToken<List<MyCollect>>(){}.getType());

						if (! myCollectList.equals("")) {
							showToast(Toast.LENGTH_SHORT, R.string.mycollect_get_success);
							synchronized (myCollectdata) {
								myCollectdata.addAll(myCollectList);
							}
						} else {
							showToast(Toast.LENGTH_SHORT, R.string.mycollect_get_no_more_content);
						}

						if (initStatus) {
							//初始化
							myCollectAdapter = new MyCollectAdapter(MyCollectActivity.this, myCollectdata);
							myCollectListView.setAdapter(myCollectAdapter);
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
						showToast(Toast.LENGTH_SHORT, R.string.mycollect_get_fail);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			default :
				showToast(Toast.LENGTH_SHORT, R.string.mycollect_get_fail);
				break;
			}
		};
	};

	// 用于刷新控件状态
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				myCollectAdapter.notifyDataSetChanged();
				myCollectListView.doneRefresh();
			} else if (msg.what == 1) {
				myCollectAdapter.notifyDataSetChanged();
				myCollectListView.doneMore();
			} else {
				super.handleMessage(msg);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mycollect_main);
		initView();
	}

	private void initView() {
		initTitle();
		titleLeftBtn.setText(R.string.mycollect_back);
		titleText.setText(R.string.mycollect_activity);
		
		titleLeftBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
			}
		});
		
		myCollectListView = (DynamicListView)findViewById(R.id.myCollectListView);
		myCollectListView.setDoMoreWhenBottom(false);	// 滚动到低端的时候不自己加载更多
		myCollectListView.setOnRefreshListener(this);
		myCollectListView.setOnMoreListener(this);

		user = getLocalUser();
		initData();
	}

	private void initData() {
		if (initStatus) {
			myCollectdata = new ArrayList<MyCollect>();
			getMyCollect();
		}
	}

	private void getMyCollect() {
		if (checkNet()) {
			getMyCollectTask();
			if (initStatus) {
				showProgressDialog(R.string.mycollect_get_ing);
			}
		} else {
			showToast(Toast.LENGTH_SHORT, R.string.net_error);
		}
	}

	private void getMyCollectTask() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject json = new JSONObject();
					json.put("account", user.getAccount());
					json.put("pageNo", pageNo++);
					LogUtils.d(TAG, "getDataTask-->send json: " + json.toString());
					HttpUtils.httpPostMethod(ConstantUtils.GET_MY_COLLECT_IP, json.toString(), getMyCollectHandler);

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
					getMyCollect();

				}
			}).start();
		}
		return false;
	}

}
