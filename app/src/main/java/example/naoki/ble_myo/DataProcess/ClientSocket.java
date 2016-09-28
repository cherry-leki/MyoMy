package example.naoki.ble_myo.DataProcess;

import android.os.Handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by Sabarada on 2016-09-02.
 */
public class ClientSocket {
    public static Socket socket;
    private static byte[] cc = new byte[128];

    public final static String HOST = "122.47.118.149";
    public final static int PORT = 4030;


    private BufferedReader networkReader;
    private BufferedWriter networkWriter;
    private PrintWriter out;

    private static final int MESSAGE = 0;
    private static final int ID_DUPLICATE_CHECK = 1;
    private static final int SIGN_UP = 2;
    private static final int LOG_IN = 3;
    private static final int REGISTER_DATA = 4;
    private static final int CALL_DATA = 5;

//    public static Socket getSocket() throws IOException {
//
//        if (socket == null) {
//            socket = new Socket();
//        }
//        if (!socket.isConnected()){
////            getMessage.start();
//        }
//
//        return socket;
//    }

//    public static void sendMessage(String msg) throws IOException {getSocket().getOutputStream().write(("S," + msg + ",E\n").getBytes());}

//    private static Thread getMessage = new Thread() {
//        @Override
//        public void run() {
//            try {
//                socket.connect(new InetSocketAddress(HOST, PORT));
//                sendMessage("0+Hello");
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            while(true)
//            {
//                try {
//                    getSocket().getInputStream().read(cc);
//                    System.out.println("line : " + Arrays.toString(cc));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//
//                }
//            }
//        }
//    };

    public static void closeSocket() {
        try {
            socket.close();
            if (socket != null) {
                socket = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
