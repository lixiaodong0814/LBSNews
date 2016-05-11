package com.lbsnews.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lbsnews.R;
import com.lbsnews.base.BaseActivity;
import com.lbsnews.ui.user.MyCollectActivity;
import com.lbsnews.ui.user.MyNewsActivity;
import com.lbsnews.ui.user.UserInfoActivity;
import com.lbsnews.utils.LogUtils;

public class SettingActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "**********SettingActivity************";

	private View selfInfoView;
	private View myFavoriteView;
	private View myNewsView;
	private View aboutView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		initView();

	}

	private void initView() {
		initTitle();
		titleText.setText(R.string.setting_activity);

		selfInfoView = (View)findViewById(R.id.selfInfoImageView);
		myFavoriteView = (View)findViewById(R.id.myFavoriteImageView);
		myNewsView = (View)findViewById(R.id.myNewsImageView);
		aboutView = (View)findViewById(R.id.aboutImageView);

		selfInfoView.setOnClickListener(this);
		myFavoriteView.setOnClickListener(this);
		myNewsView.setOnClickListener(this);
		aboutView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		Intent intent;
		switch (view.getId()) {
		case R.id.selfInfoImageView:
			LogUtils.i(TAG, "click selfInfo");
			showToast(Toast.LENGTH_SHORT, "click info");
			intent = new Intent(SettingActivity.this, UserInfoActivity.class);
			startActivity(intent);
			break;
		case R.id.myFavoriteImageView:
			intent = new Intent(SettingActivity.this, MyCollectActivity.class);
			startActivity(intent);
			break;
		case R.id.myNewsImageView:
			intent = new Intent(SettingActivity.this, MyNewsActivity.class);
			startActivity(intent);
			break;
		case R.id.aboutImageView:
	//		intent = new Intent(SettingActivity.this, About.class);
	//		startActivity(intent);
			break;
		default:
			break;
		}

	}

}
