package com.lbsnews.adapter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lbsnews.R;
import com.lbsnews.adapter.NewsAdapter.ViewHolder;
import com.lbsnews.bean.Comment;
import com.lbsnews.utils.FileUtils;
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

public class CommentAdapter extends BaseAdapter {
	private static final String TAG = "******CommentAdapter******";
	
	private LayoutInflater mInflater = null;
	private Context context = null;
	private List<Comment> data;
	
	public CommentAdapter(Context context2, List<Comment> data) {
		this.context = context2;
		this.data = data;
		this.mInflater = LayoutInflater.from(context);
	}
	
	static class ViewHolder {
		public ImageView userPicImageView;
		public TextView nickNameTextView;
		public TextView commentTextView;
		public TextView publishTimeTextView;
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
			convertView = mInflater.inflate(R.layout.comment_item, null);
			holder.userPicImageView = (ImageView)convertView.findViewById(R.id.userPicImageViewComment);
			holder.nickNameTextView = (TextView)convertView.findViewById(R.id.nickNameTextViewComment);
			holder.commentTextView = (TextView)convertView.findViewById(R.id.commentTextViewComment);
			holder.publishTimeTextView = (TextView)convertView.findViewById(R.id.publishTimeTextviewComment);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		if (data != null) {
			if (data.get(position).getCommentAccountPicStr() != null) {
				holder.userPicImageView.setImageBitmap(MediaUtils.stringToBitmap(data.get(position).getCommentAccountPicStr()));
			} else {
				holder.userPicImageView.setImageBitmap(null);
			}
			if (data.get(position).getCommentNickName() != null) {
				holder.nickNameTextView.setText(MediaUtils.decodeString(data.get(position).getCommentNickName()));
			} else {
				holder.nickNameTextView.setText("Œ¥…Ë÷√Í«≥∆");
			}
			holder.commentTextView.setText(MediaUtils.decodeString(data.get(position).getCommentContent()));
			holder.publishTimeTextView.setText(TimeUtils.DateToString(data.get(position).getCommentTime()));
		} 
		
		return convertView;
	}

	public void setData(List<Comment> data) {
		this.data = data;
	}
	
}
