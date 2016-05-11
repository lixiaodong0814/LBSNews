package com.lbsnews.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.lbsnews.R;
import com.lbsnews.base.BaseActivity;
import com.lbsnews.bean.News;
import com.lbsnews.bean.User;
import com.lbsnews.ui.CameralActivity.IMGCallBack;
import com.lbsnews.ui.PhotoAct.IMGCallBack1;
import com.lbsnews.utils.ConstantUtils;
import com.lbsnews.utils.HttpUtils;
import com.lbsnews.utils.LocationUtils;
import com.lbsnews.utils.LogUtils;
import com.lbsnews.utils.MediaUtils;
import com.lbsnews.utils.TimeUtils;

public class SendNewsActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "*******SendNewsActivity";

	private EditText textNewsEditText;
	private ImageView albumNewsImageView;
	private ImageView cameraNewsImageView;
	private ImageView picShowImageView;
	private TextView myLocationTextView;
	private String textNews;
	private News news;
	private User user;
	private Handler sendNewsHandler;
	private Bitmap bitmap;
	private static INewsCallBack insertNewsCallBack;
	private LocationClient mLocationClient;
	private MyLocationListener mMyLocationListener;
	private boolean isFirstIn = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_send);
		initMyLocation();
		initView();
		initSendNewsHandler();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (!mLocationClient.isStarted())
			mLocationClient.start();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mLocationClient.stop();
	}

	private void initView() {
		initTitle();

		titleLeftBtn.setText(R.string.send_news_cancel);
		titleLeftBtn.setOnClickListener(this);

		titleText.setText(R.string.send_news_activity);

		titleRightBtn.setText(R.string.send_news_send);
		titleRightBtn.setOnClickListener(this);

		textNewsEditText = (EditText)findViewById(R.id.textNewsEditText);
		albumNewsImageView = (ImageView)findViewById(R.id.albumNewsImageView);
		albumNewsImageView.setOnClickListener(this);

		myLocationTextView = (TextView)findViewById(R.id.myLocationTextView);

		cameraNewsImageView = (ImageView)findViewById(R.id.cameraNewsImageView);
		cameraNewsImageView.setOnClickListener(this);
		

		picShowImageView = (ImageView)findViewById(R.id.picShowImageView);

		CameralActivity.setIMGcallback(new IMGCallBack() {

			@Override
			public void callback(Bitmap data) {
				SendNewsActivity.this.bitmap = data;
			}
		});

		PhotoAct.setIMGcallback(new IMGCallBack1() {

			@Override
			public void callback(Bitmap data) {
				SendNewsActivity.this.bitmap = data;
			}
		});

		user = getLocalUser();
		news = new News();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (bitmap != null) {
			showPic(bitmap);
		}
	}

	private void initSendNewsHandler() {
		sendNewsHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					try {
						String response = msg.getData().getString("res");
						LogUtils.d(TAG, "进入获取信息反馈handler: " + response);
						JSONObject result = new JSONObject(response);
						int id = Integer.parseInt(result.getString("id"));
						if (id > 0) {
							//	Gson gson = new Gson();
							//	news = gson.fromJson(response, News.class);
							news.setId(id);
							news.setLike(false);
							news.setCollect(false);
							news.setAccountNickName(user.getNickName());
							news.setAccountPic(user.getHeadPicStr());

							dismissDialog();
							new directToNewsThread().start();
						} else {
							dismissDialog();
							showToast(Toast.LENGTH_SHORT, R.string.send_news_fail);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default :
					showToast(Toast.LENGTH_SHORT, R.string.send_news_fail);
					break;
				}
			}
		};
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_left_btn:
			cancelSendNews();
			break;
		case R.id.title_right_btn:
			sendNews();
			break;
		case R.id.albumNewsImageView:
			selectAlbumPic();
			break;
		case R.id.cameraNewsImageView:
			takePic();
			break;
		default :
			break;
		}
	}

	private void takePic() {
		Intent intent = new Intent(SendNewsActivity.this, CameralActivity.class);
		startActivity(intent);

	}

	private void selectAlbumPic() {
		Intent intent = new Intent(SendNewsActivity.this, PhotoAct.class);
		startActivity(intent);

	}

	private void showPic(Bitmap bitmap) {
		picShowImageView.setImageDrawable(MediaUtils.BitmapToDrawble(bitmap));
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

	private void sendNews() {
		textNews = textNewsEditText.getText().toString();
		if (textNews.equals("") || textNews.length() == 0) {
			showToast(Toast.LENGTH_SHORT, R.string.send_news_text_is_null);
		}  else {
			news.setTextContent(MediaUtils.encodeString(textNews));
			news.setNewsAccount(user.getAccount());
			if (this.bitmap != null) {
				news.setPicContent(MediaUtils.bitmapToString(bitmap));
			}
			news.setPublishTime(TimeUtils.getCurrentTime());
			if (this.checkNet()) {
				sendNewsTask(news);
				showProgressDialog(R.string.send_news_ing);
			} else {
				showToast(Toast.LENGTH_SHORT, R.string.net_error);
			}
		}

	}

	private void sendNewsTask(final News news) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject json = new JSONObject();
					Gson gson = new Gson();
					json.put("news", gson.toJson(news));
					HttpUtils.httpPostMethod(ConstantUtils.SEND_NWES_IP, json.toString(), sendNewsHandler);

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

	private void cancelSendNews() {
		Intent intent = new Intent(SendNewsActivity.this, MainTabActivity.class);
		startActivity(intent);
		finish();
	}

	private class directToNewsThread extends Thread {
		public void run() {
			insertNewsCallBack.callback(news);
			SendNewsActivity.this.finish();
		};

	}

	public static void getInertNewsCallBack(INewsCallBack insertNewsCallBack){
		SendNewsActivity.insertNewsCallBack = insertNewsCallBack;
	}

	public interface INewsCallBack {
		public void callback(News news);
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
		news.setMyAddress(MediaUtils.encodeString(address));
		news.setLatitude(latitude);
		news.setLongitude(longitude);
		if (isFirstIn) {
			showToast(Toast.LENGTH_SHORT, address + "\nlatitide: " + latitude + "\nlongitude: " + longitude);
			isFirstIn = false;
		}
		myLocationTextView.setText(address);
	}

}	
