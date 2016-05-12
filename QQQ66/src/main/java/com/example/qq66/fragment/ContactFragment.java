package com.example.qq66.fragment;

import java.util.ArrayList;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import com.example.qq66.Const;
import com.example.qq66.R;
import com.example.qq66.activity.ChatActivity;
import com.example.qq66.manager.XmppConnectionManager;
import com.example.qq66.utils.ActivityUtils;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ContactFragment extends BaseFragment implements OnClickListener, OnChildClickListener {
	private LinearLayout ll_new_group;
	private LinearLayout ll_new_frient;
	private ExpandableListView listView;
	private XmppConnectionManager manager;
	private ArrayList<RosterGroup> mGroups;
	private MyAdapter myAdapter;
	private ArrayAdapter<String> arrayAdapter ;
	private ArrayList<String> arrayData;
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Const.MSG_ADD_NEW_FRIEND_CONFLICT:
				Toast.makeText(getActivity(), "新好友已经在您的好友列表中了，无需再次添加", Toast.LENGTH_SHORT).show();
				break;
			case Const.MSG_ADD_NEW_FRIEND_NOT_EXIT:
				Toast.makeText(getActivity(), "服务器中不存在该用户", Toast.LENGTH_SHORT).show();
				break;
				
			case Const.MSG_ADD_NEW_FRIEND_SUCCESS:
				Toast.makeText(getActivity(), "添加好友成功", Toast.LENGTH_SHORT).show();
				myAdapter.notifyDataSetChanged();
				break;
			case Const.MSG_ADD_NEW_FRIEND_ERROR:
				Toast.makeText(getActivity(), "添加好友失败"+msg.obj, Toast.LENGTH_SHORT).show();
				break;
				
			case Const.MSG_ADD_NEW_GROUP_EXIT:
				Toast.makeText(getActivity(), "该群组已经存在", Toast.LENGTH_SHORT).show();
				break;
				
			case Const.MSG_ADD_NEW_GROUP_SUCCESS:
				myAdapter.notifyDataSetChanged();
				 Toast.makeText(getActivity(), "创建群组成功", Toast.LENGTH_SHORT).show();
				break;
			case Const.MSG_ADD_NEW_GROUP_ERROR:
				Toast.makeText(getActivity(), "创建群组失败", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected View initView() {
		View view = View.inflate(getActivity(), R.layout.fragment_contact, null);
		listView = (ExpandableListView) view.findViewById(R.id.lv_contact);
		listView.setOnChildClickListener(this);
		ll_new_frient = (LinearLayout) view.findViewById(R.id.ll_new_frined);
		ll_new_group = (LinearLayout) view.findViewById(R.id.ll_new_group);
		ll_new_frient.setOnClickListener(this);
		ll_new_group.setOnClickListener(this);
		
		
		return view;
	}
	protected void initData() {
		manager = XmppConnectionManager.getInstance();
		mGroups = manager.getGroups();
		myAdapter = new MyAdapter();
		arrayData = new ArrayList<String>();
		for(int i=0;i<mGroups.size();i++){
			RosterGroup rosterGroup = mGroups.get(i);
			String name = rosterGroup.getName();
			arrayData.add(name);
		}
		arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayData);
		listView.setAdapter(myAdapter);
		
	};
	/**
	 * 获取分组
	 * @param index
	 * @return
	 */
	private RosterGroup getRosterGroup(int index){
		return mGroups.get(index);
	}
	/**
	 * 根据分组索引获取该分组下的所有好友
	 * @param groupIndex
	 * @return
	 */
	private ArrayList<RosterEntry> getRosterEntries(int groupIndex){
		return new ArrayList<>(getRosterGroup(groupIndex).getEntries());
	}
	
	private class MyAdapter extends BaseExpandableListAdapter{

		@Override
		public int getGroupCount() {
			return mGroups.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return getRosterEntries(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return getRosterGroup(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return getRosterEntries(groupPosition).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if (convertView==null) {
				convertView = View.inflate(getActivity(),R.layout.list_contact_group,null);
			}
			ImageView iv_indicatr = (ImageView) convertView.findViewById(R.id.iv_indicator);
			TextView tv_group = (TextView) convertView.findViewById(R.id.tv_group);
			TextView tv_count = (TextView) convertView.findViewById(R.id.tv_count);
		
			iv_indicatr.setSelected(isExpanded);
			tv_group.setText(getRosterGroup(groupPosition).getName());
			tv_count.setText(getRosterEntries(groupPosition).size()+"");
			
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			if (convertView==null) {
				convertView = View.inflate(getActivity(), R.layout.list_contact_child, null);
			}
			TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			RosterEntry rosterEntry = getRosterEntries(groupPosition).get(childPosition);
			//获取用户的全名称：zhangsan@yang.com/Spark
//			rosterEntry.getUser();
			//zhangsan
//			rosterEntry.getName();
			
			tv_name.setText(rosterEntry.getName()+"===="+rosterEntry.getUser());

			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_new_frined:
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("添加新朋友");
			View view = View.inflate(getActivity(), R.layout.dialog_new_friend, null);
			builder.setView(view);
			final EditText et_name = (EditText) view.findViewById(R.id.et_name);
			final EditText et_group = (EditText) view.findViewById(R.id.et_group);
			et_group.setText(TextUtils.isEmpty(getSPData(Const.SP_KEY_GROUP))?"":getSPData(Const.SP_KEY_GROUP));
			final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
			Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
			spinner.setAdapter(arrayAdapter);
			final AlertDialog alertDialog = builder.create();
			btn_ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String name = et_name.getText().toString().trim();
					if (TextUtils.isEmpty(name)) {
						Toast.makeText(getActivity(), "好友名称不能为空", Toast.LENGTH_SHORT).show();
						return;
					}
					int selectedItemPosition = spinner.getSelectedItemPosition();
					String group = arrayData.get(selectedItemPosition);
					et_group.setText(group);
					setSPData(Const.SP_KEY_GROUP, group);
					boolean b = manager.addNewFriend(name,group,handler);
					if (b) {
						alertDialog.dismiss();
					}
				}
			});
			alertDialog.show();
			break;
			
		case R.id.ll_new_group:
			AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
			builder2.setTitle("添加群组");
			View view2 = View.inflate(getActivity(), R.layout.dialog_new_group, null);
			final EditText et_group2 = (EditText) view2.findViewById(R.id.et_group);
			Button btn_ok2 = (Button) view2.findViewById(R.id.btn_ok);
			builder2.setView(view2);
			final AlertDialog alertDialog2 = builder2.create();
			btn_ok2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String group = et_group2.getEditableText().toString();
					if (TextUtils.isEmpty(group)) {
						Toast.makeText(getActivity(), "组名称不能为空", Toast.LENGTH_SHORT).show();
						return;
					}
					boolean b =manager.addNewGroup(group,handler);
					if(b){
						alertDialog2.dismiss();
					}
					
				}
			});
			alertDialog2.show();
			
			break;

		default:
			break;
		}
		
	}
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		RosterEntry rosterEntry = getRosterEntries(groupPosition).get(childPosition);
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra(Const.INTENT_KEY_USER, rosterEntry.getUser());
		startActivity(intent);
		return true;
	}
	
}
