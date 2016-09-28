package example.naoki.ble_myo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import example.naoki.ble_myo.R;

/**
 * Created by Leki on 2016-09-03.
 */
public class SignUpActivity extends AppCompatActivity {
    EditText signup_id, signup_pw, signup_pwre;
    EditText signup_name, signup_height, signup_weight;
    boolean idRedundancy = false;

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

    public void idCheckRedundancyButtonClick(View view){
        if(idRedundancy == false){
            if(signup_id.getText().toString().equals("")){
                Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            idRedundancy = true;
            Toast.makeText(this, "중복확인 완료", Toast.LENGTH_SHORT).show();
        }
    }

    public void signUpOkButtonClick(View view){
        String blank = "";
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
        if(blank.equals(signup_pwre.getText().toString())){
            Toast.makeText(this, "패스워드 확인을 입력해주세요.", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        intent.putExtra("signup_id", signup_id.getText().toString());
        intent.putExtra("signup_pw", signup_pw.getText().toString());
        intent.putExtra("signup_name", signup_name.getText().toString());
        Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK, intent);
        finish();
    }

    public void signUpNoButtonClick(View view){
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
