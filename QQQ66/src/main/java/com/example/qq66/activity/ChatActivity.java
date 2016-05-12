package com.example.qq66.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qq66.Const;
import com.example.qq66.R;
import com.example.qq66.bean.ChatMessage;
import com.example.qq66.bean.ChatSession;
import com.example.qq66.manager.XmppConnectionManager;
import com.example.qq66.utils.ChatMessageManager;
import com.example.qq66.utils.ChatSessionManager;
import com.example.qq66.utils.DateUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

/**
 * 
 * @author wzy 2015-9-9
 * 
 */
public class ChatActivity extends BaseActivity implements MessageListener {

	private Button btn_send;
	private EditText et_send;
	private ListView lv_chat;
	private String mToUser;
	private XmppConnectionManager manager;
	private ChatManager mChatManager;
	private Chat mChat;
	private String mUser;
	private MyChatAdapter myChatAdapter;
	private ArrayList<ChatMessage> myChatMessages = new ArrayList<ChatMessage>();

	private static final int VIEW_TYPE_COUNT = 2;

	@Override
	public void initView() {
		setContentView(R.layout.activity_chat);
		et_send = (EditText) findViewById(R.id.et_send);
		btn_send = (Button) findViewById(R.id.btn_send);
		lv_chat = (ListView) findViewById(R.id.lv_chat);

		// lv_chat.setAdapter(adapter)
	}

	@Override
	protected void initData() {
		Intent intent = getIntent();
		mToUser = intent.getStringExtra(Const.INTENT_KEY_USER);
		if (TextUtils.isEmpty(mToUser)) {
			Toast.makeText(this, "没有获取到聊天对象", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		manager = XmppConnectionManager.getInstance();
		// 获取xmpp连接服务
		XMPPConnection connection = manager.getConnection();
		// 通过连接对象获取聊天管理器
		mChatManager = connection.getChatManager();
		/**
		 * 创建聊天对象 第一个参数：聊天的对方 第二个参数：消息的监听器
		 */
		mChat = mChatManager.createChat(mToUser, this);
		Const.sCurrentToUser = mToUser;
		mUser = manager.getConnection().getUser();
		myChatMessages = ChatMessageManager.getMyChatMessage(mUser, mToUser);
		myChatAdapter = new MyChatAdapter();
		lv_chat.setAdapter(myChatAdapter);
		lv_chat.setSelection(myChatMessages.size());

	}

	@Override
	protected void updateData() {
		// 必须在UI线程中执行该方法
		myChatAdapter.notifyDataSetChanged();
		rePostionListView();
	}

	public void sendMessage(View view) {
		String text = et_send.getEditableText().toString().trim();
		try {
			// Message message = new Message();
			// message.set
			// mChat.sendMessage(message);
			mChat.sendMessage(text);
			add2ChatMessage(text);
			Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
		} catch (XMPPException e) {
			e.printStackTrace();
			Toast.makeText(this, "消息发送失败", Toast.LENGTH_SHORT).show();
		}

	}

	// 保存我自己发送的消息记录
	private void add2ChatMessage(String text) {
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setFrom(mUser);
		chatMessage.setTo(mToUser);
		chatMessage.setOwner(mUser);
		chatMessage.setText(text);
		chatMessage.setTime(DateUtils.getNowDateTime());

		Const.chatMessages.add(chatMessage);
		myChatMessages = ChatMessageManager.getMyChatMessage(mUser, mToUser);
		// 必须在UI线程中执行该方法、注意顺序不能反了，先notifyData，在重新reposition位置才行
		myChatAdapter.notifyDataSetChanged();
		rePostionListView();
	}

	/**
	 * 重新定位ListView，显示最后一条数据
	 */
	private void rePostionListView() {
		lv_chat.smoothScrollToPosition(Const.chatMessages.size());
	}

	/**
	 * 将对方发送给我的消息记录下来
	 * 
	 * @param message
	 */
	private void add2ChatMessage(Message message) {
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setFrom(mToUser);
		chatMessage.setTo(mUser);
		chatMessage.setOwner(mUser);
		chatMessage.setText(message.getBody());
		chatMessage.setTime(DateUtils.getNowDateTime());
		Const.chatMessages.add(chatMessage);
		myChatMessages = ChatMessageManager.getMyChatMessage(mUser, mToUser);
//		myChatAdapter.notifyDataSetChanged();
	}

	private class MyChatAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return myChatMessages.size();
		}

		@Override
		public Object getItem(int position) {
			return myChatMessages.get(position);
		}

		@Override
		public int getItemViewType(int position) {
			ChatMessage chatMessage = myChatMessages.get(position);
			if (chatMessage.getFrom().equals(chatMessage.getOwner())) {
				return 0;
			}
			return 1;
		}

		@Override
		public int getViewTypeCount() {
			return VIEW_TYPE_COUNT;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int itemViewType = getItemViewType(position);
			ViewHolder viewHolder;
			if (itemViewType == 0) {
				// 我自己的布局条目
				if (convertView == null) {
					convertView = View.inflate(ChatActivity.this, R.layout.chat_list_item_me, null);
					viewHolder = new ViewHolder();
					TextView tv_text = (TextView) convertView.findViewById(R.id.tv_text);
					viewHolder.tv_text = tv_text;
					TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
					viewHolder.tv_time = tv_time;
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				// convertView.findViewById(id)
			} else {
				// 对方的布局条目
				if (convertView == null) {
					convertView = View.inflate(ChatActivity.this, R.layout.chat_list_item_you, null);
					viewHolder = new ViewHolder();
					TextView tv_text = (TextView) convertView.findViewById(R.id.tv_text);
					viewHolder.tv_text = tv_text;
					TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
					viewHolder.tv_time = tv_time;
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
			}
			ChatMessage chatMessage = myChatMessages.get(position);
			viewHolder.tv_text.setText(chatMessage.getText());
			viewHolder.tv_time.setText(chatMessage.getTime());

			return convertView;
		}
	}

	private static class ViewHolder {
		TextView tv_text;
		TextView tv_time;
	}
	
	/*@Override
	protected void onDestroy() {
		super.onDestroy();
		Const.sCurrentToUser = "";
		mChat.removeMessageListener(this);
	}*/
	@Override
	protected void onStop() {
		super.onStop();
		Const.sCurrentToUser = "";
//		mChat.removeMessageListener(this);
	}

	@Override
	public void processMessage(Chat chat, Message message) {
		if (TextUtils.isEmpty(message.getBody())) {
			return;
		}
		android.os.Message msg = new android.os.Message();
		msg.what = Const.MSG_CHAT_NEW_MESSAGE;
		msg.obj = message.getBody();
		Log.d("tag", message.getBody());
		ChatSession chatSession = new ChatSession();
		chatSession.setTime(DateUtils.getNowDateTime());
		chatSession.setFrom(mToUser);
		chatSession.setMessage(message.getBody());
		chatSession.setOwner(mUser);
		ChatSessionManager.saveChatSession(chatSession);
		add2ChatMessage(message);
		mHandler.sendMessage(msg);
//		Const.chatSessions.add(chatSession);
	}

}
