package com.example.mrk.verifypassman;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import okhttp3.*;



public class MainActivity extends AppCompatActivity {

    private Button mLoginButton;
    private EditText mEditLogin;
    private EditText mEditPassword;
    OkHttpClient client;
    private String fileName="temp";
    Response resp;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client=new OkHttpClient();

        mLoginButton=(Button) findViewById(R.id.loginButton);
        mEditLogin=(EditText) findViewById(R.id.LoginText);
        mEditLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditLogin.setText("");
            }
        });
        mEditPassword = (EditText) findViewById(R.id.PasswordText);
        mEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditPassword.setText("");
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String login=mEditLogin.getText().toString();
                String password=mEditPassword.getText().toString();
                if(login==null || password==null){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Заполните поля!",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                /*try{
                    login = EncryptionService.EncryptAES(login);
                    password = EncryptionService.EncryptAES(password);
                }catch (Exception e){
                    e.printStackTrace();
                }*/
                int code;
                do{
                    resp=null;
                    httpGet(login,password);
                    try{
                        Thread.sleep(800);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    if(resp!=null) code = resp.code();
                    else code=-1;
                }while(code==0 || code==-1);
                if(code==200){
                    try{
                        FileOutputStream fOut = openFileOutput(fileName, MODE_PRIVATE);
                        OutputStreamWriter osw = new OutputStreamWriter(fOut);
                        osw.write(login+" "+password);
                        osw.flush();
                        osw.close();
                        Intent i=new Intent(v.getContext(), CodeActivity.class);
                        i.putExtra("login", login);
                        i.putExtra("password", password);
                        startActivity(i);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                if(code==302){
                    Intent i=new Intent(v.getContext(), CodeActivity.class);
                    i.putExtra("login", login);
                    i.putExtra("password", password);
                    startActivity(i);
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Error",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }

    public void httpGet(String login, String password){
        //HttpUrl.Builder urlBuilder = HttpUrl.parse("http://188.225.18.68:8080/signinm").newBuilder();
        //urlBuilder.addQueryParameter("login", login);
        //urlBuilder.addQueryParameter("password", password);
        String url = "http://188.225.18.68:8080/signinm?login="+login+"&password="+password;
        Request request = new Request.Builder().url(url).build();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                savResp(response);
            }
        });
    }

    private void savResp(Response response){
        resp=response;
    }

}
