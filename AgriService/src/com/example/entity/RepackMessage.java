package com.example.entity;



public class RepackMessage {

	 public int Type ;

     public String MessageContent ;

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public String getMessageContent() {
		return MessageContent;
	}

	public void setMessageContent(String messageContent) {
		MessageContent = messageContent;
	}

	public RepackMessage(int type, String messageContent) {
		super();
		Type = type;
		MessageContent = messageContent;
	}
     
}
