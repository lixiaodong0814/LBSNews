package com.lbsnews.adapter;

import java.util.List;

import com.lbsnews.R;
import com.lbsnews.bean.MyCollect;
import com.lbsnews.utils.MediaUtils;
import com.lbsnews.utils.TimeUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyCollectAdapter extends BaseAdapter {
	private static final String TAG = "******MyCollectAdapter*****";
	private LayoutInflater mInflater = null;
	private Context context = null;
	private List<MyCollect> data;
	
	public MyCollectAdapter(Context context2, List<MyCollect> data) {
		this.context = context2;
		this.data = data;
		this.mInflater = LayoutInflater.from(context);
	}
	
	private static class ViewHolder {
		public ImageView userPicImageViewMyCollect;
		public TextView nickNameTextViewMyCollect;
		public TextView publishTimeTextViewMyCollect;
		public TextView textContentTextViewMyCollect;
		public ImageView picContentImageViewMyCollect;
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
			convertView = mInflater.inflate(R.layout.mycollect_item, null);
			holder.userPicImageViewMyCollect = (ImageView)convertView.findViewById(R.id.userImageViewMyCollect);
			holder.nickNameTextViewMyCollect = (TextView)convertView.findViewById(R.id.nickNameTextViewMyCollect);
			holder.textContentTextViewMyCollect = (TextView)convertView.findViewById(R.id.textContentTextViewMyCollect);
			holder.picContentImageViewMyCollect = (ImageView)convertView.findViewById(R.id.picContentImageViewMyCollect);
			holder.publishTimeTextViewMyCollect = (TextView)convertView.findViewById(R.id.publishTimeTextviewMyCollect);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		if (data != null) {
			if (data.get(position).getUserPicStr() != null) {
				holder.userPicImageViewMyCollect.setImageBitmap(MediaUtils.stringToBitmap(data.get(position).getUserPicStr()));
			} else {
				holder.userPicImageViewMyCollect.setImageBitmap(null);
			}
			if (data.get(position).getNickName() != null) {
				holder.nickNameTextViewMyCollect.setText(MediaUtils.decodeString(data.get(position).getNickName()));
			}
			holder.textContentTextViewMyCollect.setText(MediaUtils.decodeString(data.get(position).getTextContent()));
			if (data.get(position).getPicContent() != null) {
				holder.picContentImageViewMyCollect.setImageBitmap(MediaUtils.stringToBitmap(data.get(position).getPicContent()));
			} else {
				holder.picContentImageViewMyCollect.setImageBitmap(null);
			}
			
			holder.publishTimeTextViewMyCollect.setText(TimeUtils.DateToString(data.get(position).getPublishTime()));
		}
		
		return convertView;
	}
	
	

}
