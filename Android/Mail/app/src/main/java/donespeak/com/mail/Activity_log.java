package donespeak.com.mail;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import javax.mail.internet.InternetAddress;

public class Activity_log extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        setTitle("登陆/注销");

        String nameStr = "";
        String numberStr = "";
        if(MainActivity.userEmail != null && MainActivity.userEmail.length() != 0 ){
            try {
                InternetAddress emailAddr = new InternetAddress(MainActivity.userEmail);
                nameStr = emailAddr.getPersonal();
                numberStr = emailAddr.getAddress();
                if(nameStr == null || nameStr.length() == 0){
                    nameStr = numberStr.substring(0,numberStr.lastIndexOf("@"));
                }
            }catch(Exception e) {
                if (MainActivity.DEBUG) {
                    e.printStackTrace();
                }
                nameStr = "";
                numberStr = "";
            }
        }
        TextView name = (TextView)findViewById(R.id.user_name);
        name.setText(nameStr);
        TextView number = (TextView)findViewById(R.id.user_number);
        number.setText(numberStr);

        Button loginbtn =  (Button)findViewById(R.id.loginbtn);
        loginbtn.setOnClickListener(this);

        Button logoutbtn =  (Button)findViewById(R.id.logoutbtn);
        logoutbtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view ){
        int item = view.getId();
        if(item == R.id.loginbtn){
            Intent intent = new Intent(this, Activity_login.class);
            startActivity(intent);
        }else if(item == R.id.logoutbtn){
            MainActivity.userEmail = "";
            MainActivity.userPassword = "";

            SharedPreferences mySharedPreferences= getSharedPreferences("user_info", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putString("userEmail", "");
            editor.putString("userPassword", "");
            editor.commit();

            Log.i("login",MainActivity.userEmail == null?"null":MainActivity.userEmail);

            finish();
        }
    }

    @Override
    public void onRestart(){
        super.onRestart();
        String nameStr = "";
        String numberStr = "";
        if(MainActivity.userEmail != null && MainActivity.userEmail.length() != 0 ){
            try {
                InternetAddress emailAddr = new InternetAddress(MainActivity.userEmail);
                nameStr = emailAddr.getPersonal();
                numberStr = emailAddr.getAddress();
                if(nameStr == null || nameStr.length() == 0){
                    nameStr = numberStr.substring(0,numberStr.lastIndexOf("@"));
                }
            }catch(Exception e) {
                if (MainActivity.DEBUG) {
                    e.printStackTrace();
                }
                nameStr = "";
                numberStr = "";
            }
        }
        TextView name = (TextView)findViewById(R.id.user_name);
        name.setText(nameStr);
        TextView number = (TextView)findViewById(R.id.user_number);
        number.setText(numberStr);

    }
}
