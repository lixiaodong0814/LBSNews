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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbsnews.R;
import com.lbsnews.adapter.CommentAdapter;
import com.lbsnews.base.BaseActivity;
import com.lbsnews.base.DynamicListView;
import com.lbsnews.base.DynamicListView.DynamicListViewListener;
import com.lbsnews.bean.Comment;
import com.lbsnews.bean.User;
import com.lbsnews.utils.ConstantUtils;
import com.lbsnews.utils.HttpUtils;
import com.lbsnews.utils.LogUtils;
import com.lbsnews.utils.MediaUtils;
import com.lbsnews.utils.TimeUtils;

public class CommentActivity extends BaseActivity implements DynamicListViewListener {
	private static final String TAG = "****CommentActivity***";

	private DynamicListView commentListView;
	private CommentAdapter commentAdapter;
	private List<Comment> commentData;
	private List<Comment> commentList;
	private Handler getCommentHandler = null;
	private Handler sendCommentHandler = null;
	private User user;
	private int newsId;
	private Comment sendComment;
	private static int pageNo = 1;
	private Comment comment;
	private boolean initStatus = true;

	// 用于刷新控件状态
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				commentAdapter.notifyDataSetChanged();
				commentListView.doneRefresh();
			} else if (msg.what == 1) {
				commentAdapter.notifyDataSetChanged();
				commentListView.doneMore();
			} else {
				super.handleMessage(msg);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_main);

		initNewsId();

		initView();
	}

	private void initNewsId() {
		SharedPreferences pref = getSharedPreferences("comment", MODE_PRIVATE);
		newsId = pref.getInt("nid", 1);
		//	Intent newsIntent = getIntent();
		//	String nid = newsIntent.getExtras().getString("nid");
		//	newsId = Integer.parseInt(nid);
	}

	private void initView() {

		initCommentListView();

		initTitle();
		titleText.setText(R.string.comment_activity);
		titleLeftBtn.setText(R.string.comment_back);
		titleRightBtn.setText(R.string.comment_comment);
		titleLeftBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//		returnToMain();
			}
		});
		titleRightBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				addCommentDialog();
			}
		});

		user = getLocalUser();
		initGetCommentHandlerHandler();
		initSendCommentHandler();
		initData();
		
	}

	private void initCommentListView() {
		commentListView = (DynamicListView)findViewById(R.id.commentListView);
		commentListView.setDoMoreWhenBottom(false);	// 滚动到低端的时候不自己加载更多
		commentListView.setOnRefreshListener(this);
		commentListView.setOnMoreListener(this);
	}

	private void initGetCommentHandlerHandler() {
		getCommentHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					try {
						String response = msg.getData().getString("res");
						LogUtils.d(TAG, "进入获取信息反馈handler: " + response);
						JSONObject json = new JSONObject(response);
						int success = Integer.parseInt(json.getString("success"));
						if (success == 0) {
							Gson gson = new Gson();
							commentList = new ArrayList<Comment>();
							commentList = gson.fromJson(json.getString("commentList"),new TypeToken<List<Comment>>(){}.getType());
							if (commentData != null) {
								showToast(Toast.LENGTH_SHORT, R.string.comment_get_success);
								synchronized (commentData) {
									commentData.addAll(commentList);
								}
								
							} else {
								showToast(Toast.LENGTH_SHORT, R.string.comment_get_no_more_content);
							}
							if (initStatus) {
								commentAdapter = new CommentAdapter(CommentActivity.this, commentData);
								commentListView.setAdapter(commentAdapter);
								dismissDialog();
								initStatus = false;
							} else {
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
							}
							
						} else {
							if (initStatus) {
								dismissDialog();
								initStatus = false;
							}
							showToast(Toast.LENGTH_SHORT, R.string.comment_get_fail);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default :
					showToast(Toast.LENGTH_SHORT, R.string.comment_get_fail);
					break;
				}
			}
		};
	}

	private void initSendCommentHandler() {
		sendCommentHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					try {
						String response = msg.getData().getString("res");
						LogUtils.d(TAG, "进入获取信息反馈handler: " + response);
						JSONObject result = new JSONObject(response);
						int success = Integer.parseInt(result.getString("success"));

						if (success == 0) {
							dismissDialog();
							showToast(Toast.LENGTH_SHORT, R.string.comment_send_success);
							sendComment.setCommentAccountPicStr(user.getHeadPicStr());
							sendComment.setCommentNickName(user.getNickName());
							//setData(commentList);
							synchronized (commentData) {
								commentData.add(0, sendComment);
							}
							Message message = new Message();
							message.what = 0;
							message.arg1 = 1;
							handler.sendMessage(message);
						} else {
							dismissDialog();
							showToast(Toast.LENGTH_SHORT, R.string.comment_send_fail);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default :
					showToast(Toast.LENGTH_SHORT, R.string.comment_send_fail);
					break;
				}
			}
		};
	}

	private void initData() {
		if (initStatus) {
			commentData = new ArrayList<Comment>();
			getData();
		}
		
	}

	private void getData() {
		if (this.checkNet()) {
			getDataTask();
			if (initStatus) {
				showProgressDialog(R.string.comment_get_ing);
			}
		} else {
			showToast(Toast.LENGTH_SHORT, R.string.net_error);
		}

	}

	private void getDataTask() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject json = new JSONObject();
					json.put("newsId", newsId);
					json.put("pageNo", pageNo++);
					LogUtils.d(TAG, "getDataTask-->send json: " + json.toString());
					HttpUtils.httpPostMethod(ConstantUtils.GET_COMMENTS_IP, json.toString(), getCommentHandler);

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

	private void addCommentDialog() {
		final EditText commentEditText = new EditText(this); 
		new AlertDialog.Builder(this).setTitle(R.string.comment_activity)
		.setView(commentEditText)
		.setPositiveButton(R.string.comment_ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String input = commentEditText.getText().toString();
				if (input.equals("")) {
					showToast(Toast.LENGTH_SHORT, R.string.comment_detail_content_is_null);
				} else {
					sendComment = new Comment();
					sendComment.setCommentAccount(user.getAccount());
					sendComment.setCommentContent(MediaUtils.encodeString(input));
					sendComment.setCommentTime(TimeUtils.getCurrentTime());
					sendComment.setNewsId(newsId);
					sendComment(sendComment);
				}
			}
		})
		.setNegativeButton(R.string.comment_cancel, null)
		.show();

	}

	private void returnToMain() {
		Intent intent = new Intent(CommentActivity.this, MainTabActivity.class);
		startActivity(intent);
	}

	private void sendComment(Comment comment) {
		if (checkNet()) {
			sendCommentTask(comment);
			showProgressDialog(R.string.comment_send_ing);
		} else {
			showProgressDialog(R.string.net_error);
		}

	}

	private void sendCommentTask(final Comment comment) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject json = new JSONObject();
					Gson gson = new Gson();
					json.put("comment", gson.toJson(comment));
					LogUtils.d(TAG, "sendCommentTask-->send json: " + json.toString());
					HttpUtils.httpPostMethod(ConstantUtils.SEND_COMMENTS_IP, json.toString(), sendCommentHandler);

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
					// 刷新
					Message message = new Message();
					message.what = 0;
					message.arg1 = 0;
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
