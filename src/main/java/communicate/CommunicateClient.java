package communicate;

/**
 * Created by chenkai on 2014/10/24.
 * 通讯客户端
 */
public class CommunicateClient {

    private static CommunicateClient instence;
    private CommunicateClient(){}

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


}
