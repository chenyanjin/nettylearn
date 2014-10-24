package nettying;

import java.io.Serializable;

/**
 * Created by chenkai on 2014/10/23.
 */
public class Message implements Serializable {

    private int msgType;
    private String msgContent;
    private String msgSource;
    private String msgDest;

    public Message(int msgType, String msgContent, String msgSource, String msgDest) {
        this.msgType = msgType;
        this.msgContent = msgContent;
        this.msgSource = msgSource;
        this.msgDest = msgDest;
    }

    public Message(int msgType, String msgContent, String msgSource) {
        this.msgType = msgType;
        this.msgContent = msgContent;
        this.msgSource = msgSource;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgSource() {
        return msgSource;
    }

    public void setMsgSource(String msgSource) {
        this.msgSource = msgSource;
    }

    public String getMsgDest() {
        return msgDest;
    }

    public void setMsgDest(String msgDest) {
        this.msgDest = msgDest;
    }
}
