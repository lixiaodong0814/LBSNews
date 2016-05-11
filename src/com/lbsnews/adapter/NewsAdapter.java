package com.lbsnews.adapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbsnews.R;
import com.lbsnews.bean.Collect;
import com.lbsnews.bean.Comment;
import com.lbsnews.bean.Like;
import com.lbsnews.bean.News;
import com.lbsnews.ui.CommentActivity;
import com.lbsnews.ui.NewsActivity;
import com.lbsnews.utils.ConstantUtils;
import com.lbsnews.utils.HttpUtils;
import com.lbsnews.utils.LogUtils;
import com.lbsnews.utils.MediaUtils;
import com.lbsnews.utils.TimeUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NewsAdapter extends BaseAdapter {

	private static final String TAG = "******NewsAdapter*****";
	private LayoutInflater mInflater = null;
	private Context context = null;
	private List<News> data = null;
	private String likeOrCollectAccount = null;
	private static int likePosition = 0;
	private static int collectPosition = 0;

	public NewsAdapter(Context context, List<News> data) {
		this.context = context;
		this.data = data;
		this.mInflater = LayoutInflater.from(context);
		initLikeOrCollectAccount();
	}

	public void initLikeOrCollectAccount() {
		SharedPreferences pref = context.getSharedPreferences("data", android.content.Context.MODE_PRIVATE);
		likeOrCollectAccount = pref.getString("account", "lxd@111.com");
	}

	/**
	 * 检查网络连接状态
	 * @return 网络连接状态
	 */
	protected final boolean checkNet() {
		boolean connected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			if (info.getState() == NetworkInfo.State.CONNECTED) {
				connected = true;
			}
		}

		return connected;
	}

	private void showToast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	private void showToast(int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

	private Handler likeHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				try {
					String response = msg.getData().getString("res");
					LogUtils.d(TAG, "进入获取信息反馈handler: " + response);
					JSONObject json = new JSONObject(response);
					int success = Integer.parseInt(json.getString("success"));
					if (success == 0) {
						showToast("赞+1");
						data.get(likePosition).setLike(true);
						int likeNum = data.get(likePosition).getLikeNum();
						data.get(likePosition).setLikeNum( likeNum + 1);
						notifyDataSetChanged();
					} else {
						showToast("失败，重新尝试");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default :
				showToast(R.string.comment_get_fail);
				break;
			}
		}
	};


	private Handler collectHandler = new Handler() {
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
						showToast("收藏成功");
						data.get(collectPosition).setCollect(true);
						notifyDataSetChanged();
					} else {
						showToast("收藏失败");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default :
				showToast(R.string.comment_get_fail);
				break;
			}
		}
	};
	
	private Handler cancelCollectHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				try {
					String response = msg.getData().getString("res");
					LogUtils.d(TAG, "进入获取信息反馈handler: " + response);
					JSONObject json = new JSONObject(response);
					int success = Integer.parseInt(json.getString("success"));
					if (success == 0) {
						showToast("取消收藏成功");
						data.get(collectPosition).setCollect(false);
						notifyDataSetChanged();
					} else {
						showToast("取消收藏失败");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default :
				showToast(R.string.comment_get_fail);
				break;
			}
		};
	};

	static class ViewHolder {
		public ImageView headPicImageView;
		public TextView nickNameTextView;
		public TextView textContentTextView;
		public ImageView picContentImageView;
		public TextView publishTimeTextView;
		public ImageView likeImageView;
		public ImageView collectImageView;
		public ImageView commentImageView;
		public ImageView shareImageView;
		public TextView likeNumTextView;
		public TextView commentNumTextView;
		public TextView addrressTextViewNews;
		public TextView distanceTextViewNews;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.news_item, null);
			holder.headPicImageView = (ImageView)convertView.findViewById(R.id.userImageViewNews);
			holder.nickNameTextView = (TextView)convertView.findViewById(R.id.nickNameTextViewNews);
			holder.textContentTextView = (TextView)convertView.findViewById(R.id.textContentTextViewNews);
			holder.picContentImageView = (ImageView)convertView.findViewById(R.id.picContentImageViewNews);
			holder.publishTimeTextView = (TextView)convertView.findViewById(R.id.publishTimeTextviewNews);
			holder.likeImageView = (ImageView)convertView.findViewById(R.id.likeImageViewNews);
			holder.collectImageView = (ImageView)convertView.findViewById(R.id.collectImageViewNews);
			holder.commentImageView = (ImageView)convertView.findViewById(R.id.commentImageViewNews);
			holder.shareImageView = (ImageView)convertView.findViewById(R.id.shareImageViewNews);
			holder.likeNumTextView = (TextView)convertView.findViewById(R.id.likeNumTextViewNews);
			holder.commentNumTextView = (TextView)convertView.findViewById(R.id.commentNumTextViewNews);
			holder.addrressTextViewNews = (TextView)convertView.findViewById(R.id.addrressTextViewNews);
			holder.distanceTextViewNews = (TextView)convertView.findViewById(R.id.distanceTextviewNews);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}

		if (data != null) {
			if (data.get(position).getAccountPic() != null) {
				holder.headPicImageView.setImageBitmap(MediaUtils.stringToBitmap(data.get(position).getAccountPic()));
			} else {
				holder.headPicImageView.setImageBitmap(null);
			}
			if(data.get(position).getAccountNickName() != null) {
				holder.nickNameTextView.setText(MediaUtils.decodeString(data.get(position).getAccountNickName()));
			} else {
				holder.nickNameTextView.setText("您还没有设置昵称");
			}
			holder.textContentTextView.setText(MediaUtils.decodeString(data.get(position).getTextContent()));
			if (data.get(position).getPicContent() != null) {
				holder.picContentImageView.setImageBitmap(MediaUtils.stringToBitmap(data.get(position).getPicContent()));
			} else {
				holder.picContentImageView.setVisibility(View.GONE);
			}
			holder.publishTimeTextView.setText(TimeUtils.DateToString(data.get(position).getPublishTime()));

			if (data.get(position).isLike()) {
				holder.likeImageView.setImageResource(R.drawable.ic_like_choose);
			}
			int likeNum = data.get(position).getLikeNum();
			holder.likeNumTextView.setText(String.valueOf(likeNum));
			if (data.get(position).isCollect()) {
				holder.collectImageView.setImageResource(R.drawable.ic_collect_choose);
			}  else {
				holder.collectImageView.setImageResource(R.drawable.ic_collect_normal);
			}
			int commentNum = data.get(position).getCommentNum();
			holder.commentNumTextView.setText(String.valueOf(commentNum));
			holder.addrressTextViewNews.setText(MediaUtils.decodeString(data.get(position).getMyAddress()));
			holder.distanceTextViewNews.setText("200米");
		}
		
		

		holder.likeImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (data.get(position).isLike()) {
					showToast("您已经赞过了");
				} else {
					Like like = new Like();
					like.setLikeAccount(likeOrCollectAccount);
					like.setNid(data.get(position).getId());
					like.setNewsAccount(data.get(position).getNewsAccount());
					if (checkNet()) {
						likePosition = position;
						likeTask(like);
					} else {
						showToast(R.string.net_error);
					}

				}

			}
		});
		holder.collectImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				Collect collect = new Collect();
				collect.setNid(data.get(position).getId());
				collect.setNewsAccount(data.get(position).getNewsAccount());
				collect.setCollectAccount(likeOrCollectAccount);
				if (checkNet()) {
					collectPosition = position;
					if (data.get(position).isCollect()) {
						cancelCollectTask(collect);
					} else {
						//没有收藏，调用收藏线程
						collectTask(collect);
					}

				} else {
					showToast(R.string.net_error);
				}


			}
		});
		holder.commentImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String account = (String)data.get(position).getNewsAccount();
				int id = (Integer)data.get(position).getId();
				commentTask(account, id);
			}
		});
		holder.shareImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String account = (String)data.get(position).getNewsAccount();
				int id = (Integer)data.get(position).getId();
				shareTask(account, id);
			}
		});

		return convertView;
	}

	public void setData(List<News> data) {
		this.data = data;
	}

	private void likeTask(final Like like) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject json = new JSONObject();
					Gson gson = new Gson();
					json.put("like", gson.toJson(like));
					LogUtils.d(TAG, "collect(String account, int id)-->send json: " + json.toString());
					HttpUtils.httpPostMethod(ConstantUtils.CLICK_LIKE_IP, json.toString(), likeHandler);

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

	private void commentTask(String account, int id) {
		SharedPreferences.Editor editor = context.getSharedPreferences("comment", android.content.Context.MODE_PRIVATE).edit();
		editor.putInt("nid", id);
		editor.commit();
		Intent intent = new Intent(context, CommentActivity.class);
		context.startActivity(intent);
	}

	private void collectTask(final Collect collect) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject json = new JSONObject();
					Gson gson = new Gson();
					json.put("collect", gson.toJson(collect));
					LogUtils.d(TAG, "collectTask(final Collect collect)-->send json: " + json.toString());

					HttpUtils.httpPostMethod(ConstantUtils.COLLECT_NEWS_IP, json.toString(), collectHandler);

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

	private void cancelCollectTask(final Collect collect) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject json = new JSONObject();
					Gson gson = new Gson();
					json.put("collect", gson.toJson(collect));
					LogUtils.d(TAG, "cancelCollectTask(final Collect collect)-->send json: " + json.toString());

					HttpUtils.httpPostMethod(ConstantUtils.CANCEL_COLLECT_IP, json.toString(), cancelCollectHandler);

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

	private void shareTask(String account, int id) {

	}

}
