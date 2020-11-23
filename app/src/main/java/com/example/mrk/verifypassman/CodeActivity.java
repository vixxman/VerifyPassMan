package com.example.mrk.verifypassman;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CodeActivity extends AppCompatActivity {

    private TextView mCode;
    private Button mLogoutButton;
    private Button mUpdateButton;
    private TextView mLogin;
    private Button mQRButton;
    OkHttpClient cl;
    Response resp;
    private String fileName="temp";

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        cl= new OkHttpClient();
        mCode =(TextView) findViewById(R.id.codeView);
        mLogoutButton=(Button) findViewById(R.id.LogoutButton);
        mUpdateButton=(Button) findViewById(R.id.updateButton);
        mLogin=(TextView) findViewById(R.id.loginView);
        mQRButton=(Button) findViewById(R.id.QrButton);

        mQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ZBarScannerActivity.class);
                intent.putExtra(ZBarConstants.SCAN_MODES, 64);
                startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
            }
        });

        final String login = getIntent().getStringExtra("login");
        final String pass = getIntent().getStringExtra("password");

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int code;
                deleteFile(fileName);
                do{
                    resp=null;
                    httpGetOut(login, pass);
                    try{
                        Thread.sleep(800);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    if(resp!=null) code = resp.code();
                    else code=-1;
                }while(-1==code || code==0);
                if(code==200){
                    Intent i=new Intent(v.getContext(), MainActivity.class);
                    startActivity(i);
                }
                else{
                    mCode.setText("try update");
                }
            }
        });

        mLogin.setText(login);
        int code;
        do{
            resp=null;
            httpGet(login, pass);
            try{
                Thread.sleep(800);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            if(resp!=null) code = resp.code();
            else code=-1;
        }while(-1==code || code==0);
        if(code==200){
            try{
                mCode.setText(resp.header("antichit"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            mCode.setText("try update");
        }

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int code;
                do{
                    resp=null;
                    httpGet(login, pass);
                    try{
                        Thread.sleep(800);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    if(resp!=null) code = resp.code();
                    else code=-1;
                }while(-1==code || code==0);
                if(code==200){
                    try{
                        mCode.setText(resp.header("antichit"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if(code==401){
                    Intent i=new Intent(v.getContext(), MainActivity.class);
                    i.putExtra("login", login);
                    i.putExtra("password", pass);
                    startActivity(i);
                }
                else{
                    mCode.setText("try update");
                }
            }
        });
    }

    public void httpGet(String login, String password){
        //HttpUrl.Builder urlBuilder = HttpUrl.parse("http://188.225.18.68:8080/codem").newBuilder();
        //urlBuilder.addQueryParameter("login", login);
        //urlBuilder.addQueryParameter("password", password);
        String url = "http://188.225.18.68:8080/codem?login="+login+"&password="+password;
        //String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        Call call=cl.newCall(request);
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

    public void httpGetOut(String login, String password){
        //HttpUrl.Builder urlBuilder = HttpUrl.parse("http://188.225.18.68:8080/signoutm").newBuilder();
        //urlBuilder.addQueryParameter("login", login);
        //urlBuilder.addQueryParameter("password", password);
        //String url = urlBuilder.build().toString();
        String url = "http://188.225.18.68:8080/signoutm?login="+login+"&password="+password;
        Request request = new Request.Builder().url(url).build();
        Call call=cl.newCall(request);
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
