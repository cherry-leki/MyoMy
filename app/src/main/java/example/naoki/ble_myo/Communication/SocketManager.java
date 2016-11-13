package example.naoki.ble_myo.Communication;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
/**
 * Created by sec on 2016-11-13.
 */

public class SocketManager {
    private static Socket socket;
    private final static String ip="122.47.118.149";
    private final static int port=4030;

    private SocketManager(){}

    public static Socket getSocket() throws IOException{
        if(socket==null){
            socket=new Socket(ip,port);
        }
        if(!socket.isConnected())
            socket.connect(new InetSocketAddress(ip,port));
        //Log.w("소켓연결","성공");
        return socket;
    }

    public static void closeSocket() throws IOException{
        if(socket!=null) socket.close();
    }

    public static synchronized void send(String msg) throws IOException{
        getSocket().getOutputStream().write((msg+'\n').getBytes("EUC_KR"));
        //Log.w("서버로보냄",msg);
    }
}