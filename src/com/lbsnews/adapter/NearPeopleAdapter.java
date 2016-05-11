package com.lbsnews.adapter;

import java.util.List;

import com.lbsnews.R;
import com.lbsnews.adapter.NewsAdapter.ViewHolder;
import com.lbsnews.bean.NearPeople;
import com.lbsnews.bean.News;
import com.lbsnews.utils.LocationUtils;
import com.lbsnews.utils.MediaUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NearPeopleAdapter extends BaseAdapter {
	private static final String TAG = "******NewsAdapter*****";

	private LayoutInflater mInflater = null;
	private Context context = null;
	private List<NearPeople> data = null;
	private double latitude;
	private double longitude;
	private double userLatitude;
	private double userLongitude;

	public NearPeopleAdapter(Context context, List<NearPeople> data) {
		this.context = context;
		this.data = data;
		this.mInflater = LayoutInflater.from(context);
		initMyLocation();
	}
	
	private void initMyLocation() {
		SharedPreferences pref = context.getSharedPreferences("data", android.content.Context.MODE_PRIVATE);
		userLatitude = Double.parseDouble(pref.getString("latitude", "0.0"));
		userLongitude = Double.parseDouble(pref.getString("longitude", "0.0"));
	}

	static class ViewHolder {
		public ImageView userPicImageViewNearPeople;
		public TextView nickNameTextViewNearPeople;
		public TextView distanceTextViewNearPeople;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.nearpeople_item, null);
			holder.userPicImageViewNearPeople = (ImageView)convertView.findViewById(R.id.userPicImageViewNearPeople);
			holder.nickNameTextViewNearPeople = (TextView)convertView.findViewById(R.id.nickNameTextViewNearPeople);
			holder.distanceTextViewNearPeople = (TextView)convertView.findViewById(R.id.distanceTextViewNearPeople);
			
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		if (data != null) {
			if (data.get(position).getUserPicStr() != null) {
				holder.userPicImageViewNearPeople.setImageBitmap(MediaUtils.stringToBitmap(data.get(position).getUserPicStr()));
			} else {
				holder.userPicImageViewNearPeople.setImageBitmap(null);
			}
			if (data.get(position).getNickName() != null) {
				holder.nickNameTextViewNearPeople.setText(MediaUtils.decodeString(data.get(position).getNickName()));
			} else {
				holder.nickNameTextViewNearPeople.setText("Œ¥…Ë÷√Í«≥∆");
			}
			
			latitude = data.get(position).getLatitude();
			longitude = data.get(position).getLongitude();
			holder.distanceTextViewNearPeople.setText(LocationUtils.getDistance(latitude, longitude, userLatitude, userLongitude));
		}

		return convertView;
	}

}
