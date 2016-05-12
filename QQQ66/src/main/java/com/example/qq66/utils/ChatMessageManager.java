package com.example.qq66.utils;

import java.util.ArrayList;

import com.example.qq66.Const;
import com.example.qq66.bean.ChatMessage;

public class ChatMessageManager {
	public static ArrayList<ChatMessage> getMyChatMessage(String ower,String toUser){
		ArrayList<ChatMessage> chatMessages = Const.chatMessages;
		ArrayList<ChatMessage> myChatMessages = new ArrayList<ChatMessage>();
		for(ChatMessage message : chatMessages){
			//我发送给别人的消息
			if (message.getOwner().equals(ower)&&ower.equals(message.getFrom())) {
				if (message.getTo().equals(toUser)) {
					myChatMessages.add(message);
				}
				//别人发送给我的消息
			}else if (message.getOwner().equals(ower)&&ower.equals(message.getTo())) {
				if (message.getFrom().equals(toUser)) {
					myChatMessages.add(message);
				}
			}
			
		}
		return myChatMessages;
	}
}
