package com.example.qq66.bean;
/**
 * 代表聊天记录对象
 * @author wzy 2015-9-9
 *
 */
public class ChatMessage {
	private String owner;
	private String from;
	private String to;
	private String text;
	private String time;
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	@Override
	public String toString() {
		return "ChatMessage [owner=" + owner + ", from=" + from + ", to=" + to + ", text=" + text + ", time=" + time + "]";
	}
	public ChatMessage(String owner, String from, String to, String text, String time) {
		super();
		this.owner = owner;
		this.from = from;
		this.to = to;
		this.text = text;
		this.time = time;
	}
	public ChatMessage() {
	}
	
}
