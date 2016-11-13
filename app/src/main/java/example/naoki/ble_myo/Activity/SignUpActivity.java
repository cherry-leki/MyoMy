package example.naoki.ble_myo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import example.naoki.ble_myo.Communication.Session;
import example.naoki.ble_myo.Communication.SocketListener;
import example.naoki.ble_myo.Communication.SocketManager;
import example.naoki.ble_myo.R;

/**
 * Created by Leki on 2016-09-03.
 */
public class SignUpActivity extends AppCompatActivity {

    EditText signup_id, signup_pw, signup_pwre;
    EditText signup_name, signup_height, signup_weight;
    boolean idRedundancy = false;

    String data;
    private SocketListener sl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signup);

        signup_id = (EditText)findViewById(R.id.signup_id);
        signup_pw = (EditText)findViewById(R.id.signup_pw);
        signup_pwre = (EditText)findViewById(R.id.signup_pwre);
        signup_name = (EditText)findViewById(R.id.signup_name);
        signup_height = (EditText)findViewById(R.id.signup_height);
        signup_weight = (EditText)findViewById(R.id.signup_weight);
    }

    public Handler mainHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            int code;
            String result;
            code=Integer.parseInt(msg.obj.toString().split(",")[0]);
            result=msg.obj.toString().split(",")[1];

            if(code==1 && Session.getIdCheck()==true){
                if(result.equals("o")){
                    idRedundancy=true;
                    Log.w("중복확인","o");
                    Toast.makeText(SignUpActivity.this, "중복확인완료", Toast.LENGTH_SHORT).show();
                }
                else if(result.equals("x")){
                    idRedundancy=false;
                    Log.w("중복확인","x");
                    Toast.makeText(SignUpActivity.this, "중복된 아이디입니다", Toast.LENGTH_SHORT).show();
                }
                else if(result.equals("r")){
                    idRedundancy=false;
                    Log.w("중복확인","r");
                    Toast.makeText(SignUpActivity.this, "다시 한번 눌러주세요", Toast.LENGTH_SHORT).show();
                }
                Session.setIdCheck(false);
            }
            if(code==2 && Session.getSignUp()==true){
                if(result.equals("o")){
                    Log.w("회원가입","o");
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    intent.putExtra("signup_id", signup_id.getText().toString());
                    //intent.putExtra("signup_pw", signup_pw.getText().toString());
                    //intent.putExtra("signup_name", signup_name.getText().toString());
                    Toast.makeText(SignUpActivity.this, "회원가입 완료!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else if(result.equals("r")){
                    Log.w("회원가입","r");
                    Toast.makeText(SignUpActivity.this, "다시 한번 눌러주세요", Toast.LENGTH_SHORT).show();
                }
                Session.setSignUp(false);
            }
            sl.interrupt();
            Log.w("받긴받았음","회원가입");
        }
    };

    public void idCheckRedundancyButtonClick(View view){
        boolean check1;
        int ci1;
        if(signup_id.getText().toString().equals("")){
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        data = signup_id.getText().toString();
        check1=false;
        for(int i=0;i<data.length();i++){
            ci1=data.charAt(i);
            //Log.w("현재문자",Integer.toString(ci1));
            //a~z = 97~122, A~Z= 65~90, 0~9= 48~57
            if(ci1<=47 || (ci1>=58 && ci1<=64) || (ci1>=91 && ci1<=96) || ci1>=123){
                check1=true;
                break;
            }
        }
        if(check1==true){
            Toast.makeText(this, "아이디는 영문과 숫자만 사용해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        sl=new SocketListener(mainHandler);
        sl.setDaemon(true);
        sl.start();
        Thread worker = new Thread() {
            public void run() {
                try {
                    SocketManager.send("S/1+" + data + "/E");
                    Session.setIdCheck(true);
                }catch(IOException e){
                    sl.interrupt();
                }
            }
        };
        worker.start();
    }

    public void signUpOkButtonClick(View view){
        String blank = "";
        int ci1;
        boolean check1;
        if(blank.equals(signup_id.getText().toString())){
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!idRedundancy){
            Toast.makeText(this, "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(blank.equals(signup_pw.getText().toString())){
            Toast.makeText(this, "패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        check1=false;
        for(int i=0;i<data.length();i++){
            ci1=data.charAt(i);
            if(ci1<=47 || (ci1>=58 && ci1<=64) || (ci1>=91 && ci1<=96) || ci1>=123){
                check1=true;
                break;
            }
        }
        if(check1==true){
            Toast.makeText(this, "비밀번호는 영문과 숫자만 사용해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if(blank.equals(signup_pwre.getText().toString())){
            Toast.makeText(this, "패스워드를 한번 더 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(signup_pw.getText().toString().equals(signup_pwre.getText().toString()) == false){
            Toast.makeText(this, "패스워드 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(blank.equals(signup_name.getText().toString())){
            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(blank.equals(signup_height.getText().toString())){
            Toast.makeText(this, "키를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(blank.equals(signup_weight.getText().toString())){
            Toast.makeText(this, "몸무게를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        sl=new SocketListener(mainHandler);
        sl.setDaemon(true);
        sl.start();

        data="";
        data+="'"+signup_id.getText().toString()+"'"+",";
        data+="'"+signup_pw.getText().toString()+"'"+",";
        data+="'"+signup_name.getText().toString()+"'"+",";
        data+="'"+signup_height.getText().toString()+"'"+",";
        data+="'"+signup_weight.getText().toString()+"'";


        Thread worker = new Thread() {
            public void run() {
                try {
                    SocketManager.send("S/2+" + data + "/E");
                    Session.setSignUp(true);
                }catch(IOException e){
                    sl.interrupt();
                }
            }
        };
        worker.start();
    }

    public void signUpNoButtonClick(View view){
        finish();
    }
}
