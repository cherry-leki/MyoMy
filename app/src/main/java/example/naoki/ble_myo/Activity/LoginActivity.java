package example.naoki.ble_myo.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

import example.naoki.ble_myo.Communication.Session;
import example.naoki.ble_myo.Communication.SocketListener;
import example.naoki.ble_myo.Communication.SocketManager;
import example.naoki.ble_myo.DataProcess.ClientSocket;
import example.naoki.ble_myo.R;

/**
 * Created by Leki on 2016-09-02.
 */
public class LoginActivity extends Activity {
    EditText idEditText, pwEditText;
    Button loginButton, guestLoginButton, signUpButton, questionButton;
    String name="";

    String id,pw;
    private SocketListener sl;

    void init() {
        idEditText = (EditText) findViewById(R.id.idedittext);
        pwEditText = (EditText) findViewById(R.id.pwedittext);
        loginButton = (Button) findViewById(R.id.loginbutton);
        guestLoginButton = (Button) findViewById(R.id.guestloginbutton);
        signUpButton = (Button) findViewById(R.id.signupbutton);
        questionButton = (Button) findViewById(R.id.questionbutton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        /*
        try {
            SocketManager.getSocket();
        }catch(IOException e){}
        */
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                idEditText.setText(data.getStringExtra("signup_id"));
                //pwEditText.setText(data.getStringExtra("signup_pw"));
                //name = data.getStringExtra("signup_name");
            }
        }
    }

    public void signUpButtonClick(View view) throws InterruptedException{
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivityForResult(intent, 1);
    }

    public void questionButtonClick(View view) {
        Toast.makeText(this, "question", Toast.LENGTH_SHORT).show();
    }

    public void loginButtonClick(View view) {
        sl=new SocketListener(mainHandler);
        sl.setDaemon(true);
        sl.start();
        id=idEditText.getText().toString();
        pw=pwEditText.getText().toString();
        Thread worker = new Thread() {
            public void run() {
                try {
                    SocketManager.send("S/3+" + id + "+" + pw + "/E");
                    Session.setLoginCheck(true);
                }catch(IOException e){
                    sl.interrupt();
                }
            }
        };
        worker.start();
    }

    public void guestLoginButtonClick(View view) {
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        intent.putExtra("name", "guest");
        startActivity(intent);
    }

    public Handler mainHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            int code;
            String result,data_name;
            code=Integer.parseInt(msg.obj.toString().split(",")[0]);
            result=msg.obj.toString().split(",")[1];



            if(code==3 && Session.getLoginCheck()==true){
                if(result.equals("o")){
                    Log.w("로그인","o");
                    data_name=msg.obj.toString().split(",")[2];
                    Session.setName(data_name);
                    Session.setLogined(true);
                    Toast.makeText(LoginActivity.this, "환영합니다!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    intent.putExtra("name", data_name);
                    startActivity(intent);
                }
                else if(result.equals("x")){
                    Log.w("로그인","x");
                    Toast.makeText(LoginActivity.this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(result.equals("r")){
                    Log.w("로그인","r");
                    Toast.makeText(LoginActivity.this, "아이디를 확인해주세요", Toast.LENGTH_SHORT).show();
                }
                Session.setLoginCheck(false);
            }
            sl.interrupt();
            Log.w("받긴받았음","로그인");
        }
    };
}
