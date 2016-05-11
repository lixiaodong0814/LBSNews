package com.lbsnews.ui;

import com.lbsnews.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.CompoundButton.OnCheckedChangeListener;

@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity implements
OnCheckedChangeListener {
	private TabHost mTabHost;
	private Intent mNearPeopleIntent;
	private Intent mNewsIntent;
	private Intent mSettingIntent;

	private RadioButton btn_people;
	private RadioButton btn_news;
	private RadioButton btn_set;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.maintab);

		this.mNearPeopleIntent = new Intent(this, NearPeopleActivity.class);
		this.mNewsIntent = new Intent(this, NewsActivity.class);
		this.mSettingIntent = new Intent(this, SettingActivity.class);

		btn_people = (RadioButton)findViewById(R.id.radio_button_people);
		btn_news = (RadioButton)findViewById(R.id.radio_button_news);
		btn_set = (RadioButton)findViewById(R.id.radio_button_setting);

		btn_people.setOnCheckedChangeListener(this);
		btn_news.setOnCheckedChangeListener(this);
		btn_set.setOnCheckedChangeListener(this);

		initActivity();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonview, boolean ischecked) {
		if (ischecked) {
			switch (buttonview.getId()) {
			case R.id.radio_button_people:
				this.mTabHost.setCurrentTabByTag("PEOPLE_TAG");
				break;
			case R.id.radio_button_news:
				this.mTabHost.setCurrentTabByTag("NEWS_TAG");
				break;
			case R.id.radio_button_setting:
				this.mTabHost.setCurrentTabByTag("SETTING_TAG");
				break;
			}
		}
	}

	private void initActivity() {
		this.mTabHost = getTabHost();
		TabHost localTabHost = this.mTabHost;
		localTabHost.addTab(buildTabSpec("SETTING_TAG",  
				R.string.main_set, R.drawable.maintab_btn_main,  
				this.mSettingIntent));  
		localTabHost.addTab(buildTabSpec("PEOPLE_TAG", R.string.main_people,  
				R.drawable.maintab_btn_main, this.mNearPeopleIntent));  

		localTabHost.addTab(buildTabSpec("NEWS_TAG", R.string.main_news,  
				R.drawable.maintab_btn_main, this.mNewsIntent));  


	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon, final Intent content) {

		return this.mTabHost.newTabSpec(tag).setIndicator(getString(resLabel),  
				getResources().getDrawable(resIcon)).setContent(content);  
	}
}
