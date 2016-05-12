package com.example.qq66;

import com.example.qq66.bean.ChatMessage;
import com.example.qq66.bean.ChatSession;

import java.util.ArrayList;

public class Const {
	public static final int MSG_SPLASH = 1;
	// xmpp服务器地址
	public static final String HOST = "192.168.1.112";
	// xmpp服务器端口
	public static final int PORT = 5222;
	/**
	 * 登录成功
	 */
	public static final int MSG_LOGIN_SUCCESS = 2;
	/**
	 * 登录失败
	 */
	public static final int MSG_LOGIN_ERROR = 3;
	/**
	 * 注册失败
	 */
	public static final int MSG_REGIST_ERROR = 4;
	/**
	 * 注册成功
	 */
	public static final int MSG_REGIST_SUCCESS = 5;
	/**
	 * sp 文件名称
	 */
	public static final String SP_NAME = "qqconfig";
	/**
	 * sp 中用户名的key值
	 */
	public static final String SP_KEY_NAME = "name";
	/**
	 * sp 中用户密码的key值
	 */
	public static final String SP_KEY_PWD = "pwd";
	/**
	 * 将用户的默认分组保存在sp中
	 */
	public static final String SP_KEY_GROUP = "group";
	/**
	 * 新好友已经在您的好友列表中了，无需再次添加
	 */
	public static final int MSG_ADD_NEW_FRIEND_CONFLICT = 6;
	/**
	 * 服务器中不存在该用户
	 */
	public static final int MSG_ADD_NEW_FRIEND_NOT_EXIT = 7;
	/**
	 * 添加好友成功
	 */
	public static final int MSG_ADD_NEW_FRIEND_SUCCESS = 8;
	/**
	 * 添加好友失败
	 */
	public static final int MSG_ADD_NEW_FRIEND_ERROR = 9;
	/**
	 * 该群组已经存在
	 */
	public static final int MSG_ADD_NEW_GROUP_EXIT = 10;
	
	public static final int MSG_ADD_NEW_GROUP_ERROR = 11;
	
	public static final int MSG_ADD_NEW_GROUP_SUCCESS = 12;
	/**
	 * 通过Intent传递用户的key
	 */
	public static final String INTENT_KEY_USER = "user";
	
	public static final int MSG_CHAT_NEW_MESSAGE = 13;
	
	public static ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
	/**
	 * 当前聊天的对方
	 */
	public static String sCurrentToUser = "";
	/**
	 * 会话对象
	 */
	public static ArrayList<ChatSession> chatSessions = new ArrayList<ChatSession>();
	
}
