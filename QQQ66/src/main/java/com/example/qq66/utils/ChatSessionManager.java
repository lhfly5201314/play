package com.example.qq66.utils;

import java.util.ArrayList;

import com.example.qq66.Const;
import com.example.qq66.bean.ChatSession;

public class ChatSessionManager {
	public static void saveChatSession(ChatSession chatSession){
		ArrayList<ChatSession> chatSessions = Const.chatSessions;
		boolean contains = chatSessions.contains(chatSession);
		if (contains) {
			chatSessions.remove(chatSession);
		}
		chatSessions.add(chatSession);
		
	}
}
