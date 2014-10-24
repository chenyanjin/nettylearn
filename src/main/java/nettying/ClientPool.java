package nettying;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chenkai on 2014/10/23.
 */
public class ClientPool {

    public static ConcurrentHashMap clients;
    static {
        clients = new ConcurrentHashMap();
    }
}
