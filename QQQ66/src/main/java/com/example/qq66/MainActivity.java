package com.example.qq66;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qq66.activity.BaseActivity;
import com.example.qq66.bean.ChatMessage;
import com.example.qq66.bean.ChatSession;
import com.example.qq66.fragment.ContactFragment;
import com.example.qq66.fragment.ConversionFragment;
import com.example.qq66.fragment.DongtaiFragment;
import com.example.qq66.manager.XmppConnectionManager;
import com.example.qq66.utils.ChatSessionManager;
import com.example.qq66.utils.DateUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class MainActivity extends BaseActivity implements OnClickListener, ChatManagerListener {

	private ImageView iv_tab_contact;
	private ImageView iv_tab_conversion;
	private ImageView iv_tab_dongtai;
	private TextView tv_title;
	private int currentSelectId;
	private DongtaiFragment dongtaiFragment;
	private ContactFragment contactFragment;
	private ConversionFragment conversionFragment;

	private XmppConnectionManager manager;
	private String mUser;
	private ChatManager chatManager;

	@Override
	public void initView() {
		setContentView(R.layout.activity_main);
		iv_tab_contact = (ImageView) findViewById(R.id.iv_tab_contact);
		iv_tab_conversion = (ImageView) findViewById(R.id.iv_tab_conversion);
		iv_tab_dongtai = (ImageView) findViewById(R.id.iv_tab_dongtai);
		tv_title = (TextView) findViewById(R.id.tv_title);

		iv_tab_contact.setOnClickListener(this);
		iv_tab_conversion.setOnClickListener(this);
		iv_tab_dongtai.setOnClickListener(this);

	}

	@Override
	protected void initData() {
		dongtaiFragment = new DongtaiFragment();
		contactFragment = new ContactFragment();
		conversionFragment = new ConversionFragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
		beginTransaction.replace(R.id.fl_content, dongtaiFragment);
		beginTransaction.commit();
		iv_tab_dongtai.setSelected(true);
		tv_title.setText("动态");
		currentSelectId = R.id.iv_tab_dongtai;
		/*manager = XmppConnectionManager.getInstance();
		mUser = manager.getConnection().getUser();
		chatManager = manager.getConnection().getChatManager();
		chatManager.addChatListener(this);*/
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == currentSelectId) {
			return;
		}
		currentSelectId = v.getId();
		iv_tab_contact.setSelected(v.getId() == R.id.iv_tab_contact);
		iv_tab_conversion.setSelected(v.getId() == R.id.iv_tab_conversion);
		iv_tab_dongtai.setSelected(v.getId() == R.id.iv_tab_dongtai);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
		switch (v.getId()) {
		case R.id.iv_tab_conversion:
			beginTransaction.replace(R.id.fl_content, conversionFragment);
			tv_title.setText("消息");
			break;
		case R.id.iv_tab_contact:
			beginTransaction.replace(R.id.fl_content, contactFragment);
			tv_title.setText("联系人");
			break;
		case R.id.iv_tab_dongtai:
			beginTransaction.replace(R.id.fl_content, dongtaiFragment);
			tv_title.setText("动态");
			break;

		default:
			break;
		}
		beginTransaction.commit();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// chatManager.removeChatListener(this);
	}

	@Override
	public void chatCreated(Chat chat, boolean createdLocally) {
		/*
		 * if (createdLocally) { return; }
		 */
		chat.addMessageListener(new MessageListener() {

			@Override
			public void processMessage(Chat chat, Message message) {
				if (TextUtils.isEmpty(message.getBody())) {
					return;
				}
				String from = message.getFrom();
				//加这个判断是为了区分从spark上发过来的消息
				if (from.contains("/")) {
					from = from.substring(0, from.lastIndexOf("/"));
				}
				if (from.equals(Const.sCurrentToUser)) {
					return;
				}
				//这个message是系统自带的，为了和smack jar包里面自带的 message区分，所以这里用了全类名
				android.os.Message msg = new android.os.Message();
				msg.what = Const.MSG_CHAT_NEW_MESSAGE;
				msg.obj = message.getBody();
				mHandler.sendMessage(msg);
				ChatMessage chatMessage = new ChatMessage();
				chatMessage.setFrom(message.getFrom());
				chatMessage.setTo(message.getTo());
				chatMessage.setOwner(mUser);
				chatMessage.setText(message.getBody());
				chatMessage.setTime(DateUtils.getNowDateTime());
				Const.chatMessages.add(chatMessage);

				//chatsession真正的用来发送消息
				ChatSession chatSession = new ChatSession();
				chatSession.setTime(DateUtils.getNowDateTime());
				chatSession.setFrom(message.getFrom());
				chatSession.setMessage(message.getBody());
				chatSession.setOwner(mUser);
				ChatSessionManager.saveChatSession(chatSession);
//				Const.chatSessions.add(chatSession);
			}
		});
	}

	@Override
	protected void onResume() {//交互的时候
		super.onResume();
		// chatManager.addChatListener(this);
		startChatListener();
	}

	ChatManagerListener chatManagerListener = new ChatManagerListener() {

		@Override
		public void chatCreated(Chat chat, boolean createdLocally) {
			handleChat(chat);
		}
	};
	private MessageListener messageListener = new MessageListener() {

		@Override
		public void processMessage(Chat chat, Message message) {
			if (TextUtils.isEmpty(message.getBody())) {
				return;
			}
			String from = message.getFrom();
			if (from.contains("/")) {
				// and@yange/Spark
				from = from.substring(0, from.lastIndexOf("/"));
			}
			if (Const.sCurrentToUser != null && Const.sCurrentToUser.equals(from)) {
				return;
			}
			android.os.Message msg = new android.os.Message();
			msg.what = Const.MSG_CHAT_NEW_MESSAGE;
			msg.obj = message;
			mHandler.sendMessage(msg);
		}
	};

	protected void handleChat(Chat chat) {
		chat.addMessageListener(messageListener);
	}

	private void startChatListener() {
		chatManager = XmppConnectionManager.getInstance().getConnection().getChatManager();
		chatManager.addChatListener(chatManagerListener);
	}

}
