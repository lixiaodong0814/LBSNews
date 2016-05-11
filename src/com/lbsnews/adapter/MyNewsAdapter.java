package com.lbsnews.adapter;

import java.util.List;
import java.util.Map;

import com.lbsnews.R;
import com.lbsnews.adapter.NewsAdapter.ViewHolder;
import com.lbsnews.bean.MyNews;
import com.lbsnews.utils.MediaUtils;
import com.lbsnews.utils.TimeUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyNewsAdapter extends BaseAdapter {
	
	private static final String TAG = "******MyNewsAdapter*****";
	private LayoutInflater mInflater = null;
	private Context context = null;
	private List<MyNews> data;
	
	public MyNewsAdapter(Context context2, List<MyNews> data) {
		this.context = context2;
		this.data = data;
		this.mInflater = LayoutInflater.from(context);
	}
	
	private static class ViewHolder {
		public TextView publishTimeTextViewMyNews;
		public TextView textContentTextViewMyNews;
		public ImageView picContentImageViewMyNews;
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
			convertView = mInflater.inflate(R.layout.mynews_item, null);
			
			holder.publishTimeTextViewMyNews = (TextView)convertView.findViewById(R.id.publishTimeTextviewMyNews);
			holder.textContentTextViewMyNews = (TextView)convertView.findViewById(R.id.textContentTextViewMyNews);
			holder.picContentImageViewMyNews = (ImageView)convertView.findViewById(R.id.picContentImageViewMyNews);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		if (data != null) {
			holder.publishTimeTextViewMyNews.setText(TimeUtils.DateToString(data.get(position).getPublishTime()));
			holder.textContentTextViewMyNews.setText(MediaUtils.decodeString(data.get(position).getTextContent()));
			if (data.get(position).getPicContent() != null) {
				holder.picContentImageViewMyNews.setImageBitmap(MediaUtils.stringToBitmap(data.get(position).getPicContent()));
			} else {
				holder.picContentImageViewMyNews.setVisibility(View.GONE);
			}
		}
		
		
		return convertView;
	}

	public void setData(List<MyNews> data) {
		this.data = data;
	}
	
}
