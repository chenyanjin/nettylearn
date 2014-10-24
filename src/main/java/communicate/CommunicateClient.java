package communicate;

import io.netty.channel.Channel;
import nettying.Message;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chenkai on 2014/10/24.
 * 通讯客户端
 */
public class CommunicateClient {

    private static CommunicateClient instence;
    private final Object syncObj = new Object();
    private CommunicateClient(){
        sessions = new ConcurrentHashMap<String, Channel>();
    }
    private ConcurrentHashMap<String,Channel> sessions;
    private Channel currentChannel;

    public static CommunicateClient getInstance(){
        if(instence == null){
            synchronized (CommunicateClient.class){
                if(instence == null){
                    instence = new CommunicateClient();
                }
            }
        }
        return instence;
    }

    public void sendMessage(Message msg,String clientId){
        Channel ch = sessions.get(clientId);
        ch.writeAndFlush(msg);
    }

    /**
     * @Description 测试连接服务器，如果未连接，则尝试建立连接，并保存连接通道
     * @param ip 服务器ip
     * @param port 服务器端口
     * @param clientId 客户端标识（可以是终端唯一id）
     * @return 连接成功或者已连接返回TRUE
     */
    public boolean testConnect(String ip,int port,String clientId){
        boolean hasConnected = false;
        Channel ch = sessions.get(clientId);
        if(ch!=null){
            if(ch.isActive()){
                return true;
            }else{
                hasConnected = connect(ip,port,clientId);
            }
        }else{
            hasConnected = connect(ip,port,clientId);
        }
        return hasConnected;
    }

    private boolean connect(String ip,int port,String clientId){
        boolean isConnect = false;
        sessions.remove(clientId);
        try {
            new Thread(new ServerConnectable(ip,port,clientId,this)).start();
            startWait();
            if(sessions.get(clientId)!=null){
                isConnect = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnect;
    }


    public ConcurrentHashMap<String, Channel> getSessions() {
        return sessions;
    }
    public void startWait(){
        synchronized (syncObj){
            try {
                syncObj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void unlock(){
        synchronized (syncObj){
            syncObj.notify();
        }
    }
}
