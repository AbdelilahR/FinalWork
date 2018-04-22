package com.example.currentplacedetailsonmap.Model;

import java.util.Date;

/**
 * Class that represent the message.
 */
public class Chat
{
    public String message;
    public String id;
    private String messageText;
    private String messageUser;
    private long messageTime;

    public Chat()
    {
    }
/*
    public Chat(String message, String id) {
        this.message = message;
        this.id = id;
    }
*/


    public Chat(String messageText, String messageUser)
    {
        this.messageText = messageText;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();
    }


    public String getMessageText()
    {
        return messageText;
    }

    public void setMessageText(String messageText)
    {
        this.messageText = messageText;
    }

    public String getMessageUser()
    {
        return messageUser;
    }

    public void setMessageUser(String messageUser)
    {
        this.messageUser = messageUser;
    }

    public long getMessageTime()
    {
        return messageTime;
    }

    public void setMessageTime(long messageTime)
    {
        this.messageTime = messageTime;
    }
}
