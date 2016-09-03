package donespeak.com.mail.bean;

import java.io.Serializable;

/**
 * Created by glorior on 2016/6/18.
 */
public class MailBean implements Serializable {
    public String fromWho;
    public String fromAddress;
    public String time;
    public String bodytext;
    public long messageUID;
    public int msgNum;
    public String title;
    public boolean hasAttachment;

    public String getFromWho() {
        return fromWho;
    }

    public MailBean(String fromWho, String fromAddress, String time, String bodytext, long messageUID, int msgNum, String title) {
        this.fromWho = fromWho;
        this.fromAddress = fromAddress;
        this.time = time;
        this.bodytext = bodytext;
        this.messageUID = messageUID;
        this.msgNum = msgNum;
        this.title = title;
    }

    public MailBean() {
        this.fromWho = null;
        this.fromAddress = null;
        this.time = null;
        this.bodytext = null;
        this.messageUID = 0;
        this.msgNum = 0;
        this.title = null;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFromWho(String fromWho) {
        this.fromWho = fromWho;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setBodytext(String bodytext) {
        this.bodytext = bodytext;
    }

    public void setMessageUID(long messageUID) {
        this.messageUID = messageUID;
    }

    public void setMsgNum(int msgNum) {
        this.msgNum = msgNum;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getTime() {
        return time;
    }

    public String getBodytext() {
        return bodytext;
    }

    public long getMessageUID() {
        return messageUID;
    }

    public int getMsgNum() {
        return msgNum;
    }

    public String getTitle() {
        return title;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }
}
