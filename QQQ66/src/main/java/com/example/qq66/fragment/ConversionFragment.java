package com.example.qq66.fragment;

import com.example.qq66.Const;
import com.example.qq66.R;
import com.example.qq66.activity.ChatActivity;
import com.example.qq66.bean.ChatSession;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ConversionFragment extends BaseFragment {
	private ListView listView;
	@Override
	protected View initView() {
		View view = View.inflate(getActivity(), R.layout.fragment_conversion, null);
		
		listView = (ListView) view.findViewById(R.id.lv_conversion);
		
		return view;
	}
	protected void initData() {
		listView.setAdapter(new MyAdapter());
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				intent.putExtra(Const.INTENT_KEY_USER, Const.chatSessions.get(position).getFrom());
				startActivity(intent);
			}
		});
	};
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return Const.chatSessions.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView==null) {
				convertView = View.inflate(getActivity(), R.layout.fragment_conversion_item, null);
			}
			TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			TextView tv_message = (TextView) convertView.findViewById(R.id.tv_message);
			TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			ChatSession chatSession = Const.chatSessions.get(position);
			tv_message.setText(chatSession.getMessage());
			tv_name.setText(chatSession.getFrom());
			tv_time.setText(chatSession.getTime());
			return convertView;
		}
		
	}
}
