package com.appkefu.lib4.demo.adapter;

import java.util.List;

import com.appkefu.appkefu_kefu_newchat.R;
import com.appkefu.lib4.demo.entity.ApiEntity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ApiAdapter extends BaseAdapter {

	private Context context;
	private List<ApiEntity> list;
	private LayoutInflater inflater;
	
	public ApiAdapter(Context context, List<ApiEntity> list) {
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	} 
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void remove(ApiEntity entity) {
		list.remove(entity);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.api_item_layout, null);
			holder = new ViewHolder();
			holder.api_function = (TextView) convertView.findViewById(R.id.api_function);
			holder.unread_message_counts = (TextView) convertView.findViewById(R.id.unread_message_counts);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final ApiEntity entity = list.get(position);

		holder.api_function.setTextColor(Color.BLACK);
		holder.api_function.setText(entity.getId() +"."+ entity.getApiName());
		if(!entity.getUnreadMessageCounts().equals("0")){
			holder.unread_message_counts.setVisibility(View.VISIBLE);
			holder.unread_message_counts.setText(entity.getUnreadMessageCounts());
		}
		else{
			holder.unread_message_counts.setVisibility(View.GONE);
		}
		
		return convertView;
	}

	static class ViewHolder {
		public TextView api_function;
		public TextView unread_message_counts;
	}
}













