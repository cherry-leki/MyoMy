package example.naoki.ble_myo.Communication;

import android.os.Message;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
/**
 * Created by sec on 2016-11-13.
 */

public class SocketListener extends Thread{
    private InputStream is;
    private BufferedReader br;
    Handler mHandler;

    public SocketListener(Handler handler){
        this.mHandler=handler;
    }

    @Override
    public void run(){
        super.run();
        char[] temp=new char[20];
        while(!Thread.currentThread().isInterrupted()){
            try{
                if(br==null) {
                    br = new BufferedReader(new InputStreamReader(SocketManager.getSocket().getInputStream(),"EUC_KR"));
                    //Log.w("버퍼리더","생성");
                }
                String receiveMsg="";
                br.read(temp);
                for (int i = 0; i < temp.length; i++) receiveMsg += temp[i];
                //Log.w("서버로부터받음",receiveMsg);
                Message msg = mHandler.obtainMessage();
                msg.obj=receiveMsg;
                mHandler.sendMessage(msg);
                Log.w("핸들러로 전송",receiveMsg);

                //mHandler.sendEmptyMessage(0);
            }catch (IOException e){}
        }
    }
}
