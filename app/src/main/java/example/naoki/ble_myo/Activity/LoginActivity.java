package example.naoki.ble_myo.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

import example.naoki.ble_myo.DataProcess.ClientSocket;
import example.naoki.ble_myo.R;

/**
 * Created by Leki on 2016-09-02.
 */
public class LoginActivity extends Activity {
    EditText idEditText, pwEditText;
    Button loginButton, guestLoginButton, signUpButton, questionButton;
    String name="";

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
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                idEditText.setText(data.getStringExtra("signup_id"));
                pwEditText.setText(data.getStringExtra("signup_pw"));
                name = data.getStringExtra("signup_name");
            }
        }
    }

    public void signUpButtonClick(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivityForResult(intent, 1);
    }

    public void questionButtonClick(View view) {
//        try {
////            ClientSocket.sendMessage("0+Hello");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void loginButtonClick(View view) {
        if (idEditText.getText().toString().equals("a")) {
            if (pwEditText.getText().toString().equals("a")) {
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                if(!name.equals("")) intent.putExtra("name", name);
                else intent.putExtra("name", idEditText.getText().toString());
                startActivity(intent);
            }
            System.out.println("pw fail");
        }
        System.out.println("id fail");
    }

    public void guestLoginButtonClick(View view) {
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        intent.putExtra("name", "guest");
        startActivity(intent);
    }
}
