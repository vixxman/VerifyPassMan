package com.example.mrk.verifypassman;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoadActivity extends AppCompatActivity {

    private String fileName="temp";
    OkHttpClient cli;
    Response resp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        cli= new OkHttpClient();

        try{
            FileInputStream fIn = openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fIn);
            char[] inputBuffer = new char[50];
            isr.read(inputBuffer);
            String readString = new String(inputBuffer);
            if(readString.length()==0){
                Intent i=new Intent(this, MainActivity.class);
                startActivity(i);
            }
            else{
                String[] c=readString.split(" ");
                String l=c[0];
                String p=c[1];
                p=p.substring(0,p.indexOf(0x00));
                int code;
                do{
                    resp=null;
                    httpGet(l,p);
                    try{
                        Thread.sleep(800);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    if(resp!=null) code = resp.code();
                    else code=-1;
                }while(code==0 || code==-1);
                if(code==200 || code==302){
                    Intent i=new Intent(this, CodeActivity.class);
                    i.putExtra("login", l);
                    i.putExtra("password", p);
                    startActivity(i);
                }
                else{
                    deleteFile(fileName);
                    Intent i=new Intent(this, MainActivity.class);
                    startActivity(i);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
            Intent i=new Intent(this, MainActivity.class);
            startActivity(i);
        }

    }

    public int httpGet(String login, String password){
        //HttpUrl.Builder urlBuilder = HttpUrl.parse("http://188.225.18.68:8080/signinm").newBuilder();
        //urlBuilder.addQueryParameter("login", login);
        //urlBuilder.addQueryParameter("password", password);

        String url = "http://188.225.18.68:8080/signinm?login="+login+"&password="+password;
        Request request = new Request.Builder().url(url).build();
        Call call = cli.newCall(request);
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
        return 0;
    }

    private void savResp(Response response){
        resp=response;
    }
}


